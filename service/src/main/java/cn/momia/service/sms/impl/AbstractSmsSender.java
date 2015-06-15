package cn.momia.service.sms.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.sms.SmsSender;
import com.sun.scenario.animation.shared.CurrentTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public abstract class AbstractSmsSender extends DbAccessService implements SmsSender {

    @Override
    public boolean send(String phone)  {
        if(!isPhoneExits(phone))
            saveVerifyDb(phone,"");

        long generatedTime = getGeneratedTime(phone);
        long currentTime = new java.util.Date().getTime();
        String code = "";
        System.out.println(generatedTime);
        System.out.println(currentTime);

       if(currentTime-generatedTime <= 1000*60*30) {
            code = getGeneratedCode(phone);
            if (StringUtils.isBlank(code)) {
                code = generateCode();
                updateVerifyDb(phone, code);
            }
        }
        else {
           code = generateCode();
           updateVerifyDb(phone, code);

       }


        return doSend(phone, code);
    }


    public boolean isPhoneExits(String phone) {
        String sql = "select id from t_verify where phone=?";
        return jdbcTemplate.query(sql, new Object[]{phone}, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) return true;
                return false;
            }
        });


    }

    public Long getGeneratedTime(String phone)  {
        String sql = "select generateTime from t_verify where phone="+phone;
        long time;
        try {
            time =  jdbcTemplate.queryForObject(sql, Timestamp.class).getTime();
        }
        catch (EmptyResultDataAccessException E) {
            time = 0L;
            return time;
        }
        return time;
    }

    public String getGeneratedCode(String phone){
        String sql = "select code from t_verify where phone=?";
        String code = jdbcTemplate.query(sql, new Object[]{ phone }, new ResultSetExtractor<String>() {
           @Override
           public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
               if(resultSet.next()) return resultSet.getString(1);
               return "";
           }
       });
       return code;
    }

    private String generateCode() {
        // TODO 生成一个随即6位数
        Random random = new Random();
        String result="";
        for(int i=0;i<6;i++){
            result+=random.nextInt(10);
        }
        return result;
    }

    protected abstract boolean doSend(String phone, String code);

    private void updateVerifyDb(final String phone, final String code) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String sql = "update t_verify set code=? ,generateTime=NOW() where phone=?";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, code);
                ps.setString(2, phone);

                return ps;
            }
        });

    }
    private void saveVerifyDb(final String phone, final String code){
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String sql = "insert into t_verify(phone, code, generateTime) values(?, ?, NOW())";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, phone);
                ps.setString(2, code);
                return ps;
            }
        });
    }
}
