package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.subject.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CourseController {
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private PoiServiceApi poiServiceApi;

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommend(@RequestParam(value = "city") int cityId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        long totalCount = courseService.queryRecommendCount(cityId);
        List<Course> courses = courseService.queryRecommend(cityId, start, count);
        PagedList<CourseDto> pagedCourseDtos = buildPagedCourseDtos(start, count, totalCount, courses);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    private PagedList<CourseDto> buildPagedCourseDtos(@RequestParam int start, @RequestParam int count, long totalCount, List<Course> courses) {
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
        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for (Course course : courses) {
            courseDtos.add(buildCourseDto(course, subjectNames, placesMap));
        }
        pagedCourseDtos.setList(courseDtos);

        return pagedCourseDtos;
    }

    private CourseDto buildCourseDto(Course course, Map<Long, String> subjectNames, Map<Integer, PlaceDto> places) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setCover(course.getCover());
        courseDto.setAge(course.getAge());
        courseDto.setScheduler(course.getScheduler());

        int regionId = 0;
        List<Integer> placeIds = course.getPlaceIds();
        if (placeIds.size() > 1) {
            regionId = RegionDto.MULTI_REGION_ID;
        } else if (!placeIds.isEmpty()) {
            int placeId = placeIds.get(0);
            PlaceDto place = places.get(placeId);
            if (place != null) regionId = place.getRegionId();
        }
        courseDto.setRegion(MetaUtil.getRegionName(regionId));

        String subject = subjectNames.get(course.getId());
        courseDto.setSubject(subject == null ? "" : subject);

        courseDto.setPrice(course.getPrice());
        courseDto.setJoined(course.getJoined());

        return courseDto;
    }

    @RequestMapping(value = "/subject", method = RequestMethod.GET)
    public MomiaHttpResponse listBySubject(@RequestParam(value = "suid") int subjectId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        long totalCount = courseService.queryCountBySubject(subjectId);
        List<Course> courses = courseService.queryBySubject(subjectId, start, count);
        PagedList<CourseDto> pagedCourseDtos = buildPagedCourseDtos(start, count, totalCount, courses);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }
}
