package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ParticipantDto implements Dto {
    public static class Participants extends ArrayList<ParticipantDto> implements Dto {}

    private long id;
    private String name;
    private String sex;
    private Date birthday;
    private int idType;
    private String idNo;

    public ParticipantDto(JSONObject participantJson) {
        this.id = participantJson.getLong("id");
        this.name = participantJson.getString("name");
        this.sex = participantJson.getString("sex");
        this.birthday = participantJson.getDate("birthday");
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }
}
