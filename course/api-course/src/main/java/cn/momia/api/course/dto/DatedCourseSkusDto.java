package cn.momia.api.course.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class DatedCourseSkusDto {
    private Date date;
    private List<CourseSkuDto> skus;
    private Boolean more;

    @JSONField(format = "yyyy-MM-dd")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
