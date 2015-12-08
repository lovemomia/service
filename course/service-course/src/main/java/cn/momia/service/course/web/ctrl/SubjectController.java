package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.Region;
import cn.momia.api.course.dto.UserCourseComment;
import cn.momia.api.course.dto.Favorite;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.comment.CourseComment;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.comment.CourseCommentService;
import cn.momia.service.course.favorite.FavoriteService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.SubjectSku;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/subject")
public class SubjectController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectController.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M月d日");

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

        Set<Long> subjectIds = new HashSet<Long>();
        for (Subject subject : subjects) {
            subjectIds.add(subject.getId());
        }
        Map<Long, List<Course>> coursesMap = courseService.queryAllBySubjects(subjectIds);

        List<SubjectDto> subjectDtos = new ArrayList<SubjectDto>();
        for (Subject subject : subjects) {
            List<Course> courses = coursesMap.get(subject.getId());
            SubjectDto subjectDto = buildBaseSubjectDto(subject, courses);
            if (subjectDto != null) {
                if (courses != null && courses.size() > 0) subjectDto.setCover(courses.get(0).getCover());
                subjectDtos.add(subjectDto);
            }
        }
        PagedList<SubjectDto> pagedSubjectDtos = new PagedList<SubjectDto>(totalCount, start, count);
        pagedSubjectDtos.setList(subjectDtos);

        return MomiaHttpResponse.SUCCESS(pagedSubjectDtos);
    }

    private SubjectDto buildBaseSubjectDto(Subject subject, List<Course> courses) {
        try {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(subject.getId());
            subjectDto.setTitle(subject.getTitle());
            subjectDto.setCover(subject.getCover());
            subjectDto.setTags(subject.getTags());

            SubjectSku minPriceSku = subject.getMinPriceSku();
            subjectDto.setPrice(minPriceSku.getPrice());
            subjectDto.setOriginalPrice(minPriceSku.getOriginalPrice());

            subjectDto.setAge(getAgeRange(courses));
            subjectDto.setJoined(getJoined(courses));
            subjectDto.setScheduler(getScheduler(courses));
            subjectDto.setRegion(getRegion(courses));

            int stock = subject.getStock();
            int status = (stock == -1 || stock > 0) ? Subject.Status.OK : Subject.Status.SOLD_OUT;
            subjectDto.setStatus(status);

            return subjectDto;
        } catch (Exception e) {
            LOGGER.error("invalid subject: {}", subject.getId(), e);
            return null;
        }
    }

    private String getAgeRange(List<Course> courses) {
        if (courses.isEmpty()) return "";

        int minAge = Integer.MAX_VALUE;
        int maxAge = 0;

        for (Course course : courses) {
            minAge = Math.min(minAge, course.getMinAge());
            maxAge = Math.max(maxAge, course.getMaxAge());
        }

        if (minAge <= 0 && maxAge <= 0) throw new MomiaErrorException("invalid age of subject sku");
        if (minAge <= 0) return maxAge + "岁";
        if (maxAge <= 0) return minAge + "岁";
        if (minAge == maxAge) return minAge + "岁";
        return minAge + "-" + maxAge + "岁";
    }

    private int getJoined(List<Course> courses) {
        int joined = 0;
        for (Course course : courses) {
            joined += course.getJoined();
        }

        return joined;
    }

    private String getScheduler(List<Course> courses) {
        if (courses.isEmpty()) return "";

        List<Date> times = new ArrayList<Date>();
        for (Course course : courses) {
            Date startTime = course.getStartTime();
            Date endTime = course.getEndTime();
            if (startTime != null) times.add(startTime);
            if (endTime != null) times.add(endTime);
        }
        Collections.sort(times);

        return format(times);
    }

    private String format(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start);
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start);
            } else {
                return DATE_FORMAT.format(start) + "-" + DATE_FORMAT.format(end);
            }
        }
    }

    private String getRegion(List<Course> courses) {
        if (courses.isEmpty()) return "";

        List<Integer> regionIds = new ArrayList<Integer>();
        for (Course course : courses) {
            int regionId = course.getRegionId();
            if (!regionIds.contains(regionId)) regionIds.add(regionId);
        }

        return MetaUtil.getRegionName(regionIds.size() > 1 ? Region.MULTI_REGION_ID : regionIds.get(0));
    }

    @RequestMapping(value = "/{suid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "suid") long subjectId) {
        Subject subject = subjectService.get(subjectId);
        if (!subject.exists() || subject.getStatus() != 1) return MomiaHttpResponse.FAILED("课程体系不存在");

        List<Course> courses = courseService.queryAllBySubject(subject.getId());

        return MomiaHttpResponse.SUCCESS(buildSubjectDto(subject, courses));
    }

    private SubjectDto buildSubjectDto(Subject subject, List<Course> courses) {
        SubjectDto subjectDto = buildBaseSubjectDto(subject, courses);
        subjectDto.setIntro(subject.getIntro());
        subjectDto.setNotice(JSON.parseArray(subject.getNotice()));
        subjectDto.setImgs(subject.getImgs());

        return subjectDto;
    }

    @RequestMapping(value = "/{suid}/sku", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@PathVariable(value = "suid") long subjectId) {
        List<SubjectSku> skus = subjectService.querySkus(subjectId);
        List<SubjectSku> avaliableSkus = new ArrayList<SubjectSku>();
        for (SubjectSku sku : skus) {
            if (sku.isAvaliable()) avaliableSkus.add(sku);
        }
        return MomiaHttpResponse.SUCCESS(buildSubjectSkuDtos(avaliableSkus));
    }

    private List<SubjectSku> buildSubjectSkuDtos(List<SubjectSku> skus) {
        List<SubjectSku> skuDtos = new ArrayList<SubjectSku>();
        for (SubjectSku sku : skus) {
            SubjectSku skuDto = new SubjectSku();
            skuDto.setId(sku.getId());
            skuDto.setSubjectId(sku.getSubjectId());
            skuDto.setPrice(sku.getPrice());
            skuDto.setDesc(sku.getDesc());
            skuDto.setLimit(sku.getLimit());
            skuDto.setCourseId(sku.getCourseId());

            skuDtos.add(skuDto);
        }

        return skuDtos;
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
        Map<Long, List<Course>> coursesMap = courseService.queryAllBySubjects(subjectIds);
        Map<Long, SubjectDto> subjectsMap = new HashMap<Long, SubjectDto>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), buildBaseSubjectDto(subject, coursesMap.get(subject.getId())));
        }

        List<Favorite> results = new ArrayList<Favorite>();
        for (Favorite favorite : favorites) {
            SubjectDto subjectDto = subjectsMap.get(favorite.getRefId());
            if (subjectDto == null) continue;

            favorite.setRef((JSONObject) JSON.toJSON(subjectDto));
            results.add(favorite);
        }

        return results;
    }
}
