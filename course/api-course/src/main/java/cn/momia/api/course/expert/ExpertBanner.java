package cn.momia.api.course.expert;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by hoze on 16/6/16.
 */
public class ExpertBanner {
    private int id;
    private String cover;
    private String action;
    private int weight;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
