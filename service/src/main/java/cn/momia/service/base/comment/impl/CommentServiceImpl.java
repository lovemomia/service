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
    @Override
    public long add(final Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_comment(customerId, serverId, productId, skuId, star, content, addTime) VALUES (?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, comment.getCustomerId());
                ps.setLong(2, comment.getServerId());
                ps.setLong(3, comment.getProductId());
                ps.setLong(4, comment.getSkuId());
                ps.setInt(5, comment.getStar());
                ps.setString(6, comment.getContent());
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Comment get(long id) {
        String sql = "SELECT id, customerId, serverId, productId, skuId, star, content FROM t_comment WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Comment>() {
            @Override
            public Comment extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildComment(rs);
                return Comment.NOT_EXIST_COMMENT;
            }
        });
    }

    public Comment buildComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setCustomerId(rs.getLong("customerId"));
        comment.setServerId(rs.getLong("serverId"));
        comment.setProductId(rs.getLong("productId"));
        comment.setSkuId(rs.getLong("skuId"));
        comment.setStar(rs.getInt("star"));
        comment.setContent(rs.getString("content"));

        return comment;
    }

    @Override
    public List<Comment> queryByProduct(long productId, int start, int count) {
        final List<Comment> comments = new ArrayList<Comment>();

        String sql = "SELECT id, customerId, serverId, productId, skuId, star,content FROM t_comment WHERE productId=? AND status=1 LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { productId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                comments.add(buildComment(rs));
            }
        });

        return comments;
    }
}
