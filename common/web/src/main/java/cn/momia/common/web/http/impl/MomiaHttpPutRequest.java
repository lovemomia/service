package cn.momia.common.web.http.impl;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MomiaHttpPutRequest extends AbstractMomiaHttpRequest {
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
}
