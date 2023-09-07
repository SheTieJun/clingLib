package com.android.cling.service

import com.android.cling.ClingDLNAManager
import com.android.cling.ClingDLNAManager.Companion.AV_TRANSPORT_SERVICE
import com.android.cling.ClingDLNAManager.Companion.RENDERING_CONTROL_SERVICE
import com.android.cling.ClingDLNAManager.Companion.SERVICE_CONNECTION_MANAGER
import com.android.cling.ClingDLNAManager.Companion.SERVICE_TYPE_CONTENT_DIRECTORY
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
import org.fourthline.cling.UpnpServiceConfiguration
import org.fourthline.cling.android.AndroidNetworkAddressFactory
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.model.message.Connection
import org.fourthline.cling.model.message.StreamRequestMessage
import org.fourthline.cling.model.message.StreamResponseMessage
import org.fourthline.cling.model.message.UpnpHeaders
import org.fourthline.cling.model.meta.RemoteDeviceIdentity
import org.fourthline.cling.model.types.ServiceType
import org.fourthline.cling.transport.Router
import org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl
import org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl
import org.fourthline.cling.transport.impl.AsyncServletUpnpStream
import org.fourthline.cling.transport.impl.jetty.JettyServletContainer
import org.fourthline.cling.transport.spi.InitializationException
import org.fourthline.cling.transport.spi.NetworkAddressFactory
import org.fourthline.cling.transport.spi.StreamServer


class ClingUpnpService : AndroidUpnpServiceImpl() {

    override fun createConfiguration(): UpnpServiceConfiguration {
        return object : AndroidUpnpServiceConfiguration() {
            override fun getExclusiveServiceTypes(): Array<ServiceType> = arrayOf(
                AV_TRANSPORT_SERVICE,
                RENDERING_CONTROL_SERVICE,
                SERVICE_CONNECTION_MANAGER,
                SERVICE_TYPE_CONTENT_DIRECTORY
            )

            override fun getDescriptorRetrievalHeaders(identity: RemoteDeviceIdentity?): UpnpHeaders? {
                if ( ClingDLNAManager.getInstant().getReferer() == null) return super.getDescriptorRetrievalHeaders(identity)
                //fix 用于一些播放链接有防盗链的情况，需要设置referer
                val headers = UpnpHeaders()
                ClingDLNAManager.getInstant().getReferer()?.let {
                    headers.add("Referer", it)
                }
                return headers
            }

            override fun createNetworkAddressFactory(streamListenPort: Int): NetworkAddressFactory {
                return object : AndroidNetworkAddressFactory(streamListenPort) {

                    //fix Exception sending datagram to: /239.255.255.250: java.io.IOException: sendto failed: EPERM (Operation not permitted)
                    override fun discoverNetworkInterfaces() {
                        try {
                            try {
                                val interfaceEnumeration = NetworkInterface.getNetworkInterfaces()
                                for (iface in Collections.list(interfaceEnumeration)) {
                                    if (!iface.supportsMulticast()) { // added due to Android security requirements
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
        }
    }
}