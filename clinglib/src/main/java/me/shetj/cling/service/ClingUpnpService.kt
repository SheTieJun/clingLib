package me.shetj.cling.service

import android.content.Context
import java.io.IOException
import java.net.NetworkInterface
import java.util.*
import javax.servlet.AsyncEvent
import javax.servlet.AsyncListener
import javax.servlet.Servlet
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import me.shetj.cling.ClingManager
import org.fourthline.cling.UpnpServiceConfiguration
import org.fourthline.cling.android.AndroidNetworkAddressFactory
import org.fourthline.cling.android.AndroidRouter
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.model.message.Connection
import org.fourthline.cling.model.message.StreamRequestMessage
import org.fourthline.cling.model.message.StreamResponseMessage
import org.fourthline.cling.protocol.ProtocolFactory
import org.fourthline.cling.transport.Router
import org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl
import org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl
import org.fourthline.cling.transport.impl.AsyncServletUpnpStream
import org.fourthline.cling.transport.impl.jetty.JettyServletContainer
import org.fourthline.cling.transport.spi.InitializationException
import org.fourthline.cling.transport.spi.NetworkAddressFactory
import org.fourthline.cling.transport.spi.StreamServer


class ClingUpnpService : AndroidUpnpServiceImpl() {

    override fun createRouter(
        configuration: UpnpServiceConfiguration?,
        protocolFactory: ProtocolFactory?,
        context: Context?
    ): AndroidRouter {
        return super.createRouter(configuration, protocolFactory, context)
    }

    override fun createConfiguration(): UpnpServiceConfiguration {
        return object : AndroidUpnpServiceConfiguration() {
            override fun createNetworkAddressFactory(streamListenPort: Int): NetworkAddressFactory {
                return object : AndroidNetworkAddressFactory(streamListenPort) {

                    //fix Exception sending datagram to: /239.255.255.250: java.io.IOException: sendto failed: EPERM (Operation not permitted)
                    override fun discoverNetworkInterfaces() {
                        try {
                            try {
                                val interfaceEnumeration = NetworkInterface.getNetworkInterfaces()
                                for (iface in Collections.list(interfaceEnumeration)) {
                                    if( !iface.supportsMulticast() ) { // added due to Android security requirements
                                        continue;
                                    } // end of fix
                                    if (isUsableNetworkInterface(iface)) {
                                        synchronized(networkInterfaces) { networkInterfaces.add(iface) }
                                    } else {
                                    }
                                }
                            } catch (ex: Exception) {
                                throw InitializationException("Could not not analyze local network interfaces: $ex", ex)
                            }
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            }

            override fun createStreamServer(networkAddressFactory: NetworkAddressFactory?): StreamServer<*> {
                return object : AsyncServletStreamServerImpl(
                    AsyncServletStreamServerConfigurationImpl(
                        JettyServletContainer.INSTANCE,
                        networkAddressFactory!!.streamListenPort
                    )
                ) {


                    override fun createServlet(router: Router?): Servlet {
                        return object : HttpServlet() {
                            @Throws(ServletException::class, IOException::class)
                            override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
                                val startTime = System.currentTimeMillis()
                                val async = req.startAsync()
                                async.timeout = (getConfiguration().asyncTimeoutSeconds * 1000).toLong()
                                async.addListener(object : AsyncListener {
                                    @Throws(IOException::class)
                                    override fun onTimeout(arg0: AsyncEvent) {
                                    }

                                    @Throws(IOException::class)
                                    override fun onStartAsync(arg0: AsyncEvent) {
                                    }

                                    @Throws(IOException::class)
                                    override fun onError(arg0: AsyncEvent) {
                                    }

                                    @Throws(IOException::class)
                                    override fun onComplete(arg0: AsyncEvent) {
                                    }
                                })
                                val stream: AsyncServletUpnpStream = object : AsyncServletUpnpStream(router!!.protocolFactory, async, req) {
                                    override fun createConnection(): Connection {
                                        return AsyncServletConnection(getRequest())
                                    }

                                    //fix 用于一些播放链接有防盗链的情况，需要设置referer
                                    override fun process(requestMsg: StreamRequestMessage?): StreamResponseMessage {
                                        ClingManager.getInstant().getReferer()?.let {
                                            requestMsg?.headers?.add("Referer", it)
                                        }
                                        return super.process(requestMsg)
                                    }
                                }
                                router.received(stream)
                            }
                        }
                    }

                }

            }
        }
    }

}