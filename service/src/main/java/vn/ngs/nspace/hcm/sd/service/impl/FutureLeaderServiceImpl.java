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
import vn.ngs.nspace.hcm.sd.entities.FutureLeader;
import vn.ngs.nspace.hcm.sd.repository.FutureLeaderRepo;
import vn.ngs.nspace.hcm.sd.service.FutureLeaderService;
import vn.ngs.nspace.hcm.sd.share.dto.FutureLeaderDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FutureLeaderServiceImpl implements FutureLeaderService {

    private final FutureLeaderRepo repo;

    @Override
    public Page<FutureLeaderDTO> getList(long cid, String uid, String search, Pageable pageable) {
        Page<FutureLeader> listPages = repo.getListFutureLeader(cid, search, pageable);
        List<FutureLeaderDTO> listDTOs = FutureLeader.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public FutureLeaderDTO create(Long cid, String uid, FutureLeaderDTO dto) {
        FutureLeaderDTO dtoData = new FutureLeaderDTO();
        FutureLeader entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = FutureLeader.of(cid, uid, dto);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = FutureLeader.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public FutureLeaderDTO update(Long cid, String uid, FutureLeaderDTO dto) {
        FutureLeaderDTO dtoData = new FutureLeaderDTO();
        FutureLeader entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());
            entity.setColor(dto.getColor());
            entity.setStatus(dto.getStatus());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = FutureLeader.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<FutureLeader> listData = repo.findAllByCompanyIdAndIdIn(cid, ids);
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
    public FutureLeaderDTO getDetail(Long cid, String uid, Long id) {
        Optional<FutureLeader> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isPresent()) {
            FutureLeader entity = entityOptional.get();
            return FutureLeader.toDTO(entity);
        } else {
            throw new BusinessException("sd-future-leader-not-found");
        }
    }

    private FutureLeader validateInput(Long cid, String uid, FutureLeaderDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        FutureLeader entity;
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
            if (entity == null) {
                throw new BusinessException("sd-future-leader-not-found");
            }
            if (!dto.getCode().equals(entity.getCode())) {
                throw new BusinessException("sd-future-leader-code-not-changed");
            }
        } else {
            entity = repo.findByCompanyIdAndStatusAndCode(cid, dto.getStatus(), dto.getCode()).orElse(null);
            if (entity != null) {
                throw new BusinessException("sd-future-leader-exist");
            }
        }
        return entity;
    }
}
