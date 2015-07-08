package cn.momia.mapi.api.v1.dto.base;

import cn.momia.common.misc.AgeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ParticipantDto implements Dto {
    private long id;
    private String name;
    private String sex;
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private String type;
    private Integer idType;
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

    public String getType() { return type; }

    public Integer getIdType() {
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
        this.type = AgeUtil.isAdult(this.birthday) ? "成人" : "儿童";

        if (showIdNo) {
            this.idType = participantJson.getInteger("idType");
            this.idNo = participantJson.getString("idNo");
        }
    }
}
