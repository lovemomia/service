package cn.momia.service.im.web.ctrl;

import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.image.api.ImageFile;
import cn.momia.service.im.ImService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
