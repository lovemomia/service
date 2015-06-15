package cn.momia.common.web.http.impl;

import org.apache.http.client.methods.HttpGet;

import java.util.Map;

public class MomiaHttpGetRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpGetRequest(String uri) {
        this("anonymous", true, uri, null);
    }

    public MomiaHttpGetRequest(String uri, Map<String, String> params) {
        this("anonymous", true, uri, params);
    }

    public MomiaHttpGetRequest(String name, boolean required, String uri) {
        this(name, required, uri, null);
    }

    public MomiaHttpGetRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, params);

        httpRequestBase = new HttpGet(new StringBuilder().append(uri).append("?").append(toUrlParams(params)).toString());
    }
}
