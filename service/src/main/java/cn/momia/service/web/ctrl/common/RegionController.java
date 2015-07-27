package cn.momia.service.web.ctrl.common;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/region")
public class RegionController extends AbstractController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllCities() {
        return new ResponseMessage(commonServiceFacade.getAllRegions());
    }
}
