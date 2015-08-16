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
@RequestMapping("/recommend")
public class RecommendController extends AbstractController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage add(@RequestParam String content,
                               @RequestParam String time,
                               @RequestParam String address,
                               @RequestParam String contacts) {
        if (!commonServiceFacade.addRecommend(content, time, address, contacts)) return ResponseMessage.FAILED("提交爆料信息失败");
        return ResponseMessage.SUCCESS;
    }
}
