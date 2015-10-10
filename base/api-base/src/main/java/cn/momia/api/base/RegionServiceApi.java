package cn.momia.api.base;

import cn.momia.api.base.dto.RegionDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class RegionServiceApi extends AbstractServiceApi {
    public List<RegionDto> listAll() {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("region"));
        return CastUtil.toList((JSONArray) executeRequest(request), RegionDto.class);
    }
}
