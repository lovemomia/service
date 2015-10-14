package cn.momia.service.course.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseServiceImpl extends DbAccessService implements CourseService {
    private static final String[] COURSE_FIELDS = { "Id", "Title", "Cover", "MinAge", "MaxAge", "Joined", "Price", "Goal", "Flow", "Tips", "Institution" };
    private static final String[] COURSE_SKU_FIELDS = { "Id", "CourseId", "StartTime", "EndTime", "Deadline", "Stock", "UnlockedStock", "LockedStock", "PlaceId" };

    @Override
    public Course get(long id) {
        String sql = "SELECT " + joinFields() + " FROM SG_Course WHERE Id=? AND Status=1";
        Course course = jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Course>() {
            @Override
            public Course extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? buildCourse(rs) : Course.NOT_EXIST_COURSE;
            }
        });

        if (course.exists()) {
            course.setSkus(getSkus(id));
            course.setImgs(getImgs(id));
            course.setBook(getBook(id));
        }

        return course;
    }

    private String joinFields() {
        return StringUtils.join(COURSE_FIELDS, ",");
    }

    private Course buildCourse(ResultSet rs) {
        try {
            Course course = new Course();
            course.setId(rs.getLong("Id"));
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

    private List<CourseSku> getSkus(long id) {
        final List<CourseSku> skus = new ArrayList<CourseSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_CourseSku WHERE CourseId=? AND Status=1";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                CourseSku sku = buildCourseSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
    }

    private String joinSkuFields() {
        return StringUtils.join(COURSE_SKU_FIELDS, ",");
    }

    private CourseSku buildCourseSku(ResultSet rs) {
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

    private List<String> getImgs(long id) {
        final List<String> imgs = new ArrayList<String>();
        String sql = "SELECT Url FROM SG_CourseImg WHERE CourseId=? AND Status=1";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(rs.getString("Url"));
            }
        });

        return imgs;
    }

    private CourseBook getBook(long id) {
        final List<String> imgs = new ArrayList<String>();
        String sql = "SELECT Img FROM SG_CourseBook WHERE CourseId=? AND Status=1 ORDER BY `Order` ASC, AddTime DESC";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(rs.getString("Img"));
            }
        });

        CourseBook book = new CourseBook();
        book.setImgs(imgs);

        return book;
    }

    @Override
    public List<Course> list(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<Course>();

        final List<Course> courses = new ArrayList<Course>();
        String sql = "SELECT " + joinFields() + " FROM SG_Course WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Course course = buildCourse(rs);
                if (course.exists()) courses.add(course);
            }
        });

        List<CourseSku> skus = getSkus(ids);
        Map<Long, List<CourseSku>> skusMap = new HashMap<Long, List<CourseSku>>();
        for (CourseSku sku : skus) {
            long courseId = sku.getCourseId();
            List<CourseSku> skusOfCourse = skusMap.get(courseId);
            if (skusOfCourse == null) {
                skusOfCourse = new ArrayList<CourseSku>();
                skusMap.put(courseId, skusOfCourse);
            }
            skusOfCourse.add(sku);
        }

        for (Course course : courses) {
            course.setSkus(skusMap.get(course.getId()));
        }

        return courses;
    }

    private List<CourseSku> getSkus(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<CourseSku>();

        final List<CourseSku> skus = new ArrayList<CourseSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_CourseSku WHERE CourseId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                CourseSku sku = buildCourseSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
    }

    @Override
    public long queryRecommendCount(int cityId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseRecommend WHERE (CityId=? OR CityId=0) AND Status=1";

        return jdbcTemplate.query(sql, new Object[] { cityId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<Course> queryRecommend(int cityId, int start, int count) {
        final List<Long> courseIds = new ArrayList<Long>();
        String sql = "SELECT CourseId FROM SG_CourseRecommend WHERE (CityId=? OR CityId=0) AND Status=1 ORDER BY Weight DESC, AddTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { cityId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                courseIds.add(rs.getLong("CourseId"));
            }
        });

        return list(courseIds);
    }

    @Override
    public long queryCountBySubject(int subjectId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectCourse WHERE SubjectId=? AND Status=1";

        return jdbcTemplate.query(sql, new Object[] { subjectId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<Course> queryBySubject(int subjectId, int start, int count) {
        final List<Long> courseIds = new ArrayList<Long>();
        String sql = "SELECT CourseId FROM SG_SubjectCourse WHERE SubjectId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { subjectId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                courseIds.add(rs.getLong("CourseId"));
            }
        });

        return list(courseIds);
    }

    @Override
    public List<CourseSku> querySkus(long id, String start, String end) {
        final List<CourseSku> skus = new ArrayList<CourseSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_CourseSku WHERE CourseId=? AND StartTime>=? AND EndTime<? AND Status=1 ORDER BY StartTime ASC";
        jdbcTemplate.query(sql, new Object[] { id, start, end }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                CourseSku sku = buildCourseSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
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
}
