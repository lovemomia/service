package cn.momia.service.base.user.participant;

import java.util.Date;
import java.util.List;

public interface ParticipantService {
    long add(Participant participant);
    boolean updateName(long id, long userId, String name);
    boolean updateSex(long id, long userId, int sex);
    boolean updateBirthday(long id, long userId, Date birthday);
    Participant get(long id, long userId);
    boolean delete(long id, long userId);
    List<Participant> getByUser(long userId);
}
