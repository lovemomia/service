package cn.momia.api.poi;

import cn.momia.api.poi.dto.City;
import cn.momia.api.poi.dto.Place;
import cn.momia.api.poi.dto.Region;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.List;

public class PoiServiceApi extends HttpServiceApi {
    public List<City> listAllCities() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/city"));
        return executeReturnList(request, City.class);
    }

    public List<Region> listAllRegions() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/region"));
        return executeReturnList(request, Region.class);
    }

    public Place getPlace(int placeId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/%d", placeId));
        return executeReturnObject(request, Place.class);
    }

    public List<Place> listPlaces(Collection<Integer> placeIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("plids", StringUtils.join(placeIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/list"), builder.build());

        return executeReturnList(request, Place.class);
    }
}
