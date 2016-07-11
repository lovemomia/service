package cn.momia.api.course.expert;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hoze on 16/6/15.
 */
public class ExpertQuestion {

    private int id;
    private long expertId;
    private long courseId;
    private long userId;
    private String content;
    private String answer;
    private int status;
    private int count;
    private Date addTime;
    private int mins;

    private String userAvatar;
    private String userName;
    private boolean answerValue;
    private BigDecimal price;

    private Expert expert;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getExpertId() {
        return expertId;
    }

    public void setExpertId(long expertId) {
        this.expertId = expertId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public boolean isAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(boolean answerValue) {
        this.answerValue = answerValue;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        this.mins = mins;
    }

    public boolean exists() {
        return id > 0;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
