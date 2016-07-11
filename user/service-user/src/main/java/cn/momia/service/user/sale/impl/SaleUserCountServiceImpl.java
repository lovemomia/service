package cn.momia.service.user.sale.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.user.sale.SaleUserCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Administrator on 2016/7/8.
 */
public class SaleUserCountServiceImpl extends AbstractService implements SaleUserCountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleUserCountServiceImpl.class);

    @Override
    public int getUserCountBySaleCode(String saleCodes) {
        String sql = "select count(UserId) as userCount from SG_SaleUserCount where saleCode = ? AND Status=1 group by saleCode";
        return  queryInt(sql, new Object[]{ saleCodes });
    }

    @Override
    public void add(int userId, int saleId) {
        String sql = "INSERT INTO SG_SaleUserCount(UserId, SaleId, AddTime) VALUES (?, ?, NOW())";
        int updateCount = singleUpdate(sql, new Object[] { userId, saleId });

        if (updateCount != 1) throw new RuntimeException("fail to insert sale user count for user id: " + userId + "and sale id:"+saleId);
    }
}
