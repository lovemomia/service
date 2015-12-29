package cn.momia.service.event.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController extends BaseController {
    @Autowired private EventService eventService;

    @RequestMapping(value = "/banner", method = RequestMethod.GET)
    public MomiaHttpResponse listBanners(@RequestParam(value = "city") int cityId) {
        return MomiaHttpResponse.SUCCESS(eventService.listBanners(cityId));
    }

    @RequestMapping(value = "/icon", method = RequestMethod.GET)
    public MomiaHttpResponse listIcons(@RequestParam(value = "city") int cityId) {
        return MomiaHttpResponse.SUCCESS(eventService.listIcons(cityId));
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public MomiaHttpResponse listEvents(@RequestParam(value = "city") int cityId) {
        return MomiaHttpResponse.SUCCESS(eventService.listEvents(cityId));
    }
}
