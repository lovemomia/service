package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.course.dto.CourseBookDto;
import cn.momia.api.course.dto.CourseDetailDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CoursePlaceDto;
import cn.momia.api.course.dto.CourseSkuDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseDetail;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
import cn.momia.service.course.base.Teacher;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired private CourseService courseService;

    @RequestMapping(value = "/{coid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "coid") long courseId) {
        Course course = courseService.get(courseId);
        if (!course.exists()) return MomiaHttpResponse.FAILED("课程不存在");

        return MomiaHttpResponse.SUCCESS(buildCourseDto(course, Course.Type.FULL));
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
        for (CourseImage img : imgs) {
            urls.add(img.getUrl());
        }

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

    @RequestMapping(value = "/{coid}/detail", method = RequestMethod.GET)
    public MomiaHttpResponse detail(@PathVariable(value = "coid") long courseId) {
        CourseDetail courseDetail = courseService.getDetail(courseId);
        if (!courseDetail.exists()) return MomiaHttpResponse.FAILED("课程详情不存在");

        return MomiaHttpResponse.SUCCESS(buildCourseDetailDto(courseDetail));
    }

    private CourseDetailDto buildCourseDetailDto(CourseDetail detail) {
        CourseDetailDto courseDetailDto = new CourseDetailDto();
        courseDetailDto.setId(detail.getId());
        courseDetailDto.setCourseId(detail.getCourseId());
        courseDetailDto.setAbstracts(detail.getAbstracts());
        courseDetailDto.setDetail(JSON.parseArray(detail.getDetail()));

        return courseDetailDto;
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
        for (Course course : courses) {
            courseDtos.add(buildCourseDto(course, type));
        }

        return courseDtos;
    }

    @RequestMapping(value = "/{coid}/sku/week", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable(value = "coid") long courseId) {
        Date now = new Date();
        String start = DATE_FORMAT.format(now);
        String end = DATE_FORMAT.format(new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000));
        List<CourseSku> skus = courseService.querySkus(courseId, start, end);

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
        for (CourseSku sku : skus) {
            String date = DATE_FORMAT.format(sku.getStartTime());
            List<CourseSku> skusOfDay = skusMap.get(date);
            if (skusOfDay == null) {
                skusOfDay = new ArrayList<CourseSku>();
                skusMap.put(date, skusOfDay);
            }
            skusOfDay.add(sku);
        }

        List<DatedCourseSkusDto> datedCourseSkusDtos = new ArrayList<DatedCourseSkusDto>();
        for (Map.Entry<String, List<CourseSku>> entry : skusMap.entrySet()) {
            String date = entry.getKey();
            List<CourseSku> skusOfDay = entry.getValue();

            DatedCourseSkusDto datedCourseSkusDto = new DatedCourseSkusDto();
            datedCourseSkusDto.setDate(date);
            datedCourseSkusDto.setSkus(buildCourseSkuDtos(skusOfDay));

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

    @RequestMapping(value = "/{coid}/sku/month", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable(value = "coid") long courseId, @RequestParam int month) {
        String start = formatCurrentMonth(month);
        String end = formatNextMonth(month);
        List<CourseSku> skus = courseService.querySkus(courseId, start, end);

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

    @RequestMapping(value = "/{coid}/book", method = RequestMethod.GET)
    public MomiaHttpResponse book(@PathVariable(value = "coid") long courseId, @RequestParam int start, @RequestParam int count) {
        long totalCount = courseService.queryBookImgCount(courseId);
        List<String> bookImgs = courseService.queryBookImgs(courseId, start, count);
        PagedList<String> pagedBookImgs = new PagedList<String>(totalCount, start, count);
        pagedBookImgs.setList(bookImgs);

        return MomiaHttpResponse.SUCCESS(pagedBookImgs);
    }

    @RequestMapping(value = "/{coid}/teacher", method = RequestMethod.GET)
    public MomiaHttpResponse teacher(@PathVariable(value = "coid") long courseId, @RequestParam int start, @RequestParam int count) {
        long totalCount = courseService.queryTeacherCount(courseId);
        List<Teacher> teachers = courseService.queryTeachers(courseId, start, count);
        PagedList<Teacher> pagedTeachers = new PagedList<Teacher>(totalCount, start, count);
        pagedTeachers.setList(teachers);

        return MomiaHttpResponse.SUCCESS(pagedTeachers);
    }

    @RequestMapping(value = "/notfinished", method = RequestMethod.GET)
    public MomiaHttpResponse notFinished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        long totalCount = courseService.queryNotFinishedCountByUser(userId);
        List<Course> courses = courseService.queryNotFinishedByUser(userId, start, count);
        List<CourseDto> courseDtos = buildCourseDtos(courses, Course.Type.BASE);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        long totalCount = courseService.queryFinishedCountByUser(userId);
        List<Course> courses = courseService.queryFinishedByUser(userId, start, count);
        List<CourseDto> courseDtos = buildCourseDtos(courses, Course.Type.BASE);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    @RequestMapping(value = "/{coid}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favored(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(courseService.isFavored(userId, courseId));
    }

    @RequestMapping(value = "/{coid}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favor(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(courseService.favor(userId, courseId));
    }

    @RequestMapping(value = "/{coid}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavor(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(courseService.unfavor(userId, courseId));
    }
}
