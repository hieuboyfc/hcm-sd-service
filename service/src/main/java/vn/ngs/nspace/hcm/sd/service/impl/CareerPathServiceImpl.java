package vn.ngs.nspace.hcm.sd.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.ngs.nspace.hcm.sd.entities.CareerPath;
import vn.ngs.nspace.hcm.sd.entities.CareerPathFlow;
import vn.ngs.nspace.hcm.sd.repository.CareerPathFlowRepo;
import vn.ngs.nspace.hcm.sd.repository.CareerPathRepo;
import vn.ngs.nspace.hcm.sd.service.CareerPathService;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathDTO;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathFlowDTO;
import vn.ngs.nspace.hcm.sd.utils.DateTimeUtils;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerPathServiceImpl implements CareerPathService {

    private static Set<Integer> totalStep = new HashSet<>();

    private final CareerPathRepo repo;
    private final CareerPathFlowRepo cpFlowRepo;

    @Override
    public Page<CareerPathDTO> search(Long cid, String uid, CareerPathDTO dto, Pageable pageable) {
        String search;
        if (dto.getOrgId() == null) {
            dto.setOrgId(-1L);
        }
        if (dto.getStartDate() == null) {
            dto.setStartDate(DateTimeUtils.minDate());
        }
        if (dto.getEndDate() == null) {
            dto.setEndDate(DateTimeUtils.maxDate());
        }
        if (dto.getStatus() == null) {
            dto.setStatus(-1);
        }
        if (dto.getSearch() != null) {
            search = "%" + dto.getSearch() + "%";
        } else {
            search = "%%";
        }
        Page<CareerPath> listPages = repo.search(
                cid, dto.getStatus(), dto.getOrgId(), dto.getStartDate(), dto.getEndDate(), search, pageable
        );

        List<CareerPathDTO> listDTOs = CareerPath.toDTOs(listPages.getContent());
        List<Long> ids = listDTOs.stream().map(CareerPathDTO::getId).collect(Collectors.toList());
        List<CareerPathFlow> careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathIdIn(cid, ids);
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        if (!careerPathFlowDTOS.isEmpty()) {
            listDTOs.forEach(item -> {
                List<CareerPathFlowDTO> dtos = careerPathFlowDTOS
                        .stream()
                        .filter(o -> o.getCareerPathId().equals(item.getId()))
                        .collect(Collectors.toList());
                if (dtos.isEmpty()) return;
                getDataDetail(careerPathFlowDTOS, item, "search");
            });
        }
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public CareerPathDTO create(Long cid, String uid, CareerPathDTO dto) {
        CareerPathDTO dtoData = new CareerPathDTO();
        CareerPath entity = validateInput(cid, uid, dto, false, "create");
        if (entity == null) {
            entity = CareerPath.of(cid, dto);
            entity.setCompanyId(cid);
            entity.setCreateBy(uid);
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = CareerPath.toDTO(entity);
        }
        List<CareerPathFlow> careerPathFlows = new ArrayList<>();
        if (!dto.getCareerPathFlowDTOS().isEmpty()) {
            Integer level = 0;
            for (CareerPathFlowDTO item : dto.getCareerPathFlowDTOS()) {
                List<String> listCode = new ArrayList<>();
                getDataFromChildren(cid, uid, 0L, entity.getId(), SDUtils.Symbol.EMPTY, SDUtils.Symbol.EMPTY,
                        level, item, listCode, item.getChildren(), careerPathFlows);
            }
        }
        for (CareerPathFlow item : careerPathFlows) {
            if (item.getCareerPathId() == null) {
                throw new BusinessException("sd-career-path-in-flow-not-found");
            }
        }
        cpFlowRepo.saveAll(careerPathFlows);
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        getDataDetail(careerPathFlowDTOS, dtoData, "create");
        return dtoData;
    }

    private void getDataFromChildren(Long cid, String uid, Long parentId, Long careerPathId, String path,
                                     String pathName, Integer level, CareerPathFlowDTO dto, List<String> listCode,
                                     List<CareerPathFlowDTO> dtos, List<CareerPathFlow> careerPathFlows) {
        if (dto.getCode().length() > SDUtils.Number._8) {
            throw new BusinessException("5000", "sd-career-path-flow-code-exceed-the-character",
                    dto.getCode(), dto.getName(), SDUtils.Number._8);
        }
        if (listCode.contains(dto.getCode())) {
            throw new BusinessException("sd-career-path-flow-position-exist");
        }
        listCode.add(dto.getCode());

        level += 1;
        CareerPathFlow entity = CareerPathFlow.of(cid, dto);
        entity.setCareerPathId(careerPathId);
        entity.setLevel(level);
        entity.setParentId(parentId);
        entity.setCreateBy(uid);
        entity.setUpdateBy(uid);
        cpFlowRepo.save(entity);

        StringBuilder sbPath = new StringBuilder();
        sbPath.append(path).append(entity.getId()).append(SDUtils.Symbol.DOT);
        String textPath = sbPath.substring(SDUtils.Number._0, sbPath.length() - SDUtils.Number._1);
        entity.setPath(textPath);

        StringBuilder sbPathName = new StringBuilder();
        sbPathName.append(pathName).append(entity.getName()).append(SDUtils.Symbol.SLASH);
        String textPathName = sbPathName.substring(SDUtils.Number._0, sbPathName.length() - SDUtils.Number._1);
        entity.setPathName(textPathName);

        for (CareerPathFlowDTO item : dtos) {
            getDataFromChildren(
                    cid, uid, entity.getId(), careerPathId, sbPath.toString(), sbPathName.toString(), level,
                    item, listCode, item.getChildren(), careerPathFlows
            );
        }
        careerPathFlows.add(entity);
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
        Optional<CareerPath> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isEmpty()) {
            throw new BusinessException("sd-career-path-not-found");
        }
        CareerPath careerPath = entityOptional.get();
        CareerPathDTO careerPathDTO = CareerPath.toDTO(careerPath);

        List<CareerPathFlow> careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathId(cid, careerPathDTO.getId());
        if (careerPathFlows.isEmpty()) {
            throw new BusinessException("sd-career-path-flow-not-found");
        }
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        getDataDetail(careerPathFlowDTOS, careerPathDTO, "getDetail");
        return careerPathDTO;
    }

    private void getDataDetail(List<CareerPathFlowDTO> careerPathFlowDTOS, CareerPathDTO careerPathDTO, String type) {
        Map<Long, List<CareerPathFlowDTO>> mapTemp = careerPathFlowDTOS.stream()
                .collect(Collectors.groupingBy(e -> Optional.ofNullable(e.getParentId()).orElse(0L)));
        Integer level = 0;
        CareerPathFlowDTO childDataDTO = getDataChildren(0L, new CareerPathFlowDTO(), mapTemp, level);
        careerPathDTO.setCareerPathFlowDTOS(childDataDTO.getChildren());
        careerPathDTO.setTotalStep(totalStep.size());
        totalStep = new HashSet<>();
    }

    private CareerPathFlowDTO getDataChildren(Long id, CareerPathFlowDTO dto,
                                              Map<Long, List<CareerPathFlowDTO>> mapTemp,
                                              Integer level) {
        List<CareerPathFlowDTO> listDTOs = new ArrayList<>();
        if (id != 0) {
            level += 1;
            dto.setLevel(level);
            totalStep.add(level);
        }
        if (mapTemp.get(id) != null) {
            listDTOs = new ArrayList<>();
            for (CareerPathFlowDTO item : mapTemp.get(id)) {
                listDTOs.add(getDataChildren(item.getId(), item, mapTemp, level));
            }
        }
        dto.setChildren(listDTOs);
        return dto;
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
