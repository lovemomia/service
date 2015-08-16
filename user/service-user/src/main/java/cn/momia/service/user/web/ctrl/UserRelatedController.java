package cn.momia.service.user.web.ctrl;

import cn.momia.service.user.base.User;
import cn.momia.service.user.facade.UserServiceFacade;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.user.web.ctrl.dto.FullUserDto;
import cn.momia.service.user.web.ctrl.dto.ParticipantDto;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.base.web.ctrl.dto.ListDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class UserRelatedController extends AbstractController {
    @Autowired protected UserServiceFacade userServiceFacade;

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
