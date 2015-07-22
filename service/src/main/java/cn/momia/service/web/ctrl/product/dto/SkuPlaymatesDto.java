package cn.momia.service.web.ctrl.product.dto;

import java.io.Serializable;
import java.util.List;

public class SkuPlaymatesDto implements Serializable {
    private String time;
    private String joined;
    private List<PlaymateDto> playmates;

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

    public List<PlaymateDto> getPlaymates() {
        return playmates;
    }

    public void setPlaymates(List<PlaymateDto> playmates) {
        this.playmates = playmates;
    }
}
