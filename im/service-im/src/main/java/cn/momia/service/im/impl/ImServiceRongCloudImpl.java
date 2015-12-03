package cn.momia.service.im.impl;

import java.util.Collection;

public class ImServiceRongCloudImpl extends AbstractImService {
    @Override
    protected boolean doCreateGroup(long groupId, String groupName, Collection<Long> userIds) {
        return false;
    }

    @Override
    protected boolean doUpdateGroupName(long groupId, String groupName) {
        return false;
    }

    @Override
    protected boolean doJoinGroup(long groupId, String groupName, long userId) {
        return false;
    }

    @Override
    protected boolean doLeaveGroup(long groupId, long userId) {
        return false;
    }

    @Override
    public String generateImToken(long userId, String nickName, String avatar) {
        return null;
    }

    @Override
    public void updateNickName(long userId, String nickName) {

    }

    @Override
    public void updateAvatar(long userId, String avatar) {

    }
}
