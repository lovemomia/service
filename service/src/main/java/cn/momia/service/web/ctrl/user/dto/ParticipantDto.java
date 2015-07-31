package cn.momia.service.web.ctrl.user.dto;

import cn.momia.common.service.util.AgeUtil;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.ctrl.dto.Dto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ParticipantDto implements Dto {
    private Participant participant;
    private boolean showId = false;

    public long getId() {
        return participant.getId();
    }

    public String getName() {
        return participant.getName();
    }

    public String getSex() {
        return participant.getSex();
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getBirthday() {
        return participant.getBirthday();
    }

    public String getType() { return AgeUtil.isAdult(participant.getBirthday()) ? "成人" : "儿童"; }

    public Integer getIdType() {
        return showId ? participant.getIdType() : null;
    }

    public String getIdNo() {
        return showId ? participant.getIdNo() : null;
    }

    public ParticipantDto(Participant participant) {
        this.participant = participant;
    }

    public ParticipantDto(Participant participant, boolean showId) {
        this(participant);
        this.showId = showId;
    }
}
