package vn.ngs.nspace.hcm.sd.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.sd.entities.CareerPathSetup;
import vn.ngs.nspace.hcm.sd.repository.CareerPathSetupRepo;
import vn.ngs.nspace.hcm.sd.service.CareerPathSetupService;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathSetupDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CareerPathSetupServiceImpl implements CareerPathSetupService {

    private final CareerPathSetupRepo repo;

    @Override
    public Page<CareerPathSetupDTO> getList(Long cid, String uid, String search, Pageable pageable) {
        Page<CareerPathSetup> listPages = repo.getListCareerPathSetup(cid, search, pageable);
        List<CareerPathSetupDTO> listDTOs = CareerPathSetup.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    public CareerPathSetupDTO update(Long cid, String uid, CareerPathSetupDTO dto) {
        CareerPathSetup entity = repo.findByCompanyIdAndId(cid, dto.getId()).orElse(null);
        if (entity == null) {
            throw new BusinessException("sd-career-path-setup-not-found");
        }
        if (!dto.getCode().equals(entity.getCode())) {
            throw new BusinessException("sd-career-path-setup-code-not-changed");
        }
        entity.setIcon(dto.getIcon());
        entity.setColor(dto.getColor());
        entity.setStatus(dto.getStatus());
        entity.setUpdateBy(uid);
        repo.save(entity);
        return CareerPathSetup.toDTO(entity);
    }

    @Override
    public CareerPathSetupDTO getDetail(Long cid, String uid, Long id) {
        Optional<CareerPathSetup> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isPresent()) {
            CareerPathSetup entity = entityOptional.get();
            return CareerPathSetup.toDTO(entity);
        }
        return null;
    }
}
