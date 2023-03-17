package vn.ngs.nspace.hcm.sd.entities;

import com.xdp.lib.listener.EntityListener;
import com.xdp.lib.models.PersistableEntity;
import com.xdp.lib.utils.MapperUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathDTO;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
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
@Table(
        name = SDUtils.CAREER_PATH,
        indexes = {
                @Index(name = "CareerPath_idx", columnList = "companyId, code")
        }
)
public class CareerPath extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "com.xdp.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Column(length = 8, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;

    private Long orgId;

    @Column(length = 300)
    private String description;

    public static CareerPath of(Long cid, String uid, CareerPathDTO dto) {
        CareerPath entity = new CareerPath();
        MapperUtils.copyWithoutAudit(dto, entity);
        return entity;
    }

    public static CareerPathDTO toDTO(CareerPath entity) {
        CareerPathDTO dto = new CareerPathDTO();
        MapperUtils.copy(entity, dto);
        return dto;
    }

    public static List<CareerPathDTO> toDTOs(List<CareerPath> entities) {
        return entities.stream().map(CareerPath::toDTO).collect(Collectors.toList());
    }

}
