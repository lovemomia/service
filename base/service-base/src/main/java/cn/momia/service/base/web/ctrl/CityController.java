package cn.momia.service.base.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.city.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityController extends BaseController {
    @Autowired private CityService cityService;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse listAll() {
        return MomiaHttpResponse.SUCCESS(cityService.listAll());
    }
}
