package cn.momia.api.course.expert;

/**
 * Created by hoze on 16/6/15.
 */
public class ExpertHear {

    private int id;
    private long questionId;
    private long userId;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean exists() {
        return id > 0;
    }
}
