package cn.momia.api.course.dto;

import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;
import java.util.List;

public class SubjectDto {
    private long id;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String age;
    private int joined;
    private JSONArray intro;
    private JSONArray notice;
    private List<String> imgs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public JSONArray getIntro() {
        return intro;
    }

    public void setIntro(JSONArray intro) {
        this.intro = intro;
    }

    public JSONArray getNotice() {
        return notice;
    }

    public void setNotice(JSONArray notice) {
        this.notice = notice;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public boolean exists() {
        return id > 0;
    }
}
