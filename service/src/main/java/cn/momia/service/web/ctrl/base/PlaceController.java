package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.place.PlaceService;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/place")
public class PlaceController extends AbstractController {
    @Autowired
    private PlaceService placeService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addPlace(@RequestParam String placeJson) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getPlace(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }
}
