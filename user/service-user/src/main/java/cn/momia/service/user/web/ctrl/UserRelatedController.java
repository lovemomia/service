package cn.momia.service.user.web.ctrl;

import cn.momia.api.user.dto.ContactsDto;
import cn.momia.api.user.dto.LeaderDto;
import cn.momia.api.user.dto.LeaderStatusDto;
import cn.momia.api.user.dto.ParticipantDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.user.leader.LeaderService;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.user.participant.ParticipantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class UserRelatedController extends BaseController {
    @Autowired protected UserService userService;
    @Autowired protected ParticipantService participantService;
    @Autowired protected LeaderService leaderService;

    protected UserDto buildUserDto(User user, int type) {
        return buildUserDto(user, type, true);
    }

    protected UserDto buildUserDto(User user, int type, boolean showToken) {
        List<Participant> children = (type == User.Type.FULL ? participantService.list(user.getChildren()) : null);
        Leader leader = (type == User.Type.FULL ? leaderService.getByUser(user.getId()) : null);

        return buildUserDto(user, type, showToken, children, leader);
    }

    protected UserDto buildUserDto(User user, int type, boolean showToken, List<Participant> children, Leader leader) {
        UserDto userDto = new UserDto();
        switch (type) {
            case User.Type.FULL:
                userDto.setChildren(buildParticipantDtos(children));
                userDto.setLeader(leader.getStatus() == Leader.Status.PASSED);
            case User.Type.BASE:
                if (showToken) userDto.setToken(user.getToken());
                userDto.setMobile(user.getMobile());
                userDto.setName(user.getName());
                userDto.setSex(user.getSex());
                userDto.setBirthday(user.getBirthday());
                userDto.setCityId(user.getCityId());
                userDto.setRegionId(user.getRegionId());
                userDto.setAddress(user.getAddress());
                userDto.setInviteCode(user.getInviteCode());
            case User.Type.MINI:
                userDto.setId(user.getId());
                userDto.setNickName(user.getNickName());
                userDto.setAvatar(user.getAvatar());
            default: break;
        }

        return userDto;
    }

    protected ContactsDto buildContactsDto(User user) {
        ContactsDto contactsDto = new ContactsDto();
        contactsDto.setName(user.getName());
        contactsDto.setMobile(user.getMobile());

        if (StringUtils.isBlank(contactsDto.getName())) contactsDto.setName(user.getName());

        return contactsDto;
    }

    protected ParticipantDto buildParticipantDto(Participant participant) {
        return buildParticipantDto(participant, false);
    }

    protected ParticipantDto buildParticipantDto(Participant participant, boolean showId) {
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setId(participant.getId());
        participantDto.setUserId(participant.getUserId());
        participantDto.setName(participant.getName());
        participantDto.setSex(participant.getSex());
        participantDto.setBirthday(participant.getBirthday());
        participantDto.setType(TimeUtil.isAdult(participant.getBirthday()) ? "成人" : "儿童");

        if (showId) {
            participantDto.setIdType(participant.getIdType());
            participantDto.setIdNo(participant.getIdNo());
        }

        return participantDto;
    }

    protected List<ParticipantDto> buildParticipantDtos(List<Participant> participants) {
        List<ParticipantDto> participantDtos = new ArrayList<ParticipantDto>();
        for (Participant participant : participants) {
            participantDtos.add(buildParticipantDto(participant));
        }

        return participantDtos;
    }

    protected LeaderStatusDto buildLeaderStatusDto(Leader leader) {
        LeaderStatusDto leaderStatusDto = new LeaderStatusDto();
        leaderStatusDto.setStatus(leader.getStatus());
        leaderStatusDto.setMsg(leader.getMsg());

        return leaderStatusDto;
    }

    protected LeaderDto buildLeaderDto(Leader leader) {
        LeaderDto leaderDto = new LeaderDto();
        leaderDto.setId(leader.getId());
        leaderDto.setUserId(leader.getUserId());
        leaderDto.setName(leader.getName());
        leaderDto.setCityId(leader.getCityId());
        leaderDto.setRegionId(leader.getRegionId());
        leaderDto.setAddress(leader.getAddress());
        leaderDto.setCareer(leader.getCareer());
        leaderDto.setIntro(leader.getIntro());
        leaderDto.setStatus(leader.getStatus());

        return leaderDto;
    }

    protected List<LeaderDto> buildLeaderDtos(List<Leader> leaders) {
        List<LeaderDto> leaderDtos = new ArrayList<LeaderDto>();
        for (Leader leader : leaders) {
            leaderDtos.add(buildLeaderDto(leader));
        }

        return leaderDtos;
    }
}
