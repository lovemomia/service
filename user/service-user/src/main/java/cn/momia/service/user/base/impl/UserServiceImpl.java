package cn.momia.service.user.base.impl;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.service.AbstractService;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.child.Child;
import cn.momia.service.user.child.ChildService;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
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

public class UserServiceImpl extends AbstractService implements UserService {
    private ChildService childService;

    public void setChildService(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public boolean exists(String field, String value) {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE " + field + "=?";
        return queryInt(sql, new Object[] { value }) > 0;
    }

    @Override
    public long add(final String nickName, final String mobile, final String password) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
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
        });

        return keyHolder.getKey().longValue();
    }

    private String encryptPassword(String mobile, String password, String secretKey) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encryptedBytes = md5.digest((mobile + "|" + password + "|" + secretKey).getBytes("UTF-8"));

            Base64 base64 = new Base64();
            byte[] encryptedBase64 = base64.encode(encryptedBytes);

            return new String(encryptedBase64);
        } catch (Exception e) {
            throw new MomiaErrorException("fail to excrypt password of user: " + mobile, e);
        }
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), Configuration.getString("SecretKey.UToken") }, "|"));
    }

    private String generateInviteCode(String nickName, String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { nickName, mobile, TimeUtil.STANDARD_DATE_FORMAT.format(new Date()), Configuration.getString("SecretKey.UToken") }, "|"));
    }

    @Override
    public User get(long userId) {
        List<User> users = list(Sets.newHashSet(userId));
        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public User getByToken(String token) {
        String sql = "SELECT Id FROM SG_User WHERE Token=? AND Status=1";
        List<Long> userIds = queryLongList(sql, new Object[] { token });
        List<User> users = list(userIds);

        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public User getByMobile(String mobile) {
        String sql = "SELECT Id FROM SG_User WHERE Mobile=? AND Status=1";
        List<Long> userIds = queryLongList(sql, new Object[] { mobile });
        List<User> users = list(userIds);

        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public User getByInviteCode(String inviteCode) {
        String sql = "SELECT Id FROM SG_User WHERE InviteCode=? AND Status=1";
        List<Long> userIds = queryLongList(sql, new Object[] { inviteCode });
        List<User> users = list(userIds);

        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    @Override
    public List<User> list(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new ArrayList<User>();

        String sql = "SELECT Id, NickName, Avatar, Mobile, Cover, Name, Sex, Birthday, CityId, RegionId, Address, Payed, InviteCode, Token, ImToken, Role FROM SG_User WHERE Id IN (%s) AND Status=1";
        List<User> users = listByIds(sql, userIds, Long.class, User.class);

        Map<Long, List<Child>> childrenMap = childService.queryByUsers(userIds);
        for (User user : users) {
            user.setChildren(childrenMap.get(user.getId()));
        }

        return users;
    }

    @Override
    public boolean updateNickName(long userId, String nickName) {
        if (exists("nickName", nickName)) throw new MomiaErrorException("昵称已存在，不能使用");

        String sql = "UPDATE SG_User SET NickName=? WHERE Id=?";
        return update(sql, new Object[] { nickName, userId });
    }

    @Override
    public boolean updateAvatar(long userId, String avatar) {
        String sql = "UPDATE SG_User SET Avatar=? WHERE Id=?";
        return update(sql, new Object[] { avatar, userId });
    }

    @Override
    public boolean updateCover(long userId, String cover) {
        String sql = "UPDATE SG_User SET Cover=? WHERE Id=?";
        return update(sql, new Object[] { cover, userId });
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
    public boolean updateImToken(long userId, String imToken) {
        String sql = "UPDATE SG_User SET ImToken=? WHERE Id=?";
        return update(sql, new Object[] { imToken, userId });
    }

    @Override
    public boolean hasPassword(String mobile) {
        String sql = "SELECT Password FROM SG_User WHERE Mobile=?";
        return !StringUtils.isBlank(queryString(sql, new Object[] { mobile }));
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

    @Override
    public boolean setPayed(long userId) {
        String sql = "UPDATE SG_User SET Payed=1 WHERE Id=? AND Payed=0";
        return update(sql, new Object[] { userId });
    }

    @Override
    public List<String> listMobiles(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new ArrayList<String>();

        String sql = String.format("SELECT Mobile FROM SG_User WHERE Id IN (%s) AND Status=1", StringUtils.join(userIds, ","));
        return queryStringList(sql);
    }
}
