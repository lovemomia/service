package cn.momia.common.web.http.impl;

import org.apache.http.client.methods.HttpDelete;

import java.util.Map;

public class MomiaHttpDeleteRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpDeleteRequest(String uri) {
        this("anonymous", true, uri, null);
    }

    public MomiaHttpDeleteRequest(String uri, Map<String, String> params) {
        this("anonymous", true, uri, params);
    }

    public MomiaHttpDeleteRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, params);

        httpRequestBase = new HttpDelete(new StringBuilder().append(uri).append("?").append(toUrlParams(params)).toString());
    }
}
