package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.feedback.FeedbackService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFeedback(@RequestParam String content, @RequestParam String email, @RequestParam(required = false) String utoken) {
        User user = userService.getByToken(utoken);
        long userId = user.exists() ? user.getId() : 0;

        long feedbackId = feedbackService.add(content, email, userId);
        if (feedbackId <= 0) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add feedback");

        return new ResponseMessage("add feedback successfully");
    }
}
