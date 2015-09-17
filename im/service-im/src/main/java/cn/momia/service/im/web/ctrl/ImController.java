package cn.momia.service.im.web.ctrl;

import cn.momia.api.product.Product;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.api.product.sku.Sku;
import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.image.api.ImageFile;
import cn.momia.service.im.Group;
import cn.momia.service.im.ImService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/im")
public class ImController {
    @Autowired ImService imService;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public MomiaHttpResponse token(@RequestParam String utoken) {
        User user = UserServiceApi.USER.get(utoken);
        String token = imService.getToken(user.getId());
        if (StringUtils.isBlank(token)) token = imService.register(user.getId(), user.getNickName(), ImageFile.smallUrl(user.getAvatar()));

        return MomiaHttpResponse.SUCCESS(token);
    }

    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public MomiaHttpResponse createGroup(@RequestParam String utoken,
                                         @RequestParam(value = "pid") long productId,
                                         @RequestParam(value = "sid") long skuId) {
        // TODO check 管理员权限
        User user = UserServiceApi.USER.get(utoken);

        Group group = imService.queryGroup(productId, skuId);
        if (!group.exists()) {
            Product product = ProductServiceApi.PRODUCT.get(productId, Product.Type.MINI);
            Sku sku = ProductServiceApi.SKU.get(productId, skuId);

            String groupName = sku.getTime() + "-" + product.getTitle();
            long groupId = imService.createGroup(groupName, productId, skuId);
            if (groupId <= 0) return MomiaHttpResponse.FAILED("创建群组失败");

            if (!imService.initGroup(user.getId(), groupId, groupName)) return MomiaHttpResponse.FAILED("初始化群组失败");

            Set<Long> userIds = new HashSet<Long>();
            userIds.add(user.getId());
            imService.logGroupUsers(groupId, userIds);
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/group", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteGroup(@RequestParam String utoken,
                                         @RequestParam(value = "pid") long productId,
                                         @RequestParam(value = "sid") long skuId) {
        // TODO check 管理员权限
        User user = UserServiceApi.USER.get(utoken);

        Group group = imService.queryGroup(productId, skuId);
        if (group.exists()) {
            if (!imService.deleteGroup(user.getId(), group.getId())) return MomiaHttpResponse.FAILED("解散群组失败");
            imService.deleteGroupInfo(group.getId());
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/group/{gid}/invite", method = RequestMethod.POST)
    public MomiaHttpResponse invite(@RequestParam String utoken,
                                    @PathVariable(value = "gid") long groupId,
                                    @RequestParam String uids) {
        // TODO check 管理员权限
        User user = UserServiceApi.USER.get(utoken);

        Set<Long> userIds = new HashSet<Long>();
        for (String idStr : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) {
            long id = Long.valueOf(idStr);
            if (id > 0) userIds.add(id);
        }

        if (!imService.joinGroup(groupId, userIds)) return MomiaHttpResponse.FAILED;

        imService.logGroupUsers(groupId, userIds);

        return MomiaHttpResponse.SUCCESS;
    }
}
