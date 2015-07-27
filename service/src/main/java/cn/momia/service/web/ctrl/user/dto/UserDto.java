package cn.momia.service.web.ctrl.user.dto;

import cn.momia.common.secret.MobileEncryptor;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDto extends MiniUserDto {
    private List<ParticipantDto> children = new ArrayList<ParticipantDto>();

    public String getToken() {
        return user.getToken();
    }

    public String getMobile() {
        return MobileEncryptor.encrypt(user.getMobile());
    }

    public boolean isHasPassword() {
        return user.isHasPassword();
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
        return children;
    }

    public UserDto(User user, List<Participant> children) {
        super(user);
        for (Participant child : children) {
            this.children.add(new ParticipantDto(child));
        }
    }
}
