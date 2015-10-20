package cn.momia.service.course.base.impl;

import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseBookImage;
import cn.momia.service.course.base.CourseDetail;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
import cn.momia.service.course.base.Institution;
import cn.momia.service.course.base.Teacher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseServiceImpl extends DbAccessService implements CourseService {
    private PoiServiceApi poiServiceApi;

    public void setPoiServiceApi(PoiServiceApi poiServiceApi) {
        this.poiServiceApi = poiServiceApi;
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

        String sql = "SELECT Id, SubjectId, Title, Cover, MinAge, MaxAge, Joined, Price, Goal, Flow, Tips, InstitutionId FROM SG_Course WHERE Id IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<Course> courses = queryList(sql, Course.class);

        Set<Integer> institutionIds = new HashSet<Integer>();
        for (Course course : courses) {
            institutionIds.add(course.getInstitutionId());
        }
        Map<Integer, Institution> institutionsMap = queryInstitutions(institutionIds);
        Map<Long, List<CourseImage>> imgsMap = queryImgs(courseIds);
        Map<Long, CourseBook> booksMap = queryBooks(courseIds);
        Map<Long, List<CourseSku>> skusMap = querySkus(courseIds);

        for (Course course : courses) {
            Institution institution = institutionsMap.get(course.getInstitutionId());
            if (institution != null) course.setInstitution(institution.getIntro());
            course.setImgs(imgsMap.get(course.getId()));
            course.setBook(booksMap.get(course.getId()));
            course.setSkus(skusMap.get(course.getId()));
        }

        return courses;
    }

    private Map<Integer, Institution> queryInstitutions(Collection<Integer> institutionIds) {
        if (institutionIds.isEmpty()) return new HashMap<Integer, Institution>();

        String sql = "SELECT Id, Name, Cover, Intro FROM SG_Institution WHERE Id IN (" + StringUtils.join(institutionIds, ",") + ") AND Status=1";
        List<Institution> institutions = queryList(sql, Institution.class);

        Map<Integer, Institution> institutionsMap = new HashMap<Integer, Institution>();
        for (Institution institution : institutions) {
            institutionsMap.put(institution.getId(), institution);
        }

        return institutionsMap;
    }

    private Map<Long, List<CourseImage>> queryImgs(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<CourseImage>>();

        String sql = "SELECT Id, CourseId, Url, Width, Height FROM SG_CourseImg WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<CourseImage> imgs = queryList(sql, CourseImage.class);

        final Map<Long, List<CourseImage>> imgsMap = new HashMap<Long, List<CourseImage>>();
        for (long courseId : courseIds) imgsMap.put(courseId, new ArrayList<CourseImage>());
        for (CourseImage img : imgs) imgsMap.get(img.getCourseId()).add(img);

        return imgsMap;
    }

    private Map<Long, CourseBook> queryBooks(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, CourseBook>();

        String sql = "SELECT Id, CourseId, Img, `Order` FROM SG_CourseBook WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<CourseBookImage> imgs = queryList(sql, CourseBookImage.class);

        final Map<Long, List<CourseBookImage>> imgsMap = new HashMap<Long, List<CourseBookImage>>();
        for (long courseId : courseIds) imgsMap.put(courseId, new ArrayList<CourseBookImage>());
        for (CourseBookImage img : imgs) imgsMap.get(img.getCourseId()).add(img);

        Map<Long, CourseBook> booksMap = new HashMap<Long, CourseBook>();
        for (long courseId : courseIds) {
            List<CourseBookImage> bookImgs = imgsMap.get(courseId);
            List<String> urls = new ArrayList<String>();
            for (CourseBookImage bookImg : bookImgs) urls.add(bookImg.getImg());

            CourseBook book = new CourseBook();
            book.setImgs(urls);

            booksMap.put(courseId, book);
        }

        return booksMap;
    }

    private Map<Long, List<CourseSku>> querySkus(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<CourseSku>>();

        String sql = "SELECT Id FROM SG_CourseSku WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<Long> skuIds = queryLongList(sql);
        List<CourseSku> skus = listSkus(skuIds);

        Map<Long, List<CourseSku>> skusMap = new HashMap<Long, List<CourseSku>>();
        for (long courseId : courseIds) skusMap.put(courseId, new ArrayList<CourseSku>());
        for (CourseSku sku : skus) skusMap.get(sku.getCourseId()).add(sku);

        return skusMap;
    }

    private List<CourseSku> listSkus(List<Long> skuIds) {
        if (skuIds.isEmpty()) return new ArrayList<CourseSku>();

        String sql = "SELECT Id, CourseId, StartTime, EndTime, Deadline, Stock, UnlockedStock, LockedStock, PlaceId FROM SG_CourseSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status=1";
        List<CourseSku> skus = queryList(sql, CourseSku.class);

        return completeSkus(skus);
    }

    private List<CourseSku> completeSkus(List<CourseSku> skus) {
        Set<Integer> placeIds = new HashSet<Integer>();
        for (CourseSku sku : skus) placeIds.add(sku.getPlaceId());

        List<PlaceDto> places = poiServiceApi.list(placeIds);
        Map<Integer, PlaceDto> placesMap = new HashMap<Integer, PlaceDto>();
        for (PlaceDto place : places) placesMap.put(place.getId(), place);

        List<CourseSku> completedSkus = new ArrayList<CourseSku>();
        for (CourseSku sku : skus) {
            PlaceDto place = placesMap.get(sku.getPlaceId());
            if (place == null) continue;

            sku.setPlace(buildCourseSkuPlace(place));
            completedSkus.add(sku);
        }

        return completedSkus;
    }

    private CourseSkuPlace buildCourseSkuPlace(PlaceDto place) {
        CourseSkuPlace courseSkuPlace = new CourseSkuPlace();
        courseSkuPlace.setId(place.getId());
        courseSkuPlace.setCityId(place.getCityId());
        courseSkuPlace.setRegionId(place.getRegionId());
        courseSkuPlace.setName(place.getName());
        courseSkuPlace.setAddress(place.getAddress());
        courseSkuPlace.setLng(place.getLng());
        courseSkuPlace.setLat(place.getLat());

        return courseSkuPlace;
    }

    @Override
    public long queryBookImgCount(long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseBook WHERE CourseId=? AND Status=1";
        return queryLong(sql, new Object[] { courseId });
    }

    @Override
    public List<String> queryBookImgs(long courseId, int start, int count) {
        String sql = "SELECT Img FROM SG_CourseBook WHERE CourseId=? AND Status=1 ORDER BY `Order` ASC LIMIT ?,?";
        return queryStringList(sql, new Object[] { courseId, start, count });
    }

    @Override
    public long queryTeacherCount(long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseTeacher WHERE CourseId=? AND Status=1";
        return queryLong(sql, new Object[] { courseId });
    }

    @Override
    public List<Teacher> queryTeachers(long courseId, int start, int count) {
        String sql = "SELECT TeacherId FROM SG_CourseTeacher WHERE CourseId=? AND Status=1 LIMIT ?,?";
        List<Integer> teacherIds = queryIntList(sql, new Object[] { courseId, start, count });

        return listTeachers(teacherIds);
    }

    private List<Teacher> listTeachers(List<Integer> teacherIds) {
        if (teacherIds.isEmpty()) return new ArrayList<Teacher>();

        String sql = "SELECT Id, Name, Avatar, Education, Experience FROM SG_Teacher WHERE Id IN (" + StringUtils.join(teacherIds, ",") + ") AND Status=1";
        return queryList(sql, Teacher.class);
    }

    @Override
    public long queryCountBySubject(int subjectId) {
        String sql = "SELECT COUNT(1) FROM SG_Course WHERE SubjectId=? AND Status=1";
        return queryLong(sql, new Object[] { subjectId });
    }

    @Override
    public List<Course> queryBySubject(int subjectId, int start, int count) {
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId, start, count });

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

        String sql = "SELECT Id FROM SG_Course WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1";
        List<Long> courseIds = queryLongList(sql);
        List<Course> courses = list(courseIds);

        Map<Long, List<Course>> coursesMap = new HashMap<Long, List<Course>>();
        for (long subjectId : subjectIds) coursesMap.put(subjectId, new ArrayList<Course>());
        for (Course course : courses) coursesMap.get(course.getSubjectId()).add(course);

        return coursesMap;
    }

    @Override
    public List<CourseSku> querySkus(long courseId, String start, String end) {
        String sql = "SELECT Id FROM SG_CourseSku WHERE CourseId=? AND StartTime>=? AND EndTime<? AND Status=1 ORDER BY StartTime ASC";
        List<Long> skuIds = queryLongList(sql, new Object[] { courseId, start, end });

        return listSkus(skuIds);
    }

    @Override
    public long queryNotFinishedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime>NOW()";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<Course> queryNotFinishedByUser(long userId, int start, int count) {
        String sql = "SELECT CourseSkuId FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime>NOW() LIMIT ?,?";
        List<Long> skuIds = queryLongList(sql, new Object[] { userId, start, count });
        List<CourseSku> skus = listSkus(skuIds);

        Set<Long> courseIds = new HashSet<Long>();
        for (CourseSku sku : skus) {
            courseIds.add(sku.getCourseId());
        }
        List<Course> courses = list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<Course> notFinishedCourses = new ArrayList<Course>();
        for (CourseSku sku : skus) {
            Course course = coursesMap.get(sku.getCourseId());
            if (course == null) continue;

            Course notFinishedCourse = course.clone();
            notFinishedCourse.setSkus(Lists.newArrayList(sku));

            notFinishedCourses.add(notFinishedCourse);
        }

        return notFinishedCourses;
    }

    @Override
    public long queryFinishedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime>NOW()";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<Course> queryFinishedByUser(long userId, int start, int count) {
        String sql = "SELECT CourseSkuId FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime<=NOW() LIMIT ?,?";
        List<Long> skuIds = queryLongList(sql, new Object[] { userId, start, count });
        List<CourseSku> skus = listSkus(skuIds);

        Set<Long> courseIds = new HashSet<Long>();
        for (CourseSku sku : skus) {
            courseIds.add(sku.getCourseId());
        }
        List<Course> courses = list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<Course> finishedCourses = new ArrayList<Course>();
        for (CourseSku sku : skus) {
            Course course = coursesMap.get(sku.getCourseId());
            if (course == null) continue;

            Course finishedCourse = course.clone();
            finishedCourse.setSkus(Lists.newArrayList(sku));

            finishedCourses.add(finishedCourse);
        }

        return finishedCourses;
    }

    @Override
    public Map<Long, Integer> queryBookedCourseCounts(Set<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, Integer>();

        final Map<Long, Integer> map = new HashMap<Long, Integer>();
        for (long orderId : orderIds) map.put(orderId, 0);
        String sql = "SELECT OrderId, Count FROM SG_BookedCourse WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long orderId = rs.getLong("OrderId");
                int count = rs.getInt("Count");
                map.put(orderId, map.get(orderId) + count);
            }
        });

        return map;
    }

    @Override
    public Map<Long, Integer> queryFinishedCourseCounts(Set<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, Integer>();

        final Map<Long, Integer> map = new HashMap<Long, Integer>();
        for (long orderId : orderIds) map.put(orderId, 0);
        String sql = "SELECT OrderId, Count FROM SG_BookedCourse WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND EndTime<=NOW() AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long orderId = rs.getLong("OrderId");
                int count = rs.getInt("Count");
                map.put(orderId, map.get(orderId) + count);
            }
        });

        return map;
    }

    @Override
    public boolean booking(long userId, long packageId, long skuId) {
        return false;
    }

    @Override
    public boolean isFavored(long userId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_Favorite WHERE UserId=? AND `Type`=1 AND RefId=? AND Status=1";
        return queryInt(sql, new Object[] { userId, courseId }) > 0;
    }

    @Override
    public boolean favor(long userId, long courseId) {
        long favoretId = getFavoretId(userId, courseId);
        if (favoretId > 0) {
            String sql = "UPDATE SG_Favorite SET Status=1 WHERE Id=? AND UserId=? AND `Type`=1 AND RefId=?";
            return jdbcTemplate.update(sql, new Object[] { favoretId, userId, courseId }) == 1;
        } else {
            String sql = "INSERT INTO SG_Favorite(UserId, `Type`, RefId, AddTime) VALUES (?, 1, ?, NOW())";
            return jdbcTemplate.update(sql, new Object[] { userId, courseId }) == 1;
        }
    }


    private long getFavoretId(long userId, long courseId) {
        String sql = "SELECT Id FROM SG_Favorite WHERE UserId=? AND `Type`=1 AND RefId=?";
        return queryLong(sql, new Object[] { userId, courseId });
    }

    @Override
    public boolean unfavor(long userId, long courseId) {
        String sql = "UPDATE SG_Favorite SET Status=0 WHERE UserId=? AND `Type`=1 AND RefId=?";
        return jdbcTemplate.update(sql, new Object[] { userId, courseId }) > 0;
    }

    @Override
    public CourseDetail getDetail(long courseId) {
        String sql = "SELECT Id, CourseId, Abstracts, Detail FROM SG_CourseDetail WHERE CourseId=? AND Status=1";
        return queryObject(sql, new Object[] { courseId }, CourseDetail.class, CourseDetail.NOT_EXIST_COURSE_DETAIL);
    }

    @Override
    public Institution getInstitution(long courseId) {
        String sql = "SELECT B.Id, B.Name, B.Cover, B.Intro FROM SG_Course A INNER JOIN SG_Institution B ON A.InstitutionId=B.Id WHERE A.Id=? AND A.Status=1 AND B.Status=1";
        return queryObject(sql, new Object[] { courseId }, Institution.class, Institution.NOT_EXIST_INSTITUTION);
    }
}
