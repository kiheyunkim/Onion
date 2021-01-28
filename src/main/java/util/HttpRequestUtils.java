package util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import db.DataBase;
import enumerator.HttpMethod;
import enumerator.RequestUrlPart;
import enumerator.UrlPart;
import httpRequest.HttpRequest;
import httpRequest.Url;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    private static final String filePath = "./webapp/";
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

    public static List<String> parseHeader(BufferedReader bufferedReader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && line.length() > 0) {
            log.info(line);
            requestLines.add(line);
        }

        return requestLines;
    }

    public static HttpRequest parseHttpRequest(List<String> requestLines) {
        return new HttpRequest(requestLines.get(0));
    }

    public static Map<String, String> parseHttpRequestHeader(List<String> requestLines) {
        Map<String, String> parsedHeaders = new HashMap<>();
        int requestLineCount = requestLines.size();
        for (int i = 1; i < requestLineCount; ++i) {
            Pair pair = parseHeader(requestLines.get(i));
            parsedHeaders.put(pair.getKey(), pair.getValue());
        }

        return parsedHeaders;
    }

    public static Url parseUrlParts(String requestUrl){
        return new Url(requestUrl);
    }

    public static byte[] handleHttpRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestLines = parseHeader(bufferedReader);

        HttpRequest httpRequest = parseHttpRequest(requestLines);
        Map<String, String> parsedHeader = parseHttpRequestHeader(requestLines);

        try {
            switch (HttpMethod.valueOf(httpRequest.getMethod())) {
                case GET:
                    return disposeGetRequest(httpRequest.getUrl());
                case POST:
                    int postBodyLength = Integer.parseInt(parsedHeader.get("Content-Length"));
                    return disposePostRequest(httpRequest.getUrl(), IOUtils.readData(bufferedReader, postBodyLength));
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("Invalid Http Method");
        }

        return null;
    }

    public static byte[] disposeGetRequest(String requestUrl) throws IOException {
        Url url = parseUrlParts(requestUrl);

        //ToDO: Query String은 여기서 처리

        try{
            return Files.readAllBytes(new File(filePath + url.getPath()).toPath());
        }catch (AccessDeniedException accessDeniedException){
            return "Not Found".getBytes(StandardCharsets.UTF_8);
        }
    }

    public static byte[] disposePostRequest(String url, String postBody) throws IOException {
        String decodedPostBody = decodingWithUrlEncoding(postBody);
        Map<String, String> parsedQueryString = parseQueryString(decodedPostBody);


        User newUser = new User(parsedQueryString.get("userId"), parsedQueryString.get("password"), parsedQueryString.get("name"), parsedQueryString.get("email"));

        DataBase.addUser(newUser);

        return newUser.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static String decodingWithUrlEncoding(String originalQueryString) {
        try {
            return URLDecoder.decode(originalQueryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
