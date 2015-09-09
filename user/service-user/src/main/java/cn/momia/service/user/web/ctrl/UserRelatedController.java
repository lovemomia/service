package cn.momia.service.user.web.ctrl;

import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.ctrl.dto.ListDto;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.leader.LeaderService;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.user.participant.ParticipantService;
import cn.momia.service.user.web.ctrl.dto.FullUserDto;
import cn.momia.service.user.web.ctrl.dto.ParticipantDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class UserRelatedController extends BaseController {
    protected static final Set<String> SEX = new HashSet<String>();
    static {
        SEX.add("男");
        SEX.add("女");
    }

    @Autowired protected UserService userService;
    @Autowired protected LeaderService leaderService;
    @Autowired protected ParticipantService participantService;

    protected FullUserDto buildUserResponse(User user) {
        return buildUserResponse(user, true);
    }

    protected FullUserDto buildUserResponse(User user, boolean showToken) {
        return new FullUserDto(user, participantService.list(user.getChildren()), leaderService.getByUser(user.getId()), showToken);
    }

    protected ListDto buildParticipantsResponse(List<Participant> participants) {
        ListDto participantsDto = new ListDto();
        for (Participant participant : participants) {
            participantsDto.add(new ParticipantDto(participant));
        }

        return participantsDto;
    }
}
