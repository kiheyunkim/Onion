package httpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpResponse {
    private final List<String> headerLines;
    private byte[] body;

    public HttpResponse(HttpResponseType httpResponseType) {
        this.headerLines = new ArrayList<>();

        if (httpResponseType.equals(HttpResponseType.OK)) {
            headerLines.add("HTTP/1.1 200 OK");
        } else if (httpResponseType.equals(HttpResponseType.REDIRECT)) {
            headerLines.add("HTTP/1.1 302 Moved Permanently");
        } else if (httpResponseType.equals(HttpResponseType.NOT_FOUND)) {
            headerLines.add("HTTP/1.1 404 Not Found");
        }
    }

    public void addHeaderLine(String header) {
        headerLines.add(header);
    }

    public void addBody(byte[] body) {
        this.body = body;
    }

    public String getHeader() {
        StringBuilder headerBuilder = new StringBuilder();
        headerLines.forEach(line -> {
            headerBuilder.append(line);
            headerBuilder.append("\r\n");
        });

        headerBuilder.append("\r\n");
        return headerBuilder.toString();
    }

    public Optional<byte[]> getBody() {
        return Optional.ofNullable(body);
    }
}