package vn.ngs.nspace.hcm.sd.entities;

import com.xdp.lib.listener.EntityListener;
import com.xdp.lib.models.PersistableEntity;
import com.xdp.lib.utils.MapperUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathFlowDTO;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(value = EntityListener.class)
@Table(
        name = SDUtils.CAREER_PATH_FLOW,
        indexes = {
                @Index(name = "CareerPathFlow_Path_idx", columnList = "companyId, path"),
                @Index(name = "CareerPathFlow_Code_idx", columnList = "companyId, code"),
                @Index(name = "CareerPathFlow_Parent_idx", columnList = "companyId, parentId")
        }
)
public class CareerPathFlow extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 8, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;

    private Long careerPathId;

    private Long positionId;

    private Long parentId;

    private Integer level;

    @Column(columnDefinition = "ltree")
    private String path;

    @Column(length = 500)
    private String pathName;

    @Column(length = 300)
    private String description;

    public static CareerPathFlow of(Long cid, CareerPathFlowDTO dto) {
        CareerPathFlow entity = new CareerPathFlow();
        MapperUtils.copyWithoutAudit(dto, entity);
        entity.setCompanyId(cid);
        return entity;
    }

    public static CareerPathFlowDTO toDTO(CareerPathFlow entity, CareerPathFlow parent) {
        CareerPathFlowDTO dto = new CareerPathFlowDTO();
        MapperUtils.copy(entity, dto);
        CareerPathFlowDTO parentDTO = new CareerPathFlowDTO();
        if (parent != null) {
            MapperUtils.copy(parent, parentDTO);
            dto.setParent(parentDTO);
        }
        return dto;
    }

    public static List<CareerPathFlowDTO> toDTOs(List<CareerPathFlow> entities) {
        List<CareerPathFlowDTO> listDTOs = new ArrayList<>();
        entities.forEach(item -> {
            CareerPathFlow parent = entities
                    .stream()
                    .filter(o -> item.getParentId().equals(o.getId()))
                    .findFirst()
                    .orElse(null);
            CareerPathFlowDTO dto = toDTO(item, parent);
            listDTOs.add(dto);
        });
        return listDTOs;
    }
}
