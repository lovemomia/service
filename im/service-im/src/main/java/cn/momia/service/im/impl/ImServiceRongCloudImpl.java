package cn.momia.service.im.impl;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.im.Consts;
import cn.momia.service.im.rongcloud.RongCloudUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImServiceRongCloudImpl extends AbstractImService {
    @Override
    public String generateImToken(long userId, String nickName, String avatar) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.GetToken"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("name", nickName));
            params.add(new BasicNameValuePair("portraitUri", avatar));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            String token = responseJson.getString("token");
            if (StringUtils.isBlank(token)) throw new MomiaErrorException("fail to register im");

            return token;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to register im", e);
        }
    }

    @Override
    public boolean updateNickName(long userId, String nickName) {
        return updateUserInfo(userId, nickName, null);
    }

    private boolean updateUserInfo(long userId, String nickName, String avatar) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.UpdateUser"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            if (nickName != null) params.add(new BasicNameValuePair("name", nickName));
            if (avatar != null) params.add(new BasicNameValuePair("portraitUri", avatar));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            return responseJson.getInteger("code") == 200;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to update user info " + userId, e);
        }
    }

    @Override
    public boolean updateAvatar(long userId, String avatar) {
        return updateUserInfo(userId, null, avatar);
    }

    @Override
    protected boolean doJoinGroup(long userId, long groupId, String groupName) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.JoinGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            params.add(new BasicNameValuePair("groupName", groupName));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            return responseJson.getInteger("code") == 200;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to join group " + groupId + "/" + userId, e);
        }
    }

    @Override
    protected boolean doLeaveGroup(long userId, long groupId) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.LeaveGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            return responseJson.getInteger("code") == 200;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to leave group " + groupId + "/" + userId, e);
        }
    }

    @Override
    protected boolean doCreateGroup(long groupId, String groupName, Collection<Long> userIds) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.CreateGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (long userId : userIds) {
                params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            }
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            params.add(new BasicNameValuePair("groupName", groupName));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            return responseJson.getInteger("code") == 200;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to create group " + groupId, e);
        }
    }

    @Override
    protected boolean doUpdateGroupName(long groupId, String groupName) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.UpdateGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            params.add(new BasicNameValuePair("groupName", groupName));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            return responseJson.getInteger("code") == 200;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to update group " + groupId, e);
        }
    }

    @Override
    protected boolean doDismissGroup(long groupId) {
        try {
            HttpPost httpPost = RongCloudUtil.createHttpPost(Configuration.getString("Im.RongCloud.Service.DismissGroup"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", String.valueOf(Consts.SYSTEM_USERID)));
            params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            JSONObject responseJson = RongCloudUtil.executeRequest(httpPost);

            return responseJson.getInteger("code") == 200;
        } catch (Exception e) {
            throw new MomiaErrorException("fail to dismiss group " + groupId, e);
        }
    }
}
