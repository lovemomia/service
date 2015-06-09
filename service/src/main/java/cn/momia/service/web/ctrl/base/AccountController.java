package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.account.Account;
import cn.momia.service.base.user.account.AccountService;
import cn.momia.service.base.user.account.AccountStatistic;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cn/momia/service/base/user/{userId}")
public class AccountController extends AbstractController {
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/cn/momia/service/base/account", method = RequestMethod.GET)
    public ResponseMessage getAccount(@PathVariable long userId) {
        Account account = accountService.get(userId);

        if (!account.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "cn.momia.service.base.user cn.momia.service.base.account not found");
        return new ResponseMessage(account);
    }

    @RequestMapping(value = "/cn/momia/service/base/account/income/{startTime}/{endTime}", method = RequestMethod.GET)
    public ResponseMessage getIncomeStats(@PathVariable long userId, @PathVariable Date startTime, @PathVariable Date endTime, @RequestParam int timeUnit) {
        List<AccountStatistic> stats = accountService.getIncomeStatistic(userId, startTime, endTime, timeUnit);

        return new ResponseMessage(stats);
    }

    @RequestMapping(value = "/cn/momia/service/base/account/income/today", method = RequestMethod.GET)
    public ResponseMessage getTodayIncome(@PathVariable long userId) {
        float income = accountService.getTodayIncome(userId);

        return new ResponseMessage(income);
    }

    @RequestMapping(value = "/cn/momia/service/base/account/income/total", method = RequestMethod.GET)
    public ResponseMessage getTotalIncome(@PathVariable long userId) {
        float income = accountService.getTotalIncome(userId);

        return new ResponseMessage(income);
    }
}
