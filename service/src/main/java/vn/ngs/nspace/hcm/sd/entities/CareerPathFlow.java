package vn.ngs.nspace.hcm.sd.entities;

import com.xdp.lib.listener.EntityListener;
import com.xdp.lib.models.PersistableEntity;
import com.xdp.lib.utils.MapperUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathFlowDTO;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(value = EntityListener.class)
@Table(indexes = {
        @Index(name = "career_path_flow_idx", columnList = "companyId, path"),
        @Index(name = "career_path_flow_parent_idx", columnList = "companyId, parentId")
})
public class CareerPathFlow extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    private Long careerPathId;

    private Long positionId;

    private Long parentId;

    @Column(columnDefinition = "ltree")
    private String path;

    @Size(max = 500)
    private String pathName;

    private boolean tick;

    @Size(max = 300)
    private String description;

    public static CareerPathFlow of(CareerPathFlowDTO dto) {
        CareerPathFlow entity = new CareerPathFlow();
        MapperUtils.copyWithoutAudit(dto, entity);
        return entity;
    }

    public static CareerPathFlowDTO toDTO(CareerPathFlow entity) {
        CareerPathFlowDTO dto = new CareerPathFlowDTO();
        MapperUtils.copy(entity, dto);
        return dto;
    }

    public static List<CareerPathFlowDTO> toDTOs(List<CareerPathFlow> entities) {
        return entities.stream().map(CareerPathFlow::toDTO).collect(Collectors.toList());
    }
}
