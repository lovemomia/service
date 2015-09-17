package cn.momia.service.im.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.im.Group;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RongCloudImService extends DbAccessService implements ImService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RongCloudImService.class);

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
            HttpPost httpPost = createHttpPost(Configuration.getString("Im.RongCloud.Service.GetToken"));

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

    private HttpPost createHttpPost(String service) {
        HttpPost httpPost = new HttpPost(service);

        String nonce = UUID.randomUUID().toString();
        String timestamp = String.valueOf(new Date().getTime()).substring(0, 10);

        httpPost.setHeader("App-Key", Configuration.getString("Im.RongCloud.AppKey"));
        httpPost.setHeader("Nonce", nonce);
        httpPost.setHeader("Timestamp", timestamp);
        httpPost.setHeader("Signature", DigestUtils.sha1Hex(Configuration.getString("Im.RongCloud.AppSecret") + nonce + timestamp));

        return httpPost;
    }

    @Override
    public Group queryGroup(long productId, long skuId) {
        if (productId <= 0 || skuId <= 0) return Group.NOT_EXIST_GROUP;

        String sql = "SELECT id, name, productId, skuId FROM t_im_group WHERE productId=? AND skuId=? AND status=1";
        return jdbcTemplate.query(sql, new Object[] { productId, skuId }, new ResultSetExtractor<Group>() {
            @Override
            public Group extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? buildGroup(rs) : Group.NOT_EXIST_GROUP;
            }
        });
    }

    private Group buildGroup(ResultSet rs) throws SQLException {
        try {
            Group group = new Group();
            group.setId(rs.getLong("id"));
            group.setName(rs.getString("name"));
            group.setProductId(rs.getLong("productId"));
            group.setSkuId(rs.getLong("skuId"));

            return group;
        } catch (Exception e) {
            LOGGER.error("fail to build group: {}", rs.getLong("id"), e);
            return Group.INVALID_GROUP;
        }
    }

    @Override
    public long createGroup(final String groupName, final long productId, final long skuId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                String sql = "INSERT INTO t_im_group(name, productId, skuId, addTime) VALUES (?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, groupName);
                ps.setLong(2, productId);
                ps.setLong(3, skuId);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean initGroup(long userId, long groupId, String groupName) {
        try {
            HttpPost httpPost = createHttpPost(Configuration.getString("Im.RongCloud.Service.CreateGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            params.add(new BasicNameValuePair("groupName", groupName));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MomiaFailedException("fail to init group");
            }

            String responseEntity = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseEntity);

            int code = responseJson.getInteger("code");
            if (code != 200) throw new MomiaFailedException("fail to init group");

            return true;
        } catch (Exception e) {
            throw new MomiaFailedException("fail to init group", e);
        }
    }

    @Override
    public boolean joinGroup(long groupId, Collection<Long> userIds) {
        try {
            HttpPost httpPost = createHttpPost(Configuration.getString("Im.RongCloud.Service.JoinGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (long userId : userIds) {
                params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            }
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MomiaFailedException("fail to join group");
            }

            String responseEntity = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseEntity);

            int code = responseJson.getInteger("code");
            if (code != 200) throw new MomiaFailedException("fail to join group");

            return true;
        } catch (Exception e) {
            throw new MomiaFailedException("fail to join group", e);
        }
    }

    @Override
    public boolean deleteGroup(long userId, long groupId) {
        try {
            HttpPost httpPost = createHttpPost(Configuration.getString("Im.RongCloud.Service.DeleteGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MomiaFailedException("fail to delete group");
            }

            String responseEntity = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseEntity);

            int code = responseJson.getInteger("code");
            if (code != 200) throw new MomiaFailedException("fail to delete group");

            return true;
        } catch (Exception e) {
            throw new MomiaFailedException("fail to delete group", e);
        }
    }

    @Override
    public void logGroupUsers(long groupId, Set<Long> userIds) {
        try {
            String sql = "INSERT INTO t_im_group_user(groupId, userId, addTime) VALUES (?, ?, NOW())";
            List<Object[]> params = new ArrayList<Object[]>();
            for (long userId : userIds) {
                params.add(new Object[] { groupId, userId });
            }

            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            LOGGER.error("fail to log group users for group: {}", groupId);
        }
    }

    @Override
    public void deleteGroupInfo(long groupId) {
        try {
            String sql = "UPDATE t_im_group SET status=0 WHERE id=?";
            jdbcTemplate.update(sql, new Object[] { groupId });

            sql = "UPDATE t_im_group_user SET status=0 WHERE groupId=?";
            jdbcTemplate.update(sql, new Object[] { groupId });
        } catch (Exception e) {
            LOGGER.error("fail to delete group infod for group: {}", groupId);
        }
    }
}
