package cn.momia.service.course.base;

import java.util.Date;

public class CourseSku {
    public static final CourseSku NOT_EXIST_COURSE_SKU = new CourseSku();

    private long id;
    private long courseId;
    private Date startTime;
    private Date endTime;
    private Date deadline;
    private int stock;
    private int unlockedStock;
    private int lockedStock;
    private int placeId;
    private CourseSkuPlace place;
    private int adult;
    private int child;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getUnlockedStock() {
        return unlockedStock;
    }

    public void setUnlockedStock(int unlockedStock) {
        this.unlockedStock = unlockedStock;
    }

    public int getLockedStock() {
        return lockedStock;
    }

    public void setLockedStock(int lockedStock) {
        this.lockedStock = lockedStock;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public CourseSkuPlace getPlace() {
        return place;
    }

    public void setPlace(CourseSkuPlace place) {
        this.place = place;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public boolean exists() {
        return id > 0;
    }

    public int getJoinCount() {
        return adult + child;
    }

    public boolean isAvaliable(Date now) {
        return deadline.after(now);
    }
}
