package cn.momia.service.course.subject.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubjectServiceImpl extends DbAccessService implements SubjectService {
    private static final String[] SUBJECT_FIELDS = { "Id", "Title", "Cover", "MinAge", "MaxAge", "Joined", "Intro", "Notice" };
    private static final String[] SUBJECT_SKU_FIELDS = { "Id", "SubjectId", "`Desc`", "Price", "OriginalPrice", "Adult", "Child", "CourseCount", "Time" };

    @Override
    public Subject get(long id) {
        String sql = "SELECT " + joinFields() + " FROM SG_Subject WHERE Id=? AND Status=1";
        Subject subject = jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Subject>() {
            @Override
            public Subject extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? buildSubject(rs) : Subject.NOT_EXIST_SUBJECT;
            }
        });
        if (subject.exists()) {
            subject.setImgs(getImgs(id));
            subject.setSkus(getSkus(id));
        }

        return subject;
    }

    private String joinFields() {
        return StringUtils.join(SUBJECT_FIELDS, ",");
    }

    private Subject buildSubject(ResultSet rs) {
        try {
            Subject subject = new Subject();
            subject.setId(rs.getLong("Id"));
            subject.setTitle(rs.getString("Title"));
            subject.setCover(rs.getString("Cover"));
            subject.setMinAge(rs.getInt("MinAge"));
            subject.setMaxAge(rs.getInt("MaxAge"));
            subject.setJoined(rs.getInt("Joined"));
            subject.setIntro(rs.getString("Intro"));
            subject.setNotice(JSON.parseArray(rs.getString("Notice")));

            return subject;
        } catch (Exception e) {
            return Subject.NOT_EXIST_SUBJECT;
        }
    }

    private List<String> getImgs(long id) {
        final List<String> imgs = new ArrayList<String>();
        String sql = "SELECT Url FROM SG_SubjectImg WHERE SubjectId=? AND Status=1";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(rs.getString("Url"));
            }
        });

        return imgs;
    }

    private List<SubjectSku> getSkus(long id) {
        final List<SubjectSku> skus = new ArrayList<SubjectSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectSku WHERE SubjectId=? AND Status=1";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SubjectSku sku = buildSubjectSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
    }

    private String joinSkuFields() {
        return StringUtils.join(SUBJECT_SKU_FIELDS, ",");
    }

    private SubjectSku buildSubjectSku(ResultSet rs) {
        try {
            SubjectSku sku = new SubjectSku();
            sku.setId(rs.getLong("Id"));
            sku.setSubjectId(rs.getLong("SubjectId"));
            sku.setDesc(rs.getString("Desc"));
            sku.setPrice(rs.getBigDecimal("Price"));
            sku.setOriginalPrice(rs.getBigDecimal("OriginalPrice"));
            sku.setAdult(rs.getInt("Adult"));
            sku.setChild(rs.getInt("Child"));
            sku.setCourseCount(rs.getInt("CourseCount"));
            sku.setTime(rs.getInt("Time"));

            return sku;
        } catch (Exception e) {
            return SubjectSku.NOT_EXIST_SUBJECT_SKU;
        }
    }

    @Override
    public SubjectSku getSku(long skuId) {
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectSku WHERE Id=? AND Status=1";

        return jdbcTemplate.query(sql, new Object[] { skuId }, new ResultSetExtractor<SubjectSku>() {
            @Override
            public SubjectSku extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? buildSubjectSku(rs) : SubjectSku.NOT_EXIST_SUBJECT_SKU;
            }
        });
    }

    @Override
    public List<SubjectSku> listSkus(long id) {
        return getSkus(id);
    }

    @Override
    public Map<Long, String> queryTitlesByCourse(Set<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, String>();

        final Set<Long> subjectIds = new HashSet<Long>();
        final Map<Long, Collection<Long>> idsMap = new HashMap<Long, Collection<Long>>();
        String sql = "SELECT SubjectId, CourseId FROM SG_SubjectCourse WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long subjectId = rs.getLong("SubjectId");
                long courseId = rs.getLong("CourseId");
                subjectIds.add(subjectId);
                Collection<Long> ids = idsMap.get(courseId);
                if (ids == null) {
                    ids = new HashSet<Long>();
                    idsMap.put(courseId, ids);
                }
                ids.add(subjectId);
            }
        });

        final Map<Long, String> subjectTitles = new HashMap<Long, String>();
        sql = "SELECT Id, Title FROM SG_Subject WHERE Id IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                subjectTitles.put(rs.getLong("Id"), rs.getString("Title"));
            }
        });

        Map<Long, String> courseSubjects = new HashMap<Long, String>();
        for (long courseId : courseIds) {
            Collection<Long> ids = idsMap.get(courseId);
            if (ids == null) {
                courseSubjects.put(courseId, "");
            } else {
                List<String> titles = new ArrayList<String>();
                for (long id : ids) {
                    String title = subjectTitles.get(id);
                    if (!StringUtils.isBlank(title)) titles.add(title);
                }
                courseSubjects.put(courseId, StringUtils.join(titles, "/"));
            }
        }

        return courseSubjects;
    }

    @Override
    public void increaseJoined(long id, int count) {
        String sql = "UPDATE SG_Subject SET Joined=Joined+? WHERE Id=?";
        jdbcTemplate.update(sql, new Object[] { count, id });
    }
}
