package cn.momia.api.course.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class CourseSkuDto {
    private long id;
    private Date time;
    private CoursePlaceDto place;
    private int stock;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CoursePlaceDto getPlace() {
        return place;
    }

    public void setPlace(CoursePlaceDto place) {
        this.place = place;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
