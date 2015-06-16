package cn.momia.service.base.user.participant;

import java.util.Date;
import java.util.List;

public interface ParticipantService {
    long add(Participant participant);
    boolean update(Participant participant);
    boolean delete(long id, long userId);
    Participant get(long id);
    List<Participant> getByUser(long userId);
}
