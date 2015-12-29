package cn.momia.api.base;

import cn.momia.api.base.dto.AgeRange;
import cn.momia.api.base.dto.City;
import cn.momia.api.base.dto.Region;
import cn.momia.api.base.dto.SortType;
import cn.momia.common.api.HttpServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class MetaServiceApi extends HttpServiceApi {
    public List<City> listAllCities() {
        return listAll("/city", City.class);
    }

    private <T> List<T> listAll(String uri, Class<T> clazz) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url(uri));
        return executeReturnList(request, clazz);
    }

    public List<Region> listAllRegions() {
        return listAll("/region", Region.class);
    }

    public List<AgeRange> listAllAgeRanges() {
        return listAll("/agerange", AgeRange.class);
    }

    public List<SortType> listAllSortTypes() {
        return listAll("/sorttype", SortType.class);
    }
}
