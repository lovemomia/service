package cn.momia.service.im.rongcloud;

import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.webapp.config.Configuration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class RongCloudUtil {
    public static HttpPost createHttpPost(String service) {
        HttpPost httpPost = new HttpPost(service);

        String nonce = UUID.randomUUID().toString();
        String timestamp = String.valueOf(new Date().getTime()).substring(0, 10);

        httpPost.setHeader("App-Key", Configuration.getString("Im.RongCloud.AppKey"));
        httpPost.setHeader("Nonce", nonce);
        httpPost.setHeader("Timestamp", timestamp);
        httpPost.setHeader("Signature", DigestUtils.sha1Hex(Configuration.getString("Im.RongCloud.AppSecret") + nonce + timestamp));

        return httpPost;
    }

    public static JSONObject executeRequest(HttpPost httpPost) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new MomiaErrorException("fail to execute request: " + httpPost.getURI());

        String responseEntity = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = JSON.parseObject(responseEntity);

        int code = responseJson.getInteger("code");
        if (code != 200) throw new MomiaErrorException("fail to execute request: " + httpPost.getURI());

        return responseJson;
    }
}
