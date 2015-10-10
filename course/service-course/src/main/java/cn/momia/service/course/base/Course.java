package cn.momia.service.course.base;

import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;
import java.util.List;

public class Course {
    public static final Course NOT_EXIST_COURSE = new Course();

    public static class Type {
        public static final int BASE = 1;
        public static final int FULL = 1;
    }

    private long id;
    private String title;
    private String cover;
    private int minAge;
    private int maxAge;
    private int joined;
    private BigDecimal price;
    private JSONArray recommend;
    private JSONArray flow;
    private JSONArray extra;

    private List<Long> subjects;

    private CourseBook book;
    private List<CourseSku> skus;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public JSONArray getRecommend() {
        return recommend;
    }

    public void setRecommend(JSONArray recommend) {
        this.recommend = recommend;
    }

    public JSONArray getFlow() {
        return flow;
    }

    public void setFlow(JSONArray flow) {
        this.flow = flow;
    }

    public JSONArray getExtra() {
        return extra;
    }

    public void setExtra(JSONArray extra) {
        this.extra = extra;
    }

    public List<Long> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Long> subjects) {
        this.subjects = subjects;
    }

    public CourseBook getBook() {
        return book;
    }

    public void setBook(CourseBook book) {
        this.book = book;
    }

    public List<CourseSku> getSkus() {
        return skus;
    }

    public void setSkus(List<CourseSku> skus) {
        this.skus = skus;
    }

    public boolean exists() {
        return id > 0;
    }
}
