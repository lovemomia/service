package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.Favorite;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.favorite.FavoriteService;
import cn.momia.api.course.dto.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.SubjectSku;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/subject")
public class SubjectController extends BaseController {
    @Autowired private SubjectService subjectService;
    @Autowired private FavoriteService favoriteService;

    @RequestMapping(value = "/trial", method = RequestMethod.GET)
    public MomiaHttpResponse listTrial(@RequestParam(value = "city") long cityId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = subjectService.queryTrialCount(cityId);
        List<Subject> subjects = subjectService.queryTrial(cityId, start, count);

        List<Subject> baseSubjects = new ArrayList<Subject>();
        for (Subject subject : subjects) {
            baseSubjects.add(new Subject.Base(subject));
        }
        PagedList<Subject> pagedSubjects = new PagedList<Subject>(totalCount, start, count);
        pagedSubjects.setList(baseSubjects);

        return MomiaHttpResponse.SUCCESS(pagedSubjects);
    }

    @RequestMapping(value = "/{suid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "suid") long subjectId) {
        Subject subject = subjectService.get(subjectId);
        if (!subject.exists()) return MomiaHttpResponse.FAILED("课程体系不存在");
        return MomiaHttpResponse.SUCCESS(subject);
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

    @RequestMapping(value = "/{suid}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favored(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.isFavored(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/{suid}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favor(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.favor(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/{suid}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavor(@RequestParam(value = "uid") long userId, @PathVariable(value = "suid") long subjectId) {
        return MomiaHttpResponse.SUCCESS(favoriteService.unfavor(userId, Favorite.Type.SUBJECT, subjectId));
    }

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse favorite(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = favoriteService.queryFavoriteCount(userId, Favorite.Type.SUBJECT);
        List<Favorite> favorites = favoriteService.queryFavorites(userId, Favorite.Type.SUBJECT, start, count);

        PagedList<Favorite> pagedFavorites = new PagedList<Favorite>(totalCount, start, count);
        pagedFavorites.setList(completeFavorites(favorites));

        return MomiaHttpResponse.SUCCESS(pagedFavorites);
    }

    private List<Favorite> completeFavorites(List<Favorite> favorites) {
        Set<Long> subjectIds = new HashSet<Long>();
        for (Favorite favorite: favorites) {
            subjectIds.add(favorite.getRefId());
        }

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<Favorite> results = new ArrayList<Favorite>();
        for (Favorite favorite : favorites) {
            Subject subject = subjectsMap.get(favorite.getRefId());
            if (subject == null) continue;

            favorite.setRef((JSONObject) JSON.toJSON(new Subject.Base(subject)));
            results.add(favorite);
        }

        return results;
    }
}
