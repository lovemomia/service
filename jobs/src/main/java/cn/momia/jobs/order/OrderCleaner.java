package cn.momia.jobs.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCleaner.class);
    private static final String[] ORDER_FIELDS = { "id", "customerId", "productId", "skuId", "prices" };

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void run() {
        try {
            LOGGER.info("start to clean orders ...");

            List<Order> expiredOrders = getExpiredOrders();
            List<Order> removedOrders = removeOrders(expiredOrders);
            unlockOrders(removedOrders);

            LOGGER.info("clean orders finished");
        } catch (Exception e) {
            LOGGER.error("fail to clean expired orders", e);
        }
    }

    private List<Order> getExpiredOrders() {
        final List<Order> orders = new ArrayList<Order>();
        Date expiredTime = new Date(new Date().getTime() - 30 * 60 * 1000);
        String sql = "SELECT " + joinFields() + " FROM t_order WHERE status=? AND addTime<?";
        jdbcTemplate.query(sql, new Object[] { Order.Status.NOT_PAYED, expiredTime }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                orders.add(buildOrder(rs));
            }
        });

        return orders;
    }

    private String joinFields() {
        return StringUtils.join(ORDER_FIELDS, ",");
    }

    private Order buildOrder(ResultSet rs) throws SQLException {
        Order order = new Order();

        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customerId"));
        order.setProductId(rs.getLong("productId"));
        order.setSkuId(rs.getLong("skuId"));
        order.setPrices(parseOrderPrices(order.getId(), rs.getString("prices")));

        return order;
    }

    private List<OrderPrice> parseOrderPrices(long id, String priceJson) {
        List<OrderPrice> prices = new ArrayList<OrderPrice>();

        try {
            JSONArray pricesArray = JSON.parseArray(priceJson);
            for (int i = 0; i < pricesArray.size(); i++) {
                JSONObject priceObject = pricesArray.getJSONObject(i);
                prices.add(new OrderPrice(priceObject));
            }
        } catch (Exception e) {
            LOGGER.error("fail to parse order prices, order id: {}", id);
        }

        return prices;
    }

    private List<Order> removeOrders(List<Order> expiredOrders) {
        List<Order> removedOrders = new ArrayList<Order>();

        for (Order order : expiredOrders) {
            if (removeOrder(order.getId())) removedOrders.add(order);
        }

        return removedOrders;
    }

    public boolean removeOrder(long id) {
        try {
            String sql = "UPDATE t_order SET status=0 WHERE id=? AND status=?";
            int updateCount = jdbcTemplate.update(sql, new Object[] { id, Order.Status.NOT_PAYED });

            return updateCount == 1;
        } catch (Exception e) {
            LOGGER.error("fail to remove order: {}", id, e);
            return false;
        }
    }

    private void unlockOrders(List<Order> removedOrders) {
        for (Order order : removedOrders) {
            unlockOrder(order);
        }
    }

    private void unlockOrder(Order order) {
        try {
            unSoldOut(order.getProductId());

            long skuId = order.getSkuId();
            int count = order.getCount();
            String sql = "UPDATE t_sku SET unlockedStock=unlockedStock+?, lockedStock=lockedStock-? WHERE id=? AND lockedStock>=? AND status=1";
            if (jdbcTemplate.update(sql, new Object[] { count, count, skuId, count }) == 1) {
                deleteJoined(order.getProductId(), count);
            }
        } catch (Exception e) {
            LOGGER.error("fail to unlock order: {}", order.getId(), e);
        }
    }

    private void unSoldOut(long productId) {
        try {
            String sql = "UPDATE t_product SET soldOut=0 WHERE id=? AND soldOut=1 AND status=1";
            jdbcTemplate.update(sql, new Object[] { productId });
        } catch (Exception e) {
            LOGGER.error("fail to set sold out status of product: {}", productId, e);
        }
    }

    private void deleteJoined(long productId, int count) {
        try {
            String sql = "UPDATE t_product SET joined=joined-? WHERE id=? AND joined>=? AND status=1";
            jdbcTemplate.update(sql, new Object[] { count, productId, count });
        } catch (Exception e) {
            LOGGER.error("fail to decrease joined of product: {}", productId, e);
        }
    }
}
