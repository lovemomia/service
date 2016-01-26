package cn.momia.service.discuss.impl;

import cn.momia.api.discuss.dto.DiscussReply;
import cn.momia.api.discuss.dto.DiscussTopic;
import cn.momia.common.service.AbstractService;
import cn.momia.service.discuss.DiscussService;

import java.util.List;

public class DiscussServiceImpl extends AbstractService implements DiscussService {
    @Override
    public int queryTopicCount(int cityId) {
        return 0;
    }

    @Override
    public List<DiscussTopic> queryTopics(int cityId, int start, int count) {
        return null;
    }

    @Override
    public DiscussTopic getTopic(int topicId) {
        return null;
    }

    @Override
    public long queryRepliesCount(int topicId) {
        return 0;
    }

    @Override
    public List<DiscussReply> queryReplies(long userId, int topicId, int start, int count) {
        return null;
    }

    @Override
    public boolean reply(long userId, int topicId, String content) {
        return false;
    }

    @Override
    public boolean star(long userId, int replyId) {
        return false;
    }

    @Override
    public boolean unstar(long userId, int replyId) {
        return false;
    }
}
