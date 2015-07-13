package cn.momia.service.base.user.participant.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantServiceImpl extends DbAccessService implements ParticipantService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private static final String[] PARTICIPANT_FIELDS = { "id", "userId", "name", "sex", "birthday", "idType", "idNo" };
    @Override
    public long add(final Participant participant) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_user_participant(userId, name, sex, birthday, idType,idNo, addTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, participant.getUserId());
                ps.setString(2, participant.getName());
                ps.setString(3, participant.getSex());
                ps.setDate(4, new java.sql.Date(participant.getBirthday().getTime()));
                ps.setInt(5, participant.getIdType());
                ps.setString(6, participant.getIdNo());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(Participant participant) {
        String sql = "UPDATE t_user_participant SET name=?, sex=?, birthday=?, idType=?, idNo=? WHERE id=? AND userId=?";

        return update(sql, new Object[] { participant.getName(), participant.getSex(), participant.getBirthday(), participant.getIdType(), participant.getIdNo(), participant.getId(), participant.getUserId() });
    }

    private boolean update(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args) == 1;
    }

    @Override
    public boolean updateByName( long id, String name, long userId) {
        String sql = "UPDATE t_user_participant SET name=? WHERE id=? AND userId=?";

        return update(sql, new Object[] { name, id, userId });
    }

    @Override
    public boolean updateBySex(long id, String sex, long userId) {
        String sql = "UPDATE t_user_participant SET sex=? WHERE id=? AND userId=?";

        return update(sql, new Object[] { sex, id, userId });
    }

    @Override
    public boolean updateByBirthday(long id, Date birthday, long userId) {
        String sql = "UPDATE t_user_participant SET birthday=? WHERE id=? AND userId=?";

        return update(sql, new Object[] { birthday, id, userId });
    }

    @Override
    public boolean delete(long id, long userId) {
        String sql = "UPDATE t_user_participant SET status=0 WHERE id=? AND userId=?";

        return update(sql, new Object[] { id, userId });
    }

    @Override
    public Participant get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_user_participant WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Participant>() {
            @Override
            public Participant extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildParticipant(rs);
                return Participant.NOT_EXIST_PARTICIPANT;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(PARTICIPANT_FIELDS, ",");
    }

    public Participant buildParticipant(ResultSet rs) throws SQLException {
        try {
            Participant participant = new Participant();
            participant.setId(rs.getLong("id"));
            participant.setUserId(rs.getLong("userId"));
            participant.setName(rs.getString("name"));
            participant.setSex(rs.getString("sex"));
            participant.setBirthday(rs.getDate("birthday"));
            participant.setIdType(rs.getInt("idType"));
            participant.setIdNo(rs.getString("idNo"));

            return participant;
        } catch (Exception e) {
            LOGGER.error("fail to build participant: {}", rs.getLong("id"), e);
            return Participant.INVALID_PARTICIPANT;
        }
    }

    @Override
    public Map<Long, Participant> get(Collection<Long> ids) {
        final Map<Long, Participant> participants = new HashMap<Long, Participant>();
        if (ids == null || ids.size() <= 0) return participants;

        String sql = "SELECT " + joinFields() + " FROM t_user_participant WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Participant participant = buildParticipant(rs);
                participants.put(participant.getId(), participant);
            }
        });

        return participants;
    }

    @Override
    public List<Participant> getByUser(long userId) {
        final List<Participant> participants = new ArrayList<Participant>();

        String sql = "SELECT " + joinFields() + " FROM t_user_participant WHERE userId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { userId }, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Participant participant = buildParticipant(rs);
                if (participant.exists()) participants.add(participant);
            }
        });

        return participants;
    }
}
