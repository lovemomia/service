package cn.momia.service.feed.topic;

import java.util.List;

public interface FeedTopicService {
    FeedTopic get(long id);
    long queryCount(int type);
    List<FeedTopic> query(int type, int start, int count);
}
