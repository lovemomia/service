package cn.momia.api.base;

import cn.momia.api.base.dto.AgeRangeDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class AgeRangeServiceApi extends ServiceApi {
    public List<AgeRangeDto> listAll() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("agerange"));
        return CastUtil.toList((JSON) executeRequest(request), AgeRangeDto.class);
    }
}
