package cn.momia.service.course.subject.impl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.Region;
import cn.momia.api.course.dto.Course;
import cn.momia.api.course.dto.CourseSku;
import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.service.AbstractService;
import cn.momia.common.util.TimeUtil;
import cn.momia.service.course.base.CourseService;
import cn.momia.api.course.dto.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.SubjectSku;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubjectServiceImpl extends AbstractService implements SubjectService {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M月d日");

    private CourseService courseService;

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public Subject get(long subjectId) {
        Set<Long> subjectIds = Sets.newHashSet(subjectId);
        List<Subject> subjects = list(subjectIds);

        return subjects.isEmpty() ? Subject.NOT_EXIST_SUBJECT : subjects.get(0);
    }

    @Override
    public List<Subject> list(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new ArrayList<Subject>();

        String sql = "SELECT Id, `Type`, CityId, Title, Cover, Tags, Intro, Notice, Stock, Status FROM SG_Subject WHERE Id IN (" + StringUtils.join(subjectIds, ",") + ") AND Status<>0";
        List<Subject> subjects = queryObjectList(sql, Subject.class);

        Map<Long, List<String>> imgs = queryImgs(subjectIds);
        Map<Long, List<SubjectSku>> skus = querySkus(subjectIds);
        for (Subject subject : subjects) {
            subject.setImgs(imgs.get(subject.getId()));
            subject.setSkus(skus.get(subject.getId()));
        }

        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        Map<Long, List<Course>> coursesMap = courseService.queryAllBySubjects(subjectIds);
        List<Subject> result = new ArrayList<Subject>();
        for (long subjectId : subjectIds) {
            Subject subject = subjectsMap.get(subjectId);
            if (subject != null) {
                SubjectSku minPriceSku = getMinPriceSku(subject);
                subject.setPrice(minPriceSku.getPrice());
                subject.setOriginalPrice(minPriceSku.getOriginalPrice());

                List<Course> courses = coursesMap.get(subjectId);
                if (subject.getType() == Subject.Type.TRIAL && !courses.isEmpty()) subject.setCover(courses.get(0).getCover());
                subject.setAge(getAgeRange(courses));
                subject.setJoined(getJoined(courses));
                subject.setScheduler(getScheduler(courses));
                subject.setRegion(getRegion(courses));

                if (subject.getType() == Subject.Type.NORMAL) {
                    subject.setStatus(Subject.Status.OK);
                } else {
                    int stock = subject.getStock();
                    int avaliableCourseCount = getAvaliableCourseCount(courses);
                    subject.setStatus(stock > 0 ? (avaliableCourseCount > 0 ? Subject.Status.OK : Subject.Status.SOLD_OUT) : Subject.Status.SOLD_OUT);
                }

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

    private Map<Long, List<SubjectSku>> querySkus(Collection<Long> subjectIds) {
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

    private SubjectSku getMinPriceSku(Subject subject) {
        SubjectSku minPriceSubjectSku = SubjectSku.NOT_EXIST_SUBJECT_SKU;
        for (SubjectSku sku : subject.getSkus()) {
            if (sku.getCourseId() > 0) continue;

            if (!minPriceSubjectSku.exists()) {
                minPriceSubjectSku = sku;
            } else {
                if (minPriceSubjectSku.getPrice().compareTo(sku.getPrice()) > 0) minPriceSubjectSku = sku;
            }
        }

        return minPriceSubjectSku;
    }

    private String getAgeRange(List<Course> courses) {
        if (courses.isEmpty()) return "";

        int minAge = Integer.MAX_VALUE;
        int maxAge = 0;

        for (Course course : courses) {
            minAge = Math.min(minAge, course.getMinAge());
            maxAge = Math.max(maxAge, course.getMaxAge());
        }

        if (minAge <= 0 && maxAge <= 0) throw new MomiaErrorException("invalid age of subject sku");
        if (minAge <= 0) return maxAge + "岁";
        if (maxAge <= 0) return minAge + "岁";
        if (minAge == maxAge) return minAge + "岁";
        return minAge + "-" + maxAge + "岁";
    }

    private int getJoined(List<Course> courses) {
        int joined = 0;
        for (Course course : courses) {
            joined += course.getJoined();
        }

        return joined;
    }

    private String getScheduler(List<Course> courses) {
        if (courses.isEmpty()) return "";

        List<Date> times = new ArrayList<Date>();
        for (Course course : courses) {
            Date startTime = course.getStartTime();
            Date endTime = course.getEndTime();
            if (startTime != null) times.add(startTime);
            if (endTime != null) times.add(endTime);
        }
        Collections.sort(times);

        return format(times);
    }

    private String format(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start);
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMAT.format(start) + " " + TimeUtil.getWeekDay(start);
            } else {
                return DATE_FORMAT.format(start) + "-" + DATE_FORMAT.format(end);
            }
        }
    }

    private String getRegion(List<Course> courses) {
        if (courses.isEmpty()) return "";

        List<Integer> regionIds = new ArrayList<Integer>();
        for (Course course : courses) {
            int regionId = course.getRegionId();
            if (!regionIds.contains(regionId)) regionIds.add(regionId);
        }

        return MetaUtil.getRegionName(regionIds.size() > 1 ? Region.MULTI_REGION_ID : regionIds.get(0));
    }

    private int getAvaliableCourseCount(List<Course> courses) {
        int count = 0;
        Date now = new Date();
        for (Course course : courses) {
            List<CourseSku> skus = course.getSkus();
            for (CourseSku sku : skus) {
                if (sku.isAvaliable(now)) {
                    count++;
                    break;
                }
            }
        }

        return count;
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
    public boolean isTrial(long subjectId) {
        String sql = "SELECT Type FROM SG_Subject WHERE Id=?";
        return queryInt(sql, new Object[] { subjectId }) == Subject.Type.TRIAL;
    }

    @Override
    public long queryTrialCount(long cityId) {
        String sql = "SELECT COUNT(DISTINCT A.Id) " +
                "FROM SG_Subject A " +
                "INNER JOIN SG_Course B ON A.Id=B.SubjectId " +
                "INNER JOIN SG_CourseSku C ON B.Id=C.CourseId " +
                "WHERE A.Type=? AND A.CityId=? AND A.Status=1 AND B.Status=1 AND C.Status=1 AND DATE_ADD(DATE(C.EndTime), INTERVAL 1 DAY)>NOW()";
        return queryLong(sql, new Object[] { Subject.Type.TRIAL, cityId });
    }

    @Override
    public List<Subject> queryTrial(long cityId, int start, int count) {
        String sql = "SELECT DISTINCT A.Id " +
                "FROM SG_Subject A " +
                "INNER JOIN SG_Course B ON A.Id=B.SubjectId " +
                "INNER JOIN SG_CourseSku C ON B.Id=C.CourseId " +
                "WHERE A.Type=? AND A.CityId=? AND A.Status=1 AND B.Status=1 AND C.Status=1 AND DATE_ADD(DATE(C.EndTime), INTERVAL 1 DAY)>NOW() " +
                "ORDER BY A.Stock DESC, A.AddTime DESC LIMIT ?,?";
        List<Long> subjectIds = queryLongList(sql, new Object[] { Subject.Type.TRIAL, cityId, start, count });

        return list(subjectIds);
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
