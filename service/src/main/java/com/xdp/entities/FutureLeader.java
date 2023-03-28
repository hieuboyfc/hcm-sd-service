package com.xdp.entities;

import com.xdp.lib.models.PersistableEntity;
import com.xdp.dto.FutureLeaderDTO;
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
        name = SDUtils.FUTURE_LEADER,
        indexes = {
                @Index(name = "FutureLeader_idx", columnList = "companyId")
        }
)
// Bảng: Lãnh đạo tương lai
public class FutureLeader extends PersistableEntity<Long> {

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

    public static FutureLeader of(Long cid, String uid, FutureLeaderDTO dto) {
        return FutureLeader.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .color(dto.getColor())
                .companyId(cid)
                .status(dto.getStatus())
                .build();
    }

    public static FutureLeaderDTO toDTO(FutureLeader entity) {
        return FutureLeaderDTO.builder()
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

    public static List<FutureLeaderDTO> toDTOs(List<FutureLeader> list) {
        return list.stream().map(FutureLeader::toDTO).collect(Collectors.toList());
    }

}
