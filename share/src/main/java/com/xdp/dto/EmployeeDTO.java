package com.xdp.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class EmployeeDTO {
    private Long id;
    private String empNo;
    private String type;
    private String state;
    private String fullName;
    private String avatar;
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Date endProbationaryDate;
    private Date startDate;
    private Date endDate;
    private String workEmail;
    private String taxId;
    private Date taxIssueDate;
    private String taxIssuePlace;
    private Long bankId;
    private String bankBranch;
    private String cardNo;
    private Long managerId;
    private String mobile;
    private Integer status;
    private String userMappingId;
    private String position;
    private String orgName;
    private String orgCode;
    private String positionCode;
}