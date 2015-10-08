package cn.momia.service.base.ticket.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.ticket.TicketService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketServiceImpl extends DbAccessService implements TicketService {
    private static final int MAX_RETRY_TIME = 10;

    @Override
    public String generateTicket() {
        // TODO 优化
        for (int i = 0; i < MAX_RETRY_TIME; i++) {
            long number = (long) (Math.random() * 10000000000L);
            if (!isInvalid(number)) return String.format("%010d", number);
        }

        throw new MomiaFailedException("fail to generate ticket number");
    }

    private boolean isInvalid(long number) {
        if (isDuplicated(number)) return true;

        String sql = "INSERT INTO t_ticket(ticket) VALUES (?)";
        return jdbcTemplate.update(sql, new Object[] { number }) <= 0;
    }

    private boolean isDuplicated(long number) {
        String sql = "SELECT COUNT(1) FROM t_ticket WHERE ticket=?";

        return jdbcTemplate.query(sql, new Object[] { number }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) > 0 : false;
            }
        });
    }
}
