package util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import enumerator.HttpMethod;
import enumerator.RequestUrlPart;
import enumerator.UrlPart;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);

    /**
     * @param queryString은 URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param 쿠키 값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }

    public static byte[] handleHttpRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestLines = parsedRequest(bufferedReader);
        List<Pair> headerPairList = headerPairList(requestLines);

        String request = requestLines.get(0);
        String[] requestSplits = request.split(" ");

        String requestMethod = requestSplits[RequestUrlPart.METHOD.getIndex()];
        try {
            HttpMethod httpMethod = HttpMethod.valueOf(requestMethod);
            switch (httpMethod) {
                case GET:
                    return disposeGetRequest(requestSplits[RequestUrlPart.URL_PART.getIndex()]);
                case POST:
                    return disposePostRequest(requestSplits[RequestUrlPart.URL_PART.getIndex()]);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("Invalid Http Method");
        }

        return null;
    }

    public static List<Pair> headerPairList(List<String> requestLines) {
        List<Pair> parsedHeaderPairs = new ArrayList<>();
        int requestLineCount = requestLines.size();
        for (int i = 1; i < requestLineCount; ++i) {
            parsedHeaderPairs.add(parseHeader(requestLines.get(i)));
        }

        return parsedHeaderPairs;
    }

    public static List<String> parsedRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && line.length() > 0) {
            log.info(line);
            requestLines.add(line);
        }

        return requestLines;
    }

    public static byte[] disposeGetRequest(String request) {
        try {
            return Files.readAllBytes(new File("./webapp" + request).toPath());
        } catch (IllegalArgumentException | IOException exception) {
            String[] urlParts = request.split("\\?");
            if (urlParts.length == 2) {
                String queryString = urlParts[UrlPart.QUERY_STRING.getIndex()];
                String encodedQueryString = decodingWithUrlEncoding(queryString);
                Map<String, String> parsedQueryString = parseQueryString(encodedQueryString);

                User newUser = new User(parsedQueryString.get("userId"), parsedQueryString.get("password"), parsedQueryString.get("name"), parsedQueryString.get("email"));
                log.info(newUser.toString());
            } else {
                //ToDo: queryString이 없지만 접근하는 경우
            }

            return "1234".getBytes();
            //ToDO: get RequestParam 처리
        }
    }

    public static byte[] disposePostRequest(String request) throws IOException {


        return Files.readAllBytes(new File("./webapp" + request).toPath());
    }

    public static String decodingWithUrlEncoding(String originalQueryString) {
        try {
            return URLDecoder.decode(originalQueryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    //ToDO: G
    public static Optional<String> handleQueryString(List<String> headers){
        String httpRequestStartLine = headers.get(0);
    }
}
