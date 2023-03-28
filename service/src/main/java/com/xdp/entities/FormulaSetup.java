package com.xdp.entities;

import com.xdp.lib.models.PersistableEntity;
import com.xdp.dto.FormulaSetupDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import com.xdp.utils.DateTimeUtils;
import com.xdp.utils.SDUtils;

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
@EqualsAndHashCode(callSuper = false)
@Table(
        name = SDUtils.FORMULA_SETUP,
        indexes = {
                @Index(name = "FormulaSettings_idx", columnList = "companyId")
        }
)
// Bảng: Thiết lập công thức
public class FormulaSetup extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 8, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 3, nullable = false)
    private String type; // 01 - Lộ trình công danh; 02 - Kế nhiệm; 03 - Tất cả

    @Column(length = 5000, nullable = false)
    private String syntax;

    @Column(length = 5000)
    private String variable;

    private Date startDate;

    private Date endDate;

    public static FormulaSetup of(Long cid, String uid, FormulaSetupDTO dto) {
        return FormulaSetup.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .type(dto.getType())
                .syntax(dto.getSyntax())
                .variable(dto.getVariable())
                .startDate(DateTimeUtils.getStartOfDate(dto.getStartDate()))
                .endDate(DateTimeUtils.getEndOfDate(dto.getEndDate()))
                .status(dto.getStatus())
                .companyId(cid)
                .updateBy(uid)
                .createBy(uid)
                .build();
    }

    public static FormulaSetupDTO toDTO(FormulaSetup entity) {
        return FormulaSetupDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .type(entity.getType())
                .syntax(entity.getSyntax())
                .variable(entity.getVariable())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createDate(entity.getCreateDate())
                .modifiedDate(entity.getModifiedDate())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .companyId(entity.getCompanyId())
                .createBy(entity.getCreateBy())
                .updateBy(entity.getUpdateBy())
                .build();
    }

    public static List<FormulaSetupDTO> toDTOs(List<FormulaSetup> list) {
        return list.stream().map(FormulaSetup::toDTO).collect(Collectors.toList());
    }

}
