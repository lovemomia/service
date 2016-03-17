package cn.momia.service.course.base.impl;

import cn.momia.api.course.dto.course.Student;
import cn.momia.api.poi.MetaUtil;
import cn.momia.api.poi.dto.Institution;
import cn.momia.api.poi.dto.Region;
import cn.momia.api.course.dto.course.Course;
import cn.momia.api.course.dto.course.CourseDetail;
import cn.momia.api.course.dto.course.CourseSkuPlace;
import cn.momia.api.course.dto.course.TeacherCourse;
import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.Place;
import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.service.AbstractService;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.api.course.dto.course.BookedCourse;
import cn.momia.service.course.base.CourseService;
import cn.momia.api.course.dto.course.CourseSku;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseServiceImpl extends AbstractService implements CourseService {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M月d日");

    private static final int SORT_TYPE_JOINED = 1;
    private static final int SORT_TYPE_ADDTIME = 2;

    private PoiServiceApi poiServiceApi;

    public void setPoiServiceApi(PoiServiceApi poiServiceApi) {
        this.poiServiceApi = poiServiceApi;
    }

    @Override
    public boolean isRecommended(long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseRecommend WHERE CourseId=? AND Status<>0";
        return queryInt(sql, new Object[] { courseId }) > 0;
    }

    @Override
    public long queryRecommendCount(long cityId) {
        String sql = "SELECT COUNT(DISTINCT A.CourseId) " +
                "FROM SG_CourseRecommend A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_Subject C ON B.SubjectId=C.Id " +
                "INNER JOIN SG_CourseSku D ON A.CourseId=D.CourseId " +
                "WHERE A.Status<>0 AND B.Status=1 AND C.Status=1 AND C.CityId=? AND D.Status=1 AND DATE_ADD(DATE(D.EndTime), INTERVAL 1 DAY)>NOW()";
        return queryLong(sql, new Object[] { cityId });
    }

    @Override
    public List<Course> queryRecomend(long cityId, int start, int count) {
        String sql = "SELECT DISTINCT A.CourseId " +
                "FROM SG_CourseRecommend A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_Subject C ON B.SubjectId=C.Id " +
                "INNER JOIN SG_CourseSku D ON A.CourseId=D.CourseId " +
                "WHERE A.Status<>0 AND B.Status=1 AND C.Status=1 AND C.CityId=? AND D.Status=1 AND DATE_ADD(DATE(D.EndTime), INTERVAL 1 DAY)>NOW() " +
                "ORDER BY A.Weight DESC, A.AddTime DESC LIMIT ?,?";
        List<Long> courseIds = queryLongList(sql, new Object[] { cityId, start, count });

        return list(courseIds);
    }

    @Override
    public long queryTrialCount(long cityId) {
        String sql = "SELECT COUNT(DISTINCT A.Id) " +
                "FROM SG_Course A " +
                "INNER JOIN SG_Subject B ON A.SubjectId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.Id=C.CourseId " +
                "WHERE A.Status=1 AND B.Status=1 AND B.CityId=? AND B.Type=2 AND C.Status=1 AND DATE_ADD(DATE(C.EndTime), INTERVAL 1 DAY)>NOW()";
        return queryLong(sql, new Object[] { cityId });
    }

    @Override
    public List<Course> queryTrial(long cityId, int start, int count) {
        String sql = "SELECT DISTINCT A.Id " +
                "FROM SG_Course A " +
                "INNER JOIN SG_Subject B ON A.SubjectId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.Id=C.CourseId " +
                "WHERE A.Status=1 AND B.Status=1 AND B.CityId=? AND B.Type=2 AND C.Status=1 AND DATE_ADD(DATE(C.EndTime), INTERVAL 1 DAY)>NOW() " +
                "ORDER BY B.Stock DESC, A.Joined DESC, A.AddTime DESC LIMIT ?,?";
        List<Long> courseIds = queryLongList(sql, new Object[] { cityId, start, count });

        return list(courseIds);
    }

    @Override
    public Course get(long courseId) {
        Collection<Long> courseIds = Sets.newHashSet(courseId);
        List<Course> courses = list(courseIds);

        return courses.isEmpty() ? Course.NOT_EXIST_COURSE : courses.get(0);
    }

    @Override
    public List<Course> list(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new ArrayList<Course>();

        String sql = "SELECT A.Id, A.Type, A.ParentId, A.SubjectId, A.Title, A.KeyWord, A.Feature, A.Cover, A.MinAge, A.MaxAge, A.Insurance, A.Joined, A.Price, A.Price AS OriginalPrice, A.Goal, A.Flow, A.Tips, A.Notice, B.Notice AS SubjectNotice, A.InstitutionId, A.Status, B.Title AS Subject, B.Stock, A.AddTime FROM SG_Course A INNER JOIN SG_Subject B ON A.SubjectId=B.Id WHERE A.Id IN (" + StringUtils.join(courseIds, ",") + ") AND A.Status<>0 AND B.Status<>0";
        List<Course> courses = queryObjectList(sql, Course.class);

        Set<Integer> institutionIds = new HashSet<Integer>();
        Set<Long> courseAndParentIds = Sets.newHashSet(courseIds);
        for (Course course : courses) {
            institutionIds.add(course.getInstitutionId());
            if (course.getParentId() > 0) courseAndParentIds.add(course.getParentId());
        }
        Map<Integer, Institution> institutionsMap = queryInstitutions(institutionIds);
        Map<Long, List<String>> imgsMap = queryImgs(courseAndParentIds);
        Map<Long, List<String>> bookImgsMap = queryBookImgs(courseAndParentIds);
        Map<Long, List<CourseSku>> skusMap = querySkus(courseIds);
        Map<Long, BigDecimal> buyablesMap = queryBuyables(courseIds);

        for (Course course : courses) {
            Institution institution = institutionsMap.get(course.getInstitutionId());
            if (institution != null) course.setInstitution(institution.getIntro());

            course.setImgs(imgsMap.get(course.getId()));
            List<String> bookImgs = bookImgsMap.get(course.getId());

            if (course.getParentId() > 0) {
                course.getImgs().addAll(imgsMap.get(course.getParentId()));
                bookImgs.addAll(bookImgsMap.get(course.getParentId()));
            }

            JSONObject book = new JSONObject();
            book.put("imgs", bookImgs);
            course.setBook(book);

            course.setSkus(skusMap.get(course.getId()));

            BigDecimal price = buyablesMap.get(course.getId());
            if (price.compareTo(new BigDecimal(0)) > 0) {
                course.setPrice(price);
                course.setBuyable(true);
            }

            int stock = course.getStock();
            int avaliableSkuCount = 0;
            Date now = new Date();
            for (CourseSku sku : course.getSkus()) {
                if (sku.isBookable(now)) avaliableSkuCount++;
            }
            course.setStatus((stock != 0) ? (avaliableSkuCount > 0 ? Course.Status.OK : Course.Status.SOLD_OUT) : Course.Status.SOLD_OUT);

            course.setAge(formatAge(course));
            course.setScheduler(formatScheduler(course));
            int regionId = getRegionId(course);
            course.setRegionId(regionId);
            course.setRegion(MetaUtil.getRegionName(regionId));
        }

        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<Course> result = new ArrayList<Course>();
        for (long courseId : courseIds) {
            Course course = coursesMap.get(courseId);
            if (course != null) result.add(course);
        }

        return result;
    }

    private Map<Integer, Institution> queryInstitutions(Collection<Integer> institutionIds) {
        if (institutionIds.isEmpty()) return new HashMap<Integer, Institution>();

        List<Institution> institutions = poiServiceApi.listInstitutions(institutionIds);

        Map<Integer, Institution> institutionsMap = new HashMap<Integer, Institution>();
        for (Institution institution : institutions) {
            institutionsMap.put(institution.getId(), institution);
        }

        return institutionsMap;
    }

    private Map<Long, List<String>> queryImgs(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<String>>();

        String sql = "SELECT CourseId, Url FROM SG_CourseImg WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0 ORDER BY SortValue ASC";
        Map<Long, List<String>> imgsMap = queryListMap(sql, Long.class, String.class);

        for (long courseId : courseIds) {
            if (!imgsMap.containsKey(courseId)) imgsMap.put(courseId, new ArrayList<String>());
        }

        return imgsMap;
    }

    private Map<Long, List<String>> queryBookImgs(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<String>>();

        String sql = "SELECT CourseId, Img FROM SG_CourseBook WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0 ORDER BY `Order` ASC";
        Map<Long, List<String>> imgsMap = queryListMap(sql, Long.class, String.class);

        for (long courseId : courseIds) {
            if (!imgsMap.containsKey(courseId)) imgsMap.put(courseId, new ArrayList<String>());
        }

        return imgsMap;
    }

    private Map<Long, List<CourseSku>> querySkus(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<CourseSku>>();

        String sql = "SELECT Id FROM SG_CourseSku WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0";
        List<Long> skuIds = queryLongList(sql);
        List<CourseSku> skus = listSkus(skuIds);

        Map<Long, List<CourseSku>> skusMap = new HashMap<Long, List<CourseSku>>();
        for (long courseId : courseIds) {
            skusMap.put(courseId, new ArrayList<CourseSku>());
        }
        for (CourseSku sku : skus) {
            skusMap.get(sku.getCourseId()).add(sku);
        }

        return skusMap;
    }

    @Override
    public List<CourseSku> listSkus(Collection<Long> skuIds) {
        if (skuIds.isEmpty()) return new ArrayList<CourseSku>();

        String sql = "SELECT Id, ParentId, CourseId, StartTime, EndTime, Deadline, UnlockedStock, LockedStock AS Booked, MinBooked, PlaceId, Adult, Child, Status FROM SG_CourseSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status<>0";
        List<CourseSku> skus = queryObjectList(sql, CourseSku.class);

        Map<Long, CourseSku> skusMap = new HashMap<Long, CourseSku>();
        for (CourseSku sku : skus) {
            skusMap.put(sku.getId(), sku);
        }

        List<CourseSku> result = new ArrayList<CourseSku>();
        for (long skuId : skuIds) {
            CourseSku sku = skusMap.get(skuId);
            if (sku != null) result.add(sku);
        }

        return completeSkus(result);
    }

    private List<CourseSku> completeSkus(List<CourseSku> skus) {
        Set<Integer> placeIds = new HashSet<Integer>();
        for (CourseSku sku : skus) {
            placeIds.add(sku.getPlaceId());
        }

        List<Place> places = poiServiceApi.listPlaces(placeIds);
        Map<Integer, Place> placesMap = new HashMap<Integer, Place>();
        for (Place place : places) {
            placesMap.put(place.getId(), place);
        }

        List<CourseSku> completedSkus = new ArrayList<CourseSku>();
        for (CourseSku sku : skus) {
            Place place = placesMap.get(sku.getPlaceId());
            if (place == null) continue;

            sku.setPlace(buildCourseSkuPlace(place));
            completedSkus.add(sku);
        }

        return completedSkus;
    }

    private CourseSkuPlace buildCourseSkuPlace(Place place) {
        CourseSkuPlace courseSkuPlace = new CourseSkuPlace();
        courseSkuPlace.setId(place.getId());
        courseSkuPlace.setCityId(place.getCityId());
        courseSkuPlace.setRegionId(place.getRegionId());
        courseSkuPlace.setName(place.getName());
        courseSkuPlace.setAddress(place.getAddress());
        courseSkuPlace.setLng(place.getLng());
        courseSkuPlace.setLat(place.getLat());
        courseSkuPlace.setRoute(place.getRoute());

        return courseSkuPlace;
    }

    private Map<Long, BigDecimal> queryBuyables(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, BigDecimal>();

        final Map<Long, BigDecimal> buyablesMap = new HashMap<Long, BigDecimal>();
        for (long courseId : courseIds) {
            buyablesMap.put(courseId, new BigDecimal(0));
        }

        String sql = "SELECT CourseId, Price FROM SG_SubjectSku WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND CourseId>0 AND Status=1";
        query(sql, null, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long courseId = rs.getLong("CourseId");
                BigDecimal price = rs.getBigDecimal("Price");
                buyablesMap.put(courseId, price);
            }
        });

        return buyablesMap;
    }

    private String formatAge(Course course) {
        int minAge = course.getMinAge();
        int maxAge = course.getMaxAge();
        if (minAge <= 0 && maxAge <= 0) throw new MomiaErrorException("invalid age of course: " + course.getId());
        if (minAge <= 0) return maxAge + "岁";
        if (maxAge <= 0) return minAge + "岁";
        if (minAge == maxAge) return minAge + "岁";
        return minAge + "-" + maxAge + "岁";
    }

    private String formatScheduler(Course course) {
        List<CourseSku> skus = course.getSkus();
        Date now = new Date();
        List<Date> times = new ArrayList<Date>();
        for (CourseSku sku : skus) {
            if (!sku.isEnded(now)) {
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

    private int getRegionId(Course course) {
        List<CourseSku> skus = course.getSkus();
        List<Integer> regionIds = new ArrayList<Integer>();
        for (CourseSku sku : skus) {
            CourseSkuPlace place = sku.getPlace();
            int regionId = place.getRegionId();
            if (!regionIds.contains(regionId)) regionIds.add(regionId);
        }

        if (regionIds.isEmpty()) return 0;
        return regionIds.size() > 1 ? Region.MULTI_REGION_ID : regionIds.get(0);
    }

    @Override
    public long queryBookImgCount(long courseId) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        long parentId = getParentId(courseId);
        if (parentId > 0) courseIds.add(parentId);

        String sql = "SELECT COUNT(1) FROM SG_CourseBook WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0";
        return queryLong(sql);
    }

    public long getParentId(long courseId) {
        String sql = "SELECT ParentId FROM SG_Course WHERE Id=? AND Status<>0";
        return queryLong(sql, new Object[] { courseId });
    }

    @Override
    public List<String> queryBookImgs(long courseId, int start, int count) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        long parentId = getParentId(courseId);
        if (parentId > 0) courseIds.add(parentId);

        String sql = "SELECT Img FROM SG_CourseBook WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0 ORDER BY `Order` ASC LIMIT ?,?";
        return queryStringList(sql, new Object[] { start, count });
    }

    @Override
    public long queryTeacherIdsCount(long courseId) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        long parentId = getParentId(courseId);
        if (parentId > 0) courseIds.add(parentId);

        String sql = "SELECT COUNT(DISTINCT TeacherId) FROM SG_CourseTeacher WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0";
        return queryLong(sql);
    }

    @Override
    public List<Integer> queryTeacherIds(long courseId, int start, int count) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        long parentId = getParentId(courseId);
        if (parentId > 0) courseIds.add(parentId);

        String sql = "SELECT TeacherId FROM SG_CourseTeacher WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0 GROUP BY TeacherId LIMIT ?,?";
        return queryIntList(sql, new Object[] { start, count });
    }

    @Override
    public long queryCountBySubject(long subjectId, Collection<Long> exclusions, int minAge, int maxAge, int queryType) {
        String query = "1=1";
        if (queryType == Course.QueryType.BOOKABLE) {
            query = "B.StartTime>NOW() AND B.EndTime>NOW() AND B.Deadline>NOW() AND B.UnlockedStock>0";
        } else if (queryType == Course.QueryType.NOT_END) {
            query = "DATE_ADD(DATE(B.EndTime), INTERVAL 1 DAY)>NOW()";
        }

        String sql = exclusions.isEmpty() ?
                "SELECT COUNT(DISTINCT A.Id) " +
                        "FROM SG_Course A " +
                        "INNER JOIN SG_CourseSku B ON A.Id=B.CourseId " +
                        "WHERE A.SubjectId=? AND A.MinAge>=? AND A.MaxAge<=? AND A.Status=1 " +
                        "AND " + query + " AND B.Status=1" :
                "SELECT COUNT(DISTINCT A.Id) " +
                        "FROM SG_Course A " +
                        "INNER JOIN SG_CourseSku B ON A.Id=B.CourseId " +
                        "WHERE A.SubjectId=? AND A.MinAge>=? AND A.MaxAge<=? AND A.Id NOT IN (" + StringUtils.join(exclusions, ",") + ") AND A.Status=1 " +
                        "AND " + query + " AND B.Status=1";
        return queryLong(sql, new Object[] { subjectId, minAge, maxAge });
    }

    @Override
    public List<Course> queryBySubject(long subjectId, int start, int count, Collection<Long> exclusions, int minAge, int maxAge, int sortTypeId, int queryType) {
        String query = "1=1";
        if (queryType == Course.QueryType.BOOKABLE) {
            query = "B.StartTime>NOW() AND B.EndTime>NOW() AND B.Deadline>NOW() AND B.UnlockedStock>0";
        } else if (queryType == Course.QueryType.NOT_END) {
            query = "DATE_ADD(DATE(B.EndTime), INTERVAL 1 DAY)>NOW()";
        }

        String sort = "MIN(B.StartTime) ASC";
        if (sortTypeId == SORT_TYPE_JOINED) {
            sort = "A.Joined DESC";
        } else if (sortTypeId == SORT_TYPE_ADDTIME) {
            sort = "A.AddTime DESC";
        }

        String sql = exclusions.isEmpty() ?
                "SELECT A.Id " +
                        "FROM SG_Course A " +
                        "INNER JOIN SG_CourseSku B ON A.Id=B.CourseId " +
                        "WHERE A.SubjectId=? AND A.MinAge>=? AND A.MaxAge<=? AND A.Status=1 " +
                        "AND " + query + " AND B.Status=1 " +
                        "GROUP BY A.Id " +
                        "ORDER BY " + sort + " LIMIT ?,?" :
                "SELECT A.Id " +
                        "FROM SG_Course A " +
                        "INNER JOIN SG_CourseSku B ON A.Id=B.CourseId " +
                        "WHERE A.SubjectId=? AND A.MinAge>=? AND A.MaxAge<=? AND A.Id NOT IN (" + StringUtils.join(exclusions, ",") + ") AND A.Status=1 " +
                        "AND " + query + " AND B.Status=1 " +
                        "GROUP BY A.Id " +
                        "ORDER BY " + sort + " LIMIT ?,?";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId, minAge, maxAge, start, count });

        return list(courseIds);
    }

    @Override
    public List<Course> queryAllBySubject(long subjectId) {
        Set<Long> subjectIds = Sets.newHashSet(subjectId);
        Map<Long, List<Course>> coursesMap = queryAllBySubjects(subjectIds);

        return coursesMap.get(subjectId);
    }

    @Override
    public Map<Long, List<Course>> queryAllBySubjects(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new HashMap<Long, List<Course>>();

        String sql = "SELECT Id FROM SG_Course WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1 ORDER BY AddTime DESC";
        List<Long> courseIds = queryLongList(sql);
        List<Course> courses = list(courseIds);

        Map<Long, List<Course>> coursesMap = new HashMap<Long, List<Course>>();
        for (long subjectId : subjectIds) {
            coursesMap.put(subjectId, new ArrayList<Course>());
        }
        for (Course course : courses) {
            coursesMap.get(course.getSubjectId()).add(course);
        }

        return coursesMap;
    }

    @Override
    public List<CourseSku> querySkus(long courseId, String start, String end) {
        String sql = "SELECT Id FROM SG_CourseSku WHERE CourseId=? AND StartTime>=? AND StartTime<? AND Status=1 ORDER BY StartTime ASC";
        List<Long> skuIds = queryLongList(sql, new Object[] { courseId, start, end });

        return listSkus(skuIds);
    }

    @Override
    public CourseSku getSku(long skuId) {
        Set<Long> skuIds = Sets.newHashSet(skuId);
        List<CourseSku> skus = listSkus(skuIds);

        return skus.isEmpty() ? CourseSku.NOT_EXIST_COURSE_SKU : skus.get(0);
    }

    @Override
    public CourseSku getTrialSku(long skuId) {
        String sql = "SELECT Id FROM SG_CourseSku WHERE ParentId=? AND Status<>0";
        long trialSkuId = queryLong(sql, new Object[] { skuId });
        return getSku(trialSkuId);
    }

    @Override
    public CourseSku getBookedSku(long userId, long bookingId) {
        String sql = "SELECT CourseSkuId FROM SG_BookedCourse WHERE UserId=? AND Id=? AND Status<>0";
        List<Long> skuIds = queryLongList(sql, new Object[] { userId, bookingId });
        List<CourseSku> skus = listSkus(skuIds);

        return skus.isEmpty() ? CourseSku.NOT_EXIST_COURSE_SKU : skus.get(0);
    }

    @Override
    public boolean lockSku(long skuId) {
        String sql = "UPDATE SG_CourseSku SET UnlockedStock=UnlockedStock-1, LockedStock=LockedStock+1 WHERE Id=? AND Status=1 AND UnlockedStock>=1";
        return update(sql, new Object[] { skuId });
    }

    @Override
    public boolean unlockSku(long skuId) {
        String sql = "UPDATE SG_CourseSku SET UnlockedStock=UnlockedStock+1, LockedStock=LockedStock-1 WHERE Id=? AND Status<>0 AND LockedStock>=1";
        return update(sql, new Object[] { skuId });
    }

    @Override
    public boolean cancelSku(long skuId) {
        String sql = "UPDATE SG_CourseSku SET Status=3 WHERE Id=? OR ParentId=?";
        return update(sql, new Object[] { skuId, skuId });
    }

    @Override
    public BookedCourse getBookedCourse(long bookingId) {
        Set<Long> bookingIds = Sets.newHashSet(bookingId);
        List<BookedCourse> bookedCourses = listBookedCourses(bookingIds);

        return bookedCourses.isEmpty() ? BookedCourse.NOT_EXIST_BOOKED_COURSE : bookedCourses.get(0);
    }

    private List<BookedCourse> listBookedCourses(Collection<Long> bookingIds) {
        if (bookingIds.isEmpty()) return new ArrayList<BookedCourse>();

        String sql = "SELECT A.Id AS BookingId, A.UserId, A.OrderId, A.PackageId, A.CourseId, A.CourseSkuId, B.ParentId AS ParentCourseSkuId, B.StartTime, B.EndTime FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.Id IN (" + StringUtils.join(bookingIds, ",") + ") AND A.Status<>0 AND B.Status<>0";
        List<BookedCourse> bookedCourses = queryObjectList(sql, BookedCourse.class);

        Map<Long, BookedCourse> bookedCoursesMap = new HashMap<Long, BookedCourse>();
        for (BookedCourse bookedCourse : bookedCourses) {
            bookedCoursesMap.put(bookedCourse.getBookingId(), bookedCourse);
        }

        List<BookedCourse> result = new ArrayList<BookedCourse>();
        for (long bookingId : bookingIds) {
            BookedCourse bookedCourse = bookedCoursesMap.get(bookingId);
            if (bookedCourse != null) result.add(bookedCourse);
        }

        return result;
    }

    @Override
    public long listFinishedCount() {
        String sql = "SELECT COUNT(DISTINCT A.Id) FROM SG_Course A INNER JOIN SG_CourseSku B ON A.Id=B.CourseId WHERE A.ParentId=0 AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0";
        return queryLong(sql);
    }

    @Override
    public List<Course> listFinished(int start, int count) {
        String sql = "SELECT A.Id FROM SG_Course A INNER JOIN SG_CourseSku B ON A.Id=B.CourseId WHERE A.ParentId=0 AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0 GROUP BY A.Id ORDER BY MAX(B.StartTime) DESC LIMIT ?,?";
        List<Long> courseIds =  queryLongList(sql, new Object[] { start, count });

        return list(courseIds);
    }

    @Override
    public long listFinishedCount(long userId) {
        String sql = "SELECT COUNT(DISTINCT A.CourseId) FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<Course> listFinished(long userId, int start, int count) {
        String sql = "SELECT A.CourseId FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0 GROUP BY A.CourseId ORDER BY MAX(B.StartTime) DESC LIMIT ?,?";
        List<Long> courseIds =  queryLongList(sql, new Object[] { userId, start, count });

        return list(courseIds);
    }

    @Override
    public long queryNotFinishedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime>NOW() AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<BookedCourse> queryNotFinishedByUser(long userId, int start, int count) {
        String sql = "SELECT A.Id FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime>NOW() AND B.Status<>0 ORDER BY B.StartTime ASC LIMIT ?,?";
        List<Long> bookingIds = queryLongList(sql, new Object[] { userId, start, count });

        return listBookedCourses(bookingIds);
    }

    @Override
    public long queryFinishedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<BookedCourse> queryFinishedByUser(long userId, int start, int count) {
        String sql = "SELECT A.Id FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0 ORDER BY B.StartTime DESC LIMIT ?,?";
        List<Long> bookingIds = queryLongList(sql, new Object[] { userId, start, count });

        return listBookedCourses(bookingIds);
    }

    @Override
    public List<TeacherCourse> queryOngoingByTeacher(long userId) {
        String sql = "SELECT A.Id FROM SG_CourseTeacher A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.EndTime>NOW() AND B.Status<>0 ORDER BY B.StartTime ASC";
        List<Long> teacherCourseIds = queryLongList(sql, new Object[] { userId });

        return listTeacherCourses(teacherCourseIds);
    }

    @Override
    public long queryNotFinishedCountByTeacher(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseTeacher A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime>NOW() AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<TeacherCourse> queryNotFinishedByTeacher(long userId, int start, int count) {
        String sql = "SELECT A.Id FROM SG_CourseTeacher A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.StartTime>NOW() AND B.Status<>0 ORDER BY B.StartTime ASC LIMIT ?,?";
        List<Long> teacherCourseIds = queryLongList(sql, new Object[] { userId, start, count });

        return listTeacherCourses(teacherCourseIds);
    }

    private List<TeacherCourse> listTeacherCourses(Collection<Long> teacherCourseIds) {
        if (teacherCourseIds.isEmpty()) return new ArrayList<TeacherCourse>();

        String sql = "SELECT Id AS TeacherCourseId, CourseId, CourseSkuId FROM SG_CourseTeacher WHERE Id IN (" + StringUtils.join(teacherCourseIds, ",") + ") AND Status<>0";
        List<TeacherCourse> teacherCourses = queryObjectList(sql, TeacherCourse.class);

        Map<Long, TeacherCourse> teacherCoursesMap = new HashMap<Long, TeacherCourse>();
        for (TeacherCourse teacherCourse : teacherCourses) {
            teacherCoursesMap.put(teacherCourse.getTeacherCourseId(), teacherCourse);
        }

        List<TeacherCourse> result = new ArrayList<TeacherCourse>();
        for (long teacherCourseId : teacherCourseIds) {
            TeacherCourse teacherCourse = teacherCoursesMap.get(teacherCourseId);
            if (teacherCourse != null) result.add(teacherCourse);
        }

        return result;
    }

    @Override
    public long queryFinishedCountByTeacher(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseTeacher A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.EndTime<=NOW() AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<TeacherCourse> queryFinishedByTeacher(long userId, int start, int count) {
        String sql = "SELECT A.Id FROM SG_CourseTeacher A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.EndTime<=NOW() AND B.Status<>0 ORDER BY B.StartTime DESC LIMIT ?,?";
        List<Long> teacherCourseIds = queryLongList(sql, new Object[] { userId, start, count });

        return listTeacherCourses(teacherCourseIds);
    }

    @Override
    public Map<Long, Integer> queryBookedCourseCounts(Collection<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, Integer>();

        final Map<Long, Integer> map = new HashMap<Long, Integer>();
        for (long orderId : orderIds) {
            map.put(orderId, 0);
        }
        String sql = "SELECT OrderId, COUNT(1) AS Count FROM SG_BookedCourse WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND Status<>0 GROUP BY OrderId";
        query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long orderId = rs.getLong("OrderId");
                int count = rs.getInt("Count");
                map.put(orderId, count);
            }
        });

        return map;
    }

    @Override
    public Map<Long, Integer> queryFinishedCourseCounts(Collection<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, Integer>();

        final Map<Long, Integer> map = new HashMap<Long, Integer>();
        for (long orderId : orderIds) {
            map.put(orderId, 0);
        }
        String sql = "SELECT A.OrderId, COUNT(1) AS Count FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND A.Status<>0 AND B.EndTime<=NOW() AND B.Status<>0 GROUP BY A.OrderId";
        query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long orderId = rs.getLong("OrderId");
                int count = rs.getInt("Count");
                map.put(orderId, count);
            }
        });

        return map;
    }

    @Override
    public List<Long> queryBookedCourseIds(long packageId) {
        if (packageId <= 0) return new ArrayList<Long>();

        String sql = "SELECT CourseId FROM SG_BookedCourse WHERE PackageId=? AND Status<>0";
        return queryLongList(sql, new Object[] { packageId });
    }

    @Override
    public boolean booked(long packageId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE PackageId=? AND CourseId=? AND Status<>0";
        return queryInt(sql, new Object[] { packageId, courseId }) > 0;
    }

    @Override
    public long booking(final long userId, final long childId, final long orderId, final long packageId, final CourseSku sku) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_BookedCourse(UserId, ChildId, OrderId, PackageId, CourseId, CourseSkuId, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setLong(2, childId);
                ps.setLong(3, orderId);
                ps.setLong(4, packageId);
                ps.setLong(5, sku.getCourseId());
                ps.setLong(6, sku.getId());

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    @Override
    public void increaseJoined(long courseId, int joinCount) {
        String sql = "UPDATE SG_Course SET Joined=Joined+? WHERE Id=? AND Status<>0";
        update(sql, new Object[] { joinCount, courseId });
    }

    @Override
    public boolean cancel(long userId, long bookingId) {
        String sql = "UPDATE SG_BookedCourse SET Status=0 WHERE Id=? AND UserId=? AND Status<>0";
        return update(sql, new Object[] { bookingId, userId });
    }

    @Override
    public void decreaseJoined(long courseId, int joinCount) {
        String sql = "UPDATE SG_Course SET Joined=Joined-? WHERE Id=? AND Status<>0 AND Joined>=?";
        update(sql, new Object[] { joinCount, courseId, joinCount });
    }

    @Override
    public List<Long> queryBookedPackageIds(Collection<Long> userIds, long courseId, long courseSkuId) {
        if (userIds.isEmpty()) return new ArrayList<Long>();

        String sql = "SELECT PackageId FROM SG_BookedCourse WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND CourseId=? AND CourseSkuId=? AND Status<>0";
        return queryLongList(sql, new Object[] { courseId, courseSkuId });
    }

    @Override
    public void batchCancel(Collection<Long> userIds, long courseId, long courseSkuId) {
        if (userIds.isEmpty()) return;

        String sql = "UPDATE SG_BookedCourse SET Status=0 WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND CourseId=? AND CourseSkuId=? AND Status<>0";
        update(sql, new Object[] { courseId, courseSkuId });
    }

    @Override
    public Map<Long, Long> queryBookedPackageUsers(Collection<Long> userIds, long courseId, long courseSkuId) {
        String sql = (userIds == null || userIds.isEmpty()) ?
                "SELECT PackageId, UserId FROM SG_BookedCourse WHERE CourseId=? AND CourseSkuId=? AND Status<>0" :
                "SELECT PackageId, UserId FROM SG_BookedCourse WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND CourseId=? AND CourseSkuId=? AND Status<>0";
        return queryMap(sql, new Object[] { courseId, courseSkuId }, Long.class, Long.class);
    }

    @Override
    public CourseDetail getDetail(long courseId) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        long parentId = getParentId(courseId);
        if (parentId > 0) courseIds.add(parentId);

        String sql = "SELECT Id, CourseId, Abstracts, Detail FROM SG_CourseDetail WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0";
        List<CourseDetail> details = queryObjectList(sql, CourseDetail.class);

        if (details.isEmpty()) return CourseDetail.NOT_EXIST_COURSE_DETAIL;
        for (CourseDetail detail : details) {
            if (detail.getCourseId() == courseId) return detail;
        }

        return details.get(0);
    }

    @Override
    public int getInstitutionId(long courseId) {
        String sql = "SELECT InstitutionId FROM SG_Course WHERE Id=? AND Status<>0";
        return queryInt(sql, new Object[] { courseId });
    }

    @Override
    public long querySubjectId(long courseId) {
        String sql = "SELECT SubjectId FROM SG_Course WHERE Id=?";
        return queryInt(sql, new Object[] { courseId });
    }

    @Override
    public Map<Long, String> queryTips(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, String>();

        final Map<Long, String> tipsMap = new HashMap<Long, String>();
        for (long courseId : courseIds) {
            tipsMap.put(courseId, "");
        }
        String sql = "SELECT Id, Tips FROM SG_Course WHERE Id IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0";
        query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long courseId = rs.getLong("Id");
                String tips = rs.getString("tips");
                tipsMap.put(courseId, tips);
            }
        });

        return tipsMap;
    }

    @Override
    public boolean matched(long subjectId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_Course WHERE Id=? AND SubjectId=? AND Status<>0";
        return queryInt(sql, new Object[] { courseId, subjectId }) > 0;
    }

    @Override
    public boolean joined(long userId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.CourseId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0";
        return queryInt(sql, new Object[] { userId, courseId }) > 0;
    }

    @Override
    public boolean finished(long userId, long bookingId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.UserId=? AND A.Id=? AND A.CourseId=? AND A.Status<>0 AND B.StartTime<=NOW() AND B.Status<>0";
        return queryInt(sql, new Object[] { userId, bookingId, courseId }) > 0;
    }

    @Override
    public Map<Long, Long> queryCheckInCounts(Collection<Long> courseSkuIds) {
        if (courseSkuIds.isEmpty()) return new HashMap<Long, Long>();

        String sql = "SELECT CourseSkuId, COUNT(CourseSkuId) FROM SG_BookedCourse WHERE CourseSkuId IN (" + StringUtils.join(courseSkuIds, ",") + ") AND ChildId>0 AND CheckIn>0 AND Status<>0 GROUP BY CourseSkuId";
        return queryMap(sql, Long.class, Long.class);
    }

    @Override
    public Map<Long, Long> queryCommentedChildrenCount(Collection<Long> courseSkuIds) {
        if (courseSkuIds.isEmpty()) return new HashMap<Long, Long>();

        String sql = "SELECT A.CourseSkuId, COUNT(A.CourseSkuId) FROM SG_BookedCourse A INNER JOIN SG_ChildComment B ON A.CourseSkuId=B.CourseSkuId AND A.ChildId=B.ChildId WHERE A.CourseSkuId IN (" + StringUtils.join(courseSkuIds, ",") + ") AND A.ChildId>0 AND A.CheckIn>0 AND A.Status<>0 AND B.Status<>0 GROUP BY A.CourseSkuId";
        return queryMap(sql, Long.class, Long.class);
    }

    @Override
    public boolean checkin(long userId, long packageId, long courseId, long courseSkuId) {
        String sql = "UPDATE SG_BookedCourse SET CheckIn=1 WHERE UserId=? AND PackageId=? AND CourseId=? AND CourseSkuId=? AND Status<>0";
        return update(sql, new Object[] { userId, packageId, courseId, courseSkuId });
    }

    @Override
    public List<Student.Parent> queryParentWithoutChild(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id, B.Id AS UserId, B.Avatar, B.NickName, A.PackageId, A.CheckIn " +
                "FROM SG_BookedCourse A " +
                "INNER JOIN SG_User B ON A.UserId=B.Id " +
                "INNER JOIN SG_Course C ON A.CourseId=C.Id " +
                "INNER JOIN SG_CourseSku D ON A.CourseSkuId=D.Id " +
                "WHERE A.ChildId=0 AND (C.Id=? OR C.ParentId=?) AND (D.Id=? OR D.ParentId=?) AND A.Status<>0 AND B.Status<>0 AND C.Status<>0 AND D.Status<>0";
        return queryObjectList(sql, new Object[] { courseId, courseId, courseSkuId, courseSkuId }, Student.Parent.class);
    }

    @Override
    public List<Student> queryAllStudents(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id, B.UserId, B.Avatar, B.Name, B.Birthday, B.Sex, A.PackageId, A.CheckIn " +
                "FROM SG_BookedCourse A " +
                "INNER JOIN SG_Child B ON A.ChildId=B.Id " +
                "INNER JOIN SG_Course C ON A.CourseId=C.Id " +
                "INNER JOIN SG_CourseSku D ON A.CourseSkuId=D.Id " +
                "WHERE (C.Id=? OR C.ParentId=?) AND (D.Id=? OR D.ParentId=?) AND A.Status<>0 AND B.Status<>0 AND C.Status<>0 AND D.Status<>0";
        return queryObjectList(sql, new Object[] { courseId, courseId, courseSkuId, courseSkuId }, Student.class);
    }

    @Override
    public List<Student> queryCheckInStudents(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id, B.UserId, B.Avatar, B.Name, B.Birthday, B.Sex, A.PackageId, A.CheckIn " +
                "FROM SG_BookedCourse A " +
                "INNER JOIN SG_Child B ON A.ChildId=B.Id " +
                "INNER JOIN SG_Course C ON A.CourseId=C.Id " +
                "INNER JOIN SG_CourseSku D ON A.CourseSkuId=D.Id " +
                "WHERE A.CheckIn>0 AND (C.Id=? OR C.ParentId=?) AND (D.Id=? OR D.ParentId=?) AND A.Status<>0 AND B.Status<>0 AND C.Status<>0 AND D.Status<>0";
        return queryObjectList(sql, new Object[] { courseId, courseId, courseSkuId, courseSkuId }, Student.class);
    }

    @Override
    public List<Long> queryUserIdsOfTodaysCourse() {
        Date now = new Date();
        String lower = TimeUtil.SHORT_DATE_FORMAT.format(now);
        String upper = TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + 24 * 60 * 60 * 1000));
        String sql = "SELECT DISTINCT B.UserId FROM SG_CourseSku A INNER JOIN SG_BookedCourse B ON A.Id=B.CourseSkuId WHERE A.Status<>0 AND A.EndTime>=? AND A.EndTime<? AND B.Status<>0";

        return queryLongList(sql, new Object[] { lower, upper });
    }

    @Override
    public List<String> queryHotNewCourses() {
        Date now = new Date();
        String lower = TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + 24 * 60 * 60 * 1000));
        String upper = TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + 8L * 24 * 60 * 60 * 1000));
        String sql = "SELECT B.KeyWord FROM SG_CourseSku A INNER JOIN SG_Course B ON A.CourseId=B.Id WHERE A.Status=1 AND A.StartTime>=? AND A.StartTime<? AND B.Status=1 AND B.ParentId=0 AND B.KeyWord<>'' ORDER BY B.Joined DESC LIMIT 3";

        return queryStringList(sql, new Object[] { lower, upper });
    }

    @Override
    public List<CourseSku> queryCourseSkusClosedToday() {
        Date now = new Date();
        String lower = TimeUtil.SHORT_DATE_FORMAT.format(now);
        String upper = TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + 24 * 60 * 60 * 1000));
        String sql = "SELECT Id FROM SG_CourseSku WHERE Status<>0 AND Deadline>=? AND Deadline<?";
        List<Long> skuIds = queryLongList(sql, new Object[] { lower, upper });

        return listSkus(skuIds);
    }

    @Override
    public List<Long> queryBookedUserIds(long courseSkuId) {
        String sql = "SELECT DISTINCT A.UserId FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.Status<>0 AND B.Status<>0 AND (B.Id=? OR B.ParentId=?)";
        return queryLongList(sql, new Object[] { courseSkuId, courseSkuId });
    }
}
