package cn.momia.service.course.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.base.Course;
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

public class CourseServiceImpl extends DbAccessService implements CourseService {
    private static final String[] COURSE_FIELDS = { "Id", "Title", "Cover", "MinAge", "MaxAge", "Joined", "Price", "Goal", "Flow", "Extra" };
    private static final String[] COURSE_SKU_FIELDS = { "Id", "CourseId", "StartTime", "EndTime", "Deadline", "Stock", "UnlockedStock", "LockedStock", "PlaceId" };

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
            course.setGoal(JSON.parseArray(rs.getString("Goal")));
            course.setFlow(JSON.parseArray(rs.getString("Flow")));
            course.setExtra(JSON.parseArray(rs.getString("Extra")));

            return course;
        } catch (Exception e) {
            return Course.NOT_EXIST_COURSE;
        }
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
}
