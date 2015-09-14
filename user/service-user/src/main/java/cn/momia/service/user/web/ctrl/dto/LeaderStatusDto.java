package cn.momia.service.user.web.ctrl.dto;

import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.user.leader.Leader;

public class LeaderStatusDto implements Dto {
    private Leader leader;

    public int getStatus() {
        return leader.getStatus();
    }

    public String getMsg() {
        return getStatus() == Leader.Status.REJECTED ? leader.getMsg() : null;
    }

    public LeaderStatusDto(Leader leader) {
        this.leader = leader;
    }
}
