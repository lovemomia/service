package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;

import java.util.ArrayList;
import java.util.List;

public class FullUserDto extends BaseUserDto {
    private List<ParticipantDto> children = new ArrayList<ParticipantDto>();

    public List<ParticipantDto> getChildren() {
        return children;
    }

    public FullUserDto(User user, List<Participant> children) {
        super(user);
        for (Participant child : children) {
            this.children.add(new ParticipantDto(child));
        }
    }
}
