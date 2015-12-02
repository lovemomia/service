package cn.momia.api.poi;

import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.List;

public class PoiServiceApi extends ServiceApi {
    public PlaceDto get(int placeId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/%d", placeId));
        return executeReturnObject(request, PlaceDto.class);
    }

    public List<PlaceDto> list(Collection<Integer> placeIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("plids", StringUtils.join(placeIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/list"), builder.build());

        return executeReturnList(request, PlaceDto.class);
    }
}
