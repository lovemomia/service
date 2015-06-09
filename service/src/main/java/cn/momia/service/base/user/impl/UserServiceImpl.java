package cn.momia.service.base.user.impl;

import cn.momia.service.base.DbAccessService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserServiceImpl extends DbAccessService implements UserService {

    public boolean validateMobile(String mobile) {
        String sql = "SELECT COUNT(1) FROM t_user WHERE mobile=?";
        int count = jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        });
        return count == 0;
    }

    @Override
    public User add(final String mobile, final String token) {
        if (!validateMobile(mobile)) return User.DUPLICATE_USER;

        return addUser(mobile, token);
    }

    public User addUser(final String mobile, final String token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_user(mobile,  token, addTime) VALUES (?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, mobile);
                ps.setString(2, token);
                return ps;
            }
        }, keyHolder);

        return get(keyHolder.getKey().longValue());
    }

    @Override
    public User get(long id) {
        String sql = "select id,mobile,types,name,`desc`,token,sex,address,idCardNo,idCardPic,addTime,updateTime from t_user where id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    public User buildUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setMobile(rs.getString("mobile"));
        user.setTypes(rs.getInt("types"));
        user.setName(rs.getString("name"));
        user.setDesc(rs.getString("desc"));
        user.setToken(rs.getString("token"));
        user.setSex(rs.getInt("sex"));
        user.setAddress(rs.getString("address"));
        user.setIdCardNo(rs.getString("idCardNo"));
        user.setIdCardPic(rs.getString("idCardPic"));
        user.setAddTime(rs.getTimestamp("addTime"));
        user.setUpdateTime(rs.getTimestamp("updateTime"));
        return user;
    }

    @Override
    public User getByMobile(String mobile) {
        String sql = "select id,mobile,types,name,`desc`,token,sex,address,idCardNo,idCardPic,addTime,updateTime from t_user where mobile=? and status=1";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    @Override
    public User getByToken(String token) {
        String sql = "select id,mobile,types,name,`desc`,token,sex,address,idCardNo,idCardPic,addTime,updateTime from t_user where token=? and status=1";

        return jdbcTemplate.query(sql, new Object[] { token }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    private boolean update(long id, String sql, Object[] args) {
        User user = get(id);
        if (!user.exists()) return false;

        int affectedRowCount = jdbcTemplate.update(sql, args);
        if (affectedRowCount != 1) return false;
        else return true;
    }

    @Override
    public boolean updateName(long userId, String name) {
        String sql = "update t_user set name=? where id=?";
        return update(userId, sql, new Object[] { name, userId });
    }


    @Override
    public boolean updateDesc(long userId, String desc) {

        String sql = "update t_user set `desc`=? where id=?";
        return update(userId, sql, new Object[] { desc, userId });
    }

    @Override
    public boolean updateSex(long userId, int sex) {

        String sql = "update t_user set sex=? where id=?";
        return update(userId, sql, new Object[] { sex, userId });
    }

    @Override
    public boolean updateAvatar(long userId, String avatar) {
        String sql = "update t_user set avatar=? where id=?";
        return update(userId, sql, new Object[] { avatar, userId });
    }

    @Override
    public boolean updateAddress(long userId, String address) {
        String sql = "update t_user set address=? where id=?";
        return update(userId, sql, new Object[] { address, userId });
    }

    @Override
    public boolean updateIdCardNo(long userId, String idCardNo) {
        String sql = "update t_user set idCardNo=? where id=?";
        return update(userId, sql, new Object[] { idCardNo, userId });
    }

    @Override
    public boolean updateIdCardPic(long userId, String idCardPic) {
        String sql = "update t_user set idCardPic=? where id=?";
        return update(userId, sql, new Object[] { idCardPic, userId });
    }
}
