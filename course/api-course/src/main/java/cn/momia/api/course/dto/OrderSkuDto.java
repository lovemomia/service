package cn.momia.api.course.dto;

public class OrderSkuDto {
    private long packageId;
    private long subjectId;
    private String title;
    private String cover;
    private int bookableCourseCount;
    private String expireTime;

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
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

    public int getBookableCourseCount() {
        return bookableCourseCount;
    }

    public void setBookableCourseCount(int bookableCourseCount) {
        this.bookableCourseCount = bookableCourseCount;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
