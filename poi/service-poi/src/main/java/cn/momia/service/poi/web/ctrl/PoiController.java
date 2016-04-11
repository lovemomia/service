package cn.momia.service.poi.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.service.CachedService;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.poi.place.Place;
import cn.momia.service.poi.place.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/poi")
public class PoiController extends BaseController {
    @Autowired @Qualifier("cityService") private CachedService cityService;
    @Autowired @Qualifier("regionService") private CachedService regionService;
    @Autowired private PlaceService placeService;

    @RequestMapping(value = "/city", method = RequestMethod.GET)
    public MomiaHttpResponse listAllCities() {
        return MomiaHttpResponse.SUCCESS(cityService.listAll());
    }

    @RequestMapping(value = "/region", method = RequestMethod.GET)
    public MomiaHttpResponse listAllRegions() {
        return MomiaHttpResponse.SUCCESS(regionService.listAll());
    }

    @RequestMapping(value = "/place/{plid}", method = RequestMethod.GET)
    public MomiaHttpResponse getPlace(@PathVariable(value = "plid") int placeId) {
        Place place = placeService.get(placeId);
        return place.exists() ? MomiaHttpResponse.SUCCESS(place) : MomiaHttpResponse.FAILED("地点信息不存在");
    }

    @RequestMapping(value = "/place/list", method = RequestMethod.GET)
    public MomiaHttpResponse listPlaces(@RequestParam String plids) {
        return MomiaHttpResponse.SUCCESS(placeService.list(MomiaUtil.splitDistinctIntegers(plids)));
    }
}
