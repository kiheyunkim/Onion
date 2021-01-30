package webserver;

import httpResponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));) {

            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse httpResponse = HttpRequestUtils.handleHttpRequest(bufferedReader);

            sendResponse(dos, httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private void sendResponse(DataOutputStream dataOutputStream, HttpResponse httpResponse) throws IOException {
        dataOutputStream.writeBytes(httpResponse.getHeader());
        dataOutputStream.write(httpResponse.getBody().orElse("".getBytes(StandardCharsets.UTF_8)));
    }
}
