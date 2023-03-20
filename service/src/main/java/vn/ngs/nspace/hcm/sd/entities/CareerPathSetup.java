package vn.ngs.nspace.hcm.sd.entities;

import com.xdp.lib.models.PersistableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathSetupDTO;
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
        name = SDUtils.CAREER_PATH_SETUP,
        indexes = {
                @Index(name = "CareerPathSetup_idx", columnList = "companyId")
        }
)
// Bảng: Thiết lập lộ trình công danh
public class CareerPathSetup extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 8, nullable = false)
    private String code; // Mã thông tin thẻ vị trí công việc

    @Column(nullable = false)
    private Integer type; // 1 - Có biểu tượng và màu; 2 - Không có biểu tượng

    @Column(length = 50, nullable = false)
    private String name; // Tên thông tin thẻ vị trí công việc

    @Column(length = 20)
    private String icon;

    @Column(length = 20)
    private String color;

    public static CareerPathSetupDTO toDTO(CareerPathSetup entity) {
        return CareerPathSetupDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .type(entity.getType())
                .name(entity.getName())
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

    public static List<CareerPathSetupDTO> toDTOs(List<CareerPathSetup> list) {
        return list.stream().map(CareerPathSetup::toDTO).collect(Collectors.toList());
    }
}
