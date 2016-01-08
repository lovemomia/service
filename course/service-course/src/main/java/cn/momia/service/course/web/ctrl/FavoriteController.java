package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.course.Course;
import cn.momia.api.course.dto.favorite.Favorite;
import cn.momia.api.course.dto.subject.Subject;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.favorite.FavoriteService;
import cn.momia.service.course.subject.SubjectService;
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
public class FavoriteController extends BaseController {
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private FavoriteService favoriteService;

    @RequestMapping(value = "/course/{coid}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favoredCourse(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.isFavored(userId, Favorite.Type.COURSE, courseId));
    }

    @RequestMapping(value = "/course/{coid}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favorCourse(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.favor(userId, Favorite.Type.COURSE, courseId));
    }

    @RequestMapping(value = "/course/{coid}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavorCourse(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.unfavor(userId, Favorite.Type.COURSE, courseId));
    }

    @RequestMapping(value = "/course/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse favoriteCourse(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = favoriteService.queryFavoriteCount(userId, Favorite.Type.COURSE);
        List<Favorite> favorites = favoriteService.queryFavorites(userId, Favorite.Type.COURSE, start, count);

        PagedList<Favorite> pagedFavorites = new PagedList<Favorite>(totalCount, start, count);
        pagedFavorites.setList(completeCourseFavorites(favorites));

        return MomiaHttpResponse.SUCCESS(pagedFavorites);
    }

    private List<Favorite> completeCourseFavorites(List<Favorite> favorites) {
        Set<Long> courseIds = new HashSet<Long>();
        for (Favorite favorite: favorites) {
            courseIds.add(favorite.getRefId());
        }

        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> baseCoursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            baseCoursesMap.put(course.getId(), new Course.Base(course));
        }

        List<Favorite> results = new ArrayList<Favorite>();
        for (Favorite favorite : favorites) {
            Course course = baseCoursesMap.get(favorite.getRefId());
            if (course == null) continue;

            favorite.setRef((JSONObject) JSON.toJSON(course));
            results.add(favorite);
        }

        return results;
    }

    @RequestMapping(value = "/subject/{suid}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favoredSubject(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.isFavored(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/subject/{suid}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favorSubject(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.favor(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/subject/{suid}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavorSubject(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.unfavor(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/subject/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse favoriteSubject(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = favoriteService.queryFavoriteCount(userId, Favorite.Type.SUBJECT);
        List<Favorite> favorites = favoriteService.queryFavorites(userId, Favorite.Type.SUBJECT, start, count);

        PagedList<Favorite> pagedFavorites = new PagedList<Favorite>(totalCount, start, count);
        pagedFavorites.setList(completeSubjectFavorites(favorites));

        return MomiaHttpResponse.SUCCESS(pagedFavorites);
    }

    private List<Favorite> completeSubjectFavorites(List<Favorite> favorites) {
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
