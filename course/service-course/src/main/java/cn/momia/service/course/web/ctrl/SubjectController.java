package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.UserCourseComment;
import cn.momia.api.course.dto.Favorite;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.comment.CourseComment;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.comment.CourseCommentService;
import cn.momia.service.course.favorite.FavoriteService;
import cn.momia.api.course.dto.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.SubjectSku;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/subject")
public class SubjectController extends BaseController {
    @Autowired private CourseService courseService;
    @Autowired private CourseCommentService courseCommentService;
    @Autowired private SubjectService subjectService;
    @Autowired private FavoriteService favoriteService;

    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/trial", method = RequestMethod.GET)
    public MomiaHttpResponse listTrial(@RequestParam(value = "city") long cityId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = subjectService.queryTrialCount(cityId);
        List<Subject> subjects = subjectService.queryTrial(cityId, start, count);

        List<Subject> baseSubjects = new ArrayList<Subject>();
        for (Subject subject : subjects) {
            baseSubjects.add(new Subject.Base(subject));
        }
        PagedList<Subject> pagedSubjects = new PagedList<Subject>(totalCount, start, count);
        pagedSubjects.setList(baseSubjects);

        return MomiaHttpResponse.SUCCESS(pagedSubjects);
    }

    @RequestMapping(value = "/{suid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "suid") long subjectId) {
        Subject subject = subjectService.get(subjectId);
        if (!subject.exists()) return MomiaHttpResponse.FAILED("课程体系不存在");
        return MomiaHttpResponse.SUCCESS(subject);
    }

    @RequestMapping(value = "/{suid}/sku", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@PathVariable(value = "suid") long subjectId) {
        List<SubjectSku> skus = subjectService.querySkus(subjectId);
        List<SubjectSku> avaliableSkus = new ArrayList<SubjectSku>();
        for (SubjectSku sku : skus) {
            if (sku.isAvaliable()) avaliableSkus.add(sku);
        }

        return MomiaHttpResponse.SUCCESS(avaliableSkus);
    }

    @RequestMapping(value = "/{suid}/comment", method = RequestMethod.GET)
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

            userCourseComments.add(buildUserCourseComment(comment, course, user));
        }

        PagedList<UserCourseComment> pagedUserCourseComments = new PagedList<UserCourseComment>(totalCount, start, count);
        pagedUserCourseComments.setList(userCourseComments);

        return MomiaHttpResponse.SUCCESS(pagedUserCourseComments);
    }

    private UserCourseComment buildUserCourseComment(CourseComment comment, Course course, User user) {
        UserCourseComment userCourseComment = new UserCourseComment();
        userCourseComment.setId(comment.getId());
        userCourseComment.setCourseId(course.getId());
        userCourseComment.setCourseTitle(course.getTitle());
        userCourseComment.setUserId(user.getId());
        userCourseComment.setNickName(user.getNickName());
        userCourseComment.setAvatar(user.getAvatar());
        userCourseComment.setChildren(formatChildren(user.getChildren()));
        userCourseComment.setAddTime(TimeUtil.formatAddTime(comment.getAddTime()));
        userCourseComment.setStar(comment.getStar());
        userCourseComment.setContent(comment.getContent());
        userCourseComment.setImgs(comment.getImgs());

        return userCourseComment;
    }

    private List<String> formatChildren(List<Child> children) {
        List<String> formatedChildren = new ArrayList<String>();
        for (int i = 0; i < Math.min(2, children.size()); i++) {
            Child child = children.get(i);
            formatedChildren.add(child.getSex() + "孩" + TimeUtil.formatAge(child.getBirthday()));
        }

        return formatedChildren;
    }

    @RequestMapping(value = "/{suid}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favored(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.isFavored(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/{suid}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favor(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.favor(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/{suid}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavor(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.unfavor(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse favorite(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = favoriteService.queryFavoriteCount(userId, Favorite.Type.SUBJECT);
        List<Favorite> favorites = favoriteService.queryFavorites(userId, Favorite.Type.SUBJECT, start, count);

        PagedList<Favorite> pagedFavorites = new PagedList<Favorite>(totalCount, start, count);
        pagedFavorites.setList(completeFavorites(favorites));

        return MomiaHttpResponse.SUCCESS(pagedFavorites);
    }

    private List<Favorite> completeFavorites(List<Favorite> favorites) {
        Set<Long> subjectIds = new HashSet<Long>();
        for (Favorite favorite: favorites) {
            subjectIds.add(favorite.getRefId());
        }

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<Favorite> results = new ArrayList<Favorite>();
        for (Favorite favorite : favorites) {
            Subject subject = subjectsMap.get(favorite.getRefId());
            if (subject == null) continue;

            favorite.setRef((JSONObject) JSON.toJSON(new Subject.Base(subject)));
            results.add(favorite);
        }

        return results;
    }
}
