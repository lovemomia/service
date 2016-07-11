package cn.momia.service.user.sale.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.user.sale.Sale;
import cn.momia.service.user.sale.SaleService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public class SaleServiceImpl extends AbstractService implements SaleService{
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleServiceImpl.class);

    @Override
    public long add(final Sale sale) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_Sale (SaleCode, Mobile, Address, AddTime) VALUES (?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, sale.getSaleCode());
                ps.setString(2, sale.getMobile());
                ps.setString(3, sale.getAddress().equals("") ? "" : sale.getAddress());
                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    @Override
    public Sale getBySaleId(long  id) {
        List<Sale> sales = list(Sets.newHashSet(id));
        return sales.isEmpty() ? Sale.NOT_EXIST_Sale : sales.get(0);
    }

    @Override
    public Sale getBySaleCode(String saleCode) {
        String sql = "SELECT Id FROM SG_Sale WHERE SaleCode=? AND Status=1";
        List<Long> saleIds = queryLongList(sql, new Object[] { saleCode });
        List<Sale> users = list(saleIds);

        return users.isEmpty() ? Sale.NOT_EXIST_Sale : users.get(0);
    }

    @Override
    public List<Sale> list(Collection<Long> saleIds) {
        if (saleIds.isEmpty()) return new ArrayList<Sale>();
        String sql = "select Id, SaleCode, Mobile, Address from SG_Sale  where Id IN (%s) AND Status=1 ";
        List<Sale> sales = listByIds(sql, saleIds, Long.class, Sale.class);
        return sales;
    }

    @Override
    public Sale verifySaleCode(String saleCode) {
        int length = saleCode.trim().length();
        switch (length){
            case 1:
                saleCode = "sg0000" + saleCode;
                break;
            case 2:
                saleCode = "sg000" + saleCode;
                break;
            case  3:
                saleCode = "sg00" + saleCode;
                break;
            case  4:
                saleCode = "sg0" + saleCode;
                break;
            case  5:
                saleCode = "sg" + saleCode;
                break;
            default:break;
        }
        return queryObject("select * from SG_Sale where SaleCode = ? and Status = 1 ", new Object[] { saleCode }, Sale.class, new Sale());
    }

}
