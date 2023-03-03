package vn.ngs.nspace.hcm.sd.entities.category;

import com.xdp.lib.models.PersistableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.share.dto.category.SDReasonLeaveDTO;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

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
        name = SDUtils.SD_REASON_LEAVE,
        indexes = {@Index(name = "SDReasonLeave_idx", columnList = "companyId")}
)
// Bảng: Lý do rời đi
public class SDReasonLeave extends PersistableEntity<Long> {

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

    public static SDReasonLeave of(Long cid, String uid, SDReasonLeaveDTO dto) {
        return SDReasonLeave.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .color(dto.getColor())
                .companyId(cid)
                .status(dto.getStatus())
                .build();
    }

    public static SDReasonLeaveDTO toDTO(SDReasonLeave entity) {
        return SDReasonLeaveDTO.builder()
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

    public static List<SDReasonLeaveDTO> toDTOs(List<SDReasonLeave> list) {
        return list.stream().map(SDReasonLeave::toDTO).collect(Collectors.toList());
    }

}
