package cn.momia.api.base;

import cn.momia.api.base.dto.SortTypeDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class SortTypeServiceApi extends ServiceApi {
    public List<SortTypeDto> listAll() {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("sorttype"));
        return CastUtil.toList((JSONArray) executeRequest(request), SortTypeDto.class);
    }
}
