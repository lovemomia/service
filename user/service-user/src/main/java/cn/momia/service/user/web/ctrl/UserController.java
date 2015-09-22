package cn.momia.service.user.web.ctrl;

import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.MobileUtil;
import cn.momia.common.util.SexUtil;
import cn.momia.service.user.leader.Leader;
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
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse getUser(@PathVariable long id) {
        User user = userService.get(id);

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL, false));
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public MomiaHttpResponse updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        if (StringUtils.isBlank(nickName)) return MomiaHttpResponse.FAILED("昵称不能为空");
        if (userService.exists("nickName", nickName)) return MomiaHttpResponse.FAILED("昵称已存在，不能使用");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        String originalNickName = user.getNickName();
        if (StringUtils.isBlank(originalNickName) || !originalNickName.equals(nickName)) {
            boolean successful = userService.updateNickName(user.getId(), nickName);
            if (!successful) return MomiaHttpResponse.FAILED("更新昵称失败");
        }

        user.setNickName(nickName);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        if (StringUtils.isBlank(avatar)) return MomiaHttpResponse.FAILED("头像不能为空");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateAvatar(user.getId(), avatar);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public MomiaHttpResponse updateName(@RequestParam String utoken, @RequestParam String name) {
        if (StringUtils.isBlank(name)) return MomiaHttpResponse.FAILED("姓名不能为空");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateName(user.getId(), name);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户名字失败");

        user.setName(name);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public MomiaHttpResponse updateSex(@RequestParam String utoken, @RequestParam String sex) {
        if (StringUtils.isBlank(sex) || SexUtil.isInvalid(sex)) return MomiaHttpResponse.FAILED("无效的性别");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateSex(user.getId(), sex);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户性别失败");

        user.setSex(sex);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public MomiaHttpResponse updateBirthday(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateBirthday(user.getId(), birthday);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public MomiaHttpResponse updateCity(@RequestParam String utoken, @RequestParam(value = "city") int cityId) {
        if (cityId <= 0) return MomiaHttpResponse.FAILED("无效的城市");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateCityId(user.getId(), cityId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户城市失败");

        user.setCityId(cityId);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/region", method = RequestMethod.PUT)
    public MomiaHttpResponse updateRegion(@RequestParam String utoken, @RequestParam(value = "region") int regionId) {
        if (regionId <= 0) return MomiaHttpResponse.FAILED("无效的地区");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateRegionId(user.getId(), regionId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户所在区域失败");

        user.setRegionId(regionId);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAddress(@RequestParam String utoken, @RequestParam String address) {
        if (StringUtils.isBlank(address)) return MomiaHttpResponse.FAILED("住址不能为空");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateAddress(user.getId(), address);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户地址失败");

        user.setAddress(address);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse addChild(@RequestBody Participant[] children) {
        for(Participant child : children) if (child.isInvalid()) return MomiaHttpResponse.FAILED("添加孩子信息失败");

        long userId = 0;
        Set<Long> childIds = new HashSet<Long>();
        for(Participant child : children) {
            userId = child.getUserId();
            long childId = participantService.add(child);
            if (childId <= 0) return MomiaHttpResponse.FAILED("添加孩子信息失败");
            childIds.add(childId);
        }

        User user = userService.get(userId);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在");

        user.getChildren().addAll(childIds);
        if (!userService.updateChildren(userId, user.getChildren())) return MomiaHttpResponse.FAILED("添加孩子信息失败");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.GET)
    public MomiaHttpResponse getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();
        if (!children.contains(childId)) return MomiaHttpResponse.FAILED("孩子信息不存在");

        Participant child = participantService.get(childId);
        if (!child.exists() || child.getUserId() != user.getId()) return MomiaHttpResponse.FAILED("孩子不存在");

        return MomiaHttpResponse.SUCCESS(buildParticipantDto(child));
    }

    @RequestMapping(value = "/child/{cid}/name",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildName(@RequestParam String utoken,
                                             @PathVariable(value = "cid") long childId,
                                             @RequestParam String name) {
        if (StringUtils.isBlank(name)) return MomiaHttpResponse.FAILED("孩子姓名不能为空");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !participantService.updateName(user.getId(), childId, name)) return MomiaHttpResponse.FAILED("更新孩子姓名失败");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/child/{cid}/sex",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildSex(@RequestParam String utoken,
                                            @PathVariable(value = "cid") long childId,
                                            @RequestParam String sex) {
        if (StringUtils.isBlank(sex) || SexUtil.isInvalid(sex)) return MomiaHttpResponse.FAILED("无效的孩子性别");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !participantService.updateSex(user.getId(), childId, sex)) return MomiaHttpResponse.FAILED("更新孩子性别失败");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/child/{cid}/birthday",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildBirthday(@RequestParam String utoken,
                                                 @PathVariable(value = "cid") long childId,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !participantService.updateBirthday(user.getId(), childId, birthday)) return MomiaHttpResponse.FAILED("更新孩子生日失败");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (user.getChildren().contains(childId)) {
            user.getChildren().remove(childId);
            if (!userService.updateChildren(user.getId(), user.getChildren())) return MomiaHttpResponse.FAILED("删除孩子信息失败");
        }

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public MomiaHttpResponse listChildren(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Set<Long> childIds = user.getChildren();
        return MomiaHttpResponse.SUCCESS(buildParticipantDtos(participantService.list(childIds)));
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public MomiaHttpResponse getContacts(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildContactsDto(user));
    }

    @RequestMapping(value = "/{id}/contacts", method = RequestMethod.POST)
    public MomiaHttpResponse setContacts(@PathVariable(value = "id") long userId, @RequestParam String mobile, @RequestParam String name) {
        if (userId <= 0 || MobileUtil.isInvalid(mobile) || StringUtils.isBlank(name)) return MomiaHttpResponse.SUCCESS;

        try {
            User user = userService.getByMobile(mobile);
            if (user.exists()) {
                if (user.getId() == userId &&
                        StringUtils.isBlank(user.getName()) &&
                        !name.equals(user.getNickName())) userService.updateName(user.getId(), name);
            }
        } catch (Exception e) {
            // do nothing
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse listUsers(@RequestParam String uids, @RequestParam(defaultValue = "" + User.Type.BASE) int type) {
        List<Long> ids = new ArrayList<Long>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) ids.add(Long.valueOf(id));

        List<User> users = userService.list(ids);
        List<UserDto> userDtos = new ArrayList<UserDto>();

        switch (type) {
            case User.Type.MINI:
                for (User user : users) userDtos.add(buildUserDto(user, User.Type.MINI));
                break;
            case User.Type.FULL:
                Set<Long> childrenIds = new HashSet<Long>();
                for (User user : users) childrenIds.addAll(user.getChildren());
                List<Participant> children = participantService.list(childrenIds);
                Map<Long, Participant> childrenMap = new HashMap<Long, Participant>();
                for (Participant child : children) childrenMap.put(child.getId(), child);

                Map<Long, Leader> userLeaderInfosMap = new HashMap<Long, Leader>();
                List<Leader> leaderInfos = leaderService.listByUsers(ids);
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

                    userDtos.add(buildUserDto(user, User.Type.FULL, false, userChildren, leaderInfo));
                }
                break;
            default: for (User user : users) userDtos.add(buildUserDto(user, User.Type.BASE, false));
        }

        return MomiaHttpResponse.SUCCESS(userDtos);
    }

    @RequestMapping(value = "/{id}/payed", method = RequestMethod.GET)
    public MomiaHttpResponse isPayed(@PathVariable(value = "id") long userId) {
        User user = userService.get(userId);
        if (!user.exists()) return MomiaHttpResponse.SUCCESS(true);
        return MomiaHttpResponse.SUCCESS(user.isPayed());
    }

    @RequestMapping(value = "/{id}/payed", method = RequestMethod.POST)
    public MomiaHttpResponse setPayed(@PathVariable(value = "id") long userId) {
        if (userId <= 0) return MomiaHttpResponse.SUCCESS(false);
        return MomiaHttpResponse.SUCCESS(userService.setPayed(userId));
    }

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public MomiaHttpResponse getInviteCode(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(user.getInviteCode());
    }

    @RequestMapping(value = "/code/id", method = RequestMethod.GET)
    public MomiaHttpResponse getIdByCode(@RequestParam(value = "code") String inviteCode) {
        return MomiaHttpResponse.SUCCESS(userService.getIdByCode(inviteCode));
    }
}
