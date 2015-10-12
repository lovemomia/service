package cn.momia.api.course;

import cn.momia.api.course.dto.SubjectDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpRequest;
import com.alibaba.fastjson.JSON;

public class SubjectServiceApi extends AbstractServiceApi {
    public SubjectDto get(long subjectId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject", subjectId));
        return JSON.toJavaObject((JSON) executeRequest(request), SubjectDto.class);
    }
}
