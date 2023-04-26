package com.xdp.module_spfood.entity;

import com.xdp.lib.models.PersistableEntity;
import com.xdp.module_spfood.dto.SPFoodConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "sp_food_config",
        indexes = {
                @Index(name = "SPFoodConfig_idx", columnList = "companyId")
        }
)
// Bảng: Cấu hình API
public class SPFoodConfig extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 8, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 20)
    private String type;

    @Column(length = 300)
    private String description;

    @Column(length = 20)
    private String method;

    @Column(length = 100)
    private String linkApi;

    public static SPFoodConfig of(Long cid, String uid, SPFoodConfigDTO dto) {
        return SPFoodConfig.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .type(dto.getType())
                .description(dto.getDescription())
                .method(dto.getMethod())
                .linkApi(dto.getLinkApi())
                .companyId(cid)
                .status(dto.getStatus())
                .build();
    }

    public static SPFoodConfigDTO toDTO(SPFoodConfig entity) {
        return SPFoodConfigDTO.builder()
                .id(entity.getId())
                .code(entity.getCode().toUpperCase())
                .name(entity.getName())
                .type(entity.getType())
                .description(entity.getDescription())
                .method(entity.getMethod())
                .linkApi(entity.getLinkApi())
                .createDate(entity.getCreateDate())
                .modifiedDate(entity.getModifiedDate())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .companyId(entity.getCompanyId())
                .createBy(entity.getCreateBy())
                .updateBy(entity.getUpdateBy())
                .build();
    }

    public static List<SPFoodConfigDTO> toDTOs(List<SPFoodConfig> list) {
        return list.stream().map(SPFoodConfig::toDTO).collect(Collectors.toList());
    }

}