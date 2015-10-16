package cn.momia.service.course.subject.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectImage;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
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

public class SubjectServiceImpl extends DbAccessService implements SubjectService {
    private static final String[] SUBJECT_FIELDS = { "Id", "CityId", "Title", "Cover", "Tags", "Intro", "Notice" };
    private static final String[] SUBJECT_IMG_FIELDS = { "Id", "SubjectId", "Url", "Width", "Height" };
    private static final String[] SUBJECT_SKU_FIELDS = { "Id", "SubjectId", "`Desc`", "Price", "OriginalPrice", "Adult", "Child", "CourseCount", "Time" };

    private Function<ResultSet, Subject> subjectFunc = new Function<ResultSet, Subject>() {
        @Override
        public Subject apply(ResultSet rs) {
            try {
                Subject subject = new Subject();
                subject.setId(rs.getLong("Id"));
                subject.setCityId(rs.getInt("CityId"));
                subject.setTitle(rs.getString("Title"));
                subject.setCover(rs.getString("Cover"));
                subject.setTags(rs.getString("Tags"));
                subject.setIntro(rs.getString("Intro"));
                subject.setNotice(JSON.parseArray(rs.getString("Notice")));

                return subject;
            } catch (Exception e) {
                return Subject.NOT_EXIST_SUBJECT;
            }
        }
    };

    private Function<ResultSet, SubjectImage> subjectImageFunc = new Function<ResultSet, SubjectImage>() {
        @Override
        public SubjectImage apply(ResultSet rs) {
            try {
                SubjectImage img = new SubjectImage();
                img.setId(rs.getLong("Id"));
                img.setSubjectId(rs.getLong("SubjectId"));
                img.setUrl(rs.getString("Url"));
                img.setWidth(rs.getInt("Width"));
                img.setHeight(rs.getInt("Height"));

                return img;
            } catch (Exception e) {
                return SubjectImage.NOT_EXIST_SUBJECT_IMAGE;
            }
        }
    };

    private Function<ResultSet, SubjectSku> subjectSkuFunc = new Function<ResultSet, SubjectSku>() {
        @Override
        public SubjectSku apply(ResultSet rs) {
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
    };

    @Override
    public Subject get(long id) {
        Set<Long> ids = Sets.newHashSet(id);
        List<Subject> subjects = list(ids);

        return subjects.isEmpty() ? Subject.NOT_EXIST_SUBJECT : subjects.get(0);
    }

    @Override
    public List<Subject> list(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<Subject>();

        List<Subject> subjects = new ArrayList<Subject>();
        String sql = "SELECT " + joinFields() + " FROM SG_Subject WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<Subject>(subjects, subjectFunc));

        Map<Long, List<SubjectImage>> imgs = queryImgs(ids);
        Map<Long, List<SubjectSku>> skus = querySkus(ids);
        for (Subject subject : subjects) {
            subject.setImgs(imgs.get(subject.getId()));
            subject.setSkus(skus.get(subject.getId()));
        }

        return subjects;
    }

    private String joinFields() {
        return StringUtils.join(SUBJECT_FIELDS, ",");
    }

    private Map<Long, List<SubjectImage>> queryImgs(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<SubjectImage>>();

        List<SubjectImage> imgs = new ArrayList<SubjectImage>();
        String sql = "SELECT " + joinImgFields() + " FROM SG_SubjectImg WHERE SubjectId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<SubjectImage>(imgs, subjectImageFunc));

        final Map<Long, List<SubjectImage>> imgsMap = new HashMap<Long, List<SubjectImage>>();
        for (long id : ids) imgsMap.put(id, new ArrayList<SubjectImage>());
        for (SubjectImage img : imgs) imgsMap.get(img.getSubjectId()).add(img);

        return imgsMap;
    }

    private String joinImgFields() {
        return StringUtils.join(SUBJECT_IMG_FIELDS, ",");
    }

    private Map<Long, List<SubjectSku>> querySkus(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<SubjectSku>>();

        List<SubjectSku> skus = new ArrayList<SubjectSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectSku WHERE SubjectId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<SubjectSku>(skus, subjectSkuFunc));

        Map<Long, List<SubjectSku>> skusMap = new HashMap<Long, List<SubjectSku>>();
        for (long id : ids) skusMap.put(id, new ArrayList<SubjectSku>());
        for (SubjectSku sku : skus) skusMap.get(sku.getSubjectId()).add(sku);

        return skusMap;
    }

    private String joinSkuFields() {
        return StringUtils.join(SUBJECT_SKU_FIELDS, ",");
    }

    @Override
    public long queryFreeCount(long cityId) {
        String sql = "SELECT COUNT(1) FROM SG_Subject WHERE `Type`=? AND CityId=? AND Status=1";
        return jdbcTemplate.query(sql, new Object[] { Subject.Type.FREE, cityId }, new CountResultSetExtractor());
    }

    @Override
    public List<Subject> queryFree(long cityId, int start, int count) {
        List<Long> subjectIds = new ArrayList<Long>();
        String sql = "SELECT Id FROM SG_Subject WHERE `Type`=? AND CityId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { Subject.Type.FREE, cityId, start, count }, new LongListResultSetExtractor(subjectIds));

        return list(subjectIds);
    }

    @Override
    public SubjectSku getSku(long skuId) {
        Set<Long> skuIds = Sets.newHashSet(skuId);
        List<SubjectSku> skus = listSkus(skuIds);

        return skus.isEmpty() ? SubjectSku.NOT_EXIST_SUBJECT_SKU : skus.get(0);
    }

    @Override
    public List<SubjectSku> listSkus(Collection<Long> skuIds) {
        if (skuIds.isEmpty()) return new ArrayList<SubjectSku>();

        final List<SubjectSku> skus = new ArrayList<SubjectSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<SubjectSku>(skus, subjectSkuFunc));

        return skus;
    }

    @Override
    public List<SubjectSku> querySkus(long id) {
        Set<Long> ids = Sets.newHashSet(id);
        Map<Long, List<SubjectSku>> skus = querySkus(ids);

        return skus.get(id);
    }
}
