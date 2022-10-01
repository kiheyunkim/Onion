package webserver

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.ServerSocket

class WebServer {
	companion object {
		val logger: Logger = LoggerFactory.getLogger(WebServer::class.java)

		const val DEFAULT_PORT: Int = 8080
	}

}

fun main(args: Array<String>) {
	val port = if (args.isEmpty()) WebServer.DEFAULT_PORT else args.first().toInt()

	ServerSocket(port).use {
		WebServer.logger.info("Web Application Server started $port port.")

		while (true) {
			val connection = it.accept()
			val requestHandler = RequestHandler(connection = connection)

			requestHandler.start()
		}
	}
}