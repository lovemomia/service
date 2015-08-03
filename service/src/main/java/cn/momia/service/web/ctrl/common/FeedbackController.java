package cn.momia.service.web.ctrl.common;

import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends AbstractController {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFeedback(@RequestParam String content, @RequestParam String email) {
        long feedbackId = commonServiceFacade.addFeedback(content, email);
        if (feedbackId <= 0) return ResponseMessage.FAILED("提交反馈意见失败");

        return ResponseMessage.SUCCESS;
    }
}
