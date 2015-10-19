package cn.momia.api.base;

import cn.momia.api.base.dto.CityDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class CityServiceApi extends ServiceApi {
    public List<CityDto> listAll() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("city"));
        return CastUtil.toList((JSON) executeRequest(request), CityDto.class);
    }
}
