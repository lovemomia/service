package cn.momia.api.course.dto.material;

public class CourseMaterial {
    public static final CourseMaterial NOT_EXIST_COURSE_MATERIAL = new CourseMaterial();

    private int id;
    private String cover;
    private String title;
    private String subject;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean exists() {
        return id > 0;
    }

    public static class Base extends CourseMaterial {
        public Base(CourseMaterial material) {
            setId(material.getId());
            setCover(material.getCover());
            setTitle(material.getTitle());
            setSubject(material.getSubject());
        }
    }
}
