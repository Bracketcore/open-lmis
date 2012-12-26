package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@NoArgsConstructor
public class SupervisoryNodeRepository {
    private SupervisoryNodeMapper supervisoryNodeMapper;
    private FacilityRepository facilityRepository;

    @Autowired
    public SupervisoryNodeRepository(SupervisoryNodeMapper supervisoryNodeMapper, FacilityRepository facilityRepository) {
        this.supervisoryNodeMapper = supervisoryNodeMapper;
        this.facilityRepository = facilityRepository;
    }

    public void save(SupervisoryNode supervisoryNode) {
        supervisoryNode.getFacility().setId(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode()));
        if (supervisoryNode.getParent() != null) {
            supervisoryNode.getParent().setId(getSupervisoryNodeParentId(supervisoryNode.getId()));
            if (supervisoryNode.getParent().getId() == null) {
                throw new DataException("Supervisory Node Parent does not exist");
            }
        }

        try {
            supervisoryNodeMapper.insert(supervisoryNode);
        } catch (DuplicateKeyException e) {
            throw new DataException("Duplicate SupervisoryNode Code");
        }
    }

    public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Integer userId, Integer programId, Right right) {
        return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, right);
    }

    public Integer getIdForCode(String code) {
        Integer supervisoryNodeId = supervisoryNodeMapper.getIdForCode(code);
        if (supervisoryNodeId == null)
            throw new DataException("Invalid SupervisoryNode Code");

        return supervisoryNodeId;
    }

    public Integer getSupervisoryNodeParentId(Integer supervisoryNodeId) {
        SupervisoryNode parent = supervisoryNodeMapper.getSupervisoryNode(supervisoryNodeId).getParent();
        return parent == null ? null : parent.getId();
    }
}
