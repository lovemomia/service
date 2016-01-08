package cn.momia.service.poi.inst.impl;

import cn.momia.api.poi.dto.Institution;
import cn.momia.common.service.AbstractService;
import cn.momia.service.poi.inst.InstitutionService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstitutionServiceImpl extends AbstractService implements InstitutionService {
    @Override
    public Institution get(int institutionId) {
        Set<Integer> institutionIds = Sets.newHashSet(institutionId);
        List<Institution> institutions = list(institutionIds);

        return institutions.isEmpty() ? Institution.NOT_EXIST_INSTITUTION : institutions.get(0);
    }

    @Override
    public List<Institution> list(Collection<Integer> institutionIds) {
        if (institutionIds.isEmpty()) return new ArrayList<Institution>();

        String sql = "SELECT Id, Name, Cover, Intro FROM SG_Institution WHERE Id IN (" + StringUtils.join(institutionIds, ",") + ") AND Status<>0";
        List<Institution> institutions = queryObjectList(sql, Institution.class);

        Map<Integer, Institution> institutionsMap = new HashMap<Integer, Institution>();
        for (Institution institution : institutions) {
            institutionsMap.put(institution.getId(), institution);
        }

        List<Institution> result = new ArrayList<Institution>();
        for (int institutionId : institutionIds) {
            Institution institution = institutionsMap.get(institutionId);
            if (institution != null) result.add(institution);
        }

        return result;
    }
}
