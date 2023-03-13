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
        name = SDUtils.HEIR_SETUP,
        indexes = {
                @Index(name = "HeirSetup_idx", columnList = "companyId")
        }
)
// Bảng: Thiết lập kế nhiệm
public class HeirSetup extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    private Boolean percentLevel;

    private Double percentLevelFrom; // Mức độ phần trăm phù hơn từ

    private Double percentLevelTo; // Mức độ phần trăm phù hơn đến

    private Boolean qualification; // Năng lực

    private Boolean talentReview; // Đánh giá tài năng

    private Boolean workProcess; // Quá trình công tác

    private Boolean literacy; // Trình độ học vấn

    private Boolean resultReview; // Kết quả đánh giá

    private Boolean warehouseTalent; // Kho tài năng

    private Boolean sortByRank; // Theo thứ hạng, mức độ sẵn sàng

    private Boolean sortByDegree; // Theo mức độ sẵn sàng, thứ hạng

}
