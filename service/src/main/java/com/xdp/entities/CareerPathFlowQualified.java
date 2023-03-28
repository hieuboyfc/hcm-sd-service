package com.xdp.entities;

import com.xdp.lib.listener.EntityListener;
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
@EntityListeners(value = EntityListener.class)
@Table(
        name = SDUtils.CAREER_PATH_FLOW_QUALIFIED,
        indexes = {
                @Index(name = "CareerPathFlowQualified_idx", columnList = "companyId, qualifiedId"),
        }
)
public class CareerPathFlowQualified extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    private Long careerPathFlowId;

    private Long qualifiedId;

}
