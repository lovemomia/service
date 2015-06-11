package cn.momia.service.base.user.participant;

import java.util.Date;
import java.util.List;

public interface ParticipantService {

    long add(final Participant participant);
    boolean updateName(long id, String name);
    boolean updateSex(long id, int sex);
    boolean updateBirthday(long id, Date birthday);
    List<Participant> get(final long userId);
    boolean delete(long id);
}
