package cn.momia.common.web.http.impl;

import org.apache.http.client.methods.HttpGet;

import java.util.Map;

public class MomiaHttpGetRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpGetRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, uri, params);
    }

    protected void createHttpRequestBase(String url) {
        httpRequestBase = new HttpGet(url);
    }
}
