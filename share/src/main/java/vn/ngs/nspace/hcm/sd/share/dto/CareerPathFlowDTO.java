package vn.ngs.nspace.hcm.sd.share.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CareerPathFlowDTO {

    private Long id;
    private String code;
    private String name;
    private Long careerPathId;
    private Long positionId;
    private Long parentId;
    private Integer level;
    private String path;
    private String pathName;
    private String description;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
