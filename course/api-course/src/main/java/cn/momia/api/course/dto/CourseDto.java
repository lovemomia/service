package cn.momia.api.course.dto;

import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;
import java.util.List;

public class CourseDto {
    private long id;
    private String title;
    private String cover;
    private String age;
    private int joined;
    private BigDecimal price;
    private String scheduler;
    private String region;
    private String address;
    private String subject;

    private String goal;
    private String flow;
    private String tips;
    private String institution;
    private List<CoursePlaceDto> places;
    private List<String> imgs;
    private CourseBookDto book;

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public List<CoursePlaceDto> getPlaces() {
        return places;
    }

    public void setPlaces(List<CoursePlaceDto> places) {
        this.places = places;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public CourseBookDto getBook() {
        return book;
    }

    public void setBook(CourseBookDto book) {
        this.book = book;
    }
}
