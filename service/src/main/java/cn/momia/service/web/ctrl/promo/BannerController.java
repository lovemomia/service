package cn.momia.service.web.ctrl.promo;

import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banner")
public class BannerController extends AbstractController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getBanners(@RequestParam(value = "city") int cityId, @RequestParam int count) {
        return ResponseMessage.SUCCESS(promoServiceFacade.getBanners(cityId, count));
    }
}
