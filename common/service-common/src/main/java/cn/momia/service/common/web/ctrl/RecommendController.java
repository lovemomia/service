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
@RequestMapping("/recommend")
public class RecommendController extends BaseController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse add(@RequestParam String content,
                                 @RequestParam String time,
                                 @RequestParam String address,
                                 @RequestParam String contacts) {
        if (!commonServiceFacade.addRecommend(content, time, address, contacts)) return MomiaHttpResponse.FAILED("提交爆料信息失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
