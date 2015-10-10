package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.CourseDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired private CourseService courseService;

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommend(@RequestParam(value = "city") int cityId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        long totalCount = courseService.queryRecommendCount(cityId);
        List<Course> courses = courseService.queryRecommend(cityId, start, count);

        PagedList<CourseDto> pagedCourseDtos = new PagedList<CourseDto>(totalCount, start, count);
        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for (Course course : courses) {
            courseDtos.add(buildCourseDto(course));
        }
        pagedCourseDtos.setList(courseDtos);

        return MomiaHttpResponse.SUCCESS(pagedCourseDtos);
    }

    private CourseDto buildCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());

        return courseDto;
    }
}
