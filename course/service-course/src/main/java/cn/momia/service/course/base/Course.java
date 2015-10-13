package cn.momia.service.course.base;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.util.TimeUtil;
import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Course {
    public static final Course NOT_EXIST_COURSE = new Course();

    public static class Type {
        public static final int BASE = 1;
        public static final int FULL = 2;
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M月d日");

    private long id;
    private String title;
    private String cover;
    private int minAge;
    private int maxAge;
    private int joined;
    private BigDecimal price;
    private String goal;
    private String flow;
    private JSONArray extra;

    private List<CourseSku> skus;

    private List<String> imgs;
    private CourseBook book;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public JSONArray getExtra() {
        return extra;
    }

    public void setExtra(JSONArray extra) {
        this.extra = extra;
    }

    public List<CourseSku> getSkus() {
        return skus;
    }

    public void setSkus(List<CourseSku> skus) {
        this.skus = skus;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public CourseBook getBook() {
        return book;
    }

    public void setBook(CourseBook book) {
        this.book = book;
    }

    public boolean exists() {
        return id > 0;
    }

    public String getAge() {
        if (minAge <= 0 && maxAge <= 0) throw new MomiaFailedException("invalid age of course: " + id);
        if (minAge <= 0) return maxAge + "岁";
        if (maxAge <= 0) return minAge + "岁";
        if (minAge == maxAge) return minAge + "岁";
        return minAge + "-" + maxAge + "岁";
    }

    public String getScheduler() {
        if (skus == null || skus.isEmpty()) return "";

        Date now = new Date();
        List<Date> times = new ArrayList<Date>();
        for (CourseSku sku : skus) {
            if (sku.isAvaliable(now)) {
                times.add(sku.getStartTime());
                times.add(sku.getEndTime());
            }
        }
        Collections.sort(times);

        return format(times);
    }

    private String format(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start);
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start);
            } else {
                return DATE_FORMAT.format(start) + "-" + DATE_FORMAT.format(end);
            }
        }
    }

    public List<Integer> getPlaceIds() {
        if (skus == null || skus.isEmpty()) return new ArrayList<Integer>();

        List<Integer> placeIds = new ArrayList<Integer>();
        for (CourseSku sku : skus) {
            placeIds.add(sku.getPlaceId());
        }

        return placeIds;
    }
}
