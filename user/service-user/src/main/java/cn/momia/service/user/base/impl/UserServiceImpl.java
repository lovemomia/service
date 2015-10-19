package cn.momia.service.user.base.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserChild;
import cn.momia.service.user.base.UserService;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserServiceImpl extends DbAccessService implements UserService {
    @Override
    public boolean exists(String field, String value) {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE " + field + "=?";
        return queryInt(sql, new Object[] { value }) > 0;
    }

    @Override
    public long add(final String nickName, final String mobile, final String password) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_User(NickName, Mobile, Password, Token, InviteCode, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, nickName);
                ps.setString(2, mobile);
                ps.setString(3, encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")));
                ps.setString(4, generateToken(mobile));
                ps.setString(5, generateInviteCode(nickName, mobile));

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

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), Configuration.getString("SecretKey.UToken") }, "|"));
    }

    private String generateInviteCode(String nickName, String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { nickName, mobile, TimeUtil.STANDARD_FORMAT.format(new Date()), Configuration.getString("SecretKey.UToken") }, "|"));
    }

    @Override
    public boolean validatePassword(String mobile, String password) {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE Mobile=? AND Password=?";
        return queryInt(sql, new Object[] { mobile, encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")) }) == 1;
    }

    @Override
    public User get(long id) {
        Set<Long> ids = Sets.newHashSet(id);
        List<User> users = list(ids);

        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public List<User> list(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<User>();

        String sql = "SELECT * FROM SG_User WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        List<User> users = queryList(sql, User.class);

        Map<Long, List<UserChild>> childrenMap = queryChildren(ids);
        for (User user : users) {
            user.setChildren(childrenMap.get(user.getId()));
        }

        return users;
    }

    private Map<Long, List<UserChild>> queryChildren(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<UserChild>>();

        String sql = "SELECT Id FROM SG_UserChild WHERE UserId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        List<Long> childIds = queryLongList(sql);
        List<UserChild> children = listChildren(childIds);

        Map<Long, List<UserChild>> childrenMap = new HashMap<Long, List<UserChild>>();
        for (long id : ids) {
            childrenMap.put(id, new ArrayList<UserChild>());
        }
        for (UserChild child : children) {
            childrenMap.get(child.getUserId()).add(child);
        }

        return childrenMap;
    }

    private List<UserChild> listChildren(Collection<Long> childIds) {
        if (childIds.isEmpty()) return new ArrayList<UserChild>();

        String sql = "SELECT * FROM SG_UserChild WHERE UserId IN (" + StringUtils.join(childIds, ",") + ") AND Status=1";
        return queryList(sql, UserChild.class);
    }

    @Override
    public User getByToken(String token) {
        String sql = "SELECT Id FROM SG_User WHERE Token=? AND Status=1";
        List<Long> ids = queryLongList(sql, new Object[] { token });
        List<User> users = list(ids);

        return ids.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public User getByMobile(String mobile) {
        String sql = "SELECT Id FROM SG_User WHERE Mobile=? AND Status=1";
        List<Long> ids = queryLongList(sql, new Object[] { mobile });
        List<User> users = list(ids);

        return ids.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public boolean updateNickName(long id, String nickName) {
        String sql = "UPDATE SG_User SET NickName=? WHERE Id=?";
        return update(sql, new Object[] { nickName, id });
    }

    private boolean update(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args) == 1;
    }

    @Override
    public boolean updateAvatar(long id, String avatar) {
        String sql = "UPDATE SG_User SET Avatar=? WHERE Id=?";
        return update(sql, new Object[] { avatar, id });
    }

    @Override
    public boolean updateName(long id, String name) {
        String sql = "UPDATE SG_User SET Name=? WHERE Id=?";
        return update(sql, new Object[] { name, id });
    }

    @Override
    public boolean updateSex(long id, String sex) {
        String sql = "UPDATE SG_User SET Sex=? WHERE Id=?";
        return update(sql, new Object[] { sex, id });
    }

    @Override
    public boolean updateBirthday(long id, Date birthday) {
        String sql = "UPDATE SG_User SET Birthday=? WHERE Id=?";
        return update(sql, new Object[] { birthday, id });
    }

    @Override
    public boolean updateCityId(long id, int cityId) {
        String sql = "UPDATE SG_User SET CityId=? WHERE Id=?";
        return update(sql, new Object[] { cityId, id });
    }

    @Override
    public boolean updateRegionId(long id, int regionId) {
        String sql = "UPDATE SG_User SET RegionId=? WHERE Id=?";
        return update(sql, new Object[] { regionId, id });
    }

    @Override
    public boolean updateAddress(long id, String address) {
        String sql = "UPDATE SG_User SET Address=? WHERE Id=?";
        return update(sql, new Object[] { address, id });
    }

    @Override
    public boolean updatePassword(long id, String mobile, String password) {
        String sql = "UPDATE SG_User SET Password=? WHERE Id=?";
        return update(sql, new Object[] { encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")), id });
    }

    @Override
    public void addChild(UserChild child) {
        String sql = "INSERT INTO SG_UserChild (UserId, Avatar, Name, Sex, Birthday, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql, new Object[] {
                child.getUserId(),
                child.getAvatar(),
                child.getName(),
                child.getSex(),
                child.getBirthday()
        });
    }

    @Override
    public UserChild getChild(long childId) {
        Set<Long> childIds = Sets.newHashSet(childId);
        List<UserChild> children = listChildren(childIds);

        return children.isEmpty() ? UserChild.NOT_EXIST_USER_CHILD : children.get(0);
    }

    @Override
    public boolean updateChildAvatar(long id, long childId, String avatar) {
        String sql = "UPDATE SG_UserChild SET Avatar=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { avatar, id, childId });
    }

    @Override
    public boolean updateChildName(long id, long childId, String name) {
        String sql = "UPDATE SG_UserChild SET Name=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { name, id, childId });
    }

    @Override
    public boolean updateChildSex(long id, long childId, String sex) {
        String sql = "UPDATE SG_UserChild SET Sex=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { sex, id, childId });
    }

    @Override
    public boolean updateChildBirthday(long id, long childId, Date birthday) {
        String sql = "UPDATE SG_UserChild SET Birthday=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { birthday, id, childId });
    }

    @Override
    public boolean deleteChild(long id, long childId) {
        String sql = "UPDATE SG_UserChild SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { id, childId });
    }
}
