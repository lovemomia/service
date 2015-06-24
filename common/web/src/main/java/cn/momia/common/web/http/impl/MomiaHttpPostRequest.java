package cn.momia.common.web.http.impl;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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

    public static void main(String[] args) {
        HttpPost httpPost = new HttpPost("http://localhost:8080/participant");
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

        try {
            StringEntity entity = new StringEntity("{\"birthday\": \"2016-03-18\",\"name\": \"小吴\",\"sex\": 1}", "utf-8");
            entity.setContentType("application/json");
            entity.setContentEncoding("utf-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("fail to execute request: " + httpClient);
            }

            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
