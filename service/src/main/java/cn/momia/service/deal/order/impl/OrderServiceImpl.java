package cn.momia.service.deal.order.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderQuery;
import cn.momia.service.deal.order.OrderService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl extends DbAccessService implements OrderService {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public long add(Order order) {
        return 0;
    }

    @Override
    public Order get(long id) {
        String sql = "select id,customerId,serverId,status,addTime,updateTime from t_order where id = ?";
        final Order entity = new Order();
        final Object[] params = new Object[] { id };

        jdbcTemplate.query(sql, params, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setCustomerId(rs.getInt("customerId"));
                entity.setServerId(rs.getInt("serverId"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getDate("addTime"));
                entity.setUpdateTime(rs.getDate("updateTime"));
            }
        });
        return entity;
    }

    @Override
    public List<Order> query(OrderQuery orderQuery) {
        String sql = "select id,customerId,serverId,status,addTime,updateTime from t_order where 1=1 ";

        sql = getWhereDataSql(sql, orderQuery);// + " limit "+orderQuery.getStart()+","+orderQuery.getCount();

        List<Order> ls = new ArrayList<Order>();

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (int i = 0; i < list.size(); i++) {
            Order entity = new Order();
            entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
            entity.setCustomerId(Integer.parseInt(list.get(i).get("customerId").toString()));
            entity.setServerId(Integer.parseInt(list.get(i).get("serverId").toString()));
            entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
            try {
                entity.setAddTime(sdf.parse(list.get(i).get("addTime").toString()));
                entity.setUpdateTime(sdf.parse(list.get(i).get("updateTime").toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ls.add(entity);
        }
        return ls;
    }

    @Override
    public int getOrderCount(OrderQuery orderQuery) {
        String sql = "select id from t_order where 1 = 1 ";
        sql = getWhereDataSql(sql, orderQuery);
        return jdbcTemplate.queryForList(sql).size();
    }

    /**
     * 组装条件语句
     *
     * @param sql
     * @param orderQuery
     * @return
     */
    private String getWhereDataSql(String sql, OrderQuery orderQuery) {

        Map<String, List<Object>> map = new HashMap<String, List<Object>>();

        if (orderQuery.getCustomerId() > 0) {
            sql += " and customerId = " + orderQuery.getCustomerId();
        }
        if (orderQuery.getServerId() > 0) {
            sql += " and serverId = " + orderQuery.getServerId();
        }

        if (orderQuery.getStatus() == 0) {
            sql = sql + " and status > 0 ";
        } else {
            sql += " and status = " + orderQuery.getStatus();
        }

        if (orderQuery.getStartTime() != null) {
            sql += " and addTime > '" + sdf.format(orderQuery.getStartTime()) + "'";
        }

        if (orderQuery.getEndTime() != null) {
            sql += " and addTime < '" + sdf.format(orderQuery.getEndTime()) + "'";
        }
        return sql;
    }
}
