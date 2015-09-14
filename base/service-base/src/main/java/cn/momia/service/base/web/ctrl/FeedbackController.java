package cn.momia.service.base.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.feedback.FeedbackService;
import org.apache.commons.lang3.StringUtils;
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
    public MomiaHttpResponse add(@RequestParam String content, @RequestParam String email) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(email)) return MomiaHttpResponse.BAD_REQUEST;
        if (content.length() > 480) return MomiaHttpResponse.FAILED("反馈意见字数超出限制");

        if (feedbackService.add(content, email) <= 0) return MomiaHttpResponse.FAILED("提交反馈意见失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
