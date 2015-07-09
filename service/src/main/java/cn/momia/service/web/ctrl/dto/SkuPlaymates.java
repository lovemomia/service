package cn.momia.service.web.ctrl.dto;

import java.io.Serializable;
import java.util.List;

public class SkuPlaymates implements Serializable {
    private String time;
    private String joined;
    private List<Playmate> playmates;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public List<Playmate> getPlaymates() {
        return playmates;
    }

    public void setPlaymates(List<Playmate> playmates) {
        this.playmates = playmates;
    }
}
