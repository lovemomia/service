package cn.momia.api.base;

import cn.momia.api.base.dto.AgeRange;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class AgeRangeServiceApi extends ServiceApi {
    public List<AgeRange> listAll() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/agerange"));
        return executeReturnList(request, AgeRange.class);
    }
}
