package cn.momia.service.user.sms.impl;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.user.sms.SmsSender;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LuosimaoSmsSender implements SmsSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuosimaoSmsSender.class);

    @Override
    public boolean send(String mobile, String message) {
        try {
            HttpPost httpPost = new HttpPost(Configuration.getString("Sms.Luosimao.Service"));

            String auth = "api:" + Configuration.getString("Sms.Luosimao.Key");
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", mobile));
            params.add(new BasicNameValuePair("message", message + "【松果亲子】"));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOGGER.error("fail to send msg to user, {}/{}, http response code: {}", mobile, message, response.getStatusLine().getStatusCode());
                return false;
            }

            String responseEntity = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseEntity);

            int error = responseJson.getInteger("error");
            if (error == 0) return true;

            LOGGER.error("fail to send msg to user, {}/{}, error code is: {}", mobile, message, error);
        } catch (Exception e) {
            LOGGER.error("fail to send msg to user, {}/{}", mobile, message, e);
        }

        return false;
    }
}
