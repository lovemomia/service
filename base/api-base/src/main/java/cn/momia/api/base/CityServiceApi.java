package cn.momia.api.base;

import cn.momia.api.base.dto.CityDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class CityServiceApi extends ServiceApi {
    public List<CityDto> listAll() {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("city"));
        return CastUtil.toList((JSONArray) executeRequest(request), CityDto.class);
    }
}
