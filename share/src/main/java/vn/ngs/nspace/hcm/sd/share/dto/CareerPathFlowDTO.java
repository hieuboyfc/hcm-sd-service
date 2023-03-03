package vn.ngs.nspace.hcm.sd.share.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CareerPathFlowDTO {

    private Long id;
    private Long careerPathId;
    private Long positionId;
    private Long parentId;
    private String path;
    private String pathName;
    private boolean tick;
    private String description;

}
