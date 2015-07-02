package cn.momia.service.base.feedback;

public interface FeedbackService {
    long add(String content, String email, long userId);
}
