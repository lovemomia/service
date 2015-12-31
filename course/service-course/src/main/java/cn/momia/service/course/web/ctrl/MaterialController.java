package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.material.CourseMaterial;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.material.CourseMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/course")
public class MaterialController extends BaseController {
    @Autowired private CourseMaterialService courseMaterialService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/material/{mid}", method = RequestMethod.GET)
    public MomiaHttpResponse listMaterials(@RequestParam String utoken, @PathVariable(value = "mid") int materialId) {
        User user = userServiceApi.get(utoken);
        CourseMaterial material = courseMaterialService.getMaterial(user.getId(), materialId);
        if (!material.exists()) return MomiaHttpResponse.FAILED("教材不存在");

        return MomiaHttpResponse.SUCCESS(material);
    }

    @RequestMapping(value = "/material/list", method = RequestMethod.GET)
    public MomiaHttpResponse listMaterials(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        User user = userServiceApi.get(utoken);
        long totalCount = courseMaterialService.queryMaterialsCount(user.getId());
        List<CourseMaterial> materials = buildBaseMaterials(courseMaterialService.queryMaterials(user.getId(), start, count));

        PagedList<CourseMaterial> pagedMaterials = new PagedList<CourseMaterial>(totalCount, start, count);
        pagedMaterials.setList(materials);

        return MomiaHttpResponse.SUCCESS(pagedMaterials);
    }

    private List<CourseMaterial> buildBaseMaterials(List<CourseMaterial> materials) {
        List<CourseMaterial> baseMaterials = new ArrayList<CourseMaterial>();
        for (CourseMaterial material : materials) {
            baseMaterials.add(new CourseMaterial.Base(material));
        }

        return baseMaterials;
    }
}
