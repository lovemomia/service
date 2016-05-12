package cn.momia.api.course.activity;

import java.math.BigDecimal;
import java.util.Date;

public class Activity {
    public static final Activity NOT_EXIST_ACTIVITY = new Activity();

    private int id;
    private String cover;
    private String title;
    private String desc;
    private String message;
    private boolean needPay;
    private BigDecimal price;
    private Date startTime;
    private Date endTime;

    private boolean forNewUser;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNeedPay() {
        return needPay;
    }

    public void setNeedPay(boolean needPay) {
        this.needPay = needPay;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isForNewUser() {
        return forNewUser;
    }

    public void setForNewUser(boolean forNewUser) {
        this.forNewUser = forNewUser;
    }

    public boolean exists() {
        return id > 0;
    }
}
