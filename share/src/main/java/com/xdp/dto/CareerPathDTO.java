package com.xdp.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private String search;
    private Date startDate;
    private Date endDate;
    private Integer totalStep;
    private List<CareerPathFlowDTO> careerPathFlowDTOS = new ArrayList<>();
    private List<CareerPathFlowDTO> updateCareerPathFlowDTOS = new ArrayList<>();
    private List<Map<String, Object>> deleteCareerPathFlowDTOs = new ArrayList<>();

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
