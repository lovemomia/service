package cn.momia.service.base.feedback;

public interface FeedbackService {
    long addFeedback(String content, String email, long userId);
}
