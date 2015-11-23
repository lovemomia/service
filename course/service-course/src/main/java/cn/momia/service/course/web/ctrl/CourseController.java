package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.course.dto.BookedCourseDto;
import cn.momia.api.course.dto.CourseBookDto;
import cn.momia.api.course.dto.CourseCommentDto;
import cn.momia.api.course.dto.CourseDetailDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CoursePlaceDto;
import cn.momia.api.course.dto.CourseSkuDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.api.course.dto.FavoriteDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.ChildDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.PoiUtil;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.BookedCourse;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseComment;
import cn.momia.service.course.base.CourseDetail;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
import cn.momia.service.course.base.Institution;
import cn.momia.service.course.base.Teacher;
import cn.momia.service.course.favorite.Favorite;
import cn.momia.service.course.favorite.FavoriteService;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderPackage;
import cn.momia.service.course.order.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("MM月dd日");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final Splitter POS_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    @Autowired private CourseService courseService;
    @Autowired private OrderService orderService;
    @Autowired private FavoriteService favoriteService;

    @Autowired private UserServiceApi userServiceApi;
    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommend(@RequestParam(value = "city") long cityId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryRecommendCount(cityId);
        List<Course> courses = courseService.queryRecomend(cityId, start, count);

        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for (Course course : courses) {
            courseDtos.add(buildBaseCourseDto(course));
        }

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(courseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    private CourseDto buildBaseCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        setFieldValue(courseDto, course);

        return courseDto;
    }

    private void setFieldValue(CourseDto courseDto, Course course) {
        courseDto.setId(course.getId());
        courseDto.setSubjectId(course.getSubjectId());
        courseDto.setTitle(course.getTitle());
        courseDto.setCover(course.getCover());
        courseDto.setAge(course.getAge());
        courseDto.setInsurance(course.getInsurance() > 0);
        courseDto.setJoined(course.getJoined());
        courseDto.setPrice(course.getPrice());
        courseDto.setScheduler(course.getScheduler());
        courseDto.setRegion(MetaUtil.getRegionName(course.getRegionId()));
        courseDto.setSubject(course.getSubject());
    }

    @RequestMapping(value = "/{coid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "coid") long courseId,
                                 @RequestParam String pos,
                                 @RequestParam(required = false, defaultValue = "" + Course.Type.BASE) int type) {
        Course course = courseService.get(courseId);
        if (!course.exists()) return MomiaHttpResponse.FAILED("课程不存在");

        return MomiaHttpResponse.SUCCESS(type == Course.Type.FULL ? buildFullCourseDto(course, pos) : buildBaseCourseDto(course));
    }

    private CourseDto buildFullCourseDto(Course course, String pos) {
        CourseDto courseDto = buildBaseCourseDto(course);
        courseDto.setGoal(course.getGoal());
        courseDto.setFlow(course.getFlow());
        courseDto.setTips(course.getTips());
        courseDto.setNotice(course.getNotice());
        courseDto.setInstitution(course.getInstitution());
        courseDto.setImgs(extractImgUrls(course.getImgs()));
        courseDto.setBook(buildCourseBookDto(course.getBook()));
        courseDto.setPlace(buildCoursePlaceDto(course.getSkus(), pos));

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
        courseBookDto.setImgs(book.getImgs().subList(0, Math.min(10, book.getImgs().size())));

        return courseBookDto;
    }

    private CoursePlaceDto buildCoursePlaceDto(List<CourseSku> skus, String pos) {
        List<CourseSkuPlace> places = new ArrayList<CourseSkuPlace>();
        Map<Integer, CourseSkuPlace> placesMap = new HashMap<Integer, CourseSkuPlace>();
        Map<Integer, List<CourseSku>> skusGroupedByPlace = new HashMap<Integer, List<CourseSku>>();
        Date now = new Date();
        for (CourseSku sku : skus) {
            if (!sku.isAvaliable(now)) continue;

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

        if (places.isEmpty()) return null;

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
        CourseSku earliestSku = null;
        Date now = new Date();
        for (CourseSku sku : skus) {
            if (sku.isAvaliable(now)) {
                if (earliestSku == null) {
                    earliestSku = sku;
                } else {
                    if (sku.getStartTime().before(earliestSku.getStartTime())) earliestSku = sku;
                }
            }
        }

        return format(earliestSku);
    }

    private String format(CourseSku sku) {
        if (sku == null) return "";

        Date start = sku.getStartTime();
        Date end = sku.getEndTime();

        if (TimeUtil.isSameDay(start, end)) {
            return MONTH_DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start) +  " " + TIME_FORMAT.format(start) + "-" + TIME_FORMAT.format(end);
        } else {
            return MONTH_DATE_FORMAT.format(start) + " " + TIME_FORMAT.format(start) + "-" + MONTH_DATE_FORMAT.format(end) + " " + TIME_FORMAT.format(end);
        }
    }

    @RequestMapping(value = "/{coid}/buyable", method = RequestMethod.GET)
    public MomiaHttpResponse buyable(@PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(courseService.isRecommended(courseId));
    }

    @RequestMapping(value = "/finished/list", method = RequestMethod.GET)
    public MomiaHttpResponse listFinished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = userId <= 0 ? courseService.listFinishedCount() : courseService.listFinishedCount(userId);
        List<Course> courses = userId <= 0 ? courseService.listFinished(start, count) : courseService.listFinished(userId, start, count);
        PagedList<CourseDto> pagedCourseDtos = buildPagedCourseDtos(courses, totalCount, start, count);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    private PagedList<CourseDto> buildPagedCourseDtos(List<Course> courses, long totalCount, int start, int count) {
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

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public MomiaHttpResponse query(@RequestParam(value = "suid") long subjectId,
                                   @RequestParam(value = "pid", required = false, defaultValue = "0") long packageId,
                                   @RequestParam(value = "min", required = false, defaultValue = "1") int minAge,
                                   @RequestParam(value = "max", required = false, defaultValue = "100") int maxAge,
                                   @RequestParam(value = "sort", required = false, defaultValue = "0") int sortTypeId,
                                   @RequestParam int start,
                                   @RequestParam int count) {
        List<Long> courseIds = courseService.queryBookedCourseIds(packageId);
        long totalCount = courseService.queryCountBySubject(subjectId, courseIds, minAge, maxAge);
        List<Course> courses = courseService.queryBySubject(subjectId, start, count, courseIds, minAge, maxAge, sortTypeId);
        PagedList<CourseDto> pagedCourseDtos = buildPagedCourseDtos(courses, totalCount, start, count);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
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
        courseDetailDto.setDetail(detail.getDetail());

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
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryBookImgCount(courseId);
        List<String> bookImgs = courseService.queryBookImgs(courseId, start, count);
        PagedList<String> pagedBookImgs = new PagedList<String>(totalCount, start, count);
        pagedBookImgs.setList(bookImgs);

        return MomiaHttpResponse.SUCCESS(pagedBookImgs);
    }

    @RequestMapping(value = "/{coid}/teacher", method = RequestMethod.GET)
    public MomiaHttpResponse teacher(@PathVariable(value = "coid") long courseId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

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
            courseSkuDto.setTime(formatSkuTime(sku));
            courseSkuDto.setPlace(buildCoursePlaceDto(sku.getPlace()));
            courseSkuDto.setStock(sku.getUnlockedStock());

            courseSkuDtos.add(courseSkuDto);
        }

        return courseSkuDtos;
    }

    private String formatSkuTime(CourseSku sku) {
        if (sku == null) return "";

        Date start = sku.getStartTime();
        Date end = sku.getEndTime();

        if (TimeUtil.isSameDay(start, end)) {
            return TimeUtil.getAmPm(start) + " " + TIME_FORMAT.format(start) + "-" + TimeUtil.getAmPm(end) + " " + TIME_FORMAT.format(end);
        } else {
            return MONTH_DATE_FORMAT.format(start) + " " + TimeUtil.getAmPm(start) + " " + TIME_FORMAT.format(start) + "-" + MONTH_DATE_FORMAT.format(end) + " " + TimeUtil.getAmPm(end) + " " + TIME_FORMAT.format(end);
        }
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
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryNotFinishedCountByUser(userId);
        List<BookedCourse> bookedCourses = courseService.queryNotFinishedByUser(userId, start, count);
        List<BookedCourseDto> bookedCourseDtos = buildBookedCourseDtos(userId, bookedCourses);

        PagedList<BookedCourseDto> pagedCourseDtos = new PagedList<BookedCourseDto>(totalCount, start, count);
        pagedCourseDtos.setList(bookedCourseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    private List<BookedCourseDto> buildBookedCourseDtos(long userId, List<BookedCourse> bookedCourses) {
        Set<Long> bookingIds = new HashSet<Long>();
        Set<Long> courseIds = new HashSet<Long>();
        for (BookedCourse bookedCourse : bookedCourses) {
            bookingIds.add(bookedCourse.getId());
            courseIds.add(bookedCourse.getCourseId());
        }

        Set<Long> commentBookingIds = Sets.newHashSet(courseService.queryCommentedBookingIds(userId, bookingIds));
        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<BookedCourseDto> bookedCourseDtos = new ArrayList<BookedCourseDto>();
        for (BookedCourse bookedCourse : bookedCourses) {
            Course course = coursesMap.get(bookedCourse.getCourseId());
            if (course == null) continue;
            List<CourseSku> skus = course.getSkus();
            if (skus.isEmpty()) continue;

            BookedCourseDto bookedCourseDto = new BookedCourseDto();
            bookedCourseDto.setBookingId(bookedCourse.getId());
            if (commentBookingIds.contains(bookedCourse.getId())) bookedCourseDto.setCommented(true);
            setFieldValue(bookedCourseDto, course);
            bookedCourseDto.setScheduler(course.getScheduler(bookedCourse.getCourseSkuId()));

            CourseSkuPlace place = course.getPlace(bookedCourse.getCourseSkuId());
            if (place == null) continue;
            bookedCourseDto.setPlace(buildCoursePlaceDto(place));

            bookedCourseDtos.add(bookedCourseDto);
        }

        return bookedCourseDtos;
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryFinishedCountByUser(userId);
        List<BookedCourse> bookedCourses = courseService.queryFinishedByUser(userId, start, count);
        List<BookedCourseDto> bookedCourseDtos = buildBookedCourseDtos(userId, bookedCourses);

        PagedList<BookedCourseDto> pagedBookedCourseDtos = new PagedList<BookedCourseDto>(totalCount, start, count);
        pagedBookedCourseDtos.setList(bookedCourseDtos);

        return MomiaHttpResponse.SUCCESS(pagedBookedCourseDtos);
    }

    @RequestMapping(value = "/{coid}/joined", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(courseService.joined(userId, courseId));
    }

    @RequestMapping(value = "/booking", method = RequestMethod.POST)
    public MomiaHttpResponse booking(@RequestParam String utoken,
                                     @RequestParam(value = "pid") long packageId,
                                     @RequestParam(value = "sid") long skuId) {
        OrderPackage orderPackage = orderService.getOrderPackage(packageId);
        if (!orderPackage.exists()) return MomiaHttpResponse.FAILED("预约失败，无效的课程包");

        CourseSku sku = courseService.getSku(skuId);
        if (!sku.exists() || !sku.isAvaliable(new Date())) return MomiaHttpResponse.FAILED("预约失败，无效的课程地点");

        Order order = orderService.get(orderPackage.getOrderId());
        UserDto user = userServiceApi.get(utoken);
        if (!order.exists() || !order.isPayed() || order.getUserId() != user.getId()) return MomiaHttpResponse.FAILED("预约失败，无效的订单");

        if (courseService.booked(packageId, sku.getCourseId())) return MomiaHttpResponse.FAILED("一门课程在一个课程包内只能约一次");
        if (!courseService.matched(order.getSubjectId(), sku.getCourseId())) return MomiaHttpResponse.FAILED("课程不匹配");

        if (!courseService.lockSku(skuId)) return MomiaHttpResponse.FAILED("库存不足");
        LOGGER.info("course sku locked: {}/{}/{}", new Object[] { user, packageId, skuId });

        long bookingId = 0;
        try {
            if (orderService.decreaseBookableCount(packageId)) {
                bookingId = courseService.booking(user.getId(), order.getId(), packageId, sku);
                if (bookingId > 0) {
                    courseService.increaseJoined(sku.getCourseId(), sku.getJoinCount());
                }
            } else {
                return MomiaHttpResponse.FAILED("本课程包的课程已经约满");
            }
        } catch (Exception e) {
            LOGGER.error("exception when booking course, {}/{}/{}", new Object[] { user.getId(), packageId, skuId, e });
        } finally {
            // TODO 需要告警
            if (bookingId <= 0 && !courseService.unlockSku(skuId)) LOGGER.error("fail to unlock course sku, skuId: {}", skuId);
        }

        return MomiaHttpResponse.SUCCESS(bookingId > 0);
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public MomiaHttpResponse cancel(@RequestParam String utoken, @RequestParam(value = "bid") long bookingId) {
        UserDto user = userServiceApi.get(utoken);
        BookedCourse bookedCourse = courseService.getBookedCourse(bookingId);
        if (!bookedCourse.exists()) return MomiaHttpResponse.FAILED("取消预约的课程不存在");
        if (!bookedCourse.canCancel()) return MomiaHttpResponse.FAILED("课程开始前2天内无法取消课程");
        if (!courseService.cancel(user.getId(), bookingId)) return MomiaHttpResponse.FAILED("取消选课失败");

        try {
            if (!orderService.increaseBookableCount(bookedCourse.getPackageId())) LOGGER.error("fail to increase bookable count, booking id: {}", bookingId);
            if (!courseService.unlockSku(bookedCourse.getCourseSkuId())) LOGGER.error("fail to unlock course sku, booking id: {}", bookingId);
            CourseSku sku = courseService.getSku(bookedCourse.getCourseSkuId());
            courseService.decreaseJoined(bookedCourse.getCourseId(), sku.getJoinCount());
        } catch (Exception e) {
            LOGGER.error("error when cancel booked course, {}", bookingId, e);
        }

        return MomiaHttpResponse.SUCCESS(true);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse comment(@RequestBody CourseComment comment) {
        if (comment.isInvalid()) return MomiaHttpResponse.BAD_REQUEST;
        if (StringUtils.isBlank(comment.getContent())) return MomiaHttpResponse.FAILED("评论内容不能为空");

        if (!courseService.finished(comment.getUserId(), comment.getBookingId(), comment.getCourseId())) return MomiaHttpResponse.FAILED("你还没有上过这门课，无法评论");
        if (courseService.isCommented(comment.getUserId(), comment.getBookingId())) return MomiaHttpResponse.FAILED("一堂课只能发表一次评论");
        if (comment.getImgs() != null && comment.getImgs().size() > 9) return MomiaHttpResponse.FAILED("上传的图片过多，1条评论最多上传9张图片");

        return MomiaHttpResponse.SUCCESS(courseService.comment(comment));
    }

    @RequestMapping(value = "/{coid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listComment(@PathVariable(value = "coid") long courseId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryCommentCountByCourse(courseId);
        List<CourseComment> comments = courseService.queryCommentsByCourse(courseId, start, count);

        Set<Long> userIds = new HashSet<Long>();
        for (CourseComment comment : comments) {
            userIds.add(comment.getUserId());
        }

        List<UserDto> users = userServiceApi.list(userIds, UserDto.Type.FULL);
        Map<Long, UserDto> usersMap = new HashMap<Long, UserDto>();
        for (UserDto user : users) {
            usersMap.put(user.getId(), user);
        }

        List<CourseCommentDto> commentDtos = new ArrayList<CourseCommentDto>();
        for (CourseComment comment : comments) {
            UserDto user = usersMap.get(comment.getUserId());
            if (user == null) continue;
            commentDtos.add(buildCourseCommentDto(comment, user));
        }

        PagedList<CourseCommentDto> pagedCommentDtos = new PagedList<CourseCommentDto>(totalCount, start, count);
        pagedCommentDtos.setList(commentDtos);

        return MomiaHttpResponse.SUCCESS(pagedCommentDtos);
    }

    private CourseCommentDto buildCourseCommentDto(CourseComment comment, UserDto user) {
        CourseCommentDto courseCommentDto = new CourseCommentDto();
        courseCommentDto.setId(comment.getId());
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

    @RequestMapping(value = "/{coid}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favored(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.isFavored(userId, Favorite.Type.COURSE, courseId));
    }

    @RequestMapping(value = "/{coid}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favor(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.favor(userId, Favorite.Type.COURSE, courseId));
    }

    @RequestMapping(value = "/{coid}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavor(@RequestParam(value = "uid") long userId, @PathVariable(value = "coid") long courseId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.unfavor(userId, Favorite.Type.COURSE, courseId));
    }

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse favorite(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = favoriteService.queryFavoriteCount(userId, Favorite.Type.COURSE);
        List<Favorite> favorites = favoriteService.queryFavorites(userId, Favorite.Type.COURSE, start, count);

        PagedList<FavoriteDto> pagedFavoriteDtos = new PagedList<FavoriteDto>(totalCount, start, count);
        pagedFavoriteDtos.setList(buildFavoriteDtos(favorites));

        return MomiaHttpResponse.SUCCESS(pagedFavoriteDtos);
    }

    private List<FavoriteDto> buildFavoriteDtos(List<Favorite> favorites) {
        Set<Long> courseIds = new HashSet<Long>();
        for (Favorite favorite: favorites) {
            courseIds.add(favorite.getRefId());
        }

        List<Course> courses = courseService.list(courseIds);
        Map<Long, CourseDto> courseDtosMap = new HashMap<Long, CourseDto>();
        for (Course course : courses) {
            courseDtosMap.put(course.getId(), buildBaseCourseDto(course));
        }

        List<FavoriteDto> favoriteDtos = new ArrayList<FavoriteDto>();
        for (Favorite favorite : favorites) {
            CourseDto courseDto = courseDtosMap.get(favorite.getRefId());
            if (courseDto == null) continue;

            FavoriteDto favoriteDto = new FavoriteDto();
            favoriteDto.setId(favorite.getId());
            favoriteDto.setType(favorite.getType());
            favoriteDto.setRef((JSONObject) JSON.toJSON(courseDto));

            favoriteDtos.add(favoriteDto);
        }

        return favoriteDtos;
    }
}
