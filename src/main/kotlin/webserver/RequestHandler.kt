package webserver

import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class RequestHandler(private val connection: Socket) : Thread() {
    companion object {
        private val logger = LoggerFactory.getLogger(RequestHandler::class.java)
    }

    override fun run() {
        logger.debug("New Client Connect! Connected IP : ${connection.inetAddress}, Port : ${connection.port}")

        connection.use { conn ->
            conn.getInputStream().bufferedReader().use { bufferedReader ->
                while (true){
                    val readLine = bufferedReader.readLine()?.ifEmpty { null } ?: break
                    logger.error(readLine)
                }

                DataOutputStream(conn.getOutputStream()).use {
                    val body = "Hello World".encodeToByteArray()

                    response200Header(dataOutputStream = it, lengthOfBodyContent = body.size)
                    response200Body(dataOutputStream = it, byte = body)
                }
            }
        }
    }

    private fun response200Header(dataOutputStream: DataOutputStream, lengthOfBodyContent: Int) {
        try {
            dataOutputStream.write("HTTP/1.1 200 OK \r\n".toByteArray())
            dataOutputStream.write("Content-Type: text/html;charset=utf-8\r\n".toByteArray())
            dataOutputStream.write("Content-Length: $lengthOfBodyContent\r\n".toByteArray())
            dataOutputStream.write("\r\n".toByteArray())
        } catch (e: IOException) {
            logger.error(e.message, e)
        }
    }

    private fun response200Body(dataOutputStream: DataOutputStream, byte: ByteArray) {
        try {
            dataOutputStream.write(byte)
            dataOutputStream.flush()
        } catch (e: IOException) {
            logger.error(e.message, e)
        }
    }
}