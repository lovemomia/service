package cn.momia.mapi.api.v1.dto;

import java.util.ArrayList;
import java.util.Date;

public class ParticipantDto implements Dto {
    public static class Participants extends ArrayList<ParticipantDto> implements Dto {}

    public long id;
    public String name;
    public String sex;
    public Date birthday;
}
