package cn.momia.common.web.http.impl;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MomiaHttpPostRequest extends AbstractMomiaHttpRequest {
    public MomiaHttpPostRequest(String uri, Map<String, String> params) {
        this("anonymous", true, uri, params);
    }

    public MomiaHttpPostRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, params);

        HttpPost httpPost = new HttpPost(uri);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(toNameValuePairs(params), "utf-8"));
            httpRequestBase = httpPost;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
