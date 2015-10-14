package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.course.dto.CourseBookDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CoursePlaceDto;
import cn.momia.api.course.dto.CourseSkuDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.subject.SubjectService;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private PoiServiceApi poiServiceApi;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable long id) {
        List<Course> courses = new ArrayList<Course>();
        courses.add(courseService.get(id));
        List<CourseDto> courseDtos = buildCourseDtos(courses, null, Course.Type.FULL);

        return MomiaHttpResponse.SUCCESS(courseDtos.get(0));
    }

    private List<CourseDto> buildCourseDtos(List<Course> courses, Map<Long, CourseSku> skus, int type) {
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
            courseDto.setTips(course.getTips());
            courseDto.setInstitution(course.getInstitution());
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

    private PagedList<CourseDto> buildPagedCourseDtos(long totalCount, int start, int count, List<Course> courses) {
        List<CourseDto> courseDtos = buildCourseDtos(courses, Course.Type.BASE);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return pagedCourseDtos;
    }

    @RequestMapping(value = "/{id}/sku/week", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable long id) {
        Date now = new Date();
        String start = DATE_FORMAT.format(now);
        String end = DATE_FORMAT.format(new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000));
        List<CourseSku> skus = courseService.querySkus(id, start, end);

        return MomiaHttpResponse.SUCCESS(buildDatedCourseSkusDtos(filterUnavaliableSkus(skus)));
    }

    private List<CourseSku> filterUnavaliableSkus(List<CourseSku> skus) {
        List<CourseSku> avaliableSkus = new ArrayList<CourseSku>();
        Date now = new Date();
        for (CourseSku sku : skus) {
            if (sku.isAvaliable(now)) avaliableSkus.add(sku);
        }

        return avaliableSkus;
    }

    private List<DatedCourseSkusDto> buildDatedCourseSkusDtos(List<CourseSku> skus) {
        Map<String, List<CourseSku>> skusMap = new HashMap<String, List<CourseSku>>();
        List<Integer> placeIds = new ArrayList<Integer>();
        Set<String> hasMore = new HashSet<String>();
        for (CourseSku sku : skus) {
            String date = DATE_FORMAT.format(sku.getStartTime());
            List<CourseSku> skusOfDay = skusMap.get(date);
            if (skusOfDay == null) {
                skusOfDay = new ArrayList<CourseSku>();
                skusMap.put(date, skusOfDay);
            }

            if (skusOfDay.size() < 2) {
                skusOfDay.add(sku);
                placeIds.add(sku.getPlaceId());
            } else {
                hasMore.add(date);
            }
        }

        List<PlaceDto> places = poiServiceApi.list(placeIds);
        Map<Integer, PlaceDto> placesMap = new HashMap<Integer, PlaceDto>();
        for (PlaceDto place : places) placesMap.put(place.getId(), place);

        List<DatedCourseSkusDto> datedCourseSkusDtos = new ArrayList<DatedCourseSkusDto>();
        for (Map.Entry<String, List<CourseSku>> entry : skusMap.entrySet()) {
            String date = entry.getKey();
            List<CourseSku> skusOfDay = entry.getValue();

            DatedCourseSkusDto datedCourseSkusDto = new DatedCourseSkusDto();
            datedCourseSkusDto.setDate(date);
            datedCourseSkusDto.setSkus(buildCourseSkuDtos(skusOfDay, placesMap));
            if (hasMore.contains(date)) datedCourseSkusDto.setMore(true);

            datedCourseSkusDtos.add(datedCourseSkusDto);
        }

        Collections.sort(datedCourseSkusDtos, new Comparator<DatedCourseSkusDto>() {
            @Override
            public int compare(DatedCourseSkusDto o1, DatedCourseSkusDto o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return datedCourseSkusDtos;
    }

    private List<CourseSkuDto> buildCourseSkuDtos(List<CourseSku> skus, Map<Integer, PlaceDto> places) {
        List<CourseSkuDto> courseSkuDtos = new ArrayList<CourseSkuDto>();
        for (CourseSku sku : skus) {
            PlaceDto place = places.get(sku.getPlaceId());
            if (place == null) continue;

            CourseSkuDto courseSkuDto = new CourseSkuDto();
            courseSkuDto.setId(sku.getId());
            courseSkuDto.setPlace(buildCoursePlaceDto(place));
            courseSkuDto.setStock(sku.getUnlockedStock());

            courseSkuDtos.add(courseSkuDto);
        }

        return courseSkuDtos;
    }

    private CoursePlaceDto buildCoursePlaceDto(PlaceDto place) {
        CoursePlaceDto coursePlaceDto = new CoursePlaceDto();
        coursePlaceDto.setId(place.getId());
        coursePlaceDto.setName(place.getName());
        coursePlaceDto.setAddress(place.getAddress());
        coursePlaceDto.setLng(place.getLng());
        coursePlaceDto.setLat(place.getLat());

        return coursePlaceDto;
    }

    @RequestMapping(value = "/{id}/sku/month", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable long id, @RequestParam int month) {
        String start = formatCurrentMonth(month);
        String end = formatNextMonth(month);
        List<CourseSku> skus = courseService.querySkus(id, start, end);

        return MomiaHttpResponse.SUCCESS(buildDatedCourseSkusDtos(filterUnavaliableSkus(skus)));
    }

    private String formatCurrentMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if (month < currentMonth) return String.format("%d-%02d", currentYear + 1, month);
        return String.format("%d-%02d", currentYear, month);
    }

    private String formatNextMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int nextMonth = month + 1;
        nextMonth = nextMonth > 12 ? nextMonth - 12 : nextMonth;

        if (month < currentMonth || nextMonth < month) return String.format("%d-%02d", currentYear + 1, nextMonth);
        return String.format("%d-%02d", currentYear, nextMonth);
    }

    @RequestMapping(value = "/{id}/sku/more", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable long id, @RequestParam String date, @RequestParam String excludes) throws ParseException {
        String start = date;
        String end = DATE_FORMAT.format(new Date(DATE_FORMAT.parse(date).getTime() + 24 * 60 * 60 * 1000));

        List<CourseSku> skus = filterUnavaliableSkus(courseService.querySkus(id, start, end));

        Set<Long> excludeIds = new HashSet<Long>();
        for (String excludeId : Splitter.on(",").trimResults().omitEmptyStrings().split(excludes)) {
            excludeIds.add(Long.valueOf(excludeId));
        }

        List<CourseSku> includedSkus = new ArrayList<CourseSku>();
        Set<Integer> placeIds = new HashSet<Integer>();
        for (CourseSku sku : skus) {
            if (!excludeIds.contains(sku.getId())) {
                includedSkus.add(sku);
                placeIds.add(sku.getPlaceId());
            }
        }

        List<PlaceDto> places = poiServiceApi.list(placeIds);
        Map<Integer, PlaceDto> placesMap = new HashMap<Integer, PlaceDto>();
        for (PlaceDto place : places) placesMap.put(place.getId(), place);

        return MomiaHttpResponse.SUCCESS(buildCourseSkuDtos(includedSkus, placesMap));
    }

    @RequestMapping(value = "/notfinished", method = RequestMethod.GET)
    public MomiaHttpResponse notFinished(@RequestParam(value = "uid") long userId) {
        long totalCount = courseService.queryNotFinishedSkuCountByUser(userId);
        List<CourseSku> skus = courseService.queryNotFinishedSkuByUser(userId);

        Set<Long> courseIds = new HashSet<Long>();
        Map<Long, CourseSku> courseSkuMap = new HashMap<Long, CourseSku>();
        for (CourseSku sku : skus) {
            courseIds.add(sku.getCourseId());
            courseSkuMap.put(sku.getCourseId(), sku);
        }
        List<Course> courses = courseService.list(courseIds);
        List<CourseDto> courseDtos = buildCourseDtos(courses, courseSkuMap, Course.Type.BASE);

        PagedList<Course> pagedCourseDtos = new PagedList<Course>(totalCount, start, count)
        // TODO
        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId) {
        long totalCount = courseService.queryFinishedSkuCountByUser(userId);
        List<CourseSku> skus = courseService.queryFinishedSkuByUser(userId);

        // TODO
        return MomiaHttpResponse.SUCCESS;
    }
}
