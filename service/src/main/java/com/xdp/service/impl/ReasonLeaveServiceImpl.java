package com.xdp.service.impl;

import com.xdp.entities.ReasonLeave;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import com.xdp.repository.ReasonLeaveRepo;
import com.xdp.dto.ReasonLeaveDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.xdp.service.ReasonLeaveService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReasonLeaveServiceImpl implements ReasonLeaveService {

    private final ReasonLeaveRepo repo;

    @Override
    public Page<ReasonLeaveDTO> getList(Long cid, String uid, String search, Pageable pageable) {
        Page<ReasonLeave> listPages = repo.getListReasonLeave(cid, search, pageable);
        List<ReasonLeaveDTO> listDTOs = ReasonLeave.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public ReasonLeaveDTO create(Long cid, String uid, ReasonLeaveDTO dto) {
        ReasonLeaveDTO dtoData = new ReasonLeaveDTO();
        ReasonLeave entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = ReasonLeave.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = ReasonLeave.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public ReasonLeaveDTO update(Long cid, String uid, ReasonLeaveDTO dto) {
        ReasonLeaveDTO dtoData = new ReasonLeaveDTO();
        ReasonLeave entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setStatus(dto.getStatus());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = ReasonLeave.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<ReasonLeave> listData = repo.findAllByCompanyIdAndIdIn(cid, ids);
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
    public ReasonLeaveDTO getDetail(Long cid, String uid, Long id) {
        Optional<ReasonLeave> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isPresent()) {
            ReasonLeave entity = entityOptional.get();
            return ReasonLeave.toDTO(entity);
        } else {
            throw new BusinessException("sd-reason-leave-not-found");
        }
    }

    private ReasonLeave validateInput(Long cid, String uid, ReasonLeaveDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        ReasonLeave entity;
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
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
