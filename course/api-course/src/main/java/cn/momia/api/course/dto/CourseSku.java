package cn.momia.api.course.dto;

import cn.momia.common.util.TimeUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CourseSku {
    public static final CourseSku NOT_EXIST_COURSE_SKU = new CourseSku();

    private static final DateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("MM月dd日");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private long id;
    private long courseId;
    @JSONField(serialize = false) private Date startTime;
    @JSONField(serialize = false) private Date endTime;
    @JSONField(serialize = false) private Date deadline;
    @JSONField(serialize = false) private int unlockedStock;
    @JSONField(serialize = false) private int placeId;
    @JSONField(serialize = false) private int adult;
    @JSONField(serialize = false) private int child;

    private CourseSkuPlace place;

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

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getUnlockedStock() {
        return unlockedStock;
    }

    public void setUnlockedStock(int unlockedStock) {
        this.unlockedStock = unlockedStock;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public CourseSkuPlace getPlace() {
        return place;
    }

    public void setPlace(CourseSkuPlace place) {
        this.place = place;
    }

    public boolean exists() {
        return id > 0;
    }

    @JSONField(serialize = false)
    public int getJoinCount() {
        return adult + child;
    }

    @JSONField(serialize = false)
    public boolean isAvaliable(Date now) {
        return deadline.after(now);
    }

    public String getTime() {
        if (TimeUtil.isSameDay(startTime, endTime)) {
            return TIME_FORMAT.format(startTime) + "-" + TIME_FORMAT.format(endTime);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            calendar.add(Calendar.DATE, 1);
            Date nextDay = calendar.getTime();
            if (TimeUtil.isSameDay(nextDay, endTime)) {
                return TIME_FORMAT.format(startTime) + "-次日" + TIME_FORMAT.format(endTime);
            } else {
                return TIME_FORMAT.format(startTime) + "-" + MONTH_DATE_FORMAT.format(endTime) + " " + TIME_FORMAT.format(endTime);
            }
        }

    }

    public int getStock() {
        return unlockedStock;
    }
}
