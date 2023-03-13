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
        name = SDUtils.FUNC_VARIABLE,
        indexes = {
                @Index(name = "FuncVariable_idx", columnList = "companyId")
        }
)
// Bảng: Thiết lập tham số Hàm, Biến cho Bảng công thức
public class FuncVariable extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String code;

    private Integer type; // 1 - Hàm, 2 - Biến

    @Column(length = 200, nullable = false)
    private String name;

}
