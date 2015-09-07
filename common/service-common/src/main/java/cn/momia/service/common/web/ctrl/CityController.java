package cn.momia.service.common.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.common.facade.CommonServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityController extends BaseController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse list() {
        return MomiaHttpResponse.SUCCESS(commonServiceFacade.getAllCities());
    }
}
