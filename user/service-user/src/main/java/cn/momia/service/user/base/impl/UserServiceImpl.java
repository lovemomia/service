package cn.momia.service.user.base.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import com.google.common.base.Splitter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserServiceImpl extends DbAccessService implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final Splitter CHILDREN_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final String[] USER_FIELDS = { "id", "token", "nickName", "mobile", "password", "avatar", "name", "sex", "birthday", "cityId", "regionId", "address", "children", "inviteCode" };

    @Override
    public boolean exists(String field, String value) {
        String sql = "SELECT COUNT(1) FROM t_user WHERE " + field + "=?";

        return jdbcTemplate.query(sql, new Object[] { value }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) > 0 : false;
            }
        });
    }

    @Override
    public long add(final String nickName, final String mobile, final String password, final String token, final String inviteCode) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_user(nickName, mobile, password, token, inviteCode, addTime) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, nickName);
                ps.setString(2, mobile);
                ps.setString(3, encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")));
                ps.setString(4, token);
                ps.setString(5, inviteCode);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public String encryptPassword(String mobile, String password, String secretKey) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encryptedBytes = md5.digest((mobile + "|" + password + "|" + secretKey).getBytes("UTF-8"));

            Base64 base64 = new Base64();
            byte[] encryptedBase64 = base64.encode(encryptedBytes);

            return new String(encryptedBase64);
        } catch (Exception e) {
            throw new MomiaFailedException("fail to excrypt password of user: " + mobile, e);
        }
    }

    @Override
    public boolean validatePassword(String mobile, String password) {
        String sql = "SELECT mobile, password FROM t_user WHERE mobile=? AND password=?";

        return jdbcTemplate.query(sql, new Object[] { mobile, encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")) }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) return true;
                return false;
            }
        });
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
            user.setCityId(rs.getInt("cityId"));
            user.setRegionId(rs.getInt("regionId"));
            user.setAddress(rs.getString("address"));
            user.setChildren(parseChildren(rs.getString("children")));
            user.setInviteCode(rs.getString("inviteCode"));

            return user;
        } catch (Exception e) {
            LOGGER.error("fail to build user: {}", rs.getLong("id"), e);
            return User.INVALID_USER;
        }
    }

    private Set<Long> parseChildren(String children) {
        Set<Long> childrenIds = new HashSet<Long>();
        if (!StringUtils.isBlank(children)) {
            for (String childId : CHILDREN_SPLITTER.split(children)) {
                childrenIds.add(Long.valueOf(childId));
            }
        }

        return childrenIds;
    }

    @Override
    public List<User> get(Collection<Long> ids) {
        final List<User> users = new ArrayList<User>();
        if (ids == null || ids.size() <= 0) return users;

        String sql = "SELECT " + joinFields() + " FROM t_user WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                User user = buildUser(rs);
                if (user.exists()) users.add(user);
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
    public boolean updateNickName(long id, String nickName) {
        String sql = "UPDATE t_user SET nickName=? WHERE id=?";

        return update(sql, new Object[] { nickName, id });
    }

    private boolean update(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args) == 1;
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
    public boolean updateRegionId(long id, int regionId) {
        String sql = "UPDATE t_user SET `regionId`=? WHERE id=?";

        return update(sql, new Object[] { regionId, id });
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

        return update(sql, new Object[] { encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")), id });
    }

    @Override
    public boolean isPayed(long id) {
        String sql = "SELECT payed FROM t_user WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getBoolean("payed") : true;
            }
        });
    }

    @Override
    public boolean setPayed(long id) {
        String sql = "UPDATE t_user SET payed=? WHERE id=? AND payed=?";

        return update(sql, new Object[] { true, id, false });
    }

    @Override
    public long getIdByCode(String inviteCode) {
        String sql = "SELECT id FROM t_user WHERE inviteCode=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { inviteCode }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong("id") : 0;
            }
        });
    }
}
