package cn.momia.service.course.base.impl;

import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseBookImage;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
import cn.momia.service.course.base.Teacher;
import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
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
    private static final String[] COURSE_FIELDS = { "Id", "SubjectId", "Title", "Cover", "MinAge", "MaxAge", "Joined", "Price", "Goal", "Flow", "Tips", "Institution" };
    private static final String[] COURSE_IMG_FIELDS = { "Id", "CourseId", "Url", "Width", "Height" };
    private static final String[] COURSE_BOOK_FIELDS = { "Id", "CourseId", "Img", "`Order`" };
    private static final String[] COURSE_SKU_FIELDS = { "Id", "CourseId", "StartTime", "EndTime", "Deadline", "Stock", "UnlockedStock", "LockedStock", "PlaceId" };

    private PoiServiceApi poiServiceApi;

    private Function<ResultSet, Course> courseFunc = new Function<ResultSet, Course>() {
        @Override
        public Course apply(ResultSet rs) {
            try {
                Course course = new Course();
                course.setId(rs.getLong("Id"));
                course.setSubjectId(rs.getLong("SubjectId"));
                course.setTitle(rs.getString("Title"));
                course.setCover(rs.getString("Cover"));
                course.setMinAge(rs.getInt("MinAge"));
                course.setMaxAge(rs.getInt("MaxAge"));
                course.setJoined(rs.getInt("Joined"));
                course.setPrice(rs.getBigDecimal("Price"));
                course.setGoal(rs.getString("Goal"));
                course.setFlow(rs.getString("Flow"));
                course.setTips(rs.getString("Tips"));
                course.setInstitution(rs.getString("Institution"));

                return course;
            } catch (Exception e) {
                return Course.NOT_EXIST_COURSE;
            }
        }
    };

    private Function<ResultSet, CourseImage> courseImageFunc = new Function<ResultSet, CourseImage>() {
        @Override
        public CourseImage apply(ResultSet rs) {
            try {
                CourseImage img = new CourseImage();
                img.setId(rs.getLong("Id"));
                img.setCourseId(rs.getLong("CourseId"));
                img.setUrl(rs.getString("Url"));
                img.setWidth(rs.getInt("Width"));
                img.setHeight(rs.getInt("Height"));

                return img;
            } catch (Exception e) {
                return CourseImage.NOT_EXIST_COURSE_IMAGE;
            }
        }
    };

    private Function<ResultSet, CourseBookImage> courseBookImageFunc = new Function<ResultSet, CourseBookImage>() {
        @Override
        public CourseBookImage apply(ResultSet rs) {
            try {
                CourseBookImage img = new CourseBookImage();
                img.setId(rs.getLong("Id"));
                img.setCourseId(rs.getLong("CourseId"));
                img.setImg(rs.getString("Img"));
                img.setOrder(rs.getInt("Order"));

                return img;
            } catch (Exception e) {
                return CourseBookImage.NOT_EXIST_COURSE_BOOK_IMAGE;
            }
        }
    };

    private Function<ResultSet, CourseSku> courseSkuFunc = new Function<ResultSet, CourseSku>() {
        @Override
        public CourseSku apply(ResultSet rs) {
            try {
                CourseSku sku = new CourseSku();
                sku.setId(rs.getLong("Id"));
                sku.setCourseId(rs.getLong("CourseId"));
                sku.setStartTime(rs.getTimestamp("StartTime"));
                sku.setEndTime(rs.getTimestamp("EndTime"));
                sku.setDeadline(rs.getTimestamp("Deadline"));
                sku.setStock(rs.getInt("Stock"));
                sku.setUnlockedStock(rs.getInt("UnlockedStock"));
                sku.setLockedStock(rs.getInt("LockedStock"));
                sku.setPlaceId(rs.getInt("PlaceId"));

                return sku;
            } catch (Exception e) {
                return CourseSku.NOT_EXIST_COURSE_SKU;
            }
        }
    };

    public void setPoiServiceApi(PoiServiceApi poiServiceApi) {
        this.poiServiceApi = poiServiceApi;
    }

    @Override
    public Course get(long id) {
        Collection<Long> ids = Sets.newHashSet(id);
        List<Course> courses = list(ids);

        return courses.isEmpty() ? Course.NOT_EXIST_COURSE : courses.get(0);
    }

    @Override
    public List<Course> list(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<Course>();

        List<Course> courses = new ArrayList<Course>();
        String sql = "SELECT " + joinFields() + " FROM SG_Course WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<Course>(courses, courseFunc));

        Map<Long, List<CourseSku>> skusMap = querySkus(ids);
        Map<Long, List<CourseImage>> imgsMap = queryImgs(ids);
        Map<Long, CourseBook> booksMap = queryBooks(ids);

        for (Course course : courses) {
            course.setImgs(imgsMap.get(course.getId()));
            course.setBook(booksMap.get(course.getId()));
            course.setSkus(skusMap.get(course.getId()));
        }

        return courses;
    }

    private String joinFields() {
        return StringUtils.join(COURSE_FIELDS, ",");
    }

    private Map<Long, List<CourseImage>> queryImgs(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<CourseImage>>();

        List<CourseImage> imgs = new ArrayList<CourseImage>();
        String sql = "SELECT " + joinImgFields() + " FROM SG_CourseImg WHERE CourseId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<CourseImage>(imgs, courseImageFunc));

        final Map<Long, List<CourseImage>> imgsMap = new HashMap<Long, List<CourseImage>>();
        for (long id : ids) imgsMap.put(id, new ArrayList<CourseImage>());
        for (CourseImage img : imgs) imgsMap.get(img.getCourseId()).add(img);

        return imgsMap;
    }

    private String joinImgFields() {
        return StringUtils.join(COURSE_IMG_FIELDS, ",");
    }

    private Map<Long, CourseBook> queryBooks(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, CourseBook>();

        List<CourseBookImage> imgs = new ArrayList<CourseBookImage>();
        String sql = "SELECT " + joinBookFields() + " FROM SG_CourseBook WHERE CourseId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<CourseBookImage>(imgs, courseBookImageFunc));

        final Map<Long, List<CourseBookImage>> imgsMap = new HashMap<Long, List<CourseBookImage>>();
        for (long id : ids) imgsMap.put(id, new ArrayList<CourseBookImage>());
        for (CourseBookImage img : imgs) imgsMap.get(img.getCourseId()).add(img);

        Map<Long, CourseBook> booksMap = new HashMap<Long, CourseBook>();
        for (long id : ids) {
            List<CourseBookImage> bookImgs = imgsMap.get(id);
            List<String> urls = new ArrayList<String>();
            for (CourseBookImage bookImg : bookImgs) urls.add(bookImg.getImg());

            CourseBook book = new CourseBook();
            book.setImgs(urls);

            booksMap.put(id, book);
        }

        return booksMap;
    }

    private String joinBookFields() {
        return StringUtils.join(COURSE_BOOK_FIELDS, ",");
    }

    private Map<Long, List<CourseSku>> querySkus(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<CourseSku>>();

        List<CourseSku> skus = new ArrayList<CourseSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_CourseSku WHERE CourseId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<CourseSku>(skus, courseSkuFunc));

        skus = completeSkus(skus);

        Map<Long, List<CourseSku>> skusMap = new HashMap<Long, List<CourseSku>>();
        for (long id : ids) skusMap.put(id, new ArrayList<CourseSku>());
        for (CourseSku sku : skus) skusMap.get(sku.getCourseId()).add(sku);

        return skusMap;
    }

    private String joinSkuFields() {
        return StringUtils.join(COURSE_SKU_FIELDS, ",");
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
    public long queryTeacherCount(long id) {
        String sql = "SELECT COUNT(1) FROM SG_CourseTeacher WHERE CourseId=? AND Status=1";
        return jdbcTemplate.queryForObject(sql, new Object[] { id }, Long.class);
    }

    @Override
    public List<Teacher> queryTeachers(long id, int start, int count) {
        String sql = "SELECT TeacherId FROM SG_CourseTeacher WHERE CourseId=? AND Status=1 LIMIT ?,?";
        List<Long> teacherIds = jdbcTemplate.queryForList(sql, new Object[] { id, start, count }, Long.class);

        return listTeachers(teacherIds);
    }

    private List<Teacher> listTeachers(List<Long> teacherIds) {
//        if (teacherIds.isEmpty()) return new ArrayList<Teacher>();
//
//        String sql = "SELECT * FROM SG_Teacher WHERE Id IN (" + StringUtils.join(teacherIds, ",") + ") AND Status=1";
//        jdbcTemplate.queryForList(sql)
        return null;
    }

    @Override
    public long queryCountBySubject(int subjectId) {
        String sql = "SELECT COUNT(1) FROM SG_Course WHERE SubjectId=? AND Status=1";
        return jdbcTemplate.query(sql, new Object[] { subjectId }, new CountResultSetExtractor());
    }

    @Override
    public List<Course> queryBySubject(int subjectId, int start, int count) {
        List<Long> courseIds = new ArrayList<Long>();
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { subjectId, start, count }, new LongListResultSetExtractor(courseIds));

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

        List<Long> courseIds = new ArrayList<Long>();
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new LongListResultSetExtractor(courseIds));
        List<Course> courses = list(courseIds);

        Map<Long, List<Course>> coursesMap = new HashMap<Long, List<Course>>();
        for (long subjectId : subjectIds) coursesMap.put(subjectId, new ArrayList<Course>());
        for (Course course : courses) coursesMap.get(course.getSubjectId()).add(course);

        return coursesMap;
    }

    @Override
    public List<CourseSku> querySkus(long id, String start, String end) {
        List<CourseSku> skus = new ArrayList<CourseSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_CourseSku WHERE CourseId=? AND StartTime>=? AND EndTime<? AND Status=1 ORDER BY StartTime ASC";
        jdbcTemplate.query(sql, new Object[] { id, start, end }, new ListResultSetExtractor<CourseSku>(skus, courseSkuFunc));

        return completeSkus(skus);
    }

    @Override
    public long queryNotFinishedSkuCountByUser(long userId) {
        return 0;
    }

    @Override
    public List<CourseSku> queryNotFinishedSkuByUser(long userId) {
        return null;
    }

    @Override
    public long queryFinishedSkuCountByUser(long userId) {
        return 0;
    }

    @Override
    public List<CourseSku> queryFinishedSkuByUser(long userId) {
        return null;
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
    public boolean isFavored(long userId, long id) {
        String sql = "SELECT COUNT(1) FROM SG_Favorite WHERE UserId=? AND `Type`=1 AND RefId=? AND Status=1";
        return jdbcTemplate.query(sql, new Object[] { userId, id }, new CountResultSetExtractor()) > 0;
    }

    @Override
    public boolean favor(long userId, long id) {
        long favoretId = getFavoretId(userId, id);
        if (favoretId > 0) {
            String sql = "UPDATE SG_Favorite SET Status=1 WHERE Id=? AND UserId=? AND `Type`=1 AND RefId=?";
            return jdbcTemplate.update(sql, new Object[] { favoretId, userId, id }) == 1;
        } else {
            String sql = "INSERT INTO SG_Favorite(UserId, `Type`, RefId, AddTime) VALUES (?, 1, ?, NOW())";
            return jdbcTemplate.update(sql, new Object[] { userId, id }) == 1;
        }
    }


    private long getFavoretId(long userId, long id) {
        String sql = "SELECT Id FROM SG_Favorite WHERE UserId=? AND `Type`=1 AND RefId=?";
        return jdbcTemplate.query(sql, new Object[] { userId, id }, new LongResultSetExtractor());
    }

    @Override
    public boolean unfavor(long userId, long id) {
        String sql = "UPDATE SG_Favorite SET Status=0 WHERE UserId=? AND `Type`=1 AND RefId=?";
        return jdbcTemplate.update(sql, new Object[] { userId, id }) > 0;
    }
}
