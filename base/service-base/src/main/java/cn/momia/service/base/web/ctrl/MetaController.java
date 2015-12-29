package cn.momia.service.base.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.service.CachedService;
import cn.momia.common.webapp.ctrl.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetaController extends BaseController {
    @Autowired @Qualifier("cityService") private CachedService cityService;
    @Autowired @Qualifier("regionService") private CachedService regionService;
    @Autowired @Qualifier("ageRangeService") private CachedService ageRangeService;
    @Autowired @Qualifier("sortTypeService") private CachedService sortTypeService;

    @RequestMapping(value = "/city", method = RequestMethod.GET)
    public MomiaHttpResponse listAllCities() {
        return MomiaHttpResponse.SUCCESS(cityService.listAll());
    }

    @RequestMapping(value = "/region", method = RequestMethod.GET)
    public MomiaHttpResponse listAllRegions() {
        return MomiaHttpResponse.SUCCESS(regionService.listAll());
    }

    @RequestMapping(value = "/agerange", method = RequestMethod.GET)
    public MomiaHttpResponse listAllAgeRanges() {
        return MomiaHttpResponse.SUCCESS(ageRangeService.listAll());
    }

    @RequestMapping(value = "/sorttype", method = RequestMethod.GET)
    public MomiaHttpResponse listAllSortTypes() {
        return MomiaHttpResponse.SUCCESS(sortTypeService.listAll());
    }
}
