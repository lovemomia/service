package cn.momia.service.operate.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.operate.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController extends BaseController {
    @Autowired private ConfigService configService;

    @RequestMapping(value = "/banner", method = RequestMethod.GET)
    public MomiaHttpResponse listBanners(@RequestParam(value = "city") int cityId) {
        return MomiaHttpResponse.SUCCESS(configService.listBanners(cityId));
    }

    @RequestMapping(value = "/icon", method = RequestMethod.GET)
    public MomiaHttpResponse listIcons(@RequestParam(value = "city") int cityId) {
        return MomiaHttpResponse.SUCCESS(configService.listIcons(cityId));
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public MomiaHttpResponse listEvents(@RequestParam(value = "city") int cityId) {
        return MomiaHttpResponse.SUCCESS(configService.listEvents(cityId));
    }
}
