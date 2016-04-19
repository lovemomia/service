package cn.momia.service.user.web.ctrl;

import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.CastUtil;
import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.child.Child;
import cn.momia.service.user.child.ChildComment;
import cn.momia.service.user.child.ChildRecord;
import cn.momia.service.user.child.ChildService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/child")
public class ChildController extends BaseController {
    @Autowired private ChildService childService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse addChild(@RequestParam String utoken, @RequestParam(value = "children") String childrenJson) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        List<Child> children = CastUtil.toList(JSON.parseArray(childrenJson), Child.class);
        for (Child child : children) {
            child.setUserId(user.getId());
            if (child.isInvalid()) return MomiaHttpResponse.FAILED("添加失败，无效的孩子信息");
        }

        for (Child child : children) {
            if (child.getId() <= 0) childService.add(child);
            else childService.update(user.getId(), child.getId(), child.getAvatar(), child.getName(), child.getSex(), child.getBirthday());
        }

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public MomiaHttpResponse getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Child child = childService.get(childId);
        if (!child.exists() || (user.isNormal() && child.getUserId() != user.getId())) return MomiaHttpResponse.FAILED("孩子不存在");

        return MomiaHttpResponse.SUCCESS(child);
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse listChildren(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        return user.exists() ? MomiaHttpResponse.SUCCESS(user.getChildren()) : MomiaHttpResponse.TOKEN_EXPIRED;
    }

    @RequestMapping(value = "/{cid}/avatar",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildAvatar(@RequestParam String utoken,
                                               @PathVariable(value = "cid") long childId,
                                               @RequestParam String avatar) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateAvatar(user.getId(), childId, avatar)) return MomiaHttpResponse.FAILED("更新孩子头像失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}/name",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildName(@RequestParam String utoken,
                                             @PathVariable(value = "cid") long childId,
                                             @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateName(user.getId(), childId, name)) return MomiaHttpResponse.FAILED("更新孩子姓名失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}/sex",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildSex(@RequestParam String utoken,
                                            @PathVariable(value = "cid") long childId,
                                            @RequestParam String sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateSex(user.getId(), childId, sex)) return MomiaHttpResponse.FAILED("更新孩子性别失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}/birthday",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildBirthday(@RequestParam String utoken,
                                                 @PathVariable(value = "cid") long childId,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateBirthday(user.getId(), childId, birthday)) return MomiaHttpResponse.FAILED("更新孩子生日失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.delete(user.getId(), childId)) return MomiaHttpResponse.FAILED("删除孩子信息失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    /** 老师相关 **/

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse listByIds(@RequestParam String cids) {
        return MomiaHttpResponse.SUCCESS(childService.list(MomiaUtil.splitDistinctLongs(cids)));
    }

    @RequestMapping(value = "/tag", method = RequestMethod.GET)
    public MomiaHttpResponse tags() {
        return MomiaHttpResponse.SUCCESS(childService.listAllTags());
    }

    @RequestMapping(value = "/{cid}/record", method = RequestMethod.GET)
    public MomiaHttpResponse record(@RequestParam String utoken,
                                    @PathVariable(value = "cid") long childId,
                                    @RequestParam(value = "coid") long courseId,
                                    @RequestParam(value = "sid") long courseSkuId) {
        User user = userService.getByToken(utoken);
        if (!user.exists() || user.isNormal()) return MomiaHttpResponse.FAILED("您无权查看记录");
        return MomiaHttpResponse.SUCCESS(childService.getRecord(user.getId(), childId, courseId, courseSkuId));
    }

    @RequestMapping(value = "/{cid}/record", method = RequestMethod.POST)
    public MomiaHttpResponse record(@RequestParam String utoken,
                                    @PathVariable(value = "cid") long childId,
                                    @RequestParam(value = "coid") long courseId,
                                    @RequestParam(value = "sid") long courseSkuId,
                                    @RequestParam String record) {
        User user = userService.getByToken(utoken);
        if (!user.exists() || !user.isTeacher()) return MomiaHttpResponse.FAILED("只有老师才有资格记录");

        ChildRecord childRecord = CastUtil.toObject(JSON.parseObject(record), ChildRecord.class);
        childRecord.setTeacherUserId(user.getId());
        childRecord.setChildId(childId);
        childRecord.setCourseId(courseId);
        childRecord.setCourseSkuId(courseSkuId);

        if (!StringUtils.isBlank(childRecord.getContent()) && childRecord.getContent().length() > 300) return MomiaHttpResponse.FAILED("记录字数过多，超出限制");

        return MomiaHttpResponse.SUCCESS(childService.record(childRecord));
    }

    @RequestMapping(value = "/{cid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listChildComments(@RequestParam String utoken,
                                               @PathVariable(value = "cid") long childId,
                                               @RequestParam int start,
                                               @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        User user = userService.getByToken(utoken);
        if (!user.exists() || user.isNormal()) return MomiaHttpResponse.FAILED("您无权查看孩子的评价信息");

        long totalCount = childService.queryCommentsCount(childId);
        List<ChildComment> comments = childService.queryComments(childId, start, count);

        PagedList<ChildComment> pagedComments = new PagedList<ChildComment>(totalCount, start, count);
        pagedComments.setList(comments);

        return MomiaHttpResponse.SUCCESS(pagedComments);
    }

    @RequestMapping(value = "/{cid}/comment", method = RequestMethod.POST)
    public MomiaHttpResponse comment(@RequestParam String utoken,
                                     @PathVariable(value = "cid") long childId,
                                     @RequestParam(value = "coid") long courseId,
                                     @RequestParam(value = "sid") long courseSkuId,
                                     @RequestParam String comment) {
        if (!StringUtils.isBlank(comment) && comment.length() > 500) return MomiaHttpResponse.FAILED("评语字数过多，超出限制");

        User user = userService.getByToken(utoken);
        if (!user.exists() || !user.isTeacher()) return MomiaHttpResponse.FAILED("只有老师才有资格评价");

        ChildComment childComment = new ChildComment();
        childComment.setTeacherUserId(user.getId());
        childComment.setChildId(childId);
        childComment.setCourseId(courseId);
        childComment.setCourseSkuId(courseSkuId);
        childComment.setContent(comment);

        return MomiaHttpResponse.SUCCESS(childService.comment(childComment));
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public MomiaHttpResponse queryCommentedChildIds(@RequestParam(value = "coid") long courseId, @RequestParam(value = "sid") long courseSkuId) {
        return MomiaHttpResponse.SUCCESS(childService.queryCommentedChildIds(courseId, courseSkuId));
    }
}
