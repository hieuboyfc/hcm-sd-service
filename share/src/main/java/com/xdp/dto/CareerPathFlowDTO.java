package com.xdp.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private CareerPathFlowDTO parent;
    private List<Long> empIds;
    private List<Long> qualifiedIds;
    private List<CareerPathFlowDTO> children = new ArrayList<>();
    private List<EmployeeDTO> employeeDTOS = new ArrayList<>();
    private Integer totalEmployee;
    private Boolean expanded;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
