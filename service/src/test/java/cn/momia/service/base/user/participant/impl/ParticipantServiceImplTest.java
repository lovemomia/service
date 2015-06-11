package cn.momia.service.base.user.participant.impl;

import cn.momia.service.base.user.participant.Participant;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void testAdd() throws Exception {
        DB();
        Participant participant = new Participant();
        participant.setUserId(1);
        participant.setName("test1");
        participant.setSex(1);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String dateStringToParse = "1991-7-12";
        java.util.Date d = f.parse(dateStringToParse);
        java.sql.Date sqlDate = new java.sql.Date(d.getTime());
        participant.setBirthday(sqlDate);
        

        Participant participant1 = new Participant();
        participant1.setUserId(1);
        participant1.setName("test2");
        participant1.setSex(0);
        Date date = f.parse(f.format(new Date()));
        java.sql.Date sqlDate1 = new java.sql.Date(date.getTime());
        participant1.setBirthday(sqlDate1);
        participantService.add(1,participant1);

    }

    @Test
    public void testUpdateName() throws Exception {

        DB();
        List<Participant> participants = participantService.get(1);
        Participant participant = participants.get(1);
        participant.setName("modifyName");
        participantService.updateName(participant.getId(),participant.getName());


    }

    @Test
    public void testUpdateSex() throws Exception {
        DB();
        List<Participant> participants = participantService.get(1);
        Participant participant = participants.get(1);
        participant.setSex(1);
        participantService.updateSex(participant.getId(),participant.getSex());

    }

    @Test
    public void testUpdateBirthday() throws Exception {
        DB();
        List<Participant> participants = participantService.get(1);
        Participant participant = participants.get(1);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String dateStringToParse = "1991-7-12";
        java.util.Date d = f.parse(dateStringToParse);
        java.sql.Date sqlDate = new java.sql.Date(d.getTime());
        participant.setBirthday(sqlDate);

        participantService.updateBirthday(participant.getId(),participant.getBirthday());

    }

    @Test
    public void testGet() throws Exception {
        DB();
        List<Participant> participants = participantService.get(1);
        for(Participant participant : participants)
            System.out.println(participant.getName());

    }

    @Test
    public void testGetOne() throws Exception {
        DB();
        Participant participant = participantService.get(1,1);
        System.out.println(participant.getName());

    }
    @Test
    public void testDelete() throws Exception {

    }
}