package cn.momia.common.web.http.impl;

import cn.momia.common.web.http.MomiaHttpRequest;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.HttpParams;

import java.net.URI;
import java.util.Map;

public abstract class AbstractMomiaHttpRequest implements MomiaHttpRequest {
    private String name;
    private boolean required;
    protected HttpRequestBase httpRequestBase;

    public AbstractMomiaHttpRequest(String name, boolean required, String uri, Map<String, String> params) {
        this.name = name;
        this.required = required;

        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        if (params != null && params.size() > 0) {
            builder.append("?");
            int i = 0;
            int paramCount = params.size();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                i++;
                builder.append(entry.getKey()).append(("=")).append(entry.getValue());
                if (i < paramCount) builder.append("&");
            }
        }
        createHttpRequestBase(builder.toString());
    }

    protected abstract void createHttpRequestBase(String url);

    @Override
    public String getName() {
        return name;
    }

    @Override
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
}
