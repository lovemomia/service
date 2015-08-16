package cn.momia.service.common.web.ctrl;

import cn.momia.service.common.facade.CommonServiceFacade;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.base.web.response.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityController extends AbstractController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage list() {
        return ResponseMessage.SUCCESS(commonServiceFacade.getAllCities());
    }
}
