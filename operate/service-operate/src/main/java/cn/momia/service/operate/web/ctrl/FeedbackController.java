package cn.momia.service.operate.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.operate.feedback.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends BaseController {
    @Autowired private FeedbackService feedbackService;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse add(@RequestParam String content, @RequestParam String contact) {
        return MomiaHttpResponse.SUCCESS(feedbackService.add(content, contact) > 0);
    }
}
