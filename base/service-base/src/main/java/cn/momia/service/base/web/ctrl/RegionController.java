package cn.momia.service.base.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.region.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/region")
public class RegionController extends BaseController {
    @Autowired private RegionService regionService;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse listAll() {
        return MomiaHttpResponse.SUCCESS(regionService.listAll());
    }
}
