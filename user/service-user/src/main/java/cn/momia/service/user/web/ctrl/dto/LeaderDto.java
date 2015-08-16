package cn.momia.service.user.web.ctrl.dto;

import cn.momia.service.base.util.MobileUtil;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.base.web.ctrl.dto.ListDto;

import java.util.List;

public class LeaderDto {
    public static ListDto toDtos(List<Leader> leaders) {
        ListDto leadersDto = new ListDto();
        for (Leader leader : leaders) {
            leadersDto.add(new LeaderDto(leader));
        }

        return leadersDto;
    }

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
        return MobileUtil.encrypt(leader.getMobile());
    }

    public int getCityId() {
        return leader.getCityId();
    }

    public int getRegionId() {
        return leader.getRegionId();
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

    public int getStatus() {
        return leader.getStatus();
    }

    public LeaderDto(Leader leader) {
        this.leader = leader;
    }
}
