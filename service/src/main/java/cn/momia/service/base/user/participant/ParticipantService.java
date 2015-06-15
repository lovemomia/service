package cn.momia.service.base.user.participant;

import java.util.Date;
import java.util.List;

public interface ParticipantService {
    long add(Participant participant);
    boolean updateName(long id, String name);
    boolean updateSex(long id, int sex);
    boolean updateBirthday(long id, Date birthday);
    Participant get(long id);
    boolean delete(long id);
    List<Participant> getByUser(long userId);
}
