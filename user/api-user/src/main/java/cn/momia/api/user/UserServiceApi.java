package cn.momia.api.user;

import cn.momia.api.base.ServiceApi;
import cn.momia.api.base.http.MomiaHttpParamBuilder;
import cn.momia.api.base.http.MomiaHttpRequest;
import cn.momia.api.user.leader.Leader;
import cn.momia.api.user.leader.LeaderStatus;
import cn.momia.api.user.participant.Participant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserServiceApi extends ServiceApi {
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

        public User register(String nickName, String mobile, String password, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("nickname", nickName)
                    .add("mobile", mobile)
                    .add("password", password)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/register"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User login(String mobile, String password) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("password", password);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/login"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User loginByCode(String mobile, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/login/code"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updatePassword(String mobile, String password, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("password", password)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("auth/password"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User get(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User get(long userId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user", userId));

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateNickName(String utoken, String nickName) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("nickname", nickName);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/nickname"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateAvatar(String utoken, String avatar) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("avatar", avatar);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/avatar"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateName(String utoken, String name) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("name", name);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/name"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateSex(String utoken, String sex) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("sex", sex);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/sex"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateBirthday(String utoken, Date birthday) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("birthday", DATE_FORMAT.format(birthday));
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/birthday"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateCity(String utoken, int cityId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("city", cityId);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/city"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateRegion(String utoken, int regionId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("region", regionId);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/region"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateAddress(String utoken, String address) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("address", address);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/address"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User addChildren(List<Participant> children) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("user/child"), JSON.toJSONString(children));

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public Participant getChild(String utoken, long childId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child", childId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), Participant.class);
        }

        public User updateChildName(String utoken, long childId, String name) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("name", name);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child", childId, "name"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateChildSex(String utoken, long childId, String sex) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("sex", sex);
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child", childId, "sex"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User updateChildBirthday(String utoken, long childId, Date birthday) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("birthday", DATE_FORMAT.format(birthday));
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child", childId, "birthday"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public User deleteChild(String utoken, long childId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("user/child", childId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), User.class);
        }

        public List<Participant> listChildren(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child"), builder.build());
            JSONArray childrenJson = (JSONArray) executeRequest(request);

            List<Participant> children = new ArrayList<Participant>();
            for (int i = 0; i < childrenJson.size(); i++) {
                JSONObject childJson = childrenJson.getJSONObject(i);
                children.add(JSON.toJavaObject(childJson, Participant.class));
            }

            return children;
        }

        public Contacts getContacts(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET("contacts", true, url("user/contacts"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), Contacts.class);
        }

        public List<User> list(Collection<Long> userIds, int type) {
            if (userIds == null || userIds.isEmpty()) return new ArrayList<User>();

            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uids", StringUtils.join(userIds, ","))
                    .add("type", type);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/list"), builder.build());
            JSONArray usersJson = (JSONArray) executeRequest(request);

            List<User> users = new ArrayList<User>();
            for (int i = 0; i < usersJson.size(); i++) {
                JSONObject userJson = usersJson.getJSONObject(i);
                users.add(JSON.toJavaObject(userJson, User.class));
            }

            return users;
        }
    }

    public static class ParticipantServiceApi extends UserServiceApi {
        public void add(Participant participant) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("participant"), JSON.toJSONString(participant));
            executeRequest(request);
        }

        public Participant get(String utoken, long participantId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant", participantId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), Participant.class);
        }

        public void update(Participant participant) {
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("participant"), JSON.toJSONString(participant));
            executeRequest(request);
        }

        public void delete(String utoken, long participantId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("participant", participantId), builder.build());
            executeRequest(request);
        }

        public List<Participant> list(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant"), builder.build());
            JSONArray participantsJson = (JSONArray) executeRequest(request);

            List<Participant> participants = new ArrayList<Participant>();
            for (int i = 0; i < participantsJson.size(); i++) {
                JSONObject participantJson = participantsJson.getJSONObject(i);
                participants.add(JSON.toJavaObject(participantJson, Participant.class));
            }

            return participants;
        }

        public List<Participant> list(Set<Long> participantIds) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("paids", StringUtils.join(participantIds, ","));
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant/list"), builder.build());
            JSONArray participantsJson = (JSONArray) executeRequest(request);

            List<Participant> participants = new ArrayList<Participant>();
            for (int i = 0; i < participantsJson.size(); i++) {
                JSONObject participantJson = participantsJson.getJSONObject(i);
                participants.add(JSON.toJavaObject(participantJson, Participant.class));
            }

            return participants;
        }
    }

    public static class LeaderServiceApi extends UserServiceApi {
        public LeaderStatus getStatus(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/status"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), LeaderStatus.class);
        }

        public Leader get(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), Leader.class);
        }

        public List<Leader> list(Collection<Long> userIds) {
            if (userIds.isEmpty()) return new ArrayList<Leader>();

            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uids", StringUtils.join(userIds, ","));
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/list"), builder.build());
            JSONArray leadersJson = (JSONArray) executeRequest(request);

            List<Leader> leaders = new ArrayList<Leader>();
            for (int i = 0; i < leadersJson.size(); i++) {
                JSONObject leaderJson = leadersJson.getJSONObject(i);
                leaders.add(JSON.toJavaObject(leaderJson, Leader.class));
            }

            return leaders;
        }

        public void add(Leader leader) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("leader"), JSON.toJSONString(leader));
            executeRequest(request);
        }

        public void update(Leader leader) {
            MomiaHttpRequest request = MomiaHttpRequest.PUT(url("leader"), JSON.toJSONString(leader));
            executeRequest(request);
        }

        public void delete(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("leader"), builder.build());
            executeRequest(request);
        }
    }
}
