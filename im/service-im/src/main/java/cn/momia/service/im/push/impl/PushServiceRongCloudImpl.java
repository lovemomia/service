package cn.momia.service.im.push.impl;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.im.push.PushMsg;
import cn.momia.service.im.rongcloud.RongCloudUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PushServiceRongCloudImpl extends AbstractPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushServiceRongCloudImpl.class);

    @Override
    protected boolean doPush(long userId, PushMsg msg) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.SystemPush"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fromUserId", String.valueOf(SYSTEM_PUSH_USERID)));
            params.add(new BasicNameValuePair("toUserId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("objectName", "RC:TxtMsg"));
            params.add(new BasicNameValuePair("content", msg.toString()));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);
            if (responseJson.getInteger("code") != 200) {
                LOGGER.error("push failed: {}/{}", userId, msg);
                return false;
            }
        } catch (Exception e) {
            throw new MomiaErrorException("publish msg exception", e);
        }

        return true;
    }

    @Override
    protected void doBatchPush(PushMsg msg) {
        long totalUserCount = queryTotalUserCount();
        if (totalUserCount <= 0) return;
        int start = 0;
        while (start < totalUserCount) {
            List<Long> userIds = queryUserIds(start, 500);

            try {
                HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.SystemPush"));

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("fromUserId", String.valueOf(SYSTEM_PUSH_USERID)));
                for (long userId : userIds) {
                    params.add(new BasicNameValuePair("toUserId", String.valueOf(userId)));
                }
                params.add(new BasicNameValuePair("objectName", "RC:TxtMsg"));
                params.add(new BasicNameValuePair("content", msg.toString()));
                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                httpPost.setEntity(entity);

                JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);
                if (responseJson.getInteger("code") == 200) LOGGER.info("{} msg pushed", userIds.size());
            } catch (Exception e) {
                throw new MomiaErrorException("publish msg exception", e);
            }

            start += 500;
        }
    }

    private long queryTotalUserCount() {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE ImToken IS NOT NULL AND ImToken<>'' AND Status=1";
        return queryLong(sql);
    }

    private List<Long> queryUserIds(int start, int count) {
        String sql = "SELECT Id FROM SG_User WHERE ImToken IS NOT NULL AND ImToken<>'' AND Status=1 LIMIT ?,?";
        return queryLongList(sql, new Object[] { start, count });
    }
}
