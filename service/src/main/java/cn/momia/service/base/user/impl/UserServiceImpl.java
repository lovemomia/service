package cn.momia.service.base.user.impl;

import cn.momia.common.secret.PasswordEncryptor;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.common.DbAccessService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserServiceImpl extends DbAccessService implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final Splitter CHILDREN_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final String[] USER_FIELDS = { "id", "token", "nickName", "mobile", "password", "avatar", "name", "sex", "birthday", "cityId", "address", "children" };

    @Override
    public User add(String nickName, String mobile, String password, String token) {
        if (!validateMobile(mobile)) return User.DUPLICATE_USER;

        return addUser(nickName, mobile, PasswordEncryptor.encrypt(mobile, password, SecretKey.getPasswordSecretKey()), token);
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

    public User addUser(final String nickName, final String mobile, final String password, final String token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_user(nickName, mobile, password, token, addTime) VALUES (?, ?, ?, ?,NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, nickName);
                ps.setString(2, mobile);
                ps.setString(3, password);
                ps.setString(4, token);

                return ps;
            }
        }, keyHolder);

        return get(keyHolder.getKey().longValue());
    }

    @Override
    public User get(long id) {
        if (id <= 0) return User.NOT_EXIST_USER;

        String sql = "SELECT " + joinFields() + " FROM t_user WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(USER_FIELDS, ",");
    }

    private User buildUser(ResultSet rs) throws SQLException {
        try {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setToken(rs.getString("token"));
            user.setNickName(rs.getString("nickName"));
            user.setMobile(rs.getString("mobile"));
            user.setHasPassword(!StringUtils.isBlank(rs.getString("password")));
            user.setAvatar(rs.getString("avatar"));
            user.setName(rs.getString("name"));
            user.setSex(rs.getString("sex"));
            user.setBirthday(rs.getDate("birthday"));
            user.setCity(rs.getInt("cityId"));
            user.setAddress(rs.getString("address"));
            user.setChildren(parseChildren(rs.getString("children")));

            return user;
        } catch (Exception e) {
            LOGGER.error("fail to build user: {}", rs.getLong("id"), e);
            return User.INVALID_USER;
        }
    }

    private Set<Long> parseChildren(String children) {
        Set<Long> childrenIds = new HashSet<Long>();
        for (String childId : CHILDREN_SPLITTER.split(children)) {
            childrenIds.add(Long.valueOf(childId));
        }

        return childrenIds;
    }

    @Override
    public Map<Long, User> get(Collection<Long> ids) {
        final Map<Long, User> users = new HashMap<Long, User>();
        if (ids == null || ids.size() <= 0) return users;

        String sql = "SELECT " + joinFields() + " FROM t_user WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                User user = buildUser(rs);
                if (user.exists()) users.put(user.getId(), user);
            }
        });

        return users;
    }

    @Override
    public User getByToken(String token) {
        String sql = "SELECT " + joinFields() + " FROM t_user WHERE token=? AND status=1";

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
        String sql = "SELECT " + joinFields() + " FROM t_user WHERE mobile=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    @Override
    public User getByNickName(String nickName) {
        String sql = "SELECT " + joinFields() + " FROM t_user WHERE nickName=?";

        return jdbcTemplate.query(sql, new Object[]{nickName}, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if(rs.next()) return buildUser(rs);
                return User.NOT_EXIST_USER;
            }
        });
    }

    @Override
    public boolean validatePassword(String mobile, String password) {
        String sql = "SELECT mobile, password FROM t_user WHERE mobile=? AND password=?";

        return jdbcTemplate.query(sql, new Object[] { mobile, PasswordEncryptor.encrypt(mobile, password, SecretKey.getPasswordSecretKey()) }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) return true;
                return false;
            }
        });
    }

    @Override
    public boolean updateToken(long id, String token) {
        String sql = "UPDATE t_user SET token=? WHERE id=?";

        return update(sql, new Object[] { token, id });
    }

    private boolean update(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args) == 1;
    }

    @Override
    public boolean updateNickName(long id, String nickName) {
        String sql = "UPDATE t_user SET nickName=? WHERE id=?";

        return update(sql, new Object[] { nickName, id });
    }

    @Override
    public boolean updateAvatar(long id, String avatar) {
        String sql = "UPDATE t_user SET avatar=? WHERE id=?";

        return update(sql, new Object[] { avatar, id });
    }

    @Override
    public boolean updateName(long id, String name) {
        String sql = "UPDATE t_user SET name=? WHERE id=?";

        return update(sql, new Object[] { name, id });
    }

    @Override
    public boolean updateSex(long id, String sex) {
        String sql = "UPDATE t_user SET sex=? WHERE id=?";

        return update(sql, new Object[] { sex, id });
    }

    @Override
    public boolean updateBirthday(long id, Date birthday) {
        String sql = "UPDATE t_user SET `birthday`=? WHERE id=?";

        return update(sql, new Object[] { birthday, id });
    }

    @Override
    public boolean updateCityId(long id, int cityId) {
        String sql = "UPDATE t_user SET `cityId`=? WHERE id=?";

        return update(sql, new Object[] { cityId, id });
    }

    @Override
    public boolean updateAddress(long id, String address) {
        String sql = "UPDATE t_user SET address=? WHERE id=?";

        return update(sql, new Object[] { address, id });
    }

    @Override
    public boolean updateChildren(long id, Set<Long> children) {
        String sql = "UPDATE t_user SET children=? WHERE id=?";

        return update(sql, new Object[] { StringUtils.join(children, ","), id });
    }

    @Override
    public boolean updatePassword(long id, String mobile, String password) {
        String sql = "UPDATE t_user SET password=? WHERE id=?";

        return update(sql, new Object[] { PasswordEncryptor.encrypt(mobile, password, SecretKey.getPasswordSecretKey()), id });
    }
}
