package vn.ngs.nspace.hcm.sd.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.ngs.nspace.hcm.sd.entities.RiskLeaveWork;
import vn.ngs.nspace.hcm.sd.repository.RiskLeaveWorkRepo;
import vn.ngs.nspace.hcm.sd.service.RiskLeaveWorkService;
import vn.ngs.nspace.hcm.sd.share.dto.RiskLeaveWorkDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiskLeaveWorkServiceImpl implements RiskLeaveWorkService {

    private final RiskLeaveWorkRepo repo;

    @Override
    public Page<RiskLeaveWorkDTO> getList(long cid, String uid, String search, Pageable pageable) {
        Page<RiskLeaveWork> listPages = repo.getListRiskLeaveWork(cid, search, pageable);
        List<RiskLeaveWorkDTO> listDTOs = RiskLeaveWork.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public RiskLeaveWorkDTO create(Long cid, String uid, RiskLeaveWorkDTO dto) {
        RiskLeaveWorkDTO dtoData = new RiskLeaveWorkDTO();
        RiskLeaveWork entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = RiskLeaveWork.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = RiskLeaveWork.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public RiskLeaveWorkDTO update(Long cid, String uid, RiskLeaveWorkDTO dto) {
        RiskLeaveWorkDTO dtoData = new RiskLeaveWorkDTO();
        RiskLeaveWork entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = RiskLeaveWork.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<RiskLeaveWork> listData = repo.findAllByCompanyIdAndIdIn(cid, ids);
        if (listData.isEmpty()) {
            throw new BusinessException("sd-risk-leave-work-not-found");
        }
        listData.forEach(item -> {
            item.setUpdateBy(uid);
            item.setStatus(Constants.STATE_INACTIVE);
        });
        repo.saveAll(listData);
    }

    @Override
    public RiskLeaveWorkDTO getDetail(Long cid, String uid, Long id) {
        Optional<RiskLeaveWork> entityOptional = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id);
        if (entityOptional.isPresent()) {
            RiskLeaveWork entity = entityOptional.get();
            return RiskLeaveWork.toDTO(entity);
        } else {
            throw new BusinessException("sd-risk-leave-work-not-found");
        }
    }

    private RiskLeaveWork validateInput(Long cid, String uid, RiskLeaveWorkDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        RiskLeaveWork entity;
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndStatusAndId(cid, dto.getStatus(), id).orElse(null);
            if (entity == null) {
                throw new BusinessException("sd-risk-leave-work-not-found");
            }
            if (!dto.getCode().equals(entity.getCode())) {
                throw new BusinessException("sd-risk-leave-work-code-not-changed");
            }
        } else {
            entity = repo.findByCompanyIdAndStatusAndCode(cid, dto.getStatus(), dto.getCode()).orElse(null);
            if (entity != null) {
                throw new BusinessException("sd-risk-leave-work-exist");
            }
        }
        return entity;
    }
}
