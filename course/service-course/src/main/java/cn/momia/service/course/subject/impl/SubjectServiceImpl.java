package cn.momia.service.course.subject.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectImage;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubjectServiceImpl extends DbAccessService implements SubjectService {
    @Override
    public Subject get(long subjectId) {
        Set<Long> subjectIds = Sets.newHashSet(subjectId);
        List<Subject> subjects = list(subjectIds);

        return subjects.isEmpty() ? Subject.NOT_EXIST_SUBJECT : subjects.get(0);
    }

    @Override
    public List<Subject> list(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new ArrayList<Subject>();

        String sql = "SELECT Id, CityId, Title, Cover, Tags, Intro, Notice FROM SG_Subject WHERE Id IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1";
        List<Subject> subjects = queryList(sql, Subject.class);

        Map<Long, List<SubjectImage>> imgs = queryImgs(subjectIds);
        Map<Long, List<SubjectSku>> skus = querySkus(subjectIds);
        for (Subject subject : subjects) {
            subject.setImgs(imgs.get(subject.getId()));
            subject.setSkus(skus.get(subject.getId()));
        }

        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<Subject> result = new ArrayList<Subject>();
        for (long subjectId : subjectIds) {
            Subject subject = subjectsMap.get(subjectId);
            if (subject != null) result.add(subject);
        }

        return result;
    }

    private Map<Long, List<SubjectImage>> queryImgs(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new HashMap<Long, List<SubjectImage>>();

        String sql = "SELECT Id, SubjectId, Url, Width, Height FROM SG_SubjectImg WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1";
        List<SubjectImage> imgs = queryList(sql, SubjectImage.class);

        final Map<Long, List<SubjectImage>> imgsMap = new HashMap<Long, List<SubjectImage>>();
        for (long subjectId : subjectIds) {
            imgsMap.put(subjectId, new ArrayList<SubjectImage>());
        }
        for (SubjectImage img : imgs) {
            imgsMap.get(img.getSubjectId()).add(img);
        }

        return imgsMap;
    }

    private Map<Long, List<SubjectSku>> querySkus(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new HashMap<Long, List<SubjectSku>>();

        String sql = "SELECT Id FROM SG_SubjectSku WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1";
        List<Long> skuIds = queryLongList(sql);
        List<SubjectSku> skus = listSkus(skuIds);

        Map<Long, List<SubjectSku>> skusMap = new HashMap<Long, List<SubjectSku>>();
        for (long subjectId : subjectIds) {
            skusMap.put(subjectId, new ArrayList<SubjectSku>());
        }
        for (SubjectSku sku : skus) {
            skusMap.get(sku.getSubjectId()).add(sku);
        }

        return skusMap;
    }

    @Override
    public long queryTrialCount(long cityId) {
        String sql = "SELECT COUNT(1) FROM SG_Subject WHERE `Type`=? AND CityId=? AND Stock>0 AND Status=1";
        return queryLong(sql, new Object[] { Subject.Type.TRIAL, cityId });
    }

    @Override
    public List<Subject> queryTrial(long cityId, int start, int count) {
        String sql = "SELECT Id FROM SG_Subject WHERE `Type`=? AND CityId=? AND Stock>0 AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> subjectIds = queryLongList(sql, new Object[] { Subject.Type.TRIAL, cityId, start, count });

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

        String sql = "SELECT Id, SubjectId, `Desc`, Price, OriginalPrice, Adult, Child, CourseCount, Time, TimeUnit, `Limit` FROM SG_SubjectSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status=1";
        List<SubjectSku> skus = queryList(sql, SubjectSku.class);

        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) {
            skusMap.put(sku.getId(), sku);
        }

        List<SubjectSku> result = new ArrayList<SubjectSku>();
        for (long skuId : skuIds) {
            SubjectSku sku = skusMap.get(skuId);
            if (sku != null) result.add(sku);
        }

        return result;
    }

    @Override
    public List<SubjectSku> querySkus(long subjectId) {
        Set<Long> subjectIds = Sets.newHashSet(subjectId);
        Map<Long, List<SubjectSku>> skus = querySkus(subjectIds);

        return skus.get(subjectId);
    }

    @Override
    public boolean isForNewUser(long subjectId) {
        String sql = "SELECT Type FROM SG_Subject WHERE Id=?";
        return queryInt(sql, new Object[] { subjectId }) == Subject.Type.TRIAL;
    }
}
