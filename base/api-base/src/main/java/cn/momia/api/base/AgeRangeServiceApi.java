package cn.momia.api.base;

import cn.momia.api.base.dto.AgeRangeDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class AgeRangeServiceApi extends ServiceApi {
    public List<AgeRangeDto> listAll() {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("agerange"));
        return CastUtil.toList((JSONArray) executeRequest(request), AgeRangeDto.class);
    }
}
