package cn.momia.api.course;

import cn.momia.api.course.dto.CourseDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSONObject;

public class CourseServiceApi extends AbstractServiceApi {
    public PagedList<CourseDto> listRecommend(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course/recommend"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), CourseDto.class);
    }
}
