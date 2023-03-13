package vn.ngs.nspace.hcm.sd.share.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FormulaSetupDTO {

    private Long id;
    private String code;
    private String name;
    private String type;
    private String syntax;
    private String variable;
    private Date startDate;
    private Date endDate;
    private String search;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
