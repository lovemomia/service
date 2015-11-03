package cn.momia.api.course.dto;

import java.util.List;

public class CourseBookDto {
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
