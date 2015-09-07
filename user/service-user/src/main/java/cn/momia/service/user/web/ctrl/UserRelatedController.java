package cn.momia.service.user.web.ctrl;

import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.ctrl.dto.ListDto;
import cn.momia.service.user.base.User;
import cn.momia.service.user.facade.UserServiceFacade;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.user.web.ctrl.dto.FullUserDto;
import cn.momia.service.user.web.ctrl.dto.ParticipantDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class UserRelatedController extends BaseController {
    @Autowired protected UserServiceFacade userServiceFacade;

    protected FullUserDto buildUserResponse(User user) {
        return buildUserResponse(user, true);
    }

    protected FullUserDto buildUserResponse(User user, boolean showToken) {
        return new FullUserDto(user,
                userServiceFacade.getChildren(user.getId(), user.getChildren()),
                userServiceFacade.getLeaderInfo(user.getId()), showToken);
    }

    protected ListDto buildParticipantsResponse(List<Participant> participants) {
        ListDto participantsDto = new ListDto();
        for (Participant participant : participants) {
            participantsDto.add(new ParticipantDto(participant));
        }

        return participantsDto;
    }
}
