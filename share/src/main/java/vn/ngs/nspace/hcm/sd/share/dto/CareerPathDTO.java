package vn.ngs.nspace.hcm.sd.share.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CareerPathDTO {

    private Long id;
    private String code;
    private String name;
    private Long orgId;
    private String description;
    private List<CareerPathFlowDTO> careerPathFlowDTOS;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
