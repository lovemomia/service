package cn.momia.service.operate.feedback.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.operate.feedback.FeedbackService;

public class FeedbackServiceImpl extends AbstractService implements FeedbackService {
    @Override
    public boolean add(String content, String contact) {
        String sql = "INSERT INTO SG_Feedback(Content, Contact, AddTime) VALUES(?, ?, NOW())";
        return update(sql, new Object[] { content, contact });
    }
}
