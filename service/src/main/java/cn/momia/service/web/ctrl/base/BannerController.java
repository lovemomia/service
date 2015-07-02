package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.banner.Banner;
import cn.momia.service.base.banner.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {
    @Autowired private BannerService bannerService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getBanners(@RequestParam(value = "city") int cityId, @RequestParam int count) {
        if (cityId < 0 || count <= 0) return ResponseMessage.BAD_REQUEST;

        List<Banner> banners = bannerService.getBanners(cityId, count);

        return new ResponseMessage(banners);
    }
}
