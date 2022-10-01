package webserver

import org.slf4j.LoggerFactory
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class RequestHandler(private val connection: Socket) : Thread() {
    companion object {
        private val logger = LoggerFactory.getLogger(RequestHandler::class.java)
    }

    override fun run() {
        logger.debug("New Client Connect! Connected IP : ${connection.inetAddress}, Port : ${connection.port}")
        connection.getInputStream().use {
            connection.getOutputStream().use {
                val dataOutputStream = DataOutputStream(it)
                val body = "Hello World".encodeToByteArray()

                response200Header(dataOutputStream = dataOutputStream, lengthOfBodyContent = body.size)
                response200Body(dataOutputStream = dataOutputStream, byte = body)
            }
        }
    }

    private fun response200Header(dataOutputStream: DataOutputStream, lengthOfBodyContent: Int) {
        try {
            dataOutputStream.writeBytes("HTTP/1.1 200 OK \r\n")
            dataOutputStream.writeBytes("Content-Type: text/html;charset=utf-8\r\n")
            dataOutputStream.writeBytes("Content-Length: $lengthOfBodyContent\r\n")
            dataOutputStream.writeBytes("\r\n")
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

    private fun response200Body(dataOutputStream: DataOutputStream, byte: ByteArray) {
        try {
            dataOutputStream.write(byte)
            dataOutputStream.flush()
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }
}