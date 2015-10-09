package cn.momia.api.poi;

import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.Collection;
import java.util.List;

public class PoiServiceApi extends AbstractServiceApi {
    public static PoiServiceApi POI = new PoiServiceApi();

    public void init() {
        POI.setService(service);
    }

    public PlaceDto get(int placeId, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("type", type);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("poi", placeId), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PlaceDto.class);
    }

    public List<PlaceDto> list(Collection<Integer> placeIds, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("plids", placeIds)
                .add("type", type);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("poi/list"), builder.build());

        return CastUtil.toList((JSONArray) executeRequest(request), PlaceDto.class);
    }
}
