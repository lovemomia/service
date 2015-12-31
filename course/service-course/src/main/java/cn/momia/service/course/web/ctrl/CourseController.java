package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.Course;
import cn.momia.api.course.dto.CourseDetail;
import cn.momia.api.course.dto.CourseSkuPlace;
import cn.momia.api.course.dto.DatedCourseSkus;
import cn.momia.api.course.dto.Student;
import cn.momia.api.course.dto.TeacherCourse;
import cn.momia.api.poi.dto.Institution;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.PoiUtil;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.api.course.dto.BookedCourse;
import cn.momia.service.course.base.CourseService;
import cn.momia.api.course.dto.CourseSku;
import cn.momia.api.course.dto.Teacher;
import cn.momia.service.course.comment.CourseCommentService;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderPackage;
import cn.momia.service.course.order.OrderService;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.SubjectSku;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Splitter POS_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    @Autowired private CourseService courseService;
    @Autowired private CourseCommentService courseCommentService;
    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;

    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommend(@RequestParam(value = "city") long cityId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryRecommendCount(cityId);
        List<Course> courses = courseService.queryRecomend(cityId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedCourses(courses, totalCount, start, count));
    }

    private PagedList<Course> buildPagedCourses(List<Course> courses, long totalCount, int start, int count) {
        PagedList<Course> pagedCourses = new PagedList<Course>(totalCount, start, count);
        pagedCourses.setList(buildBaseCourses(courses));

        return pagedCourses;
    }

    private List<Course> buildBaseCourses(List<Course> courses) {
        List<Course> baseCourses = new ArrayList<Course>();
        for (Course course : courses) {
            baseCourses.add(new Course.Base(course));
        }

        return baseCourses;
    }

    @RequestMapping(value = "/trial", method = RequestMethod.GET)
    public MomiaHttpResponse listTrialCourses(@RequestParam(value = "city") long cityId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryTrialCount(cityId);
        List<Course> courses = courseService.queryTrial(cityId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedCourses(courses, totalCount, start, count));
    }

    @RequestMapping(value = "/{coid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "coid") long courseId,
                                 @RequestParam String pos,
                                 @RequestParam(required = false, defaultValue = "" + Course.ShowType.BASE) int type) {
        Course course = courseService.get(courseId);
        if (!course.exists()) return MomiaHttpResponse.FAILED("课程不存在");

        if (type == Course.ShowType.FULL) {
            course.setPlace(buildCourseSkuPlace(filterNotEndedSkus(course.getSkus()), pos));
            return MomiaHttpResponse.SUCCESS(course);
        } else {
            return MomiaHttpResponse.SUCCESS(new Course.Base(course));
        }
    }

    private List<CourseSku> filterNotEndedSkus(List<CourseSku> skus) {
        List<CourseSku> notEndedSkus = new ArrayList<CourseSku>();
        Date now = new Date();
        for (CourseSku sku : skus) {
            if (!sku.isEnded(now)) notEndedSkus.add(sku);
        }

        return notEndedSkus;
    }

    private CourseSkuPlace buildCourseSkuPlace(List<CourseSku> skus, String pos) {
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

        if (places.isEmpty()) return null;

        CourseSkuPlace place = places.get(0);
        List<CourseSku> skusOfPlace = skusGroupedByPlace.get(place.getId());
        place.setScheduler(buildPlaceScheduler(skusOfPlace));

        return place;
    }

    private String buildPlaceScheduler(List<CourseSku> skus) {
        CourseSku earliestSku = null;
        Date now = new Date();
        for (CourseSku sku : skus) {
            if (!sku.isEnded(now)) {
                if (earliestSku == null) {
                    earliestSku = sku;
                } else {
                    if (sku.getStartTime().before(earliestSku.getStartTime())) earliestSku = sku;
                }
            }
        }

        return earliestSku == null ? "" : earliestSku.getScheduler();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse listCourses(@RequestParam String coids) {
        Set<Long> courseIds = new HashSet<Long>();
        for (String courseId : Splitter.on(",").trimResults().omitEmptyStrings().split(coids)) {
            courseIds.add(Long.valueOf(courseId));
        }
        List<Course> courses = courseService.list(courseIds);

        return MomiaHttpResponse.SUCCESS(buildBaseCourses(courses));
    }

    @RequestMapping(value = "/finished/list", method = RequestMethod.GET)
    public MomiaHttpResponse listFinished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = userId <= 0 ? courseService.listFinishedCount() : courseService.listFinishedCount(userId);
        List<Course> courses = userId <= 0 ? courseService.listFinished(start, count) : courseService.listFinished(userId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedCourses(courses, totalCount, start, count));
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
        long totalCount = courseService.queryCountBySubject(subjectId, courseIds, minAge, maxAge, (packageId > 0 ? Course.QueryType.BOOKABLE : Course.QueryType.NOT_END));
        List<Course> courses = courseService.queryBySubject(subjectId, start, count, courseIds, minAge, maxAge, sortTypeId, (packageId > 0 ? Course.QueryType.BOOKABLE : Course.QueryType.NOT_END));

        return MomiaHttpResponse.SUCCESS(buildPagedCourses(courses, totalCount, start, count));
    }

    @RequestMapping(value = "/{coid}/detail", method = RequestMethod.GET)
    public MomiaHttpResponse detail(@PathVariable(value = "coid") long courseId) {
        CourseDetail courseDetail = courseService.getDetail(courseId);
        if (!courseDetail.exists()) return MomiaHttpResponse.FAILED("课程详情不存在");

        return MomiaHttpResponse.SUCCESS(courseDetail);
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

    @RequestMapping(value = "/tips", method = RequestMethod.GET)
    public MomiaHttpResponse queryTips(@RequestParam String coids) {
        Set<Long> courseIds = new HashSet<Long>();
        for (String courseId : Splitter.on(",").omitEmptyStrings().trimResults().split(coids)) {
            courseIds.add(Long.valueOf(courseId));
        }

        return MomiaHttpResponse.SUCCESS(courseService.queryTips(courseIds));
    }

    @RequestMapping(value = "/sku/list", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@RequestParam String sids) {
        Set<Long> skuIds = new HashSet<Long>();
        for (String skuId : Splitter.on(",").trimResults().omitEmptyStrings().split(sids)) {
            skuIds.add(Long.valueOf(skuId));
        }

        return MomiaHttpResponse.SUCCESS(courseService.listSkus(skuIds));
    }

    @RequestMapping(value = "/{coid}/sku/{sid}", method = RequestMethod.GET)
    public MomiaHttpResponse getSku(@PathVariable(value = "coid") long courseId, @PathVariable(value = "sid") long skuId) {
        CourseSku sku = courseService.getSku(skuId);
        if (!sku.exists() || sku.getCourseId() != courseId) return MomiaHttpResponse.FAILED("无效的场次");
        return MomiaHttpResponse.SUCCESS(sku);
    }

    @RequestMapping(value = "/{coid}/sku/week", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable(value = "coid") long courseId) {
        Date now = new Date();
        String start = DATE_FORMAT.format(now);
        String end = DATE_FORMAT.format(new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000));
        List<CourseSku> skus = courseService.querySkus(courseId, start, end);

        return MomiaHttpResponse.SUCCESS(buildDatedCourseSkus(filterNotEndedSkus(skus)));
    }

    private List<DatedCourseSkus> buildDatedCourseSkus(List<CourseSku> skus) {
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

        List<DatedCourseSkus> datedCourseSkuses = new ArrayList<DatedCourseSkus>();
        for (Map.Entry<String, List<CourseSku>> entry : skusMap.entrySet()) {
            String date = entry.getKey();
            List<CourseSku> skusOfDay = entry.getValue();

            DatedCourseSkus datedCourseSkus = new DatedCourseSkus();
            datedCourseSkus.setDate(date);
            datedCourseSkus.setSkus(skusOfDay);

            datedCourseSkuses.add(datedCourseSkus);
        }

        Collections.sort(datedCourseSkuses, new Comparator<DatedCourseSkus>() {
            @Override
            public int compare(DatedCourseSkus o1, DatedCourseSkus o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return datedCourseSkuses;
    }

    @RequestMapping(value = "/{coid}/sku/month", method = RequestMethod.GET)
    public MomiaHttpResponse listWeekSkus(@PathVariable(value = "coid") long courseId, @RequestParam int month) {
        String start = formatCurrentMonth(month);
        String end = formatNextMonth(month);
        List<CourseSku> skus = courseService.querySkus(courseId, start, end);

        return MomiaHttpResponse.SUCCESS(buildDatedCourseSkus(filterNotEndedSkus(skus)));
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

        PagedList<BookedCourse> pagedBookedCourses = new PagedList<BookedCourse>(totalCount, start, count);
        pagedBookedCourses.setList(completeBookedCourses(userId, bookedCourses));

        return MomiaHttpResponse.SUCCESS(pagedBookedCourses);
    }

    private List<BookedCourse> completeBookedCourses(long userId, List<BookedCourse> bookedCourses) {
        Set<Long> bookingIds = new HashSet<Long>();
        Set<Long> courseIds = new HashSet<Long>();
        for (BookedCourse bookedCourse : bookedCourses) {
            bookingIds.add(bookedCourse.getBookingId());
            courseIds.add(bookedCourse.getCourseId());
        }

        Set<Long> commentBookingIds = Sets.newHashSet(courseCommentService.queryCommentedBookingIds(userId, bookingIds));
        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<BookedCourse> completedBookedCourses = new ArrayList<BookedCourse>();
        for (BookedCourse bookedCourse : bookedCourses) {
            Course course = coursesMap.get(bookedCourse.getCourseId());
            if (course == null) continue;
            CourseSkuPlace place = course.getPlace(bookedCourse.getCourseSkuId());
            if (place == null) continue;

            BookedCourse completedBookedCourse = new BookedCourse(course);
            completedBookedCourse.setBookingId(bookedCourse.getBookingId());
            completedBookedCourse.setCourseSkuId(bookedCourse.getCourseSkuId());
            completedBookedCourse.setParentCourseSkuId(bookedCourse.getParentCourseSkuId());
            if (commentBookingIds.contains(bookedCourse.getBookingId())) completedBookedCourse.setCommented(true);
            completedBookedCourse.setScheduler(course.getScheduler(bookedCourse.getCourseSkuId()));
            completedBookedCourse.setPlace(place);

            completedBookedCourses.add(completedBookedCourse);
        }

        return completedBookedCourses;
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    public MomiaHttpResponse finished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryFinishedCountByUser(userId);
        List<BookedCourse> bookedCourses = courseService.queryFinishedByUser(userId, start, count);

        PagedList<BookedCourse> pagedBookedCourses = new PagedList<BookedCourse>(totalCount, start, count);
        pagedBookedCourses.setList(completeBookedCourses(userId, bookedCourses));

        return MomiaHttpResponse.SUCCESS(pagedBookedCourses);
    }
    @RequestMapping(value = "/teacher/ongoing", method = RequestMethod.GET)
    public MomiaHttpResponse teacherOngoing(@RequestParam(value = "uid") long userId) {
        List<TeacherCourse> courses = completeTeacherCourses(courseService.queryOngoingByTeacher(userId), false);
        if (courses.isEmpty()) return MomiaHttpResponse.SUCCESS(TeacherCourse.NOT_EXIST_TEACHER_COURSE);

        return MomiaHttpResponse.SUCCESS(courses.get(0));
    }

    private List<TeacherCourse> completeTeacherCourses(List<TeacherCourse> teacherCourses, boolean finished) {
        Set<Long> courseIds = new HashSet<Long>();
        for (TeacherCourse teacherCourse : teacherCourses) {
            courseIds.add(teacherCourse.getCourseId());
        }

        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<TeacherCourse> completedTeacherCourses = new ArrayList<TeacherCourse>();
        for (TeacherCourse teacherCourse : teacherCourses) {
            Course course = coursesMap.get(teacherCourse.getCourseId());
            if (course == null) continue;
            CourseSkuPlace place = course.getPlace(teacherCourse.getCourseSkuId());
            if (place == null) continue;

            TeacherCourse completedTeacherCourse = new TeacherCourse();
            completedTeacherCourse.setCourseId(teacherCourse.getCourseId());
            completedTeacherCourse.setCourseSkuId(teacherCourse.getCourseSkuId());
            completedTeacherCourse.setCover(course.getCover());
            completedTeacherCourse.setTitle(course.getTitle());
            completedTeacherCourse.setScheduler(course.getScheduler(teacherCourse.getCourseSkuId()));
            completedTeacherCourse.setAddress(place.getAddress());

            completedTeacherCourses.add(completedTeacherCourse);
        }

        if (finished) {
            Set<Long> courseSkuIds = new HashSet<Long>();
            for (TeacherCourse teacherCourse : completedTeacherCourses) {
                courseSkuIds.add(teacherCourse.getCourseSkuId());
            }

            Map<Long, Long> checkInCountMap = courseService.queryCheckInCounts(courseSkuIds);
            Map<Long, Long> commentedCountMap = courseService.queryCommentedChildrenCount(courseSkuIds);

            for (TeacherCourse teacherCourse : completedTeacherCourses) {
                if (checkInCountMap.get(teacherCourse.getCourseSkuId()) == commentedCountMap.get(teacherCourse.getCourseSkuId())) teacherCourse.setCommented(true);
            }
        }

        return completedTeacherCourses;
    }

    @RequestMapping(value = "/teacher/notfinished", method = RequestMethod.GET)
    public MomiaHttpResponse teacherNotFinished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryNotFinishedCountByTeacher(userId);
        List<TeacherCourse> courses = courseService.queryNotFinishedByTeacher(userId, start, count);

        PagedList<TeacherCourse> pagedCourses = new PagedList<TeacherCourse>(totalCount, start, count);
        pagedCourses.setList(completeTeacherCourses(courses, false));

        return MomiaHttpResponse.SUCCESS(pagedCourses);
    }

    @RequestMapping(value = "/teacher/finished", method = RequestMethod.GET)
    public MomiaHttpResponse teacherFinished(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = courseService.queryFinishedCountByTeacher(userId);
        List<TeacherCourse> courses = courseService.queryFinishedByTeacher(userId, start, count);

        PagedList<TeacherCourse> pagedCourses = new PagedList<TeacherCourse>(totalCount, start, count);
        pagedCourses.setList(completeTeacherCourses(courses, true));

        return MomiaHttpResponse.SUCCESS(pagedCourses);
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

        SubjectSku subjectSku = subjectService.getSku(orderPackage.getSkuId());
        if (!subjectSku.exists()) return MomiaHttpResponse.FAILED("预约失败，无效的课程包");

        CourseSku sku = courseService.getSku(skuId);
        if (!sku.exists() || !sku.isBookable(new Date())) return MomiaHttpResponse.FAILED("预约失败，无效的课程场次或本场次已截止选课");
        if (orderPackage.getCourseId() > 0 && orderPackage.getCourseId() != sku.getCourseId()) return MomiaHttpResponse.FAILED("预约失败，课程与购买的包不匹配");

        Map<Long, Date> startTimes = courseService.queryStartTimesByPackages(Sets.newHashSet(packageId));
        Date startTime = startTimes.get(packageId);
        if (startTime != null) {
            Date endTime = TimeUtil.add(startTime, subjectSku.getTime(), subjectSku.getTimeUnit());
            if (endTime.before(sku.getStartTime())) return MomiaHttpResponse.FAILED("预约失败，该课程的时间超出了课程包的有效期");
        }

        Order order = orderService.get(orderPackage.getOrderId());
        User user = userServiceApi.get(utoken);
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

        if (bookingId <= 0) return MomiaHttpResponse.FAILED("选课失败");

        BookedCourse bookedCourse = courseService.getBookedCourse(bookingId);
        List<BookedCourse> completedBookedCourses = completeBookedCourses(user.getId(), Lists.newArrayList(bookedCourse));
        if (completedBookedCourses.isEmpty()) return MomiaHttpResponse.FAILED("选课失败");

        return MomiaHttpResponse.SUCCESS(completedBookedCourses.get(0));
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public MomiaHttpResponse cancel(@RequestParam String utoken, @RequestParam(value = "bid") long bookingId) {
        User user = userServiceApi.get(utoken);
        BookedCourse bookedCourse = courseService.getBookedCourse(bookingId);
        if (!bookedCourse.exists()) return MomiaHttpResponse.FAILED("取消预约的课程不存在");
        if (!bookedCourse.canCancel()) return MomiaHttpResponse.FAILED("取消预约的课程必须提前至少3天");
        if (!courseService.cancel(user.getId(), bookingId)) return MomiaHttpResponse.FAILED("取消选课失败");

        try {
            if (!orderService.increaseBookableCount(bookedCourse.getPackageId())) LOGGER.error("fail to increase bookable count, booking id: {}", bookingId);
            if (!courseService.unlockSku(bookedCourse.getCourseSkuId())) LOGGER.error("fail to unlock course sku, booking id: {}", bookingId);
            CourseSku sku = courseService.getSku(bookedCourse.getCourseSkuId());
            courseService.decreaseJoined(bookedCourse.getCourseId(), sku.getJoinCount());
        } catch (Exception e) {
            LOGGER.error("error when cancel booked course, {}", bookingId, e);
        }

        List<BookedCourse> completedBookedCourses = completeBookedCourses(user.getId(), Lists.newArrayList(bookedCourse));
        if (completedBookedCourses.isEmpty()) return MomiaHttpResponse.FAILED("取消选课失败");

        return MomiaHttpResponse.SUCCESS(completedBookedCourses.get(0));
    }

    @RequestMapping(value = "/course/checkin", method = RequestMethod.POST)
    public MomiaHttpResponse checkin(@RequestParam String utoken,
                                     @RequestParam(value = "uid") long userId,
                                     @RequestParam(value = "pid") long packageId,
                                     @RequestParam(value = "coid") long courseId,
                                     @RequestParam(value = "sid") long courseSkuId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(courseService.checkin(userId, packageId, courseId, courseSkuId));
    }

    @RequestMapping(value = "/course/ongoing/student", method = RequestMethod.GET)
    public MomiaHttpResponse ongoingStudents(@RequestParam String utoken,
                                             @RequestParam(value = "coid") long courseId,
                                             @RequestParam(value = "sid") long courseSkuId) {
        List<Student> students = courseService.queryAllStudents(courseId, courseSkuId);
        List<Long> userIds = courseService.queryUserIdsWithoutChild(courseId, courseSkuId);
        List<User> users = userServiceApi.list(userIds, User.Type.MINI);
        for (User user : users) {
            Student student = new Student();
            student.setType(Student.Type.PARENT);
            student.setId(user.getId());
            student.setUserId(user.getId());
            student.setAvatar(user.getAvatar());
            student.setName(user.getNickName());

            students.add(student);
        }

        return MomiaHttpResponse.SUCCESS(students);
    }

    @RequestMapping(value = "/course/notfinished/student", method = RequestMethod.GET)
    public MomiaHttpResponse notfinishedStudents(@RequestParam String utoken,
                                                 @RequestParam(value = "coid") long courseId,
                                                 @RequestParam(value = "sid") long courseSkuId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(courseService.queryAllStudents(courseId, courseSkuId));
    }

    @RequestMapping(value = "/course/finished/student", method = RequestMethod.GET)
    public MomiaHttpResponse finishedStudents(@RequestParam String utoken,
                                              @RequestParam(value = "coid") long courseId,
                                              @RequestParam(value = "sid") long courseSkuId) {
        User user = userServiceApi.get(utoken);

        List<Student> students = courseService.queryCheckInStudents(courseId, courseSkuId);

        Set<Long> commentedChildIds = Sets.newHashSet(courseService.queryCommentedChildIds(courseId, courseSkuId));
        for (Student student : students) {
            if (commentedChildIds.contains(student.getId())) student.setCommented(true);
        }

        return MomiaHttpResponse.SUCCESS(students);
    }
}
