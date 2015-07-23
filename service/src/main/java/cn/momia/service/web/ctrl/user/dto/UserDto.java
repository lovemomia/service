package cn.momia.service.web.ctrl.user.dto;

import cn.momia.common.secret.MobileEncryptor;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.ctrl.dto.Dto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDto implements Dto {
    private User user;
    private List<ParticipantDto> participants = new ArrayList<ParticipantDto>();

    public long getId() {
        return user.getId();
    }

    public String getToken() {
        return user.getToken();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public String getMobile() {
        return MobileEncryptor.encrypt(user.getMobile());
    }

    public boolean isHasPassword() {
        return user.isHasPassword();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getName() {
        return user.getName();
    }

    public String getSex() {
        return user.getSex();
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getBirthday() {
        return user.getBirthday();
    }

    public int getCity() {
        return user.getCity();
    }

    public String getAddress() {
        return user.getAddress();
    }

    public List<ParticipantDto> getChildren() {
        return participants;
    }

    public UserDto(User user, List<Participant> participants) {
        this.user = user;
        for (Participant participant : participants) {
            this.participants.add(new ParticipantDto(participant));
        }
    }
}
