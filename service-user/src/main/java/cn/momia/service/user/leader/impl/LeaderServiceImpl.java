package cn.momia.service.user.leader.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.user.leader.LeaderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LeaderServiceImpl extends DbAccessService implements LeaderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderServiceImpl.class);

    private static final String[] LEADER_FIELDS = { "id", "userId", "name", "mobile", "cityId", "regionId", "address", "career", "intro", "msg", "status" };

    @Override
    public Leader getByUser(long userId) {
        String sql = "SELECT " + joinFields() + " FROM t_user_leader WHERE userId=? LIMIT 1";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Leader>() {
            @Override
            public Leader extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildLeader(rs);
                return Leader.NOT_EXIST_LEADER;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(LEADER_FIELDS, ",");
    }

    private Leader buildLeader(ResultSet rs) throws SQLException {
        try {
            Leader leader = new Leader();
            leader.setId(rs.getLong("id"));
            leader.setUserId(rs.getLong("userId"));
            leader.setName(rs.getString("name"));
            leader.setMobile(rs.getString("mobile"));
            leader.setCityId(rs.getInt("cityId"));
            leader.setRegionId(rs.getInt("regionId"));
            leader.setAddress(rs.getString("address"));
            leader.setCareer(rs.getString("career"));
            leader.setIntro(rs.getString("intro"));
            leader.setMsg(rs.getString("msg"));
            leader.setStatus(rs.getInt("status"));

            return leader;
        } catch (Exception e) {
            LOGGER.error("fail to build leader info: {}", rs.getLong("id"), e);
            return Leader.INVALID_LEADER;
        }
    }

    @Override
    public long add(final Leader leader) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_user_leader(userId, name, mobile, cityId, regionId, address, career, intro, addTime) VALUES(?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, leader.getUserId());
                ps.setString(2, leader.getName());
                ps.setString(3, leader.getMobile());
                ps.setInt(4, leader.getCityId());
                ps.setInt(5, leader.getRegionId());
                ps.setString(6, leader.getAddress());
                ps.setString(7, leader.getCareer());
                ps.setString(8, leader.getIntro());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(Leader leader) {
        String sql = "UPDATE t_user_leader SET name=?, mobile=?, cityId=?, regionId=?, address=?, career=?, intro=?, status=1 WHERE userId=?";

        return jdbcTemplate.update(sql, new Object[] { leader.getName(),
                leader.getMobile(),
                leader.getCityId(),
                leader.getRegionId(),
                leader.getAddress(),
                leader.getCareer(),
                leader.getIntro(),
                leader.getUserId() }) > 0;
    }

    @Override
    public boolean deleteByUser(long userId) {
        String sql = "UPDATE t_user_leader SET status=0 WHERE userId=?";

        return jdbcTemplate.update(sql) > 0;
    }

    @Override
    public boolean reapply(Leader leader) {
        return update(leader);
    }
}
