package cn.momia.service.common.recommend;

import cn.momia.service.base.Service;

public interface RecommendService extends Service {
    long add(String content, String time, String address, String contacts);
}
