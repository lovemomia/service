package cn.momia.service.base.user.impl;

import cn.momia.service.base.DbAccessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl extends DbAccessService implements UserService {
    @Override
    public User add(final String mobile, final String token) {
        if (!validateMobile(mobile)) return User.DUPLICATE_USER;

        return addUser(mobile, token);
    }

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

    public User addUser(final String mobile, final String token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_user(mobile, token, addTime) VALUES (?, ?, NOW())";
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
        String sql = "SELECT id, token, mobile, avatar, name, sex, birthday, cityId, address FROM t_user WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    private User buildUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setToken(rs.getString("token"));
        user.setMobile(rs.getString("mobile"));
        user.setAvatar(rs.getString("avatar"));
        user.setName(rs.getString("name"));
        user.setSex(rs.getString("sex"));
        user.setBirthday(rs.getDate("birthday"));
        user.setCityId(rs.getInt("cityId"));
        user.setAddress(rs.getString("address"));

        return user;
    }

    @Override
    public Map<Long, User> get(List<Long> ids) {
        final Map<Long, User> users = new HashMap<Long, User>();
        if (ids.size() <= 0) return users;

        String sql = "SELECT id, token, mobile, avatar, name, sex, birthday, cityId, address FROM t_user WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                User user = buildUser(rs);
                users.put(user.getId(), user);
            }
        });

        return users;
    }

    @Override
    public User getByToken(String token) {
        String sql = "SELECT id, token, mobile, avatar, name, sex, birthday, cityId, address FROM t_user WHERE token=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { token }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    @Override
    public User getByMobile(String mobile) {
        String sql = "SELECT id, token, mobile, avatar, name, sex, birthday, cityId, address FROM t_user WHERE mobile=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    @Override
    public boolean updateToken(long id, String token) {
        String sql = "UPDATE t_user SET token=? WHERE id=?";

        return update(id, sql, new Object[] { token, id });
    }

    @Override
    public boolean updateAvatar(long id, String avatar) {
        String sql = "UPDATE t_user SET avatar=? WHERE id=?";

        return update(id, sql, new Object[] { avatar, id });
    }

    private boolean update(long id, String sql, Object[] args) {
        User user = get(id);
        if (!user.exists()) return false;

        int affectedRowCount = jdbcTemplate.update(sql, args);
        if (affectedRowCount != 1) return false;

        return true;
    }

    @Override
    public boolean updateName(long id, String name) {
        String sql = "UPDATE t_user SET name=? WHERE id=?";

        return update(id, sql, new Object[] { name, id });
    }

    @Override
    public boolean updateSex(long id, String sex) {
        String sql = "UPDATE t_user SET sex=? WHERE id=?";

        return update(id, sql, new Object[] { sex, id });
    }

    @Override
    public boolean updateBirthday(long id, Date birthday) {
        String sql = "UPDATE t_user SET `birthday`=? WHERE id=?";

        return update(id, sql, new Object[] { birthday, id });
    }

    @Override
    public boolean updateCityId(long id, int cityId) {
        String sql = "UPDATE t_user SET `cityId`=? WHERE id=?";

        return update(id, sql, new Object[] { cityId, id });
    }

    @Override
    public boolean updateAddress(long id, String address) {
        String sql = "UPDATE t_user SET address=? WHERE id=?";

        return update(id, sql, new Object[] { address, id });
    }
}
