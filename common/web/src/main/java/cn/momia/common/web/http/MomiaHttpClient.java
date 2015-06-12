package cn.momia.common.web.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MomiaHttpClient {
    public JSONObject execute(MomiaHttpRequest request) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new RuntimeException("fail to execute request: " + request);
        }

        String entity = EntityUtils.toString(response.getEntity());

        return JSON.parseObject(entity);
    }
}
