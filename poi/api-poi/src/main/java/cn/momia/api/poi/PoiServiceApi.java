package cn.momia.api.poi;

import cn.momia.api.poi.dto.City;
import cn.momia.api.poi.dto.Institution;
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
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/city"));
        return executeReturnList(request, City.class);
    }

    public List<Region> listAllRegions() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/region"));
        return executeReturnList(request, Region.class);
    }

    public Place getPlace(int placeId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/place/%d", placeId));
        return executeReturnObject(request, Place.class);
    }

    public List<Place> listPlaces(Collection<Integer> placeIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("plids", StringUtils.join(placeIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/place/list"), builder.build());

        return executeReturnList(request, Place.class);
    }

    public Institution getInstitution(int InstitutionId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/inst/%d", InstitutionId));
        return executeReturnObject(request, Institution.class);
    }

    public List<Institution> listInstitutions(Collection<Integer> InstitutionIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("instids", StringUtils.join(InstitutionIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/inst/list"), builder.build());

        return executeReturnList(request, Institution.class);
    }
}
