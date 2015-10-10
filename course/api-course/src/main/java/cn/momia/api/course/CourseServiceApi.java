package cn.momia.api.course;

import cn.momia.common.api.AbstractServiceApi;

public class CourseServiceApi extends AbstractServiceApi {
    public static SubjectServiceApi SUBJECT = new SubjectServiceApi();

    public void init() {
        SUBJECT.setService(service);
    }

    public static class SubjectServiceApi extends CourseServiceApi {

    }
}
