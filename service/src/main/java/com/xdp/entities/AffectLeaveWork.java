package com.xdp.entities;

import com.xdp.lib.models.PersistableEntity;
import com.xdp.dto.AffectLeaveWorkDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import com.xdp.utils.SDUtils;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(
        name = SDUtils.AFFECT_LEAVE_WORK,
        indexes = {
                @Index(name = "AffectLeaveWork_idx", columnList = "companyId")
        }
)
// Bảng: Ảnh hưởng nghỉ việc
public class AffectLeaveWork extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 8, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(length = 20)
    private String icon;

    @Column(length = 20)
    private String color;

    public static AffectLeaveWork of(Long cid, String uid, AffectLeaveWorkDTO dto) {
        return AffectLeaveWork.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .color(dto.getColor())
                .companyId(cid)
                .status(dto.getStatus())
                .build();
    }

    public static AffectLeaveWorkDTO toDTO(AffectLeaveWork entity) {
        return AffectLeaveWorkDTO.builder()
                .id(entity.getId())
                .code(entity.getCode().toUpperCase())
                .name(entity.getName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .color(entity.getColor())
                .createDate(entity.getCreateDate())
                .modifiedDate(entity.getModifiedDate())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .companyId(entity.getCompanyId())
                .createBy(entity.getCreateBy())
                .updateBy(entity.getUpdateBy())
                .build();
    }

    public static List<AffectLeaveWorkDTO> toDTOs(List<AffectLeaveWork> list) {
        return list.stream().map(AffectLeaveWork::toDTO).collect(Collectors.toList());
    }

}
