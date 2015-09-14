package cn.momia.service.user.web.ctrl.dto;

import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.user.participant.Participant;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParticipantDto implements Dto {
    public static List<ParticipantDto> toDtos(List<Participant> participants) {
        List<ParticipantDto> dtos = new ArrayList<ParticipantDto>();
        for (Participant participant : participants) {
            dtos.add(new ParticipantDto(participant));
        }

        return dtos;
    }

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

    public String getType() { return TimeUtil.isAdult(participant.getBirthday()) ? "成人" : "儿童"; }

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
