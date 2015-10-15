package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.course.dto.CourseBookDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CoursePlaceDto;
import cn.momia.api.course.dto.CourseSkuDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
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
        Course course = courseService.get(id);
        CourseDto courseDto = buildCourseDto(course, Course.Type.FULL);

        return MomiaHttpResponse.SUCCESS(courseDto);
    }

    private CourseDto buildCourseDto(Course course, int type) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setCover(course.getCover());
        courseDto.setAge(course.getAge());
        courseDto.setJoined(course.getJoined());
        courseDto.setPrice(course.getPrice());
        courseDto.setScheduler(course.getScheduler());
        courseDto.setRegion(MetaUtil.getRegionName(course.getRegionId()));

        if (type == Course.Type.FULL) {
            courseDto.setGoal(course.getGoal());
            courseDto.setFlow(course.getFlow());
            courseDto.setTips(course.getTips());
            courseDto.setInstitution(course.getInstitution());
            courseDto.setImgs(extractImgUrls(course.getImgs()));
            courseDto.setBook(buildCourseBookDto(course.getBook()));
            courseDto.setPlace(buildCoursePlaceDto(course.getSkus()));
        }

        return courseDto;
    }

    private List<String> extractImgUrls(List<CourseImage> imgs) {
        List<String> urls = new ArrayList<String>();
        for (CourseImage img : imgs) urls.add(img.getUrl());

        return urls;
    }

    private CourseBookDto buildCourseBookDto(CourseBook book) {
        if (book == null) return null;

        CourseBookDto courseBookDto = new CourseBookDto();
        courseBookDto.setImgs(book.getImgs());

        return courseBookDto;
    }

    private CoursePlaceDto buildCoursePlaceDto(List<CourseSku> skus) {
        // TODO
        return null;
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public MomiaHttpResponse query(@RequestParam(value = "suid") int subjectId,
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

    private List<CourseDto> buildCourseDtos(List<Course> courses, int type) {
        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for (Course course : courses) courseDtos.add(buildCourseDto(course, type));

        return courseDtos;
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
        Set<String> hasMore = new HashSet<String>();
        for (CourseSku sku : skus) {
            String date = DATE_FORMAT.format(sku.getStartTime());
            List<CourseSku> skusOfDay = skusMap.get(date);
            if (skusOfDay == null) {
                skusOfDay = new ArrayList<CourseSku>();
                skusMap.put(date, skusOfDay);
            }

            if (skusOfDay.size() < 2) skusOfDay.add(sku);
            else hasMore.add(date);
        }

        List<DatedCourseSkusDto> datedCourseSkusDtos = new ArrayList<DatedCourseSkusDto>();
        for (Map.Entry<String, List<CourseSku>> entry : skusMap.entrySet()) {
            String date = entry.getKey();
            List<CourseSku> skusOfDay = entry.getValue();

            DatedCourseSkusDto datedCourseSkusDto = new DatedCourseSkusDto();
            datedCourseSkusDto.setDate(date);
            datedCourseSkusDto.setSkus(buildCourseSkuDtos(skusOfDay));
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

    private List<CourseSkuDto> buildCourseSkuDtos(List<CourseSku> skus) {
        List<CourseSkuDto> courseSkuDtos = new ArrayList<CourseSkuDto>();
        for (CourseSku sku : skus) {
            CourseSkuDto courseSkuDto = new CourseSkuDto();
            courseSkuDto.setId(sku.getId());
            courseSkuDto.setPlace(buildCoursePlaceDto(sku.getPlace()));
            courseSkuDto.setStock(sku.getUnlockedStock());

            courseSkuDtos.add(courseSkuDto);
        }

        return courseSkuDtos;
    }

    private CoursePlaceDto buildCoursePlaceDto(CourseSkuPlace place) {
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
        for (CourseSku sku : skus) {
            if (!excludeIds.contains(sku.getId())) {
                includedSkus.add(sku);
            }
        }

        return MomiaHttpResponse.SUCCESS(buildCourseSkuDtos(includedSkus));
    }

    @RequestMapping(value = "/notfinished", method = RequestMethod.GET)
    public MomiaHttpResponse notFinished(@RequestParam(value = "uid") long userId) {
//        long totalCount = courseService.queryNotFinishedSkuCountByUser(userId);
//        List<CourseSku> skus = courseService.queryNotFinishedSkuByUser(userId);
//
//        Set<Long> courseIds = new HashSet<Long>();
//        Map<Long, CourseSku> courseSkuMap = new HashMap<Long, CourseSku>();
//        for (CourseSku sku : skus) {
//            courseIds.add(sku.getCourseId());
//            courseSkuMap.put(sku.getCourseId(), sku);
//        }
//        List<Course> courses = courseService.list(courseIds);
//        List<CourseDto> courseDtos = buildCourseDtos(courses, Course.Type.BASE);
//
//        PagedList<Course> pagedCourseDtos = new PagedList<Course>(totalCount, start, count)
//        // TODO
//        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
        return null;
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId) {
        long totalCount = courseService.queryFinishedSkuCountByUser(userId);
        List<CourseSku> skus = courseService.queryFinishedSkuByUser(userId);

        // TODO
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favored(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        return MomiaHttpResponse.SUCCESS(courseService.isFavored(userId, id));
    }

    @RequestMapping(value = "/{id}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favor(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        return MomiaHttpResponse.SUCCESS(courseService.favor(userId, id));
    }

    @RequestMapping(value = "/{id}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavor(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        return MomiaHttpResponse.SUCCESS(courseService.unfavor(userId, id));
    }
}
