package cn.momia.service.base.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.recommend.RecommendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
public class RecommendController extends BaseController {
    @Autowired private RecommendService recommendService;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse add(@RequestParam String content,
                                 @RequestParam String time,
                                 @RequestParam String address,
                                 @RequestParam String contacts) {
        if (StringUtils.isBlank(content) ||
                StringUtils.isBlank(time) ||
                StringUtils.isBlank(address) ||
                StringUtils.isBlank(contacts)) return MomiaHttpResponse.BAD_REQUEST;
        if (content.length() > 600) return MomiaHttpResponse.FAILED("爆料字数超出限制");

        if (recommendService.add(content, time, address, contacts) <= 0) return MomiaHttpResponse.FAILED("提交爆料信息失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
