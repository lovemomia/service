package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class ParticipantDto implements Dto {
    private long id;
    private String name;
    private String sex;
    private Date birthday;
    private int idType;
    private String idNo;

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

    public String getIdNo() {
        return idNo;
    }

    public ParticipantDto(JSONObject participantJson) {
        this(participantJson, false);
    }

    public ParticipantDto(JSONObject participantJson, boolean showIdNo) {
        this.id = participantJson.getLong("id");
        this.name = participantJson.getString("name");
        this.sex = participantJson.getString("sex");
        this.birthday = participantJson.getDate("birthday");

        if (showIdNo) {
            this.idType = participantJson.getInteger("idType");
            this.idNo = participantJson.getString("idNo");
        }
    }
}
