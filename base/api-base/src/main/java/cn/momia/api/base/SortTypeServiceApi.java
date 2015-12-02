package cn.momia.api.base;

import cn.momia.api.base.dto.SortType;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class SortTypeServiceApi extends ServiceApi {
    public List<SortType> listAll() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/sorttype"));
        return executeReturnList(request, SortType.class);
    }
}
