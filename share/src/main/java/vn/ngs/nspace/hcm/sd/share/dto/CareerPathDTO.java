package vn.ngs.nspace.hcm.sd.share.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CareerPathDTO {

    private Long id;
    private Long code;
    private Date name;
    private Date orgId;
    private String description;

}
