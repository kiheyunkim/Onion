package httpRequest;

import enumerator.UrlPart;

public class Url {
    private final String path;
    private final String queryString;

    public Url(String requestUrl) {
        String[] requestUrlSplits = requestUrl.split(" ");
        if (requestUrlSplits.length == 2) {
            path = requestUrlSplits[UrlPart.URL.getIndex()];
            queryString = requestUrlSplits[UrlPart.QUERY_STRING.getIndex()];
        } else {
            path = requestUrl;
            queryString = null;
        }

    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
