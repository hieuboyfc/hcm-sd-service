package vn.ngs.nspace.hcm.sd.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.ngs.nspace.hcm.sd.entities.CareerPath;
import vn.ngs.nspace.hcm.sd.repository.CareerPathRepo;
import vn.ngs.nspace.hcm.sd.service.CareerPathService;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathDTO;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerPathServiceImpl implements CareerPathService {

    private final CareerPathRepo repo;

    @Override
    public Page<CareerPathDTO> search(Long cid, String uid, CareerPathDTO dto, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public CareerPathDTO create(Long cid, String uid, CareerPathDTO dto) {
        CareerPathDTO dtoData = new CareerPathDTO();
        CareerPath entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = CareerPath.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = CareerPath.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    public CareerPathDTO update(Long cid, String uid, CareerPathDTO dto) {
        return null;
    }

    @Override
    public void delete(Long cid, String uid, List<Long> ids) {

    }

    @Override
    public CareerPathDTO getDetail(Long cid, String uid, Long id) {
        return null;
    }

    private CareerPath validateInput(Long cid, String uid, CareerPathDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        CareerPath entity;
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
            if (entity == null) {
                throw new BusinessException("sd-career-path-not-found");
            }
            if (!dto.getCode().equals(entity.getCode())) {
                throw new BusinessException("sd-career-path-code-not-changed");
            }
        } else {
            entity = repo.findByCompanyIdAndStatusAndCode(cid, dto.getStatus(), dto.getCode()).orElse(null);
            if (entity != null) {
                throw new BusinessException("sd-career-path-exist");
            }
        }
        return entity;
    }
}
