package cn.momia.api.course.dto;

import java.math.BigDecimal;
import java.util.List;

public class CourseDto {
    public static class Type {
        public static final int BASE = 1;
        public static final int FULL = 2;
    }

    private long id;
    private long subjectId;
    private String title;
    private String cover;
    private String age;
    private boolean insurance;
    private int joined;
    private BigDecimal price;
    private String scheduler;
    private String region;

    private String goal;
    private String flow;
    private String tips;
    private String notice;
    private String institution;
    private List<String> imgs;
    private CourseBookDto book;

    private CoursePlaceDto place;

    private String subject;
    private Boolean favored;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
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

    public boolean isInsurance() {
        return insurance;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
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

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
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

    public CoursePlaceDto getPlace() {
        return place;
    }

    public void setPlace(CoursePlaceDto place) {
        this.place = place;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean isFavored() {
        return favored;
    }

    public void setFavored(Boolean favored) {
        this.favored = favored;
    }
}
