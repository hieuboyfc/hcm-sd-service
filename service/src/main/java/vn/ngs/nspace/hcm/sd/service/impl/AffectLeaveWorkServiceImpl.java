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
import vn.ngs.nspace.hcm.sd.entities.AffectLeaveWork;
import vn.ngs.nspace.hcm.sd.repository.AffectLeaveWorkRepo;
import vn.ngs.nspace.hcm.sd.service.AffectLeaveWorkService;
import vn.ngs.nspace.hcm.sd.share.dto.AffectLeaveWorkDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AffectLeaveWorkServiceImpl implements AffectLeaveWorkService {

    private final AffectLeaveWorkRepo repo;

    @Override
    public Page<AffectLeaveWorkDTO> getList(Long cid, String uid, String search, Pageable pageable) {
        Page<AffectLeaveWork> listPages = repo.getListAffectLeaveWork(cid, search, pageable);
        List<AffectLeaveWorkDTO> listDTOs = AffectLeaveWork.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public AffectLeaveWorkDTO create(Long cid, String uid, AffectLeaveWorkDTO dto) {
        AffectLeaveWorkDTO dtoData = new AffectLeaveWorkDTO();
        AffectLeaveWork entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = AffectLeaveWork.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = AffectLeaveWork.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public AffectLeaveWorkDTO update(Long cid, String uid, AffectLeaveWorkDTO dto) {
        AffectLeaveWorkDTO dtoData = new AffectLeaveWorkDTO();
        AffectLeaveWork entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setStatus(dto.getStatus());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = AffectLeaveWork.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<AffectLeaveWork> listData = repo.findAllByCompanyIdAndIdIn(cid, ids);
        if (listData.isEmpty()) {
            throw new BusinessException("sd-affect-leave-work-not-found");
        }
        listData.forEach(item -> {
            item.setUpdateBy(uid);
            item.setStatus(Constants.STATE_INACTIVE);
        });
        repo.saveAll(listData);
    }

    @Override
    public AffectLeaveWorkDTO getDetail(Long cid, String uid, Long id) {
        Optional<AffectLeaveWork> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isPresent()) {
            AffectLeaveWork entity = entityOptional.get();
            return AffectLeaveWork.toDTO(entity);
        } else {
            throw new BusinessException("sd-affect-leave-work-not-found");
        }
    }

    private AffectLeaveWork validateInput(Long cid, String uid, AffectLeaveWorkDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        AffectLeaveWork entity;
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
            if (entity == null) {
                throw new BusinessException("sd-affect-leave-work-not-found");
            }
            if (!dto.getCode().equals(entity.getCode())) {
                throw new BusinessException("sd-affect-leave-work-code-not-changed");
            }
        } else {
            entity = repo.findByCompanyIdAndStatusAndCode(cid, dto.getStatus(), dto.getCode()).orElse(null);
            if (entity != null) {
                throw new BusinessException("sd-affect-leave-work-exist");
            }
        }
        return entity;
    }
}
