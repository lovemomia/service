package cn.momia.mapi.api.v1.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ParticipantDto implements Dto {
    public static class Participants extends ArrayList<ParticipantDto> implements Dto {}

    private long id;
    private String name;
    private String sex;
    private Date birthday;

    public ParticipantDto(JSONObject participantJson) {
        this.id = participantJson.getLong("id");
        this.name = participantJson.getString("name");
        this.sex = participantJson.getString("sex");
        this.birthday = participantJson.getDate("birthday");
    }
}
