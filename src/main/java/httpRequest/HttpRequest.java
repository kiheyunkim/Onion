package httpRequest;

import enumerator.RequestUrlPart;

public class HttpRequest {
    private final String method;
    private final String url;
    private final String version;

    public HttpRequest(String httpRequestStartLine) {
        String[] urlParts = httpRequestStartLine.split(" ");
        this.method = urlParts[RequestUrlPart.METHOD.getIndex()];
        this.url = urlParts[RequestUrlPart.URL_PART.getIndex()];
        this.version = urlParts[RequestUrlPart.PROTOCOL.getIndex()];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
