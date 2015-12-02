package cn.momia.api.base;

import cn.momia.api.base.dto.RegionDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class RegionServiceApi extends ServiceApi {
    public List<RegionDto> listAll() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/region"));
        return executeReturnList(request, RegionDto.class);
    }
}
