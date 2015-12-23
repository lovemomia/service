package cn.momia.service.teacher.impl;

import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.service.AbstractService;
import cn.momia.service.teacher.TeacherService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TeacherServiceImpl extends AbstractService implements TeacherService {
    @Override
    public TeacherStatus status(long userId) {
        String sql = "SELECT Status, Msg FROM SG_Teacher WHERE UserId=? AND Status<>0";
        return queryObject(sql, new Object[] { userId }, TeacherStatus.class, TeacherStatus.NOT_EXIST_TEACHER_STATUS);
    }

    @Override
    public long add(final Teacher teacher) {
        long teacherId = getIdByUser(teacher.getUserId());
        if (teacherId > 0) {
            String sql = "UPDATE SG_Teacher SET Pic=?, Name=?, IdNo=?, Sex=?, Birthday=?, Address=?, Status=? WHERE Id=? AND UserId=?";
            update(sql, new Object[] { teacher.getPic(), teacher.getName(), teacher.getIdNo(), teacher.getSex(), teacher.getBirthday(), teacher.getAddress(), TeacherStatus.Status.NOT_CHECKED, teacherId, teacher.getUserId() });
        } else {
            KeyHolder keyHolder = insert(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO SG_Teacher(UserId, Pic, Name, IdNo, Sex, Birthday, Address, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, teacher.getUserId());
                    ps.setString(2, teacher.getPic());
                    ps.setString(3, teacher.getName());
                    ps.setString(4, teacher.getIdNo());
                    ps.setString(5, teacher.getSex());
                    ps.setDate(6, new java.sql.Date(teacher.getBirthday().getTime()));
                    ps.setString(7, teacher.getAddress());
                    ps.setInt(8, TeacherStatus.Status.NOT_CHECKED);

                    return ps;
                }
            });

            teacherId = keyHolder.getKey().longValue();
        }

        return teacherId;
    }

    private long getIdByUser(long userId) {
        String sql = "SELECT Id FROM SG_Teacher WHERE UserId=?";
        return queryLong(sql, new Object[] { userId });
    }
}
