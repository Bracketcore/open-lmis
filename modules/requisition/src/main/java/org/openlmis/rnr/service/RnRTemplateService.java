package org.openlmis.rnr.service;

import org.openlmis.rnr.dao.RnRColumnMapper;
import org.openlmis.rnr.domain.RnRColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RnRTemplateService {
    private RnRColumnMapper rnrColumnMapper;

    @Autowired
    public RnRTemplateService(RnRColumnMapper rnrColumnMapper) {
        this.rnrColumnMapper = rnrColumnMapper;
    }

    public List<RnRColumn> fetchAllMasterColumns() {
        List<RnRColumn> rnRColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
        return rnRColumns==null ? new ArrayList<RnRColumn>(): rnRColumns;
    }
}
