package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.leader.Leader;
import com.alibaba.fastjson.JSONObject;

public class LeaderStatusDto {
    private Leader leader;
    private JSONObject desc;

    public int getStatus() {
        return leader.getStatus();
    }

    public String getMsg() {
        return leader.getMsg();
    }

    public JSONObject getDesc() {
        return desc;
    }

    public LeaderStatusDto(Leader leader, JSONObject desc) {
        this.leader = leader;
        this.desc = desc;
    }
}
