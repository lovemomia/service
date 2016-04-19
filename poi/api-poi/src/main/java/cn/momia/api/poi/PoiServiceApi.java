package cn.momia.api.poi;

import cn.momia.api.poi.dto.City;
import cn.momia.api.poi.dto.Place;
import cn.momia.api.poi.dto.Region;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class PoiServiceApi extends HttpServiceApi {
    public List<City> listAllCities() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/poi/city")), City.class);
    }

    public List<Region> listAllRegions() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/poi/region")), Region.class);
    }

    public Place getPlace(int placeId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/poi/place/%d", placeId)), Place.class);
    }

    public List<Place> listPlaces(Collection<Integer> placeIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("plids", StringUtils.join(placeIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/poi/place/list"), builder.build()), Place.class);
    }
}
