package cn.momia.service.poi.inst.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.poi.inst.Institution;
import cn.momia.service.poi.inst.InstitutionService;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;

public class InstitutionServiceImpl extends AbstractService implements InstitutionService {
    @Override
    public Institution get(int institutionId) {
        List<Institution> institutions = list(Sets.newHashSet(institutionId));
        return institutions.isEmpty() ? Institution.NOT_EXIST_INSTITUTION : institutions.get(0);
    }

    @Override
    public List<Institution> list(Collection<Integer> institutionIds) {
        String sql = "SELECT Id, Name, Cover, Intro FROM SG_Institution WHERE Id IN (%s) AND Status=1";
        return listByIds(sql, institutionIds, Integer.class, Institution.class);
    }
}
