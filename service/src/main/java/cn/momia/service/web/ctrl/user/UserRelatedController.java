package cn.momia.service.web.ctrl.user;

import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.dto.ListDto;
import cn.momia.service.web.ctrl.user.dto.ParticipantDto;
import cn.momia.service.web.ctrl.user.dto.FullUserDto;

import java.util.List;

public abstract class UserRelatedController extends AbstractController {
    protected FullUserDto buildUserResponse(User user) {
        return new FullUserDto(user,
                userServiceFacade.getChildren(user.getId(), user.getChildren()),
                userServiceFacade.getLeaderInfo(user.getId()));
    }

    protected ListDto buildParticipantsResponse(List<Participant> participants) {
        ListDto participantsDto = new ListDto();
        for (Participant participant : participants) {
            participantsDto.add(new ParticipantDto(participant));
        }

        return participantsDto;
    }
}
