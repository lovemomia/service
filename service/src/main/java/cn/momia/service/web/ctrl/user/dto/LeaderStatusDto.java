package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.leader.Leader;

public class LeaderStatusDto {
    private Leader leader;

    public int getStatus() {
        return leader.getStatus();
    }

    public String getMsg() {
        return leader.getMsg();
    }

    public LeaderStatusDto(Leader leader) {
        this.leader = leader;
    }
}
