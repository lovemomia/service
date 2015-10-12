package cn.momia.api.course;

import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class SubjectServiceApi extends AbstractServiceApi {
    public SubjectDto get(long subjectId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject", subjectId));
        return JSON.toJavaObject((JSON) executeRequest(request), SubjectDto.class);
    }

    public List<SubjectSkuDto> listSkus(long subjectId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject", subjectId, "sku"));
        return CastUtil.toList((JSONArray) executeRequest(request), SubjectSkuDto.class);
    }
}
