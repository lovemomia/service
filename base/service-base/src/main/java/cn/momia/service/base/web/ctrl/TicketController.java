package cn.momia.service.base.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketController extends BaseController {
    @Autowired private TicketService ticketService;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse ticket() {
        return MomiaHttpResponse.SUCCESS(ticketService.generateTicket());
    }
}
