package cn.momia.common.web.http;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MomiaHttpRequest implements HttpUriRequest, HttpEntityEnclosingRequest {
    public static MomiaHttpRequest GET(String uri) {
        return GET("anonymous", true, uri, null);
    }

    public static MomiaHttpRequest GET(String uri, Map<String, String> params) {
        return GET("anonymous", true, uri, params);
    }

    public static MomiaHttpRequest GET(String name, boolean required, String uri) {
        return GET(name, required, uri, null);
    }

    public static MomiaHttpRequest GET(String name, boolean required, String uri, Map<String, String> params) {
        return new MomiaHttpRequest(name, required, uri, params) {
            @Override
            protected HttpRequestBase createHttpMethod(String uri, Map<String, String> params) {
                return new HttpGet(new StringBuilder().append(uri).append("?").append(toUrlParams(params)).toString());
            }
        };
    }

    public static MomiaHttpRequest POST(String uri, Map<String, String> params) {
        return POST("anonymous", true, uri, params);
    }

    public static MomiaHttpRequest POST(String name, boolean required, String uri, Map<String, String> params) {
        return new MomiaHttpRequest(name, required, uri, params) {
            @Override
            protected HttpRequestBase createHttpMethod(String uri, Map<String, String> params) {
                try {
                    HttpPost httpPost = new HttpPost(uri);
                    if (params != null && !params.isEmpty()) {
                        HttpEntity entity = new UrlEncodedFormEntity(toNameValuePairs(params), "UTF-8");
                        httpPost.setEntity(entity);
                        setEntity(entity);
                    }

                    return httpPost;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static MomiaHttpRequest POST(String uri, String content) {
        return POST(uri, content, "application/json");
    }

    public static MomiaHttpRequest POST(String uri, final String content, final String contentType) {
        return new MomiaHttpRequest("anonymous", true, uri, null) {
            @Override
            protected HttpRequestBase createHttpMethod(String uri, Map<String, String> params) {
                HttpEntity entity = toEntity(content, contentType);

                HttpPost httpPost = new HttpPost(uri);
                httpPost.addHeader(HTTP.CONTENT_TYPE, contentType);
                httpPost.setEntity(entity);
                setEntity(entity);

                return httpPost;
            }
        };
    }

    public static MomiaHttpRequest PUT(String uri, Map<String, String> params) {
        return PUT("anonymous", true, uri, params);
    }

    public static MomiaHttpRequest PUT(String name, boolean required, String uri, Map<String, String> params) {
        return new MomiaHttpRequest(name, required, uri, params) {
            @Override
            protected HttpRequestBase createHttpMethod(String uri, Map<String, String> params) {
                try {
                    HttpPut httpPut = new HttpPut(uri);
                    if (params != null && !params.isEmpty()) {
                        HttpEntity entity = new UrlEncodedFormEntity(toNameValuePairs(params), "UTF-8");
                        httpPut.setEntity(entity);
                        setEntity(entity);
                    }

                    return httpPut;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static MomiaHttpRequest PUT(String uri, String content) {
        return PUT(uri, content, "application/json");
    }

    public static MomiaHttpRequest PUT(String uri, final String content, final String contentType) {
        return new MomiaHttpRequest("anonymous", true, uri, null) {
            @Override
            protected HttpRequestBase createHttpMethod(String uri, Map<String, String> params) {
                HttpEntity entity = toEntity(content, contentType);

                HttpPut httpPut = new HttpPut(uri);
                httpPut.addHeader(HTTP.CONTENT_TYPE, contentType);
                httpPut.setEntity(entity);
                setEntity(entity);

                return httpPut;
            }
        };
    }

    public static MomiaHttpRequest DELETE(String uri) {
        return DELETE("anonymous", true, uri, null);
    }

    public static MomiaHttpRequest DELETE(String uri, Map<String, String> params) {
        return DELETE("anonymous", true, uri, params);
    }

    public static MomiaHttpRequest DELETE(String name, boolean required, String uri) {
        return DELETE(name, required, uri, null);
    }

    public static MomiaHttpRequest DELETE(String name, boolean required, String uri, Map<String, String> params) {
        return new MomiaHttpRequest(name, required, uri, params) {
            @Override
            protected HttpRequestBase createHttpMethod(String uri, Map<String, String> params) {
                return new HttpDelete(new StringBuilder().append(uri).append("?").append(toUrlParams(params)).toString());
            }
        };
    }

    private String name;
    private boolean required;

    protected HttpEntity entity;
    protected HttpRequestBase httpRequestBase;

    public MomiaHttpRequest(String uri) {
        this("anonymous", true, uri, null);
    }

    public MomiaHttpRequest(String uri, Map<String, String> params) {
        this("anonymous", true, uri, params);
    }

    public MomiaHttpRequest(String name, boolean required, String uri) {
        this(name, required, uri, null);
    }

    public MomiaHttpRequest(String name, boolean required, String uri, Map<String, String> params) {
        this.name = name;
        this.required = required;

        httpRequestBase = createHttpMethod(uri, params);
    }

    protected abstract HttpRequestBase createHttpMethod(String uri, Map<String, String> params);

    protected String toUrlParams(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (params != null && params.size() > 0) {
            int i = 0;
            int paramCount = params.size();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                i++;
                builder.append(entry.getKey()).append(("=")).append(entry.getValue());
                if (i < paramCount) builder.append("&");
            }
        }

        return builder.toString();
    }

    protected List<NameValuePair> toNameValuePairs(Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return nameValuePairs;
    }

    protected static StringEntity toEntity(String content, String contentType) {
        StringEntity entity = new StringEntity(content, "UTF-8");
        entity.setContentType(contentType);
        entity.setContentEncoding("UTF-8");

        return entity;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public String getMethod() {
        return httpRequestBase.getMethod();
    }

    @Override
    public URI getURI() {
        return httpRequestBase.getURI();
    }

    @Override
    public void abort() throws UnsupportedOperationException {
        httpRequestBase.abort();
    }

    @Override
    public boolean isAborted() {
        return httpRequestBase.isAborted();
    }

    @Override
    public RequestLine getRequestLine() {
        return httpRequestBase.getRequestLine();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return httpRequestBase.getProtocolVersion();
    }

    @Override
    public boolean containsHeader(String name) {
        return httpRequestBase.containsHeader(name);
    }

    @Override
    public Header[] getHeaders(String name) {
        return httpRequestBase.getHeaders(name);
    }

    @Override
    public Header getFirstHeader(String name) {
        return httpRequestBase.getFirstHeader(name);
    }

    @Override
    public Header getLastHeader(String name) {
        return httpRequestBase.getLastHeader(name);
    }

    @Override
    public Header[] getAllHeaders() {
        return httpRequestBase.getAllHeaders();
    }

    @Override
    public void addHeader(Header header) {
        httpRequestBase.addHeader(header);
    }

    @Override
    public void addHeader(String name, String value) {
        httpRequestBase.addHeader(name, value);
    }

    @Override
    public void setHeader(Header header) {
        httpRequestBase.setHeader(header);
    }

    @Override
    public void setHeader(String name, String value) {
        httpRequestBase.setHeader(name, value);
    }

    @Override
    public void setHeaders(Header[] headers) {
        httpRequestBase.setHeaders(headers);
    }

    @Override
    public void removeHeader(Header header) {
        httpRequestBase.removeHeader(header);
    }

    @Override
    public void removeHeaders(String name) {
        httpRequestBase.removeHeaders(name);
    }

    @Override
    public HeaderIterator headerIterator() {
        return httpRequestBase.headerIterator();
    }

    @Override
    public HeaderIterator headerIterator(String name) {
        return httpRequestBase.headerIterator(name);
    }

    @Override
    public HttpParams getParams() {
        return httpRequestBase.getParams();
    }

    @Override
    public void setParams(HttpParams params) {
        httpRequestBase.setParams(params);
    }

    @Override
    public boolean expectContinue() {
        final Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);

        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public HttpEntity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "MomiaHttpRequest{" +
                "name='" + name + '\'' +
                ", required=" + required +
                ", entity=" + entity +
                ", httpRequestBase=" + httpRequestBase +
                '}';
    }
}
