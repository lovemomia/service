package cn.momia.service.web.ctrl.user;

import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.product.facade.Product;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.ctrl.dto.ListDto;
import cn.momia.service.web.ctrl.dto.PagedListDto;
import cn.momia.service.web.ctrl.product.dto.BaseProductDto;
import cn.momia.service.web.ctrl.user.dto.ContactsDto;
import cn.momia.service.web.ctrl.user.dto.ParticipantDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController extends UserRelatedController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public ResponseMessage updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        String originalNickName = user.getNickName();
        if (StringUtils.isBlank(originalNickName) || !originalNickName.equals(nickName)) {
            boolean successful = userServiceFacade.updateUserNickName(user.getId(), nickName);
            if (!successful) return ResponseMessage.FAILED("更新失败，昵称已存在");
        }

        user.setNickName(nickName);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserAvatar(user.getId(), avatar);
        if (!successful) return ResponseMessage.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserName(user.getId(), name);
        if (!successful) return ResponseMessage.FAILED("更新用户名字失败");

        user.setName(name);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserSex(user.getId(), sex);
        if (!successful) return ResponseMessage.FAILED("更新用户性别失败");

        user.setSex(sex);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserBirthday(user.getId(), birthday);
        if (!successful) return ResponseMessage.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public ResponseMessage updateCity(@RequestParam String utoken, @RequestParam(value = "city") int cityId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserCityId(user.getId(), cityId);
        if (!successful) return ResponseMessage.FAILED("更新用户城市失败");

        user.setCityId(cityId);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/region", method = RequestMethod.PUT)
    public ResponseMessage updateRegion(@RequestParam String utoken, @RequestParam(value = "region") int regionId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserRegionId(user.getId(), regionId);
        if (!successful) return ResponseMessage.FAILED("更新用户所在区域失败");

        user.setRegionId(regionId);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserAddress(user.getId(), address);
        if (!successful) return ResponseMessage.FAILED("更新用户地址失败");

        user.setAddress(address);
        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addChild(@RequestBody Participant[] children) {
        for(Participant child : children) if (child.isInvalid()) return ResponseMessage.FAILED("添加孩子信息失败");

        long userId = 0;
        Set<Long> childIds = new HashSet<Long>();
        for(Participant child : children) {
            userId = child.getUserId();
            long childId = userServiceFacade.addChild(child);
            if (childId <= 0) return ResponseMessage.FAILED("添加孩子信息失败");
            childIds.add(childId);
        }

        User user = userServiceFacade.getUser(userId);
        if (!user.exists()) return ResponseMessage.FAILED("用户不存在");

        user.getChildren().addAll(childIds);
        if (!userServiceFacade.updateUserChildren(userId, user.getChildren())) return ResponseMessage.FAILED("添加孩子信息失败");

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }


    @RequestMapping(value = "/child/name",method = RequestMethod.PUT)
    public ResponseMessage updateParticipantName(@RequestParam String utoken,
                                                 @RequestParam(value = "cid") long childId,
                                                 @RequestParam String name) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !userServiceFacade.updateChildName(user.getId(), childId, name)) return ResponseMessage.FAILED("更新孩子姓名失败");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/child/sex",method = RequestMethod.PUT)
    public ResponseMessage updateParticipantBySex(@RequestParam String utoken,
                                                  @RequestParam(value = "cid") long childId,
                                                  @RequestParam String sex) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !userServiceFacade.updateChildSex(user.getId(), childId, sex)) return ResponseMessage.FAILED("更新孩子性别失败");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/child/birthday",method = RequestMethod.PUT)
    public ResponseMessage updateParticipantByBirthday(@RequestParam String utoken,
                                                       @RequestParam(value = "cid") long childId,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date birthday) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!user.getChildren().contains(childId) ||
                !userServiceFacade.updateChildBirthday(user.getId(), childId, birthday)) return ResponseMessage.FAILED("更新孩子生日失败");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        user.getChildren().remove(childId);
        if (!userServiceFacade.updateUserChildren(user.getId(), user.getChildren())) return ResponseMessage.FAILED("删除孩子信息失败");

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.GET)
    public ResponseMessage getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();
        if (!children.contains(childId)) return ResponseMessage.FAILED("孩子信息不存在");

        Participant child = userServiceFacade.getChild(user.getId(), childId);
        if (!child.exists()) return ResponseMessage.FAILED("孩子不存在");

        return ResponseMessage.SUCCESS(new ParticipantDto(child));
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public ResponseMessage getChildren(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> childIds = user.getChildren();
        return ResponseMessage.SUCCESS(buildParticipantsResponse(userServiceFacade.getChildren(user.getId(), childIds)));
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public ResponseMessage getContacts(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return ResponseMessage.SUCCESS(new ContactsDto(user));
    }

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public ResponseMessage getFavoritesOfUser(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long totalCount = productServiceFacade.queryFavoritesCount(user.getId());
        List<Product> products = productServiceFacade.queryFavorites(user.getId(), start, count);

        return ResponseMessage.SUCCESS(buildFavoritesDto(totalCount, products, start, count));
    }

    private PagedListDto buildFavoritesDto(long totalCount, List<Product> products, int start, int count) {
        PagedListDto favoritesDto = new PagedListDto(totalCount, start, count);
        ListDto baseProductsDto = new ListDto();
        for (Product product : products) {
            baseProductsDto.add(new BaseProductDto(product));
        }
        favoritesDto.addAll(baseProductsDto);

        return favoritesDto;
    }
}
