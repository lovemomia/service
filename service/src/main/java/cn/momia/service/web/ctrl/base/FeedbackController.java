package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.feedback.FeedbackService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import org.apache.commons.lang3.StringUtils;
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
    public ResponseMessage addFeedback(@RequestParam(required = false) String utoken,
                                       @RequestParam String content,
                                       @RequestParam String email) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(email)) return ResponseMessage.BAD_REQUEST;

        long userId = 0;
        if (!StringUtils.isBlank(utoken)) {
            User user = userService.getByToken(utoken);
            if (user.exists()) userId = user.getId();
        }

        long feedbackId = feedbackService.add(content, email, userId);
        if (feedbackId <= 0) return new ResponseMessage(ErrorCode.FAILED, "fail to add feedback");

        return ResponseMessage.SUCCESS;
    }
}
