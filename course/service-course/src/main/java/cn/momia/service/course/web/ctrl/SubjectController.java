package cn.momia.service.course.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectImage;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/subject")
public class SubjectController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectController.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M月d日");

    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;

    @RequestMapping(value = "/free", method = RequestMethod.GET)
    public MomiaHttpResponse listFree(@RequestParam(value = "city") long cityId, @RequestParam int start, @RequestParam int count) {
        long totalCount = subjectService.queryFreeCount(cityId);
        List<Subject> subjects = subjectService.queryFree(cityId, start, count);

        Set<Long> subjectIds = new HashSet<Long>();
        for (Subject subject : subjects) {
            subjectIds.add(subject.getId());
        }
        Map<Long, List<Course>> coursesMap = courseService.queryAllBySubjects(subjectIds);

        List<SubjectDto> subjectDtos = new ArrayList<SubjectDto>();
        for (Subject subject : subjects) {
            SubjectDto subjectDto = buildBaseSubjectDto(subject, coursesMap.get(subject.getId()));
            if (subjectDto != null) subjectDtos.add(subjectDto);
        }
        PagedList<SubjectDto> pagedSubjectDtos = new PagedList<SubjectDto>(totalCount, start, count);
        pagedSubjectDtos.setList(subjectDtos);

        return MomiaHttpResponse.SUCCESS(pagedSubjectDtos);
    }

    private SubjectDto buildBaseSubjectDto(Subject subject, List<Course> courses) {
        try {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(subject.getId());
            subjectDto.setTitle(subject.getTitle());
            subjectDto.setCover(subject.getCover());
            subjectDto.setTags(subject.getTags());

            SubjectSku minPriceSku = subject.getMinPriceSku();
            subjectDto.setPrice(minPriceSku.getPrice());
            subjectDto.setOriginalPrice(minPriceSku.getOriginalPrice());

            subjectDto.setAge(getAgeRange(courses));
            subjectDto.setJoined(getJoined(courses));
            subjectDto.setScheduler(getScheduler(courses));
            subjectDto.setRegion(getRegion(courses));

            return subjectDto;
        } catch (Exception e) {
            LOGGER.error("invalid subject: {}", subject.getId(), e);
            return null;
        }
    }

    private String getAgeRange(List<Course> courses) {
        if (courses.isEmpty()) return "";

        int minAge = Integer.MAX_VALUE;
        int maxAge = 0;

        for (Course course : courses) {
            minAge = Math.min(minAge, course.getMinAge());
            maxAge = Math.max(maxAge, course.getMaxAge());
        }

        if (minAge <= 0 && maxAge <= 0) throw new MomiaFailedException("invalid age of subject sku");
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
            times.add(course.getStartTime());
            times.add(course.getEndTime());
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

        return MetaUtil.getRegionName(regionIds.size() > 1 ? RegionDto.MULTI_REGION_ID : regionIds.get(0));
    }

    @RequestMapping(value = "/{suid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "suid") long subjectId) {
        Subject subject = subjectService.get(subjectId);
        if (!subject.exists()) return MomiaHttpResponse.FAILED("课程体系不存在");

        List<Course> courses = courseService.queryAllBySubject(subject.getId());

        return MomiaHttpResponse.SUCCESS(buildSubjectDto(subject, courses));
    }

    private SubjectDto buildSubjectDto(Subject subject, List<Course> courses) {
        SubjectDto subjectDto = buildBaseSubjectDto(subject, courses);
        subjectDto.setIntro(subject.getIntro());
        subjectDto.setNotice(JSON.parseArray(subject.getNotice()));
        subjectDto.setImgs(extractImgUrls(subject.getImgs()));

        return subjectDto;
    }

    private List<String> extractImgUrls(List<SubjectImage> imgs) {
        List<String> urls = new ArrayList<String>();
        for (SubjectImage img : imgs) {
            urls.add(img.getUrl());
        }

        return urls;
    }

    @RequestMapping(value = "/{suid}/sku", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(buildSubjectSkuDtos(subjectService.querySkus(subjectId)));
    }

    private List<SubjectSkuDto> buildSubjectSkuDtos(List<SubjectSku> skus) {
        List<SubjectSkuDto> skuDtos = new ArrayList<SubjectSkuDto>();
        for (SubjectSku sku : skus) {
            SubjectSkuDto skuDto = new SubjectSkuDto();
            skuDto.setId(sku.getId());
            skuDto.setSubjectId(sku.getSubjectId());
            skuDto.setPrice(sku.getPrice());
            skuDto.setDesc(sku.getDesc());

            skuDtos.add(skuDto);
        }

        return skuDtos;
    }
}
