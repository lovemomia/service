package cn.momia.service.common.web.ctrl;

import cn.momia.service.common.facade.CommonServiceFacade;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.base.web.response.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends AbstractController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage add(@RequestParam String content, @RequestParam String email) {
        if (!commonServiceFacade.addFeedback(content, email)) return ResponseMessage.FAILED("提交反馈意见失败");
        return ResponseMessage.SUCCESS;
    }
}
