package cn.momia.service.event.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.event.banner.BannerService;
import cn.momia.service.event.base.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController extends BaseController {
    @Autowired private BannerService bannerService;
    @Autowired private EventService eventService;

    @RequestMapping(value = "/banner", method = RequestMethod.GET)
    public MomiaHttpResponse listBanners(@RequestParam(value = "city") int cityId, @RequestParam int count) {
        return MomiaHttpResponse.SUCCESS(bannerService.list(cityId, count));
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public MomiaHttpResponse listEvents(@RequestParam(value = "city") int cityId, @RequestParam int count) {
        return MomiaHttpResponse.SUCCESS(eventService.list(cityId, count));
    }
}
