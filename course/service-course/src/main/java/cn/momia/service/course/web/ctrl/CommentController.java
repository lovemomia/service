package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.Course;
import cn.momia.api.course.dto.CourseCommentChild;
import cn.momia.api.course.dto.UserCourseComment;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.comment.CourseComment;
import cn.momia.service.course.comment.CourseCommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class CommentController extends BaseController {
    @Autowired private CourseService courseService;
    @Autowired private CourseCommentService courseCommentService;

    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/course/comment", method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse comment(@RequestBody CourseComment comment) {
        if (comment.isInvalid()) return MomiaHttpResponse.BAD_REQUEST;
        if (StringUtils.isBlank(comment.getContent())) return MomiaHttpResponse.FAILED("评论内容不能为空");

        if (!courseService.finished(comment.getUserId(), comment.getBookingId(), comment.getCourseId())) return MomiaHttpResponse.FAILED("你还没有上过这门课，无法评论");
        if (courseCommentService.isCommented(comment.getUserId(), comment.getBookingId())) return MomiaHttpResponse.FAILED("一堂课只能发表一次评论");
        if (comment.getImgs() != null && comment.getImgs().size() > 9) return MomiaHttpResponse.FAILED("上传的图片过多，1条评论最多上传9张图片");

        return MomiaHttpResponse.SUCCESS(courseCommentService.comment(comment));
    }

    @RequestMapping(value = "/course/{coid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listComment(@PathVariable(value = "coid") long courseId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseCommentService.queryCommentCountByCourse(courseId);
        List<CourseComment> comments = courseCommentService.queryCommentsByCourse(courseId, start, count);

        Set<Long> userIds = new HashSet<Long>();
        for (CourseComment comment : comments) {
            userIds.add(comment.getUserId());
        }

        List<User> users = userServiceApi.list(userIds, User.Type.FULL);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) {
            usersMap.put(user.getId(), user);
        }

        List<UserCourseComment> userCourseComments = new ArrayList<UserCourseComment>();
        for (CourseComment comment : comments) {
            User user = usersMap.get(comment.getUserId());
            if (user == null) continue;
            userCourseComments.add(buildUserCourseComment(comment, user));
        }

        PagedList<UserCourseComment> pagedUserCourseComments = new PagedList<UserCourseComment>(totalCount, start, count);
        pagedUserCourseComments.setList(userCourseComments);

        return MomiaHttpResponse.SUCCESS(pagedUserCourseComments);
    }

    private UserCourseComment buildUserCourseComment(CourseComment comment, User user) {
        UserCourseComment userCourseComment = new UserCourseComment();
        userCourseComment.setId(comment.getId());
        userCourseComment.setUserId(user.getId());
        userCourseComment.setNickName(user.getNickName());
        userCourseComment.setAvatar(user.getAvatar());

        List<CourseCommentChild> childrenDetail = formatChildrenDetail(user.getChildren());
        List<String> children = formatChildren(childrenDetail);
        userCourseComment.setChildrenDetail(childrenDetail);
        userCourseComment.setChildren(children);

        userCourseComment.setAddTime(TimeUtil.formatAddTime(comment.getAddTime()));
        userCourseComment.setStar(comment.getStar());
        userCourseComment.setContent(comment.getContent());
        userCourseComment.setImgs(comment.getImgs());

        return userCourseComment;
    }


    private List<CourseCommentChild> formatChildrenDetail(List<Child> children) {
        List<CourseCommentChild> commentChildren = new ArrayList<CourseCommentChild>();
        for (int i = 0; i < Math.min(2, children.size()); i++) {
            Child child = children.get(i);
            CourseCommentChild commentChild = new CourseCommentChild();
            commentChild.setSex(child.getSex());
            commentChild.setName(child.getName());
            commentChild.setAge(TimeUtil.formatAge(child.getBirthday()));

            commentChildren.add(commentChild);
        }

        return commentChildren;
    }

    private List<String> formatChildren(List<CourseCommentChild> childrenDetail) {
        List<String> formatedChildren = new ArrayList<String>();
        for (CourseCommentChild child : childrenDetail) {
            formatedChildren.add(child.getSex() + "孩" + child.getAge());
        }

        return formatedChildren;
    }

    @RequestMapping(value = "/course/comment/img", method = RequestMethod.GET)
    public MomiaHttpResponse getLatestImgs(@RequestParam(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(courseCommentService.queryLatestImgs(userId));
    }

    @RequestMapping(value = "/subject/{suid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listComments(@PathVariable(value = "suid") long subjectId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseCommentService.queryCommentCountBySubject(subjectId);
        List<CourseComment> comments = courseCommentService.queryCommentsBySubject(subjectId, start, count);

        Set<Long> courseIds = new HashSet<Long>();
        Set<Long> userIds = new HashSet<Long>();
        for (CourseComment comment : comments) {
            courseIds.add(comment.getCourseId());
            userIds.add(comment.getUserId());
        }

        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<User> users = userServiceApi.list(userIds, User.Type.FULL);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) {
            usersMap.put(user.getId(), user);
        }

        List<UserCourseComment> userCourseComments = new ArrayList<UserCourseComment>();
        for (CourseComment comment : comments) {
            Course course = coursesMap.get(comment.getCourseId());
            if (course == null) continue;
            User user = usersMap.get(comment.getUserId());
            if (user == null) continue;

            userCourseComments.add(buildUserCourseComment(comment, user, course));
        }

        PagedList<UserCourseComment> pagedUserCourseComments = new PagedList<UserCourseComment>(totalCount, start, count);
        pagedUserCourseComments.setList(userCourseComments);

        return MomiaHttpResponse.SUCCESS(pagedUserCourseComments);
    }

    private UserCourseComment buildUserCourseComment(CourseComment comment, User user, Course course) {
        UserCourseComment userCourseComment = buildUserCourseComment(comment, user);

        userCourseComment.setCourseId(course.getId());
        userCourseComment.setCourseTitle(course.getTitle());

        return userCourseComment;
    }
}
