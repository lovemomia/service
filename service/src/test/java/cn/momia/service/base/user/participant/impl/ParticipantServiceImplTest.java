package cn.momia.service.base.user.participant.impl;

import cn.momia.service.base.user.participant.Participant;
import com.alibaba.fastjson.JSON;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by ysm on 15-6-11.
 */
public class ParticipantServiceImplTest {
    private ParticipantServiceImpl participantService = new ParticipantServiceImpl();
    public static final String url = "jdbc:mysql://120.55.102.12:3306/tongqu?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "tongqu";
    public static final String password = "Tongqu!@#456";

    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    public JdbcTemplate jdbcTemplate = new JdbcTemplate();
    public void DB()throws Exception{
        dataSource.setDriverClass(name);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setMaxPoolSize(30);
        dataSource.setMaxIdleTime(7200);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setIdleConnectionTestPeriod(5);
        dataSource.setPreferredTestQuery("SELECT 1");
        dataSource.setCheckoutTimeout(1800000);
        jdbcTemplate.setDataSource(dataSource);
        participantService.setJdbcTemplate(jdbcTemplate);
    }

    @Test
    public void testGet() throws Exception {
        DB();
        List<Participant> participants = participantService.getByUser(1);
        for(Participant participant : participants)
            System.out.println(participant.getName());
    }

    @Test
    public void testDelete() throws Exception {

    }
}