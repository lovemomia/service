package cn.momia.api.course.expert;

/**
 * Created by hoze on 16/6/23.
 */
public class HearResult {

    private ExpertOrder order;
    private ExpertQuestion question;
    private int status;

    public ExpertOrder getOrder() {
        return order;
    }

    public void setOrder(ExpertOrder order) {
        this.order = order;
    }

    public ExpertQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ExpertQuestion question) {
        this.question = question;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
