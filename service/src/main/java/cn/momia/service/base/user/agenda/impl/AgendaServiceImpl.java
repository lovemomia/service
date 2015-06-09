package cn.momia.service.base.user.agenda.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.user.agenda.Agenda;
import cn.momia.service.base.user.agenda.AgendaService;

public class AgendaServiceImpl extends DbAccessService implements AgendaService {
    @Override
    public Agenda getDaliyAgenda(long userId) {
        return null;
    }

    @Override
    public Agenda getWeeklyAgenda(long userId) {
        return null;
    }
}
