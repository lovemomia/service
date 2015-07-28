package cn.momia.service.common.feedback;

import cn.momia.common.service.Service;

public interface FeedbackService extends Service {
    long add(String content, String email);
}
