package cn.momia.service.user.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.MobileUtil;
import cn.momia.common.webapp.ctrl.dto.ListDto;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.user.web.ctrl.dto.BaseUserDto;
import cn.momia.service.user.web.ctrl.dto.ContactsDto;
import cn.momia.service.user.web.ctrl.dto.FullUserDto;
import cn.momia.service.user.web.ctrl.dto.MiniUserDto;
import cn.momia.service.user.web.ctrl.dto.ParticipantDto;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController extends UserRelatedController {
    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse getUser(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse getUser(@PathVariable long id) {
        User user = userServiceFacade.getUser(id);

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user, false));
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public MomiaHttpResponse updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        String originalNickName = user.getNickName();
        if (StringUtils.isBlank(originalNickName) || !originalNickName.equals(nickName)) {
            boolean successful = userServiceFacade.updateUserNickName(user.getId(), nickName);
            if (!successful) return MomiaHttpResponse.FAILED("更新失败，昵称已存在");
        }

        user.setNickName(nickName);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserAvatar(user.getId(), avatar);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public MomiaHttpResponse updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserName(user.getId(), name);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户名字失败");

        user.setName(name);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public MomiaHttpResponse updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserSex(user.getId(), sex);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户性别失败");

        user.setSex(sex);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public MomiaHttpResponse updateBirthday(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserBirthday(user.getId(), birthday);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public MomiaHttpResponse updateCity(@RequestParam String utoken, @RequestParam(value = "city") int cityId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserCityId(user.getId(), cityId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户城市失败");

        user.setCityId(cityId);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/region", method = RequestMethod.PUT)
    public MomiaHttpResponse updateRegion(@RequestParam String utoken, @RequestParam(value = "region") int regionId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserRegionId(user.getId(), regionId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户所在区域失败");

        user.setRegionId(regionId);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserAddress(user.getId(), address);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户地址失败");

        user.setAddress(address);
        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse addChild(@RequestBody Participant[] children) {
        for(Participant child : children) if (child.isInvalid()) return MomiaHttpResponse.FAILED("添加孩子信息失败");

        long userId = 0;
        Set<Long> childIds = new HashSet<Long>();
        for(Participant child : children) {
            userId = child.getUserId();
            long childId = userServiceFacade.addChild(child);
            if (childId <= 0) return MomiaHttpResponse.FAILED("添加孩子信息失败");
            childIds.add(childId);
        }

        User user = userServiceFacade.getUser(userId);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在");

        user.getChildren().addAll(childIds);
        if (!userServiceFacade.updateUserChildren(userId, user.getChildren())) return MomiaHttpResponse.FAILED("添加孩子信息失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.GET)
    public MomiaHttpResponse getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();
        if (!children.contains(childId)) return MomiaHttpResponse.FAILED("孩子信息不存在");

        Participant child = userServiceFacade.getChild(user.getId(), childId);
        if (!child.exists()) return MomiaHttpResponse.FAILED("孩子不存在");

        return MomiaHttpResponse.SUCCESS(new ParticipantDto(child));
    }

    @RequestMapping(value = "/child/{cid}/name",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildName(@RequestParam String utoken,
                                           @PathVariable(value = "cid") long childId,
                                           @RequestParam String name) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !userServiceFacade.updateChildName(user.getId(), childId, name)) return MomiaHttpResponse.FAILED("更新孩子姓名失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(userServiceFacade.getUserByToken(utoken)));
    }

    @RequestMapping(value = "/child/{cid}/sex",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildSex(@RequestParam String utoken,
                                          @PathVariable(value = "cid") long childId,
                                          @RequestParam String sex) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !userServiceFacade.updateChildSex(user.getId(), childId, sex)) return MomiaHttpResponse.FAILED("更新孩子性别失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(userServiceFacade.getUserByToken(utoken)));
    }

    @RequestMapping(value = "/child/{cid}/birthday",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildBirthday(@RequestParam String utoken,
                                               @PathVariable(value = "cid") long childId,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date birthday) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !userServiceFacade.updateChildBirthday(user.getId(), childId, birthday)) return MomiaHttpResponse.FAILED("更新孩子生日失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(userServiceFacade.getUserByToken(utoken)));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        user.getChildren().remove(childId);
        if (!userServiceFacade.updateUserChildren(user.getId(), user.getChildren())) return MomiaHttpResponse.FAILED("删除孩子信息失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public MomiaHttpResponse listChildren(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Set<Long> childIds = user.getChildren();
        return MomiaHttpResponse.SUCCESS(buildParticipantsResponse(userServiceFacade.getChildren(user.getId(), childIds)));
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public MomiaHttpResponse getContacts(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(new ContactsDto(user));
    }

    @RequestMapping(value = "/{id}/contacts", method = RequestMethod.POST)
    public MomiaHttpResponse processContacts(@PathVariable(value = "id") long userId, @RequestParam String mobile, @RequestParam String name) {
        if (userId <= 0 || MobileUtil.isInvalid(mobile) || StringUtils.isBlank(name)) return MomiaHttpResponse.BAD_REQUEST;

        userServiceFacade.processContacts(userId, mobile, name);

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse listUsers(@RequestParam String uids, @RequestParam(defaultValue = "" + User.Type.BASE) int type) {
        List<Long> ids = new ArrayList<Long>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) ids.add(Long.valueOf(id));

        List<User> users = userServiceFacade.getUsers(ids);
        ListDto usersDto = new ListDto();

        switch (type) {
            case User.Type.MINI:
                for (User user : users) usersDto.add(new MiniUserDto(user));
                break;
            case User.Type.FULL:
                Set<Long> childrenIds = new HashSet<Long>();
                for (User user : users) childrenIds.addAll(user.getChildren());
                List<Participant> children = userServiceFacade.getParticipants(childrenIds);
                Map<Long, Participant> childrenMap = new HashMap<Long, Participant>();
                for (Participant child : children) childrenMap.put(child.getId(), child);

                Map<Long, Leader> userLeaderInfosMap = new HashMap<Long, Leader>();
                List<Leader> leaderInfos = userServiceFacade.getLeaderInfos(ids);
                for (Leader leaderInfo : leaderInfos) userLeaderInfosMap.put(leaderInfo.getUserId(), leaderInfo);

                for (User user : users) {
                    List<Participant> userChildren = new ArrayList<Participant>();
                    Set<Long> userChildrenIds = user.getChildren();
                    for (long userChildId : userChildrenIds) {
                        Participant child = childrenMap.get(userChildId);
                        if (child != null) userChildren.add(child);
                    }

                    Leader leaderInfo = userLeaderInfosMap.get(user.getId());
                    if (leaderInfo == null) leaderInfo = Leader.NOT_EXIST_LEADER;

                    usersDto.add(new FullUserDto(user, userChildren, leaderInfo, false));
                }
                break;
            default: for (User user : users) usersDto.add(new BaseUserDto(user, false));
        }

        return MomiaHttpResponse.SUCCESS(usersDto);
    }

    @RequestMapping(value = "/{id}/payed", method = RequestMethod.GET)
    public MomiaHttpResponse isPayed(@PathVariable(value = "id") long userId) {
        if (userId <= 0) return MomiaHttpResponse.SUCCESS(true);
        return MomiaHttpResponse.SUCCESS(userServiceFacade.isPayed(userId));
    }

    @RequestMapping(value = "/{id}/payed", method = RequestMethod.POST)
    public MomiaHttpResponse setPayed(@PathVariable(value = "id") long userId) {
        if (userId <= 0) return MomiaHttpResponse.SUCCESS(false);
        return MomiaHttpResponse.SUCCESS(userServiceFacade.setPayed(userId));
    }

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public MomiaHttpResponse getInviteCode(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(user.getInviteCode());
    }

    @RequestMapping(value = "/code/id", method = RequestMethod.GET)
    public MomiaHttpResponse getIdByCode(@RequestParam(value = "code") String inviteCode) {
        return MomiaHttpResponse.SUCCESS(userServiceFacade.getIdByCode(inviteCode));
    }
}
