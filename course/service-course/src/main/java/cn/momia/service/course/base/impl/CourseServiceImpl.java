package cn.momia.service.course.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CourseServiceImpl extends DbAccessService implements CourseService {
    private static final String[] COURSE_FIELDS = { "Id", "Title", "Cover", "MinAge", "MaxAge", "Joined", "Price", "Recommend", "Flow", "Extra" };

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
            course.setRecommend(JSON.parseArray(rs.getString("Recommend")));
            course.setFlow(JSON.parseArray(rs.getString("Flow")));
            course.setExtra(JSON.parseArray(rs.getString("Extra")));

            return course;
        } catch (Exception e) {
            return Course.NOT_EXIST_COURSE;
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
}
