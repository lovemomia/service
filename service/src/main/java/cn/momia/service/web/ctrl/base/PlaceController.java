package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.place.Place;
import cn.momia.service.base.place.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/place")
public class PlaceController {
    @Autowired
    private PlaceService placeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getPlace(@PathVariable long id) {
        Place place = placeService.get(id);

        if (!place.exists()) return new ResponseMessage(ErrorCode.FAILED, "place not exists");
        return new ResponseMessage(place);
    }
}
