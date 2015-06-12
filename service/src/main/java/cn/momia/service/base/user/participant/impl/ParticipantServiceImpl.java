package cn.momia.service.base.user.participant.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParticipantServiceImpl extends DbAccessService implements ParticipantService {

    public long addParticipant(final long userId,final Participant participant) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into t_user_participant(userId, name, sex, birthday, addTime) values(?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setString(2, participant.getName());
                ps.setInt(3, participant.getSex());
                java.sql.Date sqlDate = new java.sql.Date(participant.getBirthday().getTime());
                ps.setDate(4,  sqlDate);
                return ps;
            }
        },keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public long add(long userId,Participant participant) {
        return addParticipant(userId,participant);
    }

    public  boolean update( String sql, Object[] args){
        int affectedRowCount = jdbcTemplate.update(sql, args);
        if (affectedRowCount != 1) return false;
        else return true;
    }

    @Override
    public boolean updateName(final long id, final String name) {
        String sql = "update t_user_participant set name=? where id=?";
        return update(sql,new Object[]{name,id});
    }

    @Override
    public boolean updateSex(long id, int sex) {
        String sql = "update t_user_participant set sex=? where id=?";
        return update(sql,new Object[]{sex,id});
    }

    @Override
    public boolean updateBirthday(long id, java.util.Date birthday) {
       String sql = "update t_user_participant set birthday=? where id=?";
        return update(sql,new Object[]{birthday,id});
    }


    public Participant buildParticipant(ResultSet rs) throws SQLException {
        Participant participant = new Participant();

        participant.setId(rs.getLong("id"));
        participant.setUserId(rs.getLong("userId"));
        participant.setName(rs.getString("name"));
        participant.setSex(rs.getInt("sex"));
        participant.setBirthday(rs.getDate("birthday"));

        return participant;

    }

    @Override
    public List<Participant> get(long userId) {
        final List<Participant> participants = new ArrayList<Participant>();
        String sql = "select id, userId, name, sex, birthday from t_user_participant where userId=?";
        jdbcTemplate.query(sql, new Object[]{userId}, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                participants.add(buildParticipant(resultSet));
            }
        });
        return participants;
    }

    @Override
    public Participant get(long userId, long id) {
        String sql = "select id,userId,name,sex,birthday from t_user_participant where userId=? AND id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId,id }, new ResultSetExtractor<Participant>() {
            @Override
            public Participant extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildParticipant(rs);
                return Participant.NOT_EXIST_PARTICIPANT;
            }
        });
    }

    @Override
    public boolean delete(long id) {
        String sql = "delete from t_user_participant where id=?";
        int affectedRowCount = jdbcTemplate.update(sql,new Object[]{id});
        if (affectedRowCount != 1) return false;
        else return true;

    }
}
