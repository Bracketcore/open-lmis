package org.openlmis.rnr.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class RnrDTO {

  private Integer id;
  private String programName;
  private Integer programId;
  private String facilityName;
  private String facilityCode;
  private Date submittedDate;
  private Date modifiedDate;
  private Date periodStartDate;
  private Date periodEndDate;

  public static List<RnrDTO> prepareForListApproval(List<Rnr> requisitions) {
    List<RnrDTO> result = new ArrayList<>();
    for (Rnr requisition : requisitions) {
      result.add(prepareForListApproval(requisition));
    }
    return result;
  }

  private static RnrDTO prepareForListApproval(Rnr requisition) {
    RnrDTO rnrDTO = new RnrDTO();
    rnrDTO.id = requisition.getId();
    rnrDTO.programId = requisition.getProgram().getId();
    rnrDTO.programName = requisition.getProgram().getName();
    rnrDTO.facilityCode = requisition.getFacility().getCode();
    rnrDTO.facilityName = requisition.getFacility().getName();
    rnrDTO.submittedDate = requisition.getSubmittedDate();
    rnrDTO.modifiedDate = requisition.getModifiedDate();
    rnrDTO.periodStartDate = requisition.getPeriod().getStartDate();
    rnrDTO.periodEndDate = requisition.getPeriod().getEndDate();
    return rnrDTO;
  }
}
