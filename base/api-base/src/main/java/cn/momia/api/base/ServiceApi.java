package cn.momia.api.base;

import cn.momia.api.base.exception.MomiaExpiredException;
import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.api.base.http.MomiaHttpRequest;
import cn.momia.api.base.http.MomiaHttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public abstract class ServiceApi {
    protected String service;

    public void setService(String service) {
        this.service = service;
    }

    protected String url(Object... paths) {
        StringBuilder urlBuilder = new StringBuilder().append(service);
        for (Object path : paths) urlBuilder.append("/").append(path);

        return urlBuilder.toString();
    }

    protected Object executeRequest(MomiaHttpRequest request) {
        HttpClient httpClient = createHttpClient();
        try {
            HttpResponse response = httpClient.execute(request);
            if (!checkResponseStatus(response)) throw new RuntimeException("fail to execute request: " + request);;

            MomiaHttpResponse momiaHttpResponse = buildResponse(response);
            if (momiaHttpResponse.isTokenExpired()) throw new MomiaExpiredException();
            if (!momiaHttpResponse.isSuccessful()) throw new MomiaFailedException(momiaHttpResponse.getErrmsg());

            return momiaHttpResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClient createHttpClient() {
        // TODO more configuration of http client
        return HttpClients.createDefault();
    }

    private boolean checkResponseStatus(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private MomiaHttpResponse buildResponse(HttpResponse response) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = JSON.parseObject(entity);

        return JSON.toJavaObject(responseJson, MomiaHttpResponse.class);
    }
}
