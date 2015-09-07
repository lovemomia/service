package cn.momia.service.common.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.common.facade.CommonServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends BaseController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse add(@RequestParam String content, @RequestParam String email) {
        if (!commonServiceFacade.addFeedback(content, email)) return MomiaHttpResponse.FAILED("提交反馈意见失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
