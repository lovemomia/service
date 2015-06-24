package cn.momia.common.web.http.impl;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MomiaHttpPostRequest extends AbstractMomiaEntityEnclosingHttpRequest {
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

    public MomiaHttpPostRequest(String uri, String json) {
        super("anonymous", true, null);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

        StringEntity entity = new StringEntity(json, "utf-8");
        entity.setContentType("application/json");
        entity.setContentEncoding("utf-8");
        httpPost.setEntity(entity);

        setEntity(entity);
        httpRequestBase = httpPost;
    }
}
