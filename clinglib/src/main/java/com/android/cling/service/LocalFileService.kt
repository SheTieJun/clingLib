package com.android.cling.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.android.cling.util.Utils.HTTP_SERVLET_KEY
import com.android.cling.util.Utils.PORT_LISTEN_DEFAULT
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception
import java.util.logging.Logger
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder


/**
 * 本地服务器，用来访问本地的文件
 *  http://localhost:8080/localeFile/xxx.txt
 */
class LocalFileService : Service() {

    private val server = Server(PORT_LISTEN_DEFAULT)

    override fun onCreate() {
        super.onCreate()
        Log.i("LocalFileService","createServer")
        createServer()
    }

    @SuppressLint("RestrictedApi")
    private fun createServer() {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            val contextHandler = ServletContextHandler(ServletContextHandler.SESSIONS)
            contextHandler.contextPath = "/"
            server.handler = contextHandler
            // http://localhost:8080/localeFile/xxx.txt
            contextHandler.addServlet(ServletHolder(FileServlet()), "/$HTTP_SERVLET_KEY/*")
            server.start()
            server.join()
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("LocalFileService","onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }


    internal class FileServlet : HttpServlet() {
        @Throws(IOException::class)
        override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
            try {
                Log.i("LocalFileService","request.pathInfo = ${request.pathInfo}")
                val filePath = request.pathInfo.substring(1)
                val file = File(filePath)
                if (file.exists() && file.isFile) {
                    response.contentType = "application/octet-stream"
                    response.setContentLength(file.length().toInt())
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.name + "\"")
                    FileInputStream(file).use { fileInputStream ->
                        response.outputStream.use { outputStream ->
                            val buffer = ByteArray(64000)
                            var bytesRead: Int
                            while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                } else {
                    response.status = HttpServletResponse.SC_NOT_FOUND
                }
            }catch (e:Exception){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,e.message)
            }
        }
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)

    }

    override fun bindService(service: Intent, conn: ServiceConnection, flags: Int): Boolean {
        return super.bindService(service, conn, flags)
    }

    override fun stopService(name: Intent?): Boolean {
        Log.i("LocalFileService","stopService")
        server.stop()
        return super.stopService(name)
    }

    override fun onDestroy() {
        server.destroy()
        super.onDestroy()
    }
}