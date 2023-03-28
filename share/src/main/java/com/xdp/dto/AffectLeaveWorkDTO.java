package com.xdp.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class AffectLeaveWorkDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String color;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
