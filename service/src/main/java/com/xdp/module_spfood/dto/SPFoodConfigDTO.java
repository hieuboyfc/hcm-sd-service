package com.xdp.module_spfood.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SPFoodConfigDTO {

    private Long id;
    private String code;
    private String name;
    private String type;
    private String description;
    private String method;
    private String linkApi;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
