package com.xdp.module_spfood.entity;

import com.xdp.lib.models.PersistableEntity;
import com.xdp.module_spfood.dto.SPFoodTicketDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "sp_food_ticket",
        indexes = {
                @Index(name = "SPFoodTicket_idx", columnList = "companyId"),
                @Index(name = "SPFoodTicket_createBy_idx", columnList = "createBy"),
                @Index(name = "SPFoodTicket_requestedBy_idx", columnList = "requestedBy"),
                @Index(name = "SPFoodTicket_responsibleId_idx", columnList = "responsibleId"),
                @Index(name = "SPFoodTicket_type_idx", columnList = "type")
        }
)
// Bảng: Hỗ trợ sự cố
public class SPFoodTicket extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 20)
    private String type;

    @Column(length = 50)
    private String requestType; // Loại yêu cầu

    @Column(length = 50)
    private String handleType; // Loại xử lý

    @Column(length = 100)
    private String groupSetup; // Nhóm cài đặt

    @Column(length = 500)
    private String photo;

    @Column(columnDefinition = "varchar")
    private String description;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String phone;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date responseDeadline; // Thời hạn phản hồi

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date deadline; // Thời hạn xử lý

    @Column(columnDefinition = "varchar")
    private String solution; // Giải pháp

    private Long projectId;

    private Long rootPhaseId;

    private String requestedBy;

    private String responsibleId;

    public static SPFoodTicket of(Long cid, SPFoodTicketDTO dto) {
        return SPFoodTicket.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .type(dto.getType())
                .requestType(dto.getRequestType())
                .handleType(dto.getHandleType())
                .groupSetup(dto.getGroupSetup())
                .photo(dto.getPhoto())
                .description(dto.getDescription())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .startDate(new Date())
                .responseDeadline(dto.getResponseDeadline())
                .deadline(dto.getDeadline())
                .solution(dto.getSolution())
                .projectId(dto.getProjectId())
                .rootPhaseId(dto.getRootPhaseId())
                .requestedBy(dto.getRequestedBy())
                .responsibleId(dto.getResponsibleId())
                .status(dto.getStatus())
                .companyId(cid)
                .build();
    }

    public static SPFoodTicketDTO toDTO(SPFoodTicket entity) {
        return SPFoodTicketDTO.builder()
                .id(entity.getId())
                .code(entity.getCode().toUpperCase())
                .name(entity.getName())
                .type(entity.getType())
                .requestType(entity.getRequestType())
                .handleType(entity.getHandleType())
                .groupSetup(entity.getGroupSetup())
                .photo(entity.getPhoto())
                .description(entity.getDescription())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .startDate(entity.getStartDate())
                .responseDeadline(entity.getResponseDeadline())
                .deadline(entity.getDeadline())
                .solution(entity.getSolution())
                .projectId(entity.getProjectId())
                .rootPhaseId(entity.getRootPhaseId())
                .solution(entity.getSolution())
                .requestedBy(entity.getRequestedBy())
                .responsibleId(entity.getResponsibleId())
                .status(entity.getStatus())
                .createDate(entity.getCreateDate())
                .modifiedDate(entity.getModifiedDate())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .companyId(entity.getCompanyId())
                .createBy(entity.getCreateBy())
                .updateBy(entity.getUpdateBy())
                .build();
    }

    public static List<SPFoodTicketDTO> toDTOs(List<SPFoodTicket> list) {
        return list.stream().map(SPFoodTicket::toDTO).collect(Collectors.toList());
    }
}
