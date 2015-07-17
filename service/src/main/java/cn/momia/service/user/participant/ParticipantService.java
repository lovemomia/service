package cn.momia.service.user.participant;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ParticipantService {
    long add(Participant participant);

    Participant get(long id);
    Map<Long, Participant> get(Collection<Long> ids);
    List<Participant> getByUser(long userId);

    boolean update(Participant participant);
    boolean updateName(long userId, long id, String name);
    boolean updateSex(long userId, long id, String sex);
    boolean updateBirthday(long userId, long id, Date birthday);
    boolean delete(long userId, long id);
}
