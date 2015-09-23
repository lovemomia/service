package cn.momia.service.feed.topic;

import java.util.List;

public interface FeedTopicService {
    FeedTopic get(long id);
    long queryCount();
    List<FeedTopic> query(int start, int count);
}
