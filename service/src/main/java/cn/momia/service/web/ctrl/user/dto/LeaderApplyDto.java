package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.leader.Leader;
import cn.momia.service.web.ctrl.dto.ListDto;
import com.alibaba.fastjson.JSONObject;

public class LeaderApplyDto {
    private Leader leader;
    private ListDto skus;
    private JSONObject desc;

    public int getStatus() {
        return leader.getStatus();
    }

    public String getMsg() {
        return leader.getMsg();
    }

    public ListDto getSkus() {
        return skus;
    }

    public JSONObject getDesc() {
        return desc;
    }

    public LeaderApplyDto(Leader leader, ListDto skus) {
        this.leader = leader;
        this.skus = skus;
    }

    public LeaderApplyDto(Leader leader, JSONObject desc) {
        this.leader = leader;
        this.desc = desc;
    }
}
