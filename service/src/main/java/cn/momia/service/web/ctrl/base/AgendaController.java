package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.agenda.Agenda;
import cn.momia.service.base.user.agenda.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cn/momia/service/base/user/{userId}/cn.momia.service.base.agenda")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;

    @RequestMapping(value = "/daliy", method = RequestMethod.GET)
    public ResponseMessage daliyAgenda(@PathVariable long userId) {
        Agenda agenda = agendaService.getDaliyAgenda(userId);
        if (!agenda.exists())
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to get daliy cn.momia.service.base.agenda of cn.momia.service.base.user: " + userId);

        return new ResponseMessage(agenda);
    }

    @RequestMapping(value = "/weekly", method = RequestMethod.GET)
    public ResponseMessage weeklyAgenda(@PathVariable long userId) {
        Agenda agenda = agendaService.getWeeklyAgenda(userId);
        if (!agenda.exists())
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to get weekly cn.momia.service.base.agenda of cn.momia.service.base.user: " + userId);

        return new ResponseMessage(agenda);
    }
}
