package cn.momia.service.course.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.api.course.dto.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.subject.SubjectSku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController extends BaseController {
    @Autowired private SubjectService subjectService;

    @RequestMapping(value = "/{suid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "suid") long subjectId) {
        Subject subject = subjectService.get(subjectId);
        if (!subject.exists()) return MomiaHttpResponse.FAILED("课程体系不存在");
        return MomiaHttpResponse.SUCCESS(subject);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "city") int cityId) {
        List<Subject> subjects = new ArrayList<Subject>();
        for (Subject subject : subjectService.list(cityId)) {
            subjects.add(new Subject.Base(subject));
        }

        return MomiaHttpResponse.SUCCESS(subjects);
    }

    @RequestMapping(value = "/{suid}/sku", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@PathVariable(value = "suid") long subjectId) {
        List<SubjectSku> skus = subjectService.querySkus(subjectId);
        List<SubjectSku> avaliableSkus = new ArrayList<SubjectSku>();
        for (SubjectSku sku : skus) {
            if (sku.isAvaliable()) avaliableSkus.add(sku);
        }

        return MomiaHttpResponse.SUCCESS(avaliableSkus);
    }
}
