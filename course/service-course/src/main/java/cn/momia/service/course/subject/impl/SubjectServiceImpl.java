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
    private static final String[] SUBJECT_FIELDS = { "Id", "CityId", "RegionId", "Title", "Cover", "Tags", "MinAge", "MaxAge", "Joined", "Intro", "Notice", "StartTime", "EndTime" };
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
            subject.setCityId(rs.getInt("CityId"));
            subject.setRegionId(rs.getInt("RegionId"));
            subject.setTitle(rs.getString("Title"));
            subject.setCover(rs.getString("Cover"));
            subject.setTags(rs.getString("Tags"));
            subject.setMinAge(rs.getInt("MinAge"));
            subject.setMaxAge(rs.getInt("MaxAge"));
            subject.setJoined(rs.getInt("Joined"));
            subject.setStartTime(rs.getTimestamp("StartTime"));
            subject.setEndTime(rs.getTimestamp("EndTime"));
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
    public List<Subject> list(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<Subject>();

        final List<Subject> subjects = new ArrayList<Subject>();
        String sql = "SELECT " + joinFields() + " FROM SG_Subject WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Subject subject = buildSubject(rs);
                if (subject.exists()) subjects.add(subject);
            }
        });

        Map<Long, List<String>> imgs = getImgs(ids);
        Map<Long, List<SubjectSku>> skus = getSkus(ids);
        for (Subject subject : subjects) {
            subject.setImgs(imgs.get(subject.getId()));
            subject.setSkus(skus.get(subject.getId()));
        }

        return subjects;
    }

    private Map<Long, List<String>> getImgs(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<String>>();

        final Map<Long, List<String>> imgs = new HashMap<Long, List<String>>();
        for (long id : ids) imgs.put(id, new ArrayList<String>());
        String sql = "SELECT SubjectId, Url FROM SG_SubjectImg WHERE SubjectId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long subjectId = rs.getLong("SubjectId");
                String img = rs.getString("Url");
                imgs.get(subjectId).add(img);
            }
        });

        return imgs;
    }

    private Map<Long, List<SubjectSku>> getSkus(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<SubjectSku>>();

        final Map<Long, List<SubjectSku>> skus = new HashMap<Long, List<SubjectSku>>();
        for (long id : ids) skus.put(id, new ArrayList<SubjectSku>());
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectSku WHERE SubjectId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SubjectSku sku = buildSubjectSku(rs);
                if (sku.exists()) skus.get(sku.getSubjectId()).add(sku);
            }
        });

        return skus;
    }

    @Override
    public long queryFreeCount(long cityId) {
        String sql = "SELECT COUNT(1) FROM SG_Subject WHERE `Type`=? AND CityId=? AND Status=1";

        return jdbcTemplate.query(sql, new Object[] { Subject.Type.FREE, cityId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<Subject> queryFree(long cityId, int start, int count) {
        final List<Long> subjectIds = new ArrayList<Long>();
        String sql = "SELECT Id FROM SG_Subject WHERE `Type`=? AND CityId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { Subject.Type.FREE, cityId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                subjectIds.add(rs.getLong("Id"));
            }
        });


        return list(subjectIds);
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
    public List<SubjectSku> listSkus(Collection<Long> skuIds) {
        if (skuIds.isEmpty()) return new ArrayList<SubjectSku>();

        final List<SubjectSku> skus = new ArrayList<SubjectSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SubjectSku sku = buildSubjectSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
    }

    @Override
    public List<SubjectSku> querySkus(long id) {
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
