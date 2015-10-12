package cn.momia.service.poi.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.poi.PlaceService;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/poi")
public class PoiController extends BaseController {
    @Autowired private PlaceService placeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable int id, @RequestParam int type) {
        return MomiaHttpResponse.SUCCESS(placeService.get(id, type));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String plids, @RequestParam int type) {
        Set<Integer> ids = new HashSet<Integer>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(plids)) {
            ids.add(Integer.valueOf(id));
        }

        return MomiaHttpResponse.SUCCESS(placeService.list(ids, type));
    }
}
