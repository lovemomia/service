package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.city.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("city")
public class CityController {
    @Autowired CityService cityService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllCities() {
        return new ResponseMessage(cityService.getAll());
    }
}
