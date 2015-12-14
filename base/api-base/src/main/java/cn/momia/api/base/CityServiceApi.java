package cn.momia.api.base;

import cn.momia.api.base.dto.City;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class CityServiceApi extends ServiceApi {
    public List<City> listAll() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/city"));
        return executeReturnList(request, City.class);
    }
}
