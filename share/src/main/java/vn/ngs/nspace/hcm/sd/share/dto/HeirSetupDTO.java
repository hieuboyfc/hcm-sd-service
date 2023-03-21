package vn.ngs.nspace.hcm.sd.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HeirSetupDTO {

    private Long id;
    private Boolean percentLevel; // Mức độ phần trăm phù hợp
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
