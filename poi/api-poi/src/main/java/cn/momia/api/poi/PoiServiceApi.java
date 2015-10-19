package cn.momia.api.poi;

import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.List;

public class PoiServiceApi extends ServiceApi {
    public PlaceDto get(int placeId) {
        return get(placeId, PlaceDto.Type.BASE);
    }

    public PlaceDto get(int placeId, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("type", type);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("poi", placeId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), PlaceDto.class);
    }

    public List<PlaceDto> list(Collection<Integer> placeIds) {
        return list(placeIds, PlaceDto.Type.BASE);
    }

    public List<PlaceDto> list(Collection<Integer> placeIds, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("plids", StringUtils.join(placeIds, ","))
                .add("type", type);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("poi/list"), builder.build());

        return CastUtil.toList((JSON) executeRequest(request), PlaceDto.class);
    }
}
