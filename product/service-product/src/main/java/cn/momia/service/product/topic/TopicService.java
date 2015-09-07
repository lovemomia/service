package cn.momia.service.product.topic;

import java.util.List;
import java.util.Map;

public interface TopicService {
    Topic get(long id);
    List<TopicGroup> getTopicGroups(long id);
    Map<Long,List<Long>> getProductIds(List<Long> groupIds);
}
