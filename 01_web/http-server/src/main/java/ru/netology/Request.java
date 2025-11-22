package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, List<String>> queryParams;
    private final String body;

    public Request(String method, String path, String body) {
        this.method = method;
        
        // Разделяем путь и query parameters
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            this.path = path.substring(0, queryIndex);
            String queryString = path.substring(queryIndex + 1);
            this.queryParams = parseQueryParams(queryString);
        } else {
            this.path = path;
            this.queryParams = new HashMap<>();
        }
        
        this.body = body;
    }

    private Map<String, List<String>> parseQueryParams(String queryString) {
        Map<String, List<String>> params = new HashMap<>();
        
        List<NameValuePair> pairs = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        for (NameValuePair pair : pairs) {
            params.computeIfAbsent(pair.getName(), k -> new ArrayList<>())
                  .add(pair.getValue());
        }
        
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    public List<String> getQueryParams(String name) {
        return queryParams.getOrDefault(name, Collections.emptyList());
    }

    public Map<String, List<String>> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", queryParams=" + queryParams +
                ", body='" + body + '\'' +
                '}';
    }
}
