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
import vn.ngs.nspace.hcm.sd.entities.category.SDFutureLeader;
import vn.ngs.nspace.hcm.sd.repository.SDFutureLeaderRepo;
import vn.ngs.nspace.hcm.sd.service.SDFutureLeaderService;
import vn.ngs.nspace.hcm.sd.share.dto.category.SDFutureLeaderDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SDFutureLeaderServiceImpl implements SDFutureLeaderService {

    private final SDFutureLeaderRepo repo;

    @Override
    public Page<SDFutureLeaderDTO> getList(long cid, String uid, String search, Pageable pageable) {
        Page<SDFutureLeader> listPages = repo.getListFutureLeader(
                cid, Constants.STATE_ACTIVE, search, pageable
        );
        List<SDFutureLeaderDTO> listDTOs = SDFutureLeader.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public SDFutureLeaderDTO create(Long cid, String uid, SDFutureLeaderDTO dto) {
        SDFutureLeaderDTO dtoData = new SDFutureLeaderDTO();
        SDFutureLeader entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = SDFutureLeader.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDFutureLeader.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public SDFutureLeaderDTO update(Long cid, String uid, SDFutureLeaderDTO dto) {
        SDFutureLeaderDTO dtoData = new SDFutureLeaderDTO();
        SDFutureLeader entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDFutureLeader.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<SDFutureLeader> listData = repo.findAllByCompanyIdAndStatusAndIdIn(cid, Constants.STATE_ACTIVE, ids);
        if (listData.isEmpty()) {
            throw new BusinessException("sd-future-leader-not-found");
        }
        listData.forEach(item -> {
            item.setUpdateBy(uid);
            item.setStatus(Constants.STATE_INACTIVE);
        });
        repo.saveAll(listData);
    }

    @Override
    public SDFutureLeaderDTO getDetail(Long cid, String uid, Long id) {
        Optional<SDFutureLeader> entityOptional = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id);
        if (entityOptional.isPresent()) {
            SDFutureLeader entity = entityOptional.get();
            return SDFutureLeader.toDTO(entity);
        }
        return null;
    }

    private SDFutureLeader validateInput(Long cid, String uid, SDFutureLeaderDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }
        SDFutureLeader entity = repo
                .findByCompanyIdAndStatusAndCode(cid, Constants.STATE_ACTIVE, dto.getCode())
                .orElse(null);
        if (isEdit) {
            if (entity == null) {
                throw new BusinessException("sd-future-leader-not-found");
            }
        } else {
            if (entity != null) {
                throw new BusinessException("sd-future-leader-exist");
            }
        }
        return entity;
    }
}
