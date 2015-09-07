package cn.momia.service.base.sms.impl;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.base.sms.SmsSender;
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

public class LuosimaoSmsSender implements SmsSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuosimaoSmsSender.class);

    @Override
    public boolean send(String mobile, String msg) {
        try {
            // TODO 使用HttpClient
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api", Configuration.getString("Sms.Luosimao.Key")));
            WebResource webResource = client.resource(Configuration.getString("Sms.Luosimao.Service"));
            MultivaluedMapImpl formData = new MultivaluedMapImpl();
            formData.add("mobile", mobile);
            formData.add("message", msg);
            ClientResponse response =  webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
            int status = response.getStatus();
            if (status != 200) {
                LOGGER.error("fail to send msg to user, {}/{}", mobile, msg);
                return false;
            }

            String textEntity = response.getEntity(String.class);
            JSONObject responseJson = JSON.parseObject(textEntity);
            int error = responseJson.getInteger("error");

            if (error == 0) return true;

            LOGGER.error("fail to send msg to user, {}/{}, error code is: {}", mobile, msg, error);
        } catch (Exception e) {
            LOGGER.error("fail to send msg to user, {}/{}", mobile, msg, e);
        }

        return false;
    }
}
