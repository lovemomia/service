package cn.momia.service.base.comment.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.comment.Comment;
import cn.momia.service.base.comment.CommentService;
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
import java.util.List;

public class CommentServiceImpl extends DbAccessService implements CommentService {
    public long addComment(final Comment comment, final long productId, final long skuId) {
        final long customerId = comment.getCustomerId();
        final long serverId = comment.getServerId();
        final int star = comment.getStar();
        final String content = comment.getContent();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_comment(customerId,  serverId, skuId, productId, star, content, addTime) VALUES (?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, customerId);
                ps.setLong(2, serverId);
                ps.setLong(3, skuId);
                ps.setLong(4, productId);
                ps.setInt(5, star);
                ps.setString(6, content);
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();

    }

    @Override
    public long add(Comment comment,long productId,long skuId) {
        return addComment(comment,productId,skuId);
    }

    public Comment buildComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setCustomerId(resultSet.getLong("customerId"));
        comment.setServerId(resultSet.getLong("serverId"));
        comment.setSkuId(resultSet.getLong("skuId"));
        comment.setProductId(resultSet.getLong("productId"));
        comment.setStar(resultSet.getInt("star"));
        comment.setContent(resultSet.getString("content"));
        return comment;
    }

    @Override
    public Comment get(long id) {
        String sql = "select id, customerId, serverId, skuId, productId, star, content from t_comment where id=? and status=1";
        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Comment>() {
            @Override
            public Comment extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) return buildComment(resultSet);
                return Comment.NOT_EXIST_COMMENT;
            }
        });
    }

    public List<Comment> queryByPage(int TYPE, long id, int star, int start, int count) {
        String name = "";
        String sql = "";
        Object[] objects;
        switch (TYPE){
            case 0: name = "skuId";
            case 1: name = "serverId";
            case 2: name = "productId";
        }
        while (start >= 0 && count > 0) {
            if (star == 0) {
                sql = "select id, customerId, serverId, skuId, productId, star,content from t_comment where " + name + "=? and status=1 limit ?,?";
                objects = new Object[] { id, start, count };
            } else {
                sql = "select id, customerId, serverId, skuId, productId, star,content from t_comment where " + name + "=? and star=? and status=1 limit ?,?";
                objects = new Object[] { id, star, start, count };
            }
            final List<Comment> comments = new ArrayList<Comment>();
            jdbcTemplate.query(sql, objects, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    comments.add(buildComment(resultSet));
                }
            });
            return comments;
        }
        return null;
    }

    @Override
    public List<Comment> queryBySku(long skuId, int star, int start, int count) {
        int TYPE = Comment.Type.SKU;
        return queryByPage(TYPE, skuId, star, start, count);
    }

    @Override
    public List<Comment> queryByServer(long serverId, int star, int start, int count) {
        int TYPE = Comment.Type.SERVER;
        return queryByPage(TYPE, serverId, star, start, count);
    }

    @Override
    public List<Comment> queryByProduct(long productId, int star, int start, int count) {
        int TYPE = Comment.Type.PRODUCT;
        return queryByPage(TYPE, productId, star, start, count);
    }
}
