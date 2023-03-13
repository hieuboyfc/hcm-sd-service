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
import vn.ngs.nspace.hcm.sd.entities.SDReasonLeave;
import vn.ngs.nspace.hcm.sd.repository.SDReasonLeaveRepo;
import vn.ngs.nspace.hcm.sd.service.SDReasonLeaveService;
import vn.ngs.nspace.hcm.sd.share.dto.SDReasonLeaveDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SDReasonLeaveServiceImpl implements SDReasonLeaveService {

    private final SDReasonLeaveRepo repo;

    @Override
    public Page<SDReasonLeaveDTO> getList(long cid, String uid, String search, Pageable pageable) {
        Page<SDReasonLeave> listPages = repo.getListReasonLeave(cid, search, pageable);
        List<SDReasonLeaveDTO> listDTOs = SDReasonLeave.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public SDReasonLeaveDTO create(Long cid, String uid, SDReasonLeaveDTO dto) {
        SDReasonLeaveDTO dtoData = new SDReasonLeaveDTO();
        SDReasonLeave entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = SDReasonLeave.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDReasonLeave.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public SDReasonLeaveDTO update(Long cid, String uid, SDReasonLeaveDTO dto) {
        SDReasonLeaveDTO dtoData = new SDReasonLeaveDTO();
        SDReasonLeave entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDReasonLeave.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<SDReasonLeave> listData = repo.findAllByCompanyIdAndIdIn(cid, ids);
        if (listData.isEmpty()) {
            throw new BusinessException("sd-reason-leave-not-found");
        }
        listData.forEach(item -> {
            item.setUpdateBy(uid);
            item.setStatus(Constants.STATE_INACTIVE);
        });
        repo.saveAll(listData);
    }

    @Override
    public SDReasonLeaveDTO getDetail(Long cid, String uid, Long id) {
        Optional<SDReasonLeave> entityOptional = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id);
        if (entityOptional.isPresent()) {
            SDReasonLeave entity = entityOptional.get();
            return SDReasonLeave.toDTO(entity);
        } else {
            throw new BusinessException("sd-reason-leave-not-found");
        }
    }

    private SDReasonLeave validateInput(Long cid, String uid, SDReasonLeaveDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        SDReasonLeave entity;
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndStatusAndId(cid, dto.getStatus(), id).orElse(null);
            if (entity == null) {
                throw new BusinessException("sd-reason-leave-not-found");
            }
            if (!dto.getCode().equals(entity.getCode())) {
                throw new BusinessException("sd-reason-leave-code-not-changed");
            }
        } else {
            entity = repo.findByCompanyIdAndStatusAndCode(cid, dto.getStatus(), dto.getCode()).orElse(null);
            if (entity != null) {
                throw new BusinessException("sd-reason-leave-exist");
            }
        }
        return entity;
    }
}
