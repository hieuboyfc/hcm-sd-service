package vn.ngs.nspace.hcm.sd.entities;

import com.xdp.lib.models.PersistableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.share.dto.SDFormulaSetupDTO;
import vn.ngs.nspace.hcm.sd.utils.DateTimeUtils;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

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
        name = SDUtils.SD_FORMULA_SETUP,
        indexes = {@Index(name = "SDFormulaSettings_idx", columnList = "companyId")}
)
public class SDFormulaSetup extends PersistableEntity<Long> {

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

    @Column(length = 5000, nullable = false)
    private String variable;

    private Date startDate;

    private Date endDate;

    public static SDFormulaSetup of(Long cid, String uid, SDFormulaSetupDTO dto) {
        return SDFormulaSetup.builder()
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

    public static SDFormulaSetupDTO toDTO(SDFormulaSetup entity) {
        return SDFormulaSetupDTO.builder()
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

    public static List<SDFormulaSetupDTO> toDTOs(List<SDFormulaSetup> list) {
        return list.stream().map(SDFormulaSetup::toDTO).collect(Collectors.toList());
    }

}
