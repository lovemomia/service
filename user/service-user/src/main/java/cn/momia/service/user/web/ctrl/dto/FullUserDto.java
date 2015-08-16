package cn.momia.service.user.web.ctrl.dto;

import cn.momia.service.user.base.User;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.user.participant.Participant;

import java.util.ArrayList;
import java.util.List;

public class FullUserDto extends BaseUserDto {
    private List<ParticipantDto> children = new ArrayList<ParticipantDto>();
    private Leader leader;

    public List<ParticipantDto> getChildren() {
        return children;
    }

    public boolean isLeader() {
        return leader.getStatus() == Leader.Status.PASSED;
    }

    public FullUserDto(User user, List<Participant> children, Leader leader) {
        super(user);
        for (Participant child : children) {
            this.children.add(new ParticipantDto(child));
        }
        this.leader = leader;
    }
}
