package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.SubjectDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subject")
public class SubjectController {
    @Autowired private SubjectService subjectService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable long id) {
        return MomiaHttpResponse.SUCCESS(buildSubjectDto(subjectService.get(id)));
    }

    private SubjectDto buildSubjectDto(Subject subject) {
        SubjectDto subjectDto = new SubjectDto();
        if (subject.exists()) {
            subjectDto.setId(subject.getId());
            subjectDto.setTitle(subject.getTitle());

            SubjectSku minPriceSku = subject.getMinPriceSku();
            subjectDto.setPrice(minPriceSku.getPrice());
            subjectDto.setOriginalPrice(minPriceSku.getOriginalPrice());

            subjectDto.setAge(subject.getAge());
            subjectDto.setJoined(subject.getJoined());
            subjectDto.setIntro(subject.getIntro());
            subjectDto.setNotice(subject.getNotice());
            subjectDto.setImgs(subject.getImgs());
        }

        return subjectDto;
    }
}
