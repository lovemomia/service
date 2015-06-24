package cn.momia.common.web.http.impl;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MomiaHttpPutRequest extends AbstractMomiaEntityEnclosingHttpRequest {
    public MomiaHttpPutRequest(String uri, Map<String, String> params) {
        this("anonymous", true, uri, params);
    }

    public MomiaHttpPutRequest(String name, boolean required, String uri, Map<String, String> params) {
        super(name, required, params);

        HttpPut httpPut = new HttpPut(uri);
        try {
            httpPut.setEntity(new UrlEncodedFormEntity(toNameValuePairs(params), "utf-8"));
            httpRequestBase = httpPut;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public MomiaHttpPutRequest(String uri, String json) {
        super("anonymous", true, null);

        HttpPut httpPut = new HttpPut(uri);
        httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");

        StringEntity entity = new StringEntity(json, "utf-8");
        entity.setContentType("application/json");
        entity.setContentEncoding("utf-8");
        httpPut.setEntity(entity);

        setEntity(entity);
        httpRequestBase = httpPut;
    }
}
