package vn.ngs.nspace.hcm.sd.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.ngs.nspace.hcm.sd.entities.CareerPath;
import vn.ngs.nspace.hcm.sd.entities.CareerPathFlow;
import vn.ngs.nspace.hcm.sd.entities.CareerPathFlowEmp;
import vn.ngs.nspace.hcm.sd.entities.CareerPathFlowQualified;
import vn.ngs.nspace.hcm.sd.repository.CareerPathFlowEmpRepo;
import vn.ngs.nspace.hcm.sd.repository.CareerPathFlowQualifiedRepo;
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
@Transactional
@RequiredArgsConstructor
public class CareerPathServiceImpl implements CareerPathService {

    private static Set<Integer> totalStep;

    static {
        totalStep = new HashSet<>();
    }

    private final CareerPathRepo repo;
    private final CareerPathFlowRepo cpFlowRepo;
    private final CareerPathFlowEmpRepo cpFlowEmpRepo;
    private final CareerPathFlowQualifiedRepo cpFlowQualifiedRepo;

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
        Map<Long, List<Long>> empIds = new HashMap<>();
        Map<Long, List<Long>> qualifiedIds = new HashMap<>();
        if (!dto.getCareerPathFlowDTOS().isEmpty()) {
            Integer level = 0;
            for (CareerPathFlowDTO item : dto.getCareerPathFlowDTOS()) {
                List<String> duplicateCodes = new ArrayList<>();
                getDataFromChildren(cid, uid, 0L, entity.getId(), SDUtils.Symbol.EMPTY, SDUtils.Symbol.EMPTY,
                        level, item, duplicateCodes, item.getChildren(), careerPathFlows, empIds, qualifiedIds, "create");
            }
        }
        for (CareerPathFlow item : careerPathFlows) {
            if (item.getCareerPathId() == null) {
                throw new BusinessException("sd-career-path-in-flow-not-found");
            }
        }
        cpFlowRepo.saveAll(careerPathFlows);
        createFlowEmpAndQualified(cid, uid, empIds, qualifiedIds, "create");
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        getDataDetail(careerPathFlowDTOS, dtoData, "create");
        return dtoData;
    }

    @Override
    @Transactional
    public CareerPathDTO update(Long cid, String uid, CareerPathDTO dto) {
        CareerPathDTO dtoData = new CareerPathDTO();
        CareerPath entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setOrgId(dto.getOrgId());
            entity.setDescription(dto.getDescription());
            entity.setStatus(dto.getStatus());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = CareerPath.toDTO(entity);
            dto.setId(dtoData.getId());
        }
        List<CareerPathFlow> careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathIdOrderByLevelDesc(cid, dto.getId());
        deleteCareerPathFlow(cid, dto, careerPathFlows);
        updateCareerPathFlow(cid, uid, dto, careerPathFlows);
        CareerPathDTO careerPathFlowDTO = getDetail(cid, uid, dto.getId());
        dtoData.setCareerPathFlowDTOS(careerPathFlowDTO.getCareerPathFlowDTOS());
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<CareerPath> careerPaths = repo.findAllByCompanyIdAndIdIn(cid, ids);
        if (careerPaths.isEmpty()) {
            throw new BusinessException("sd-career-path-not-found");
        }
        careerPaths.forEach(item -> {
            item.setUpdateBy(uid);
            item.setStatus(Constants.STATE_INACTIVE);
        });
        repo.saveAll(careerPaths);

        List<Long> flowIds = new ArrayList<>();
        List<CareerPathFlow> careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathIdIn(cid, ids);
        if (!careerPathFlows.isEmpty()) {
            careerPathFlows.forEach(item -> {
                item.setUpdateBy(uid);
                item.setStatus(Constants.STATE_INACTIVE);
            });
            cpFlowRepo.saveAll(careerPathFlows);
            flowIds = careerPathFlows.stream().map(CareerPathFlow::getId).collect(Collectors.toList());
        }

        List<CareerPathFlowEmp> careerPathFlowEmps = cpFlowEmpRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, flowIds);
        if (!careerPathFlowEmps.isEmpty()) {
            careerPathFlowEmps.forEach(item -> {
                item.setUpdateBy(uid);
                item.setStatus(Constants.STATE_INACTIVE);
            });
            cpFlowEmpRepo.saveAll(careerPathFlowEmps);
        }

        List<CareerPathFlowQualified> careerPathFlowQualifieds = cpFlowQualifiedRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, flowIds);
        if (!careerPathFlowQualifieds.isEmpty()) {
            careerPathFlowQualifieds.forEach(item -> {
                item.setUpdateBy(uid);
                item.setStatus(Constants.STATE_INACTIVE);
            });
            cpFlowQualifiedRepo.saveAll(careerPathFlowQualifieds);
        }
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
        if (!listDTOs.isEmpty()) {
            dto.setExpanded(true);
        }
        dto.setChildren(listDTOs);
        return dto;
    }

    @Transactional
    void getDataFromChildren(Long cid, String uid, Long parentId, Long careerPathId, String path,
                             String pathName, Integer level, CareerPathFlowDTO dto, List<String> duplicateCodes,
                             List<CareerPathFlowDTO> dtos, List<CareerPathFlow> careerPathFlows,
                             Map<Long, List<Long>> empIds, Map<Long, List<Long>> qualifiedIds, String type) {
        if (dto.getCode().length() > SDUtils.Number._8) {
            throw new BusinessException("5000", "sd-career-path-flow-code-exceed-the-character",
                    dto.getCode(), dto.getName(), SDUtils.Number._8);
        }
        if (duplicateCodes.contains(dto.getCode())) {
            throw new BusinessException("sd-career-path-flow-position-exist");
        }
        duplicateCodes.add(dto.getCode());

        level += 1;
        CareerPathFlow entity = CareerPathFlow.of(cid, dto);
        entity.setCareerPathId(careerPathId);
        entity.setLevel(level);
        entity.setParentId(parentId);
        entity.setCreateBy(uid);
        entity.setUpdateBy(uid);
        cpFlowRepo.save(entity);

        if (!dto.getEmpIds().isEmpty()) {
            empIds.put(entity.getId(), dto.getEmpIds());
        }

        if (!dto.getQualifiedIds().isEmpty()) {
            qualifiedIds.put(entity.getId(), dto.getQualifiedIds());
        }

        StringBuilder sbPath = new StringBuilder();
        String textPath;
        if (type.equalsIgnoreCase("update") && !SDUtils.Symbol.EMPTY.equals(path)) {
            sbPath.append(path).append(SDUtils.Symbol.DOT).append(entity.getId());
            textPath = sbPath.toString();
        } else {
            sbPath.append(path).append(entity.getId()).append(SDUtils.Symbol.DOT);
            textPath = sbPath.substring(SDUtils.Number._0, sbPath.length() - SDUtils.Number._1);
        }
        entity.setPath(textPath);

        StringBuilder sbPathName = new StringBuilder();
        String textPathName;
        if (type.equalsIgnoreCase("update") && !SDUtils.Symbol.EMPTY.equals(pathName)) {
            sbPathName.append(pathName).append(SDUtils.Symbol.SLASH).append(entity.getName());
            textPathName = sbPathName.toString();
        } else {
            sbPathName.append(pathName).append(entity.getName()).append(SDUtils.Symbol.SLASH);
            textPathName = sbPathName.substring(SDUtils.Number._0, sbPathName.length() - SDUtils.Number._1);
        }
        entity.setPathName(textPathName);

        for (CareerPathFlowDTO item : dtos) {
            getDataFromChildren(
                    cid, uid, entity.getId(), careerPathId, sbPath.toString(), sbPathName.toString(), level,
                    item, duplicateCodes, item.getChildren(), careerPathFlows, empIds, qualifiedIds, type
            );
        }
        careerPathFlows.add(entity);
    }

    @Transactional
    void deleteCareerPathFlow(Long cid, CareerPathDTO dto, List<CareerPathFlow> careerPathFlows) {
        if (!dto.getDeleteCareerPathFlowDTOs().isEmpty()) {
            List<CareerPathFlow> deleteItems = new ArrayList<>();
            try {
                List<Long> ids = dto.getDeleteCareerPathFlowDTOs()
                        .stream()
                        .map(o -> Long.valueOf(o.get("id").toString()))
                        .collect(Collectors.toList());
                List<Long> parentIds = dto.getDeleteCareerPathFlowDTOs()
                        .stream()
                        .map(o -> Long.valueOf(o.get("parentId").toString()))
                        .collect(Collectors.toList());
                List<Long> parents = new ArrayList<>();
                for (CareerPathFlow item : careerPathFlows) {
                    if (ids.contains(item.getId()) && parentIds.contains(item.getParentId())) {
                        parents.add(item.getId());
                        deleteItems.add(item);
                        continue;
                    }
                    if (!parents.isEmpty() && parents.contains(item.getParentId())) {
                        deleteItems.add(item);
                    }
                }

                if (!deleteItems.isEmpty()) {
                    cpFlowRepo.deleteAll(deleteItems);
                }

                List<CareerPathFlowEmp> careerPathFlowEmps = cpFlowEmpRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, ids);
                if (!careerPathFlowEmps.isEmpty()) {
                    cpFlowEmpRepo.deleteAll(careerPathFlowEmps);
                }

                List<CareerPathFlowQualified> careerPathFlowQualifieds = cpFlowQualifiedRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, ids);
                if (!careerPathFlowQualifieds.isEmpty()) {
                    cpFlowQualifiedRepo.deleteAll(careerPathFlowQualifieds);
                }

            } catch (Exception e) {
                System.out.println("Error -> Class: CareerPathServiceImpl.class | Method: deleteCareerPathFlow()");
                e.printStackTrace();
                throw new BusinessException("sd-career-path-flow-delete");
            }
        }
    }

    @Transactional
    void updateCareerPathFlow(Long cid, String uid, CareerPathDTO dto, List<CareerPathFlow> listData) {
        if (!dto.getUpdateCareerPathFlowDTOS().isEmpty()) {
            List<CareerPathFlow> careerPathFlows = new ArrayList<>();
            Map<Long, List<Long>> empIds = new HashMap<>();
            Map<Long, List<Long>> qualifiedIds = new HashMap<>();
            Integer level = 0;
            String path = "";
            String pathName = "";
            for (CareerPathFlowDTO item : dto.getUpdateCareerPathFlowDTOS()) {
                if (item.getParentId() == null) {
                    throw new BusinessException("sd-career-path-flow-parent-id-not-found");
                }

                CareerPathFlow parent = listData
                        .stream()
                        .filter(o -> item.getParentId() != 0 && o.getId() != null && o.getId().equals(item.getParentId()))
                        .findFirst()
                        .orElse(null);
                if (parent != null) {
                    level = parent.getLevel();
                    path = parent.getPath();
                    pathName = parent.getPathName();
                }

                List<String> duplicateCodes = new ArrayList<>();
                List<Long> parentIds = new ArrayList<>();
                Long idFirst = item.getParentId();
                for (CareerPathFlow o : listData) {
                    if (idFirst != null && idFirst.equals(o.getId())) {
                        idFirst = null;
                        parentIds.add(o.getParentId());
                        duplicateCodes.add(o.getCode());
                        continue;
                    }
                    if (parentIds.contains(0L)) {
                        continue;
                    }
                    if (parentIds.contains(o.getId()) || parentIds.contains(o.getParentId())) {
                        parentIds.add(o.getParentId());
                        duplicateCodes.add(o.getCode());
                    }
                }

                getDataFromChildren(cid, uid, item.getParentId(), dto.getId(), path, pathName,
                        level, item, duplicateCodes, item.getChildren(), careerPathFlows, empIds, qualifiedIds, "update");
            }
            if (!careerPathFlows.isEmpty()) {
                cpFlowRepo.saveAll(careerPathFlows);
                createFlowEmpAndQualified(cid, uid, empIds, qualifiedIds, "update");
            }
        }
    }

    @Transactional
    void createFlowEmpAndQualified(Long cid, String uid, Map<Long, List<Long>> empIds,
                                   Map<Long, List<Long>> qualifiedIds, String type) {
        if (!empIds.isEmpty()) {
            List<CareerPathFlowEmp> listEntities = new ArrayList<>();
            empIds.forEach((key, value) -> {
                value.forEach(id -> {
                    CareerPathFlowEmp flowEmp = new CareerPathFlowEmp();
                    flowEmp.setCareerPathFlowId(key);
                    flowEmp.setEmpId(id);
                    flowEmp.setStatus(Constants.STATE_ACTIVE);
                    flowEmp.setCompanyId(cid);
                    flowEmp.setCreateBy(uid);
                    flowEmp.setUpdateBy(uid);
                    listEntities.add(flowEmp);
                });
            });
            cpFlowEmpRepo.saveAll(listEntities);
        }
        if (!qualifiedIds.isEmpty()) {
            List<CareerPathFlowQualified> listEntities = new ArrayList<>();
            qualifiedIds.forEach((key, value) -> {
                value.forEach(id -> {
                    CareerPathFlowQualified flowQualified = new CareerPathFlowQualified();
                    flowQualified.setCareerPathFlowId(key);
                    flowQualified.setQualifiedId(id);
                    flowQualified.setStatus(Constants.STATE_ACTIVE);
                    flowQualified.setCompanyId(cid);
                    flowQualified.setCreateBy(uid);
                    flowQualified.setUpdateBy(uid);
                    listEntities.add(flowQualified);
                });
            });
            cpFlowQualifiedRepo.saveAll(listEntities);
        }
    }

    private CareerPath validateInput(Long cid, String uid, CareerPathDTO dto, Boolean isEdit, String type) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException("required-field-null");
        }

        CareerPath entity = null;
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
            List<CareerPath> list = repo.findAllByCompanyIdAndCode(cid, dto.getCode());
            if (list.size() > SDUtils.Number._0) {
                throw new BusinessException("sd-career-path-exist");
            }
        }
        return entity;
    }

}
