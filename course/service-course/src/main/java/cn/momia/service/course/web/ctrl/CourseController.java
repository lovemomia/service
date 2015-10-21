package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.course.dto.CourseBookDto;
import cn.momia.api.course.dto.CourseDetailDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CoursePlaceDto;
import cn.momia.api.course.dto.CourseSkuDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.PoiUtil;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseDetail;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
import cn.momia.service.course.base.Institution;
import cn.momia.service.course.base.Teacher;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderPackage;
import cn.momia.service.course.subject.order.OrderService;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Splitter POS_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    @Autowired private CourseService courseService;
    @Autowired private OrderService orderService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/{coid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "coid") long courseId, @RequestParam String pos) {
        Course course = courseService.get(courseId);
        if (!course.exists()) return MomiaHttpResponse.FAILED("课程不存在");

        return MomiaHttpResponse.SUCCESS(buildFullCourseDto(course, pos));
    }

    private CourseDto buildFullCourseDto(Course course, String pos) {
        CourseDto courseDto = buildBaseCourseDto(course);
        courseDto.setGoal(course.getGoal());
        courseDto.setFlow(course.getFlow());
        courseDto.setTips(course.getTips());
        courseDto.setInstitution(course.getInstitution());
        courseDto.setImgs(extractImgUrls(course.getImgs()));
        courseDto.setBook(buildCourseBookDto(course.getBook()));
        courseDto.setPlace(buildCoursePlaceDto(course.getSkus(), pos));

        return courseDto;
    }

    private CourseDto buildBaseCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setCover(course.getCover());
        courseDto.setAge(course.getAge());
        courseDto.setJoined(course.getJoined());
        courseDto.setPrice(course.getPrice());
        courseDto.setScheduler(course.getScheduler());
        courseDto.setRegion(MetaUtil.getRegionName(course.getRegionId()));

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
        if (book == null || book.getImgs().isEmpty()) return null;

        CourseBookDto courseBookDto = new CourseBookDto();
        courseBookDto.setImgs(book.getImgs());

        return courseBookDto;
    }

    private CoursePlaceDto buildCoursePlaceDto(List<CourseSku> skus, String pos) {
        List<CourseSkuPlace> places = new ArrayList<CourseSkuPlace>();
        Map<Integer, CourseSkuPlace> placesMap = new HashMap<Integer, CourseSkuPlace>();
        Map<Integer, List<CourseSku>> skusGroupedByPlace = new HashMap<Integer, List<CourseSku>>();
        for (CourseSku sku : skus) {
            CourseSkuPlace place = sku.getPlace();
            if (place == null) continue;

            places.add(place);
            placesMap.put(place.getId(), place);
            List<CourseSku> skuList = skusGroupedByPlace.get(place.getId());
            if (skuList == null) {
                skuList = new ArrayList<CourseSku>();
                skusGroupedByPlace.put(place.getId(), skuList);
            }
            skuList.add(sku);
        }

        if (!StringUtils.isBlank(pos)) {
            List<Double> lngLat = new ArrayList<Double>();
            for (String val : POS_SPLITTER.split(pos)) {
                lngLat.add(Double.valueOf(val));
            }

            if (lngLat.size() == 2) {
                final double lng = lngLat.get(0);
                final double lat = lngLat.get(1);
                Collections.sort(places, new Comparator<CourseSkuPlace>() {
                    @Override
                    public int compare(CourseSkuPlace place1, CourseSkuPlace place2) {
                        boolean place1HasNoPosition = place1.hasNoPosition();
                        boolean place2HasNoPosition = place2.hasNoPosition();

                        if (place1HasNoPosition && place2HasNoPosition) return -1;
                        if (place1HasNoPosition) return 1;
                        if (place2HasNoPosition) return -1;

                        int distance1 = PoiUtil.distance(place1.getLng(), place1.getLat(), lng, lat);
                        int distance2 = PoiUtil.distance(place2.getLng(), place2.getLat(), lng, lat);

                        return distance1 - distance2;
                    }
                });
            }
        }

        CourseSkuPlace place = places.get(0);

        CoursePlaceDto coursePlaceDto = new CoursePlaceDto();
        coursePlaceDto.setId(place.getId());
        coursePlaceDto.setName(place.getName());
        coursePlaceDto.setAddress(place.getAddress());
        coursePlaceDto.setLng(place.getLng());
        coursePlaceDto.setLat(place.getLat());
        List<CourseSku> skusOfPlace = skusGroupedByPlace.get(place.getId());
        coursePlaceDto.setScheduler(buildPlaceScheduler(skusOfPlace));

        return coursePlaceDto;
    }

    private String buildPlaceScheduler(List<CourseSku> skus) {
        Date now = new Date();
        List<Date> times = new ArrayList<Date>();
        for (CourseSku sku : skus) {
            if (sku.isAvaliable(now)) {
                times.add(sku.getStartTime());
                times.add(sku.getEndTime());
            }
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
        List<CourseDto> courseDtos = buildCourseDtos(courses);
        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return pagedCourseDtos;
    }

    private List<CourseDto> buildCourseDtos(List<Course> courses) {
        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for (Course course : courses) {
            courseDtos.add(buildBaseCourseDto(course));
        }

        return courseDtos;
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

    @RequestMapping(value = "/{coid}/institution", method = RequestMethod.GET)
    public MomiaHttpResponse institution(@PathVariable(value = "coid") long courseId) {
        Institution institution = courseService.getInstitution(courseId);
        if (!institution.exists()) return MomiaHttpResponse.FAILED("机构不存在");

        return MomiaHttpResponse.SUCCESS(institution);
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

    @RequestMapping(value = "/notfinished", method = RequestMethod.GET)
    public MomiaHttpResponse notFinished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        long totalCount = courseService.queryNotFinishedCountByUser(userId);
        List<Course> courses = courseService.queryNotFinishedByUser(userId, start, count);
        List<CourseDto> courseDtos = buildCourseDtos(courses);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        long totalCount = courseService.queryFinishedCountByUser(userId);
        List<Course> courses = courseService.queryFinishedByUser(userId, start, count);
        List<CourseDto> courseDtos = buildCourseDtos(courses);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    @RequestMapping(value = "/booking", method = RequestMethod.POST)
    public MomiaHttpResponse booking(@RequestParam String utoken,
                                     @RequestParam(value = "pkgid") long packageId,
                                     @RequestParam(value = "sid") long skuId) {
        OrderPackage orderPackage = orderService.getOrderPackage(packageId);
        if (!orderPackage.exists()) return MomiaHttpResponse.FAILED("预约失败，无效的课程包");

        CourseSku sku = courseService.getSku(skuId);
        if (!sku.exists() || !sku.isAvaliable(new Date())) return MomiaHttpResponse.FAILED("预约失败，无效的课程地点");

        Order order = orderService.get(orderPackage.getOrderId());
        UserDto user = userServiceApi.get(utoken);
        if (!order.exists() || !order.isPayed() || order.getUserId() != user.getId()) return MomiaHttpResponse.FAILED("预约失败，无效的订单");

        if (!courseService.lockSku(skuId)) return MomiaHttpResponse.FAILED("库存不足");
        LOGGER.info("course sku locked: {}/{}/{}", new Object[] { user, packageId, skuId });

        long bookingId = 0;
        try {
            if (orderService.decreaseBookableCount(packageId)) {
                bookingId = courseService.booking(user.getId(), order.getId(), packageId, sku);
                return MomiaHttpResponse.SUCCESS(true);
            } else {
                return MomiaHttpResponse.FAILED("本课程包的课程已经约满");
            }
        } catch (Exception e) {
            LOGGER.error("fail to booking course, {}/{}/{}", new Object[] { user.getId(), packageId, skuId, e });
        } finally {
            // TODO 需要告警
            if (bookingId <= 0 && !courseService.unlockSku(skuId)) LOGGER.error("fail to unlock course sku, skuId: {}", skuId);
        }

        return MomiaHttpResponse.FAILED("下单失败");
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
