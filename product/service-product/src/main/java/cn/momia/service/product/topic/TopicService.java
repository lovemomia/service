package cn.momia.service.product.topic;

import cn.momia.service.base.Service;

import java.util.List;
import java.util.Map;

public interface TopicService extends Service {
    Topic get(long id);
    List<TopicGroup> getTopicGroups(long id);
    Map<Long,List<Long>> getProductIds(List<Long> groupIds);
}
