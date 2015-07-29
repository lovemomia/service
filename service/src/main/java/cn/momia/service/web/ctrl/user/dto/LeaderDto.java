package cn.momia.service.web.ctrl.user.dto;

import cn.momia.common.secret.MobileEncryptor;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.web.util.MetaUtil;

public class LeaderDto {
    private Leader leader;

    public long getId() {
        return leader.getId();
    }

    public long getUserId() {
        return leader.getUserId();
    }

    public String getName() {
        return leader.getName();
    }

    public String getMobile() {
        return MobileEncryptor.encrypt(leader.getMobile());
    }

    public int getCityId() {
        return leader.getCityId();
    }

    public String getCityName() {
        return MetaUtil.getCityName(leader.getCityId());
    }

    public int getRegionId() {
        return leader.getRegionId();
    }

    public String getRegionName() {
        return MetaUtil.getRegionName(leader.getRegionId());
    }

    public String getAddress() {
        return leader.getAddress();
    }

    public String getCareer() {
        return leader.getCareer();
    }

    public String getIntro() {
        return leader.getIntro();
    }

    public LeaderDto(Leader leader) {
        this.leader = leader;
    }
}
