package cn.momia.service.web.ctrl.common;

import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.common.city.City;
import cn.momia.service.common.region.Region;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/region")
public class RegionController extends AbstractController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllCities() {
        return ResponseMessage.SUCCESS(commonServiceFacade.getAllRegions());
    }

    @RequestMapping(value = "/district/tree", method = RequestMethod.GET)
    public ResponseMessage getDistrictTree() {
        List<City> cities = commonServiceFacade.getAllCities();
        List<Region> regions = commonServiceFacade.getAllRegions();
        Map<Integer, List<Region>> districtsOfCities = new HashMap<Integer, List<Region>>();
        for (Region region : regions) {
            if (region.getParentId() > 0) continue;

            int cityId = region.getCityId();
            List<Region> districts = districtsOfCities.get(cityId);
            if (districts == null) {
                districts = new ArrayList<Region>();
                districtsOfCities.put(cityId, districts);
            }
            districts.add(region);
        }

        JSONArray treeJson = new JSONArray();
        for (City city : cities) {
            JSONObject cityDistrictsJson = new JSONObject();
            cityDistrictsJson.put("city", city);
            cityDistrictsJson.put("districts", districtsOfCities.get(city.getId()));

            treeJson.add(cityDistrictsJson);
        }

        return ResponseMessage.SUCCESS(treeJson);
    }
}
