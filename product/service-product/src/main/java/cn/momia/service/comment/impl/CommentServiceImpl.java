package cn.momia.service.comment.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.comment.Comment;
import cn.momia.service.comment.CommentImage;
import cn.momia.service.comment.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommentServiceImpl extends DbAccessService implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    private static final String[] COMMENT_FIELDS = { "id", "orderId", "productId", "skuId", "userid", "star", "content", "addTime" };

    @Override
    public long add(final Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_product_comment(orderId, productId, skuId, userId, star, content, addTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, comment.getOrderId());
                ps.setLong(2, comment.getProductId());
                ps.setLong(3, comment.getSkuId());
                ps.setLong(4, comment.getUserId());
                ps.setInt(5, comment.getStar());
                ps.setString(6, comment.getContent());

                return ps;
            }
        }, keyHolder);

        long commentId = keyHolder.getKey().longValue();
        if (commentId > 0) addImgs(commentId, comment.getImgs());

        return commentId;
    }

    private void addImgs(long commentId, List<CommentImage> imgs) {
        if (imgs == null || imgs.isEmpty()) return;

        try {
            String sql = "INSERT INTO t_product_comment_img(commentId, url, width, height, addTime) VALUES(?, ?, ?, ?, NOW())";
            List<Object[]> args = new ArrayList<Object[]>();
            for (CommentImage img : imgs) {
                args.add(new Object[] { commentId, img.getUrl(), img.getWidth(), img.getHeight() });
            }
            jdbcTemplate.batchUpdate(sql, args);
        } catch (Exception e) {
            LOGGER.error("fail to add imgs for comment: {}", commentId, e);
        }
    }

    @Override
    public long queryCountByProduct(long productId) {
        String sql = "SELECT count(1) FROM t_product_comment WHERE productId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { productId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<Comment> queryByProduct(long productId, int start, int count) {
        final List<Comment> comments = new ArrayList<Comment>();
        String sql = "SELECT " + joinFields()+ " FROM t_product_comment WHERE productId=? AND status=1 ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { productId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Comment comment = buildComment(rs);
                if (comment.exists()) comments.add(comment);
            }
        });

        queryImgs(comments);

        return comments;
    }

    private String joinFields() {
        return StringUtils.join(COMMENT_FIELDS, ",");
    }

    private Comment buildComment(ResultSet rs) throws SQLException {
        try {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setOrderId(rs.getLong("orderId"));
            comment.setProductId(rs.getLong("productId"));
            comment.setSkuId(rs.getLong("skuId"));
            comment.setUserId(rs.getLong("userId"));
            comment.setStar(rs.getInt("star"));
            comment.setContent(rs.getString("content"));
            comment.setAddTime(rs.getTimestamp("addTime"));

            return comment;
        } catch (Exception e) {
            LOGGER.error("fail to build comment: {}", rs.getLong("id"), e);
            return Comment.NOT_EXIST_COMMENT;
        }
    }

    private void queryImgs(List<Comment> comments) {
        Set<Long> commentIds = new HashSet<Long>();
        for (Comment comment : comments) commentIds.add(comment.getId());
        if (commentIds.isEmpty()) return;

        final Map<Long, List<CommentImage>> commentImgsMap = new HashMap<Long, List<CommentImage>>();
        String sql = "SELECT commentId, url, width, height FROM t_product_comment_img WHERE commentId IN (" + StringUtils.join(commentIds, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long commentId = rs.getLong("commentId");
                List<CommentImage> imgs = commentImgsMap.get(commentId);
                if (imgs == null) {
                    imgs = new ArrayList<CommentImage>();
                    commentImgsMap.put(commentId, imgs);
                }
                imgs.add(new CommentImage(rs.getString("url"), rs.getInt("width"), rs.getInt("height")));
            }
        });

        for (Comment comment : comments) {
            List<CommentImage> imgs = commentImgsMap.get(comment.getId());
            if (imgs == null) imgs = new ArrayList<CommentImage>();
            comment.setImgs(imgs);
        }
    }
}
