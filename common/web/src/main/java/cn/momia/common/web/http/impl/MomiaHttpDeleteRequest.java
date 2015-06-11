package cn.momia.common.web.http.impl;

import org.apache.http.client.methods.HttpDelete;

import java.util.Map;

public class MomiaHttpDeleteRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpDeleteRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, uri, params);
    }

    @Override
    protected void createHttpRequestBase(String url) {
        httpRequestBase = new HttpDelete(url);
    }
}
