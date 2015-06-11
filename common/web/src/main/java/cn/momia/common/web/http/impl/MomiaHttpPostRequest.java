package cn.momia.common.web.http.impl;

import java.util.Map;

public class MomiaHttpPostRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpPostRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, uri, params);
    }

    @Override
    protected void createHttpRequestBase(String url) {

    }
}
