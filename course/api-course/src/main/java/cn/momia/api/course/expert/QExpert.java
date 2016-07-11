package cn.momia.api.course.expert;

/**
 * Created by hoze on 16/6/23.
 */
public class QExpert {
    private ExpertQuestion question;
    private Expert expert;
    private ExpertCourse course;

    public ExpertQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ExpertQuestion question) {
        this.question = question;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public ExpertCourse getCourse() {
        return course;
    }

    public void setCourse(ExpertCourse course) {
        this.course = course;
    }
}
