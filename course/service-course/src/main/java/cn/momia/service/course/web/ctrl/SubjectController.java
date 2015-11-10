package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.course.dto.CourseCommentDto;
import cn.momia.api.course.dto.FavoriteDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.ChildDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseComment;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.favorite.Favorite;
import cn.momia.service.course.favorite.FavoriteService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectImage;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
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
            SubjectDto subjectDto = buildBaseSubjectDto(subject, coursesMap.get(subject.getId()));
            if (subjectDto != null) subjectDtos.add(subjectDto);
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

            subjectDto.setStock(subject.getStock());

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

        if (minAge <= 0 && maxAge <= 0) throw new MomiaFailedException("invalid age of subject sku");
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

        return MetaUtil.getRegionName(regionIds.size() > 1 ? RegionDto.MULTI_REGION_ID : regionIds.get(0));
    }

    @RequestMapping(value = "/{suid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "suid") long subjectId) {
        Subject subject = subjectService.get(subjectId);
        if (!subject.exists()) return MomiaHttpResponse.FAILED("课程体系不存在");

        List<Course> courses = courseService.queryAllBySubject(subject.getId());

        return MomiaHttpResponse.SUCCESS(buildSubjectDto(subject, courses));
    }

    private SubjectDto buildSubjectDto(Subject subject, List<Course> courses) {
        SubjectDto subjectDto = buildBaseSubjectDto(subject, courses);
        subjectDto.setIntro(subject.getIntro());
        subjectDto.setNotice(JSON.parseArray(subject.getNotice()));
        subjectDto.setImgs(extractImgUrls(subject.getImgs()));

        return subjectDto;
    }

    private List<String> extractImgUrls(List<SubjectImage> imgs) {
        List<String> urls = new ArrayList<String>();
        for (SubjectImage img : imgs) {
            urls.add(img.getUrl());
        }

        return urls;
    }

    @RequestMapping(value = "/{suid}/sku", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(buildSubjectSkuDtos(subjectService.querySkus(subjectId)));
    }

    private List<SubjectSkuDto> buildSubjectSkuDtos(List<SubjectSku> skus) {
        List<SubjectSkuDto> skuDtos = new ArrayList<SubjectSkuDto>();
        for (SubjectSku sku : skus) {
            SubjectSkuDto skuDto = new SubjectSkuDto();
            skuDto.setId(sku.getId());
            skuDto.setSubjectId(sku.getSubjectId());
            skuDto.setPrice(sku.getPrice());
            skuDto.setDesc(sku.getDesc());
            skuDto.setLimit(sku.getLimit());

            skuDtos.add(skuDto);
        }

        return skuDtos;
    }

    @RequestMapping(value = "/{suid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listComments(@PathVariable(value = "suid") long subjectId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryCommentCountBySubject(subjectId);
        List<CourseComment> comments = courseService.queryCommentsBySubject(subjectId, start, count);

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

        List<UserDto> users = userServiceApi.list(userIds, UserDto.Type.FULL);
        Map<Long, UserDto> usersMap = new HashMap<Long, UserDto>();
        for (UserDto user : users) {
            usersMap.put(user.getId(), user);
        }

        List<CourseCommentDto> commentDtos = new ArrayList<CourseCommentDto>();
        for (CourseComment comment : comments) {
            Course course = coursesMap.get(comment.getCourseId());
            if (course == null) continue;
            UserDto user = usersMap.get(comment.getUserId());
            if (user == null) continue;

            commentDtos.add(buildCourseCommentDto(comment, course, user));
        }

        PagedList<CourseCommentDto> pagedCommentDtos = new PagedList<CourseCommentDto>(totalCount, start, count);
        pagedCommentDtos.setList(commentDtos);

        return MomiaHttpResponse.SUCCESS(pagedCommentDtos);
    }

    private CourseCommentDto buildCourseCommentDto(CourseComment comment, Course course, UserDto user) {
        CourseCommentDto courseCommentDto = new CourseCommentDto();
        courseCommentDto.setId(comment.getId());
        courseCommentDto.setCourseId(course.getId());
        courseCommentDto.setCourseTitle(course.getTitle());
        courseCommentDto.setUserId(user.getId());
        courseCommentDto.setNickName(user.getNickName());
        courseCommentDto.setAvatar(user.getAvatar());
        courseCommentDto.setChildren(formatChildren(user.getChildren()));
        courseCommentDto.setAddTime(comment.getAddTime());
        courseCommentDto.setStar(comment.getStar());
        courseCommentDto.setContent(comment.getContent());
        courseCommentDto.setImgs(comment.getImgs());

        return courseCommentDto;
    }

    private List<String> formatChildren(List<ChildDto> children) {
        List<String> formatedChildren = new ArrayList<String>();
        for (int i = 0; i < Math.min(2, children.size()); i++) {
            ChildDto child = children.get(i);
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

        PagedList<FavoriteDto> pagedFavoriteDtos = new PagedList<FavoriteDto>(totalCount, start, count);
        pagedFavoriteDtos.setList(buildFavoriteDtos(favorites));

        return MomiaHttpResponse.SUCCESS(pagedFavoriteDtos);
    }

    private List<FavoriteDto> buildFavoriteDtos(List<Favorite> favorites) {
        Set<Long> subjectIds = new HashSet<Long>();
        for (Favorite favorite: favorites) {
            subjectIds.add(favorite.getRefId());
        }

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, List<Course>> coursesMap = courseService.queryAllBySubjects(subjectIds);
        Map<Long, SubjectDto> subjectDtosMap = new HashMap<Long, SubjectDto>();
        for (Subject subject : subjects) {
            subjectDtosMap.put(subject.getId(), buildBaseSubjectDto(subject, coursesMap.get(subject.getId())));
        }

        List<FavoriteDto> favoriteDtos = new ArrayList<FavoriteDto>();
        for (Favorite favorite : favorites) {
            SubjectDto subjectDto = subjectDtosMap.get(favorite.getRefId());
            if (subjectDto == null) continue;

            FavoriteDto favoriteDto = new FavoriteDto();
            favoriteDto.setId(favorite.getId());
            favoriteDto.setType(favorite.getType());
            favoriteDto.setRef((JSONObject) JSON.toJSON(subjectDto));

            favoriteDtos.add(favoriteDto);
        }

        return favoriteDtos;
    }
}
