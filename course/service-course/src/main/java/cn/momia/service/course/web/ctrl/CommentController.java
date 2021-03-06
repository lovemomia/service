package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.course.BookedCourse;
import cn.momia.api.course.dto.course.Course;
import cn.momia.api.course.dto.comment.TimelineUnit;
import cn.momia.api.course.dto.comment.UserCourseComment;
import cn.momia.api.user.ChildServiceApi;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.comment.CourseComment;
import cn.momia.service.course.comment.CourseCommentService;
import com.alibaba.fastjson.JSONObject;
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
    @Autowired private ChildServiceApi childServiceApi;

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

        List<JSONObject> childrenDetail = childServiceApi.formatChildrenDetail(user.getChildren());
        List<String> children = childServiceApi.formatChildren(childrenDetail);
        userCourseComment.setChildrenDetail(childrenDetail);
        userCourseComment.setChildren(children);

        userCourseComment.setAddTime(TimeUtil.formatAddTime(comment.getAddTime()));
        userCourseComment.setStar(comment.getStar());
        userCourseComment.setContent(comment.getContent());
        userCourseComment.setImgs(comment.getImgs());

        return userCourseComment;
    }

    @RequestMapping(value = "/course/comment/img", method = RequestMethod.GET)
    public MomiaHttpResponse getLatestImgs(@RequestParam(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(courseCommentService.queryLatestImgs(userId));
    }

    @RequestMapping(value = "/course/timeline", method = RequestMethod.GET)
    public MomiaHttpResponse timeline(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryFinishedCountByUser(userId);
        List<BookedCourse> bookedCourses = courseService.queryFinishedByUser(userId, start, count);

        Set<Long> courseIds = new HashSet<Long>();
        for(BookedCourse bookedCourse : bookedCourses) {
            courseIds.add(bookedCourse.getCourseId());
        }
        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<CourseComment> comments = courseCommentService.queryComments(userId, courseIds);
        Map<Long, CourseComment> commentsMap = new HashMap<Long, CourseComment>();
        Set<Long> userIds = new HashSet<Long>();
        for (CourseComment comment : comments) {
            commentsMap.put(comment.getCourseId(), comment);
            userIds.add(comment.getUserId());

        }

        List<User> users = userServiceApi.list(userIds, User.Type.FULL);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) {
            usersMap.put(user.getId(), user);
        }

        List<TimelineUnit> timeline = new ArrayList<TimelineUnit>();
        for (BookedCourse bookedCourse : bookedCourses) {
            Course course = coursesMap.get(bookedCourse.getCourseId());
            if (course == null) continue;

            TimelineUnit unit = new TimelineUnit();
            unit.setCourseId(bookedCourse.getCourseId());
            unit.setCourseTitle(course.getTitle());
            unit.setTime(bookedCourse.getStartTime());

            CourseComment comment = commentsMap.get(bookedCourse.getCourseId());
            if (comment != null) {
                User user = usersMap.get(comment.getUserId());
                if (user != null) unit.setComment(buildUserCourseComment(comment, user));
            }

            timeline.add(unit);
        }

        PagedList<TimelineUnit> pagedTimeline = new PagedList<TimelineUnit>(totalCount, start, count);
        pagedTimeline.setList(timeline);

        return MomiaHttpResponse.SUCCESS(pagedTimeline);
    }

    @RequestMapping(value = "/comment/timeline", method = RequestMethod.GET)
    public MomiaHttpResponse commentTimeline(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseCommentService.queryCommentCountByUser(userId);
        List<CourseComment> comments = courseCommentService.queryCommentsByUser(userId, start, count);

        Set<Long> courseIds = new HashSet<Long>();
        Set<Long> userIds = new HashSet<Long>();
        for(CourseComment comment : comments) {
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

        List<TimelineUnit> timeline = new ArrayList<TimelineUnit>();
        for (CourseComment comment : comments) {
            Course course = coursesMap.get(comment.getCourseId());
            if (course == null) continue;

            User user = usersMap.get(comment.getUserId());
            if (user == null) continue;

            TimelineUnit unit = new TimelineUnit();
            unit.setCourseId(course.getId());
            unit.setCourseTitle(course.getTitle());
            unit.setTime(comment.getAddTime());
            unit.setComment(buildUserCourseComment(comment, user));

            timeline.add(unit);
        }

        PagedList<TimelineUnit> pagedTimeline = new PagedList<TimelineUnit>(totalCount, start, count);
        pagedTimeline.setList(timeline);

        return MomiaHttpResponse.SUCCESS(pagedTimeline);
    }

    @RequestMapping(value = "/subject/{suid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listComments(@PathVariable(value = "suid") long subjectId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseCommentService.queryCommentCountBySubject(subjectId);
        List<CourseComment> comments = courseCommentService.queryCommentsBySubject(subjectId, start, count);

        PagedList<UserCourseComment> pagedUserCourseComments = new PagedList<UserCourseComment>(totalCount, start, count);
        pagedUserCourseComments.setList(buildUserCourseComments(comments));

        return MomiaHttpResponse.SUCCESS(pagedUserCourseComments);
    }

    private List<UserCourseComment> buildUserCourseComments(List<CourseComment> comments) {
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
        return userCourseComments;
    }

    private UserCourseComment buildUserCourseComment(CourseComment comment, User user, Course course) {
        UserCourseComment userCourseComment = buildUserCourseComment(comment, user);

        userCourseComment.setCourseId(course.getId());
        userCourseComment.setCourseTitle(course.getTitle());

        return userCourseComment;
    }

    @RequestMapping(value = "/subject/{suid}/comment/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommendedComments(@PathVariable(value = "suid") long subjectId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        List<CourseComment> comments = courseCommentService.queryRecommendedCommentsBySubject(subjectId, start, count);
        return MomiaHttpResponse.SUCCESS(buildUserCourseComments(comments));
    }
}
