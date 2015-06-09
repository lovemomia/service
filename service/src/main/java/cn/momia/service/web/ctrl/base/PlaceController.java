package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.place.Place;
import cn.momia.service.base.place.PlaceService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PlaceController extends AbstractController {
    @Autowired
    private PlaceService placeService;

    @RequestMapping(value = "/cn/momia/service/base/user/{userId}/place", method = RequestMethod.POST)
    public ResponseMessage add(@PathVariable long userId, @RequestParam String placeParam) {
        JSONObject obj = JSON.parseObject(placeParam);

        Place place = new Place();
        place.setName(obj.getString("name"));
        place.setAddress(obj.getString("address"));
        place.setLng(obj.getFloat("lng"));
        place.setLat(obj.getFloat("lat"));

        List<String> imgs = new ArrayList<String>();
        JSONArray imgArray = obj.getJSONArray("imgs");
        for (int i = 0; i < imgArray.size(); i++) {
            imgs.add(imgArray.getString(i));
        }
        place.setImgs(imgs);

        long placeId = placeService.add(userId, place);

        if (placeId <= 0) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add place");
        return new ResponseMessage("add place successfully");
    }

    @RequestMapping(value = "/cn/momia/service/base/user/{userId}/place", method = RequestMethod.GET)
    public ResponseMessage getPlaces(@PathVariable long userId, @RequestParam int start, @RequestParam int count) {
        List<Place> places = placeService.getPlaces(userId, start, count);

        return new ResponseMessage(places);
    }

    @RequestMapping(value = "/place/{placeId}", method = RequestMethod.GET)
    public ResponseMessage get(@PathVariable long placeId) {
        Place place = placeService.get(placeId);

        if (!place.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "place: " + placeId + " not exists");
        return new ResponseMessage(place);
    }

    @RequestMapping(value = "/place/{placeId}", method = RequestMethod.DELETE)
    public ResponseMessage delete(@PathVariable long placeId) {
        boolean successful = placeService.delete(placeId);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to delete place: " + placeId);
        return new ResponseMessage("delete place successfully");
    }

    @RequestMapping(value = "/place/{placeId}/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@PathVariable long placeId, @RequestParam String name) {
        boolean successful = placeService.updateName(placeId, name);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update name of place: " + placeId);
        return new ResponseMessage("update name of place successfully");
    }

    @RequestMapping(value = "/place/{placeId}/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@PathVariable long placeId, @RequestParam String address) {
        boolean successful = placeService.updateAddress(placeId, address);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update address of place: " + placeId);
        return new ResponseMessage("update address of place successfully");
    }

    @RequestMapping(value = "/place/{placeId}/desc", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@PathVariable long placeId, @RequestParam String desc) {
        boolean successful = placeService.updateDesc(placeId, desc);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update desc of place: " + placeId);
        return new ResponseMessage("update desc of place successfully");
    }

    @RequestMapping(value = "/place/{placeId}/poi", method = RequestMethod.PUT)
    public ResponseMessage updatePoi(@PathVariable long placeId, @RequestParam float lng, @RequestParam float lat) {
        boolean successful = placeService.updatePoi(placeId, lng, lat);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update poi of place: " + placeId);
        return new ResponseMessage("update poi of place successfully");
    }

    @RequestMapping(value = "/place/{placeId}/image", method = RequestMethod.POST)
    public ResponseMessage addImage(@PathVariable long placeId, @RequestParam String url) {
        boolean successful = placeService.addImage(placeId, url);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add upload of place: " + placeId);
        return new ResponseMessage("add upload of place successfully");
    }

    @RequestMapping(value = "/place/{placeId}/image", method = RequestMethod.DELETE)
    public ResponseMessage deleteImage(@PathVariable long placeId, @RequestParam long imageId) {
        boolean successful = placeService.deleteImage(placeId, imageId);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to delete upload of place: " + placeId + ", upload: " + imageId);
        return new ResponseMessage("delete upload of place successfully");
    }
}
