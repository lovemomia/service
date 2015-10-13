package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.course.dto.CourseBookDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CoursePlaceDto;
import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.subject.SubjectService;
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
@RequestMapping("/course")
public class CourseController extends BaseController {
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private PoiServiceApi poiServiceApi;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable long id) {
        List<Course> courses = new ArrayList<Course>();
        courses.add(courseService.get(id));
        List<CourseDto> courseDtos = buildCourseDtos(courses, Course.Type.FULL);

        return MomiaHttpResponse.SUCCESS(courseDtos.get(0));
    }

    private List<CourseDto> buildCourseDtos(List<Course> courses, int type) {
        Set<Long> courseIds = new HashSet<Long>();
        Set<Integer> placeIds = new HashSet<Integer>();
        for (Course course : courses) {
            courseIds.add(course.getId());
            placeIds.addAll(course.getPlaceIds());
        }

        Map<Long, String> subjectNames = subjectService.queryTitlesByCourse(courseIds);
        List<PlaceDto> places = poiServiceApi.list(placeIds);
        Map<Integer, PlaceDto> placesMap = new HashMap<Integer, PlaceDto>();
        for (PlaceDto place : places) placesMap.put(place.getId(), place);

        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for (Course course : courses) {
            courseDtos.add(buildCourseDto(course, subjectNames, placesMap, type));
        }

        return courseDtos;
    }

    private CourseDto buildCourseDto(Course course, Map<Long, String> subjectNames, Map<Integer, PlaceDto> places, int type) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setCover(course.getCover());
        courseDto.setAge(course.getAge());
        courseDto.setJoined(course.getJoined());
        courseDto.setPrice(course.getPrice());
        courseDto.setScheduler(course.getScheduler());

        List<Integer> placeIds = course.getPlaceIds();
        List<PlaceDto> placesOfCourse = new ArrayList<PlaceDto>();
        for (int placeId : placeIds) {
            PlaceDto place = places.get(placeId);
            if (place != null) placesOfCourse.add(place);
        }

        int regionId = 0;
        if (placesOfCourse.size() > 1) {
            regionId = RegionDto.MULTI_REGION_ID;
        } else if (!placesOfCourse.isEmpty()) {
            regionId = placesOfCourse.get(0).getRegionId();
        }
        courseDto.setRegion(MetaUtil.getRegionName(regionId));

        String subject = subjectNames.get(course.getId());
        courseDto.setSubject(subject == null ? "" : subject);

        if (type == Course.Type.FULL) {
            courseDto.setGoal(course.getGoal());
            courseDto.setFlow(course.getFlow());
            courseDto.setExtra(course.getExtra());
            courseDto.setPlaces(buildCoursePlaceDtos(placesOfCourse, 1));
            courseDto.setImgs(course.getImgs());
            courseDto.setBook(buildCourseBookDto(course.getBook()));
        }

        return courseDto;
    }

    private List<CoursePlaceDto> buildCoursePlaceDtos(List<PlaceDto> places, int count) {
        int index = 0;
        List<CoursePlaceDto> coursePlaceDtos = new ArrayList<CoursePlaceDto>();
        for (PlaceDto place : places) {
            CoursePlaceDto coursePlaceDto = new CoursePlaceDto();
            coursePlaceDto.setId(place.getId());
            coursePlaceDto.setName(place.getName());
            coursePlaceDto.setAddress(place.getAddress());
            coursePlaceDto.setLng(place.getLng());
            coursePlaceDto.setLat(place.getLat());

            coursePlaceDtos.add(coursePlaceDto);

            index++;
            if (index >= count) break;
        }

        return coursePlaceDtos;
    }

    private CourseBookDto buildCourseBookDto(CourseBook book) {
        if (book == null) return null;

        CourseBookDto courseBookDto = new CourseBookDto();
        courseBookDto.setImgs(book.getImgs());

        return courseBookDto;
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommend(@RequestParam(value = "city") int cityId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        long totalCount = courseService.queryRecommendCount(cityId);
        List<Course> courses = courseService.queryRecommend(cityId, start, count);
        PagedList<CourseDto> pagedCourseDtos = buildPagedCourseDtos(totalCount, start, count, courses);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    private PagedList<CourseDto> buildPagedCourseDtos(long totalCount, int start, int count, List<Course> courses) {
        List<CourseDto> courseDtos = buildCourseDtos(courses, Course.Type.BASE);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return pagedCourseDtos;
    }

    @RequestMapping(value = "/subject", method = RequestMethod.GET)
    public MomiaHttpResponse listBySubject(@RequestParam(value = "suid") int subjectId,
                                           @RequestParam(value = "min", required = false, defaultValue = "0") int minAge,
                                           @RequestParam(value = "max", required = false, defaultValue = "0") int maxAge,
                                           @RequestParam(value = "sort", required = false, defaultValue = "0") int sortTypeId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        // TODO filter and sort
        long totalCount = courseService.queryCountBySubject(subjectId);
        List<Course> courses = courseService.queryBySubject(subjectId, start, count);
        PagedList<CourseDto> pagedCourseDtos = buildPagedCourseDtos(totalCount, start, count, courses);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }
}
