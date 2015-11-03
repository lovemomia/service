package cn.momia.service.course.base;

import java.util.List;

public class CourseBook {
    private List<String> imgs;
    private List<String> largeImgs;

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public List<String> getLargeImgs() {
        return largeImgs;
    }

    public void setLargeImgs(List<String> largeImgs) {
        this.largeImgs = largeImgs;
    }
}
