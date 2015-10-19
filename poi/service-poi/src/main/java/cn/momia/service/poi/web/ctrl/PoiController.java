package cn.momia.service.poi.web.ctrl;

import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.poi.Place;
import cn.momia.service.poi.PlaceImage;
import cn.momia.service.poi.PlaceService;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/poi")
public class PoiController extends BaseController {
    @Autowired private PlaceService placeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable int id) {
        return MomiaHttpResponse.SUCCESS(buildPlaceDto(placeService.get(id)));
    }

    private PlaceDto buildPlaceDto(Place place) {
        PlaceDto placeDto = new PlaceDto();
        placeDto.setId(place.getId());
        placeDto.setCityId(place.getCityId());
        placeDto.setRegionId(place.getRegionId());
        placeDto.setName(place.getName());
        placeDto.setAddress(place.getAddress());
        placeDto.setDesc(place.getDesc());
        placeDto.setCover(place.getCover());
        placeDto.setLng(place.getLng());
        placeDto.setLat(place.getLat());

        List<String> imgs = new ArrayList<String>();
        for (PlaceImage img : place.getImgs()) {
            imgs.add(img.getUrl());
        }
        placeDto.setImgs(imgs);

        return placeDto;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String plids) {
        Set<Integer> ids = new HashSet<Integer>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(plids)) {
            ids.add(Integer.valueOf(id));
        }

        return MomiaHttpResponse.SUCCESS(buildPlaceDtos(placeService.list(ids)));
    }

    private List<PlaceDto> buildPlaceDtos(List<Place> places) {
        List<PlaceDto> placeDtos = new ArrayList<PlaceDto>();
        for (Place place : places) {
            placeDtos.add(buildPlaceDto(place));
        }

        return placeDtos;
    }
}
