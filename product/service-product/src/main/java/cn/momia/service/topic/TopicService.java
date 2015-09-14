package cn.momia.service.topic;

import java.util.List;
import java.util.Map;

public interface TopicService {
    Topic get(long id);
    List<TopicGroup> listTopicGroups(long id);
    Map<Long, List<Long>> queryProductIds(List<Long> groupIds);
}
