package cn.momia.api.poi;

import cn.momia.api.poi.dto.Place;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.List;

public class PoiServiceApi extends HttpServiceApi {
    public Place get(int placeId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/%d", placeId));
        return executeReturnObject(request, Place.class);
    }

    public List<Place> list(Collection<Integer> placeIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("plids", StringUtils.join(placeIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/poi/list"), builder.build());

        return executeReturnList(request, Place.class);
    }
}
