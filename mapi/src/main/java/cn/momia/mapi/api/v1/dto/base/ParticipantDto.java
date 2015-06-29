package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParticipantDto implements Dto {
    private long id;
    private String name;
    private String sex;
    private Date birthday;
    private int idType;
    private String idNo;
    private String type;

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
    public String getType() { return type; }

    public ParticipantDto(JSONObject participantJson) {
        this(participantJson, false);
    }

    public ParticipantDto(JSONObject participantJson, boolean showIdNo) {
        this.id = participantJson.getLong("id");
        this.name = participantJson.getString("name");
        this.sex = participantJson.getString("sex");
        this.birthday = participantJson.getDate("birthday");
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = formatter.format(currentTime);
        String myBirthday = formatter.format(this.birthday);

        long day=getDays(nowTime,myBirthday);
        long year=day/365;
        if(year>=15)
            this.type = "成人";
        else
           this.type = "儿童";


        if (showIdNo) {
            this.idType = participantJson.getInteger("idType");
            this.idNo = participantJson.getString("idNo");
        }
    }

    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return Math.abs(day);
    }
}
