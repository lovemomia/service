package cn.momia.common.web.http.impl;

import java.util.Map;

public class MomiaHttpPutRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpPutRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, uri, params);
    }

    @Override
    protected void createHttpRequestBase(String url) {

    }
}
