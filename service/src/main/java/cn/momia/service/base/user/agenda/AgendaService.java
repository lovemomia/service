package cn.momia.service.base.user.agenda;

public interface AgendaService {
    Agenda getDaliyAgenda(long userId);
    Agenda getWeeklyAgenda(long userId);
}
