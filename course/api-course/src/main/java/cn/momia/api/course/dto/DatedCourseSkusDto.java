package cn.momia.api.course.dto;

import java.util.List;

public class DatedCourseSkusDto {
    private String date;
    private List<CourseSkuDto> skus;
    private Boolean more;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CourseSkuDto> getSkus() {
        return skus;
    }

    public void setSkus(List<CourseSkuDto> skus) {
        this.skus = skus;
    }

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }
}
