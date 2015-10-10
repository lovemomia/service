package cn.momia.api.user;

import cn.momia.api.user.dto.ContactsDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.api.user.dto.LeaderDto;
import cn.momia.api.user.dto.LeaderStatusDto;
import cn.momia.api.user.dto.ParticipantDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserServiceApi extends AbstractServiceApi {
    public static BaseUserServiceApi USER = new BaseUserServiceApi();
    public static ParticipantServiceApi PARTICIPANT = new ParticipantServiceApi();
    public static LeaderServiceApi LEADER = new LeaderServiceApi();

    public void init() {
        USER.setService(service);
        PARTICIPANT.setService(service);
        LEADER.setService(service);
    }

    public static class BaseUserServiceApi extends UserServiceApi {
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        public UserDto register(String nickName, String mobile, String password, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("nickname", nickName)
                    .add("mobile", mobile)
                    .add("password", password)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/register"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto login(String mobile, String password) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("password", password);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/login"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto loginByCode(String mobile, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/login/code"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updatePassword(String mobile, String password, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("password", password)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("auth/password"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto get(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto get(long userId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user", userId));
            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public boolean exists(long userId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user", userId, "exists"));
            return (Boolean) executeRequest(request);
        }

        public UserDto updateNickName(String utoken, String nickName) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("nickname", nickName);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/nickname"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateAvatar(String utoken, String avatar) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("avatar", avatar);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/avatar"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateName(String utoken, String name) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("name", name);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/name"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateSex(String utoken, String sex) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("sex", sex);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/sex"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateBirthday(String utoken, Date birthday) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("birthday", DATE_FORMAT.format(birthday));
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/birthday"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateCity(String utoken, int cityId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("city", cityId);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/city"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateRegion(String utoken, int regionId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("region", regionId);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/region"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateAddress(String utoken, String address) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("address", address);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/address"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto addChildren(List<ParticipantDto> children) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("user/child"), JSON.toJSONString(children));
            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public ParticipantDto getChild(String utoken, long childId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child", childId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), ParticipantDto.class);
        }

        public UserDto updateChildName(String utoken, long childId, String name) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("name", name);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child", childId, "name"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateChildSex(String utoken, long childId, String sex) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("sex", sex);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child", childId, "sex"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto updateChildBirthday(String utoken, long childId, Date birthday) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("birthday", DATE_FORMAT.format(birthday));
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child", childId, "birthday"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public UserDto deleteChild(String utoken, long childId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("user/child", childId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), UserDto.class);
        }

        public List<ParticipantDto> listChildren(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), ParticipantDto.class);
        }

        public ContactsDto getContacts(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/contacts"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), ContactsDto.class);
        }

        public void setContacts(long userId, String mobile, String name) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("name", name);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("user", userId, "contacts"), builder.build());
            executeRequest(request);
        }

        public List<UserDto> list(Collection<Long> userIds, int type) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uids", StringUtils.join(userIds, ","))
                    .add("type", type);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/list"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), UserDto.class);
        }

        public boolean isPayed(long userId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user", userId, "payed"));
            return (Boolean) executeRequest(request);
        }

        public boolean setPayed(long userId) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("user", userId, "payed"));
            return (Boolean) executeRequest(request);
        }

        public String getInviteCode(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/code"), builder.build());

            return (String) executeRequest(request);
        }

        public long getIdByInviteCode(String inviteCode) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("code", inviteCode);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/code/id"), builder.build());

            return ((Number) executeRequest(request)).longValue();
        }
    }

    public static class ParticipantServiceApi extends UserServiceApi {
        public void add(ParticipantDto participantDto) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("participant"), JSON.toJSONString(participantDto));
            executeRequest(request);
        }

        public ParticipantDto get(String utoken, long participantId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant", participantId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), ParticipantDto.class);
        }

        public void update(ParticipantDto participantDto) {
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("participant"), JSON.toJSONString(participantDto));
            executeRequest(request);
        }

        public void delete(String utoken, long participantId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("participant", participantId), builder.build());
            executeRequest(request);
        }

        public List<ParticipantDto> list(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), ParticipantDto.class);
        }

        public List<ParticipantDto> list(Set<Long> participantIds) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("paids", StringUtils.join(participantIds, ","));
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant/list"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), ParticipantDto.class);
        }

        public void checkParticipants(long userId, Collection<Long> participantIds) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("paids", StringUtils.join(participantIds, ","));
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant/check"), builder.build());
            executeRequest(request);
        }
    }

    public static class LeaderServiceApi extends UserServiceApi {
        public LeaderStatusDto getStatus(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/status"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), LeaderStatusDto.class);
        }

        public void add(LeaderDto leaderDto) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("leader"), JSON.toJSONString(leaderDto));
            executeRequest(request);
        }

        public LeaderDto get(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), LeaderDto.class);
        }

        public List<LeaderDto> list(Collection<Long> userIds) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uids", StringUtils.join(userIds, ","));
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/list"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), LeaderDto.class);
        }
    }
}
