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
import vn.ngs.nspace.hcm.sd.entities.category.SDRiskLeaveWork;
import vn.ngs.nspace.hcm.sd.repository.SDRiskLeaveWorkRepo;
import vn.ngs.nspace.hcm.sd.service.SDRiskLeaveWorkService;
import vn.ngs.nspace.hcm.sd.share.dto.category.SDRiskLeaveWorkDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SDRiskLeaveWorkServiceImpl implements SDRiskLeaveWorkService {

    private final SDRiskLeaveWorkRepo repo;

    @Override
    public Page<SDRiskLeaveWorkDTO> getList(long cid, String uid, String search, Pageable pageable) {
        Page<SDRiskLeaveWork> listPages = repo.getListRiskLeaveWork(
                cid, Constants.STATE_ACTIVE, search, pageable
        );
        List<SDRiskLeaveWorkDTO> listDTOs = SDRiskLeaveWork.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public SDRiskLeaveWorkDTO create(Long cid, String uid, SDRiskLeaveWorkDTO dto) {
        SDRiskLeaveWorkDTO dtoData = new SDRiskLeaveWorkDTO();
        SDRiskLeaveWork entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = SDRiskLeaveWork.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDRiskLeaveWork.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public SDRiskLeaveWorkDTO update(Long cid, String uid, SDRiskLeaveWorkDTO dto) {
        SDRiskLeaveWorkDTO dtoData = new SDRiskLeaveWorkDTO();
        SDRiskLeaveWork entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDRiskLeaveWork.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<SDRiskLeaveWork> listData = repo.findAllByCompanyIdAndStatusAndIdIn(cid, Constants.STATE_ACTIVE, ids);
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
    public SDRiskLeaveWorkDTO getDetail(Long cid, String uid, Long id) {
        Optional<SDRiskLeaveWork> entityOptional = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id);
        if (entityOptional.isPresent()) {
            SDRiskLeaveWork entity = entityOptional.get();
            return SDRiskLeaveWork.toDTO(entity);
        }
        return null;
    }

    private SDRiskLeaveWork validateInput(Long cid, String uid, SDRiskLeaveWorkDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }
        SDRiskLeaveWork entity = repo
                .findByCompanyIdAndStatusAndCode(cid, Constants.STATE_ACTIVE, dto.getCode())
                .orElse(null);
        if (isEdit) {
            if (entity == null) {
                throw new BusinessException("sd-risk-leave-work-not-found");
            }
        } else {
            if (entity != null) {
                throw new BusinessException("sd-risk-leave-work-exist");
            }
        }
        return entity;
    }
}
