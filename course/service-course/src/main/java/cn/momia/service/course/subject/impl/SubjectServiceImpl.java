package cn.momia.service.course.subject.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.api.course.dto.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.subject.SubjectSku;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubjectServiceImpl extends AbstractService implements SubjectService {
    @Override
    public Subject get(long subjectId) {
        Set<Long> subjectIds = Sets.newHashSet(subjectId);
        List<Subject> subjects = list(subjectIds);

        return subjects.isEmpty() ? Subject.NOT_EXIST_SUBJECT : subjects.get(0);
    }

    @Override
    public List<Subject> list(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new ArrayList<Subject>();

        String sql = "SELECT Id, `Type`, CityId, Title, SubTitle, Cover, Tags, Intro, Notice, Stock, Status FROM SG_Subject WHERE Id IN (" + StringUtils.join(subjectIds, ",") + ") AND Status<>0";
        List<Subject> subjects = queryObjectList(sql, Subject.class);

        Map<Long, List<String>> imgs = queryImgs(subjectIds);
        Map<Long, List<SubjectSku>> skus = querySkus(subjectIds);
        for (Subject subject : subjects) {
            List<String> subjectImgs = imgs.get(subject.getId());
            if (subjectImgs.size() > 1) subjectImgs = subjectImgs.subList(0, 1);
            subject.setImgs(subjectImgs);
            subject.setSkus(skus.get(subject.getId()));
        }

        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<Subject> result = new ArrayList<Subject>();
        for (long subjectId : subjectIds) {
            Subject subject = subjectsMap.get(subjectId);
            if (subject != null) {
                SubjectSku minPriceSku = getMinPriceSku(subject);
                subject.setPrice(minPriceSku.getPrice());
                subject.setOriginalPrice(minPriceSku.getOriginalPrice());
                subject.setStatus(Subject.Status.OK);

                result.add(subject);
            }
        }

        return result;
    }

    private Map<Long, List<String>> queryImgs(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new HashMap<Long, List<String>>();

        String sql = "SELECT SubjectId, Url FROM SG_SubjectImg WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status<>0";
        Map<Long, List<String>> imgsMap = queryListMap(sql, Long.class, String.class);

        for (long subjectId : subjectIds) {
            if (!imgsMap.containsKey(subjectId)) imgsMap.put(subjectId, new ArrayList<String>());
        }

        return imgsMap;
    }

    private SubjectSku getMinPriceSku(Subject subject) {
        SubjectSku minPriceSubjectSku = SubjectSku.NOT_EXIST_SUBJECT_SKU;
        for (SubjectSku sku : subject.getSkus()) {
            if ((subject.getType() == Subject.Type.NORMAL && (sku.getCourseId() > 0 || sku.getStatus() != 1)) ||
                    (subject.getType() == Subject.Type.TRIAL && sku.getCourseId() <= 0)) continue;

            if (!minPriceSubjectSku.exists()) {
                minPriceSubjectSku = sku;
            } else {
                if (minPriceSubjectSku.getPrice().compareTo(sku.getPrice()) > 0) minPriceSubjectSku = sku;
            }
        }

        return minPriceSubjectSku;
    }

    @Override
    public List<Subject> list(int cityId) {
        String sql = "SELECT Id FROM SG_Subject WHERE CityId=? AND `Type`=? AND Status=1";
        List<Long> subjectIds = queryLongList(sql, new Object[] { cityId, Subject.Type.NORMAL });

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

        String sql = "SELECT Id, SubjectId, `Desc`, Price, OriginalPrice, Adult, Child, CourseCount, Time, TimeUnit, `Limit`, Status, CourseId FROM SG_SubjectSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status<>0";
        List<SubjectSku> skus = queryObjectList(sql, SubjectSku.class);

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
    public Map<Long, List<SubjectSku>> querySkus(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new HashMap<Long, List<SubjectSku>>();

        String sql = "SELECT Id FROM SG_SubjectSku WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status<>0";
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
    public boolean isTrial(long subjectId) {
        String sql = "SELECT Type FROM SG_Subject WHERE Id=?";
        return queryInt(sql, new Object[] { subjectId }) == Subject.Type.TRIAL;
    }

    @Override
    public boolean increaseStock(long subjectId, int count) {
        String sql = "UPDATE SG_Subject SET Stock=Stock+? WHERE Id=? AND Status=1";
        return update(sql, new Object[] { count, subjectId });
    }

    @Override
    public boolean decreaseStock(long subjectId, int count) {
        String sql = "UPDATE SG_Subject SET Stock=Stock-? WHERE Id=? AND Stock>=? AND Status=1";
        return update(sql, new Object[] { count, subjectId, count });
    }
}
