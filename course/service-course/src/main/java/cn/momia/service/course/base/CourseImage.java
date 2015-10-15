package cn.momia.service.course.base;

import cn.momia.common.service.Entity;

public class CourseImage implements Entity {
    public static final CourseImage NOT_EXIST_COURSE_IMAGE = new CourseImage();

    private long id;
    private long courseId;
    private String url;
    private int width;
    private int height;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean exists() {
        return id > 0;
    }
}
