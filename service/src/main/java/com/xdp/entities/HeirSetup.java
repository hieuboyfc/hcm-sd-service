package com.xdp.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xdp.lib.models.PersistableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import com.xdp.utils.SDUtils;

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

    // - Cài đặt danh sách gợi ý người kế nhiệm -> Bao gồm:
    // + Mức độ phù hợp
    // + Phần trăm phù hợp
    @Column(length = 300)
    @JsonProperty(value = "suggestSuccessor")
    private String suggestSuccessor;

    // - Cài đặt thứ tự hiển thị người kế nhiệm -> Bao gồm:
    // + Theo thứ hạng
    // + Mức độ sẵn sàng
    // + Theo mức độ sẵn sàng, thứ hạng
    @Column(length = 300)
    @JsonProperty(value = "displaySuccessor")
    private String displaySuccessor;

    // - Cài đặt hiển thị trên thẻ tài năng -> Bao gồm:
    // + Năng lực
    // + Đánh giá tài năng
    // + Quá trình công tác
    // + Trình độ học vấn
    // + Kết quả đánh giá
    // + Kho tài năng
    @Column(length = 1000)
    @JsonProperty(value = "displayTalentCard")
    private String displayTalentCard;

}
