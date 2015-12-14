package cn.momia.api.course.dto;

import java.util.List;

public class DatedCourseSkus {
    private String date;
    private List<CourseSku> skus;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CourseSku> getSkus() {
        return skus;
    }

    public void setSkus(List<CourseSku> skus) {
        this.skus = skus;
    }
}
