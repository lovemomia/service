package cn.momia.service.sms.impl;

import cn.momia.common.web.secret.SecretKey;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class LuosimaoSmsSender extends AbstractSmsSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuosimaoSmsSender.class);

    @Override
    protected boolean doSend(String mobile, String code) {
        try {
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api", SecretKey.get("luosimao")));
            WebResource webResource = client.resource(conf.getString("Sms.Luosimao.Service"));
            MultivaluedMapImpl formData = new MultivaluedMapImpl();
            formData.add("mobile", mobile);
            formData.add("message", "验证码：" + code + "，30分钟内有效【哆啦亲子】");
            ClientResponse response =  webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
            int status = response.getStatus();
            if (status != 200) {
                LOGGER.error("fail to send verify code");
                return false;
            }

            String textEntity = response.getEntity(String.class);
            JSONObject responseJson = JSON.parseObject(textEntity);
            int error = responseJson.getInteger("error");

            if (error == 0) return true;

            LOGGER.error("fail to send verify code, error code is: {}", error);
        } catch (Exception e) {
            LOGGER.error("fail to send verify code", e);
        }

        return false;
    }
}
