package cn.momia.service.user.base.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.child.Child;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.base.child.ChildService;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserServiceImpl extends DbAccessService implements UserService {
    private ChildService childService;

    public void setChildService(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public long add(final String nickName, final String mobile, final String password) {
        if (exists("nickName", nickName)) throw new MomiaFailedException("昵称已存在，不能使用");
        if (exists("mobile", mobile)) throw new MomiaFailedException("手机号已经注册过");

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

    private boolean exists(String field, String value) {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE " + field + "=?";
        return queryInt(sql, new Object[] { value }) > 0;
    }

    private String encryptPassword(String mobile, String password, String secretKey) {
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
    public User get(long userId) {
        Set<Long> userIds = Sets.newHashSet(userId);
        List<User> users = list(userIds);

        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public List<User> list(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new ArrayList<User>();

        String sql = "SELECT Id, NickName, Avatar, Mobile, Name, Sex, Birthday, CityId, RegionId, Address, Payed, Token FROM SG_User WHERE Id IN (" + StringUtils.join(userIds, ",") + ") AND Status=1";
        List<User> users = queryList(sql, User.class);

        Map<Long, List<Child>> childrenMap = childService.queryByUsers(userIds);
        for (User user : users) {
            user.setChildren(childrenMap.get(user.getId()));
        }

        return users;
    }

    @Override
    public User getByToken(String token) {
        String sql = "SELECT Id FROM SG_User WHERE Token=? AND Status=1";
        List<Long> userIds = queryLongList(sql, new Object[] { token });
        List<User> users = list(userIds);

        return userIds.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public User getByMobile(String mobile) {
        String sql = "SELECT Id FROM SG_User WHERE Mobile=? AND Status=1";
        List<Long> userIds = queryLongList(sql, new Object[] { mobile });
        List<User> users = list(userIds);

        return userIds.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public boolean updateNickName(long userId, String nickName) {
        if (exists("nickName", nickName)) throw new MomiaFailedException("昵称已存在，不能使用");

        String sql = "UPDATE SG_User SET NickName=? WHERE Id=?";
        return update(sql, new Object[] { nickName, userId });
    }

    @Override
    public boolean updateAvatar(long userId, String avatar) {
        String sql = "UPDATE SG_User SET Avatar=? WHERE Id=?";
        return update(sql, new Object[] { avatar, userId });
    }

    @Override
    public boolean updateName(long userId, String name) {
        String sql = "UPDATE SG_User SET Name=? WHERE Id=?";
        return update(sql, new Object[] { name, userId });
    }

    @Override
    public boolean updateSex(long userId, String sex) {
        String sql = "UPDATE SG_User SET Sex=? WHERE Id=?";
        return update(sql, new Object[] { sex, userId });
    }

    @Override
    public boolean updateBirthday(long userId, Date birthday) {
        String sql = "UPDATE SG_User SET Birthday=? WHERE Id=?";
        return update(sql, new Object[] { birthday, userId });
    }

    @Override
    public boolean updateCityId(long userId, int cityId) {
        String sql = "UPDATE SG_User SET CityId=? WHERE Id=?";
        return update(sql, new Object[] { cityId, userId });
    }

    @Override
    public boolean updateRegionId(long userId, int regionId) {
        String sql = "UPDATE SG_User SET RegionId=? WHERE Id=?";
        return update(sql, new Object[] { regionId, userId });
    }

    @Override
    public boolean updateAddress(long userId, String address) {
        String sql = "UPDATE SG_User SET Address=? WHERE Id=?";
        return update(sql, new Object[] { address, userId });
    }

    @Override
    public boolean validatePassword(String mobile, String password) {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE Mobile=? AND Password=?";
        return queryInt(sql, new Object[] { mobile, encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")) }) == 1;
    }

    @Override
    public boolean updatePassword(long userId, String mobile, String password) {
        String sql = "UPDATE SG_User SET Password=? WHERE Id=?";
        return update(sql, new Object[] { encryptPassword(mobile, password, Configuration.getString("SecretKey.Password")), userId });
    }
}
