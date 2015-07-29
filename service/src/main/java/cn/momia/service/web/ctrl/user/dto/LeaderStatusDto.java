package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.leader.Leader;
import com.alibaba.fastjson.JSONArray;

public class LeaderStatusDto {
    private Leader leader;
    private JSONArray desc;

    public int getStatus() {
        return leader.getStatus();
    }

    public String getMsg() {
        return leader.getMsg();
    }

    public JSONArray getDesc() {
        return desc;
    }

    public LeaderStatusDto(Leader leader, JSONArray desc) {
        this.leader = leader;
        this.desc = desc;
    }
}
