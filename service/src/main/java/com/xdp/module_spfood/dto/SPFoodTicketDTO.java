package com.xdp.module_spfood.dto;

import lombok.*;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SPFoodTicketDTO {

    private Long id;
    private String code;
    private String name;
    private String type;
    private String requestType; // Loại yêu cầu
    private String handleType; // Loại xử lý
    private String groupSetup; // Nhóm cài đặt
    private String photo;
    private String description;
    private String email;
    private String phone;
    private Date startDate;
    private Date responseDeadline; // Thời hạn phản hồi
    private Date deadline; // Thời hạn xử lý
    private String solution; // Giải pháp
    private Long projectId;
    private Long rootPhaseId;
    private String requestedBy;
    private String responsibleId;

    private Map<String, Object> project;
    private Map<String, Object> phase;
    private Map<String, Object> requested;
    private Map<String, Object> responsible;
    private String statusName;

    private Date createDate;
    private Date modifiedDate;
    private Long version;
    private Integer status;
    private Long companyId;
    private String createBy;
    private String updateBy;

}
