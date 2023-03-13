package vn.ngs.nspace.hcm.sd.entities;

import com.xdp.lib.models.PersistableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

import javax.persistence.*;

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

    @Column(length = 8)
    private String code; // Mã thông tin thẻ vị trí công việc

    private Integer type; // 1 - Có biểu tượng và màu; 2 - Không có biểu tượng

    @Column(length = 50)
    private String name; // Tên thông tin thẻ vị trí công việc

    @Column(length = 20)
    private String icon;

    @Column(length = 20)
    private String color;

}
