package cn.momia.service.feed.topic;

import java.util.Collection;
import java.util.List;

public interface FeedTopicService {
    FeedTopic get(long id);
    List<FeedTopic> list(Collection<Long> ids);

    long queryCount(int type);
    List<FeedTopic> query(int type, int start, int count);
}
