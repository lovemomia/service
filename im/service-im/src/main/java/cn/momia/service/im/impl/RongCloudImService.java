package cn.momia.service.im.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.im.ImService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RongCloudImService extends DbAccessService implements ImService {
    @Override
    public String getToken(long userId) {
        if (userId <= 0) return "";
        String sql = "SELECT token FROM t_im_token WHERE userId=? AND status=1";
        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getString(1) : "";
            }
        });
    }

    @Override
    public String register(long userId, String nickName, String avatar) {
        try {
            HttpPost httpPost = new HttpPost(Configuration.getString("Im.RongCloud.Service"));

            String nonce = UUID.randomUUID().toString();
            String timestamp = String.valueOf(new Date().getTime()).substring(0, 10);

            httpPost.setHeader("App-Key", Configuration.getString("Im.RongCloud.AppKey"));
            httpPost.setHeader("Nonce", nonce);
            httpPost.setHeader("Timestamp", timestamp);
            httpPost.setHeader("Signature", DigestUtils.sha1Hex(Configuration.getString("Im.RongCloud.AppSecret") + nonce + timestamp));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("name", nickName));
            params.add(new BasicNameValuePair("portraitUri", avatar));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MomiaFailedException("fail to register im");
            }

            String responseEntity = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseEntity);

            int code = responseJson.getInteger("code");
            if (code != 200) throw new MomiaFailedException("fail to register im");

            String token = responseJson.getString("token");
            if (StringUtils.isBlank(token)) throw new MomiaFailedException("fail to register im");

            String sql = "INSERT INTO t_im_token(userId, token, addTime) VALUES (?, ?, NOW())";
            int count = jdbcTemplate.update(sql, new Object[] { userId, token });
            if (count <= 0) throw new MomiaFailedException("fail to register im");

            return token;
        } catch (Exception e) {
            throw new MomiaFailedException("fail to register im", e);
        }
    }
}
