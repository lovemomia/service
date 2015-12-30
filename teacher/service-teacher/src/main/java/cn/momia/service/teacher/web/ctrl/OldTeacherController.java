package cn.momia.service.teacher.web.ctrl;

import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.Student;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.teacher.TeacherService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/teacher")
public class OldTeacherController extends BaseController {
    @Autowired private TeacherService teacherService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/material/{mid}", method = RequestMethod.GET)
    public MomiaHttpResponse listMaterials(@RequestParam String utoken, @PathVariable(value = "mid") int materialId) {
        User user = userServiceApi.get(utoken);
        Material material = teacherService.getMaterial(user.getId(), materialId);
        if (!material.exists()) return MomiaHttpResponse.FAILED("教材不存在");

        return MomiaHttpResponse.SUCCESS(material);
    }

    @RequestMapping(value = "/material/list", method = RequestMethod.GET)
    public MomiaHttpResponse listMaterials(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        User user = userServiceApi.get(utoken);
        long totalCount = teacherService.queryMaterialsCount(user.getId());
        List<Material> materials = getBaseInfo(teacherService.queryMaterials(user.getId(), start, count));

        PagedList<Material> pagedMaterials = new PagedList<Material>(totalCount, start, count);
        pagedMaterials.setList(materials);

        return MomiaHttpResponse.SUCCESS(pagedMaterials);
    }

    private List<Material> getBaseInfo(List<Material> materials) {
        List<Material> baseMaterials = new ArrayList<Material>();
        for (Material material : materials) {
            baseMaterials.add(new Material.Base(material));
        }

        return baseMaterials;
    }

    @RequestMapping(value = "/course/ongoing/student", method = RequestMethod.GET)
    public MomiaHttpResponse ongoingStudents(@RequestParam String utoken,
                                             @RequestParam(value = "coid") long courseId,
                                             @RequestParam(value = "sid") long courseSkuId) {
        List<Student> students = teacherService.queryAllStudents(courseId, courseSkuId);
        List<Long> userIds = teacherService.queryUserIdsWithoutChild(courseId, courseSkuId);
        List<User> users = userServiceApi.list(userIds, User.Type.MINI);
        for (User user : users) {
            Student student = new Student();
            student.setType(Student.Type.PARENT);
            student.setId(user.getId());
            student.setUserId(user.getId());
            student.setAvatar(user.getAvatar());
            student.setName(user.getNickName());

            students.add(student);
        }

        return MomiaHttpResponse.SUCCESS(students);
    }

    @RequestMapping(value = "/course/notfinished/student", method = RequestMethod.GET)
    public MomiaHttpResponse notfinishedStudents(@RequestParam String utoken,
                                                 @RequestParam(value = "coid") long courseId,
                                                 @RequestParam(value = "sid") long courseSkuId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.queryAllStudents(courseId, courseSkuId));
    }

    @RequestMapping(value = "/course/finished/student", method = RequestMethod.GET)
    public MomiaHttpResponse finishedStudents(@RequestParam String utoken,
                                              @RequestParam(value = "coid") long courseId,
                                              @RequestParam(value = "sid") long courseSkuId) {
        User user = userServiceApi.get(utoken);

        List<Student> students = teacherService.queryCheckInStudents(courseId, courseSkuId);

        Set<Long> commentedChildIds = Sets.newHashSet(teacherService.queryCommentedChildIds(courseId, courseSkuId));
        for (Student student : students) {
            if (commentedChildIds.contains(student.getId())) student.setCommented(true);
        }

        return MomiaHttpResponse.SUCCESS(students);
    }

    @RequestMapping(value = "/course/checkin", method = RequestMethod.POST)
    public MomiaHttpResponse checkin(@RequestParam String utoken,
                                     @RequestParam(value = "uid") long userId,
                                     @RequestParam(value = "pid") long packageId,
                                     @RequestParam(value = "coid") long courseId,
                                     @RequestParam(value = "sid") long courseSkuId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.checkin(userId, packageId, courseId, courseSkuId));
    }
}
