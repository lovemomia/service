package cn.momia.service.deal.web.ctrl.dto;

import cn.momia.common.api.dto.Dto;

import java.util.List;

public class SkuPlaymatesDto implements Dto {
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
