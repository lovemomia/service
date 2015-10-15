package cn.momia.service.course.base;

import cn.momia.common.service.Entity;

public class CourseBookImage implements Entity {
    public static final CourseBookImage NOT_EXIST_COURSE_BOOK_IMAGE = new CourseBookImage();

    private long id;
    private long courseId;
    private String img;
    private int order;

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean exists() {
        return id > 0;
    }
}
