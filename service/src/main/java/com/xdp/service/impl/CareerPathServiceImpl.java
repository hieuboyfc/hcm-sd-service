package com.xdp.service.impl;

import com.xdp.dto.EmployeeDTO;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import com.xdp.service.CareerPathService;
import com.xdp.service.ExecuteHcmService;
import com.xdp.dto.CareerPathDTO;
import com.xdp.dto.CareerPathFlowDTO;
import com.xdp.dto.PositionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.xdp.entities.CareerPath;
import com.xdp.entities.CareerPathFlow;
import com.xdp.entities.CareerPathFlowEmp;
import com.xdp.entities.CareerPathFlowQualified;
import com.xdp.repository.CareerPathFlowEmpRepo;
import com.xdp.repository.CareerPathFlowQualifiedRepo;
import com.xdp.repository.CareerPathFlowRepo;
import com.xdp.repository.CareerPathRepo;
import com.xdp.utils.DateTimeUtils;
import com.xdp.utils.SDUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CareerPathServiceImpl implements CareerPathService {

    private final CareerPathRepo repo;
    private final CareerPathFlowRepo cpFlowRepo;
    private final CareerPathFlowEmpRepo cpFlowEmpRepo;
    private final CareerPathFlowQualifiedRepo cpFlowQualifiedRepo;

    private final ExecuteHcmService executeHcmService;

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
            List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, careerPathFlowDTOS, "search");
            listDTOs.forEach(item -> {
                List<CareerPathFlowDTO> dtos = careerPathFlowDTOS
                        .stream()
                        .filter(o -> o.getCareerPathId().equals(item.getId()))
                        .collect(Collectors.toList());
                if (dtos.isEmpty()) return;
                getDataDetail(dtos, item, positionDTOS, "search");
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
        if (!careerPathFlows.isEmpty()) {
            validateInput(careerPathFlows, "create");
            cpFlowRepo.saveAll(careerPathFlows);
            createCareerPathFlowEmpAndQualified(cid, uid, empIds, qualifiedIds, "create");
            List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
            List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, careerPathFlowDTOS, "create");
            getDataDetail(careerPathFlowDTOS, dtoData, positionDTOS, "create");
        }
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
    @Transactional
    public CareerPathDTO refresh(Long cid, String uid, Long id) {
        List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, null, "refresh");
        CareerPathDTO careerPathDTO = validateDataDetail(cid, id);
        List<CareerPathFlow> careerPathFlows = new ArrayList<>();
        if (careerPathDTO.getId() != null) {
            careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathId(cid, careerPathDTO.getId());
        }
        if (careerPathFlows.isEmpty()) {
            throw new BusinessException("sd-career-path-flow-not-found");
        }

        int totalRecordChange = 0;
        for (CareerPathFlow item : careerPathFlows) {
            PositionDTO positionDTO = positionDTOS
                    .stream()
                    .filter(o -> o.getCode().equals(item.getCode()) && o.getId().equals(item.getPositionId()))
                    .findFirst()
                    .orElse(null);
            if (positionDTO != null) {
                if (!item.getCode().equals(positionDTO.getCode())
                        || item.getName().equals(positionDTO.getName())
                        || !item.getStatus().equals(positionDTO.getStatus())) {
                    item.setCode(positionDTO.getCode());
                    item.setName(positionDTO.getName());
                    item.setStatus(positionDTO.getStatus());
                    String pathName = item.getPathName().replace(item.getName(), positionDTO.getName());
                    item.setPathName(pathName);
                    totalRecordChange += 1;
                }
            }
        }
        if (totalRecordChange > 0) {
            cpFlowRepo.saveAll(careerPathFlows);
        }

        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        if (!careerPathFlowDTOS.isEmpty()) {
            List<Long> careerPathFlowIds = careerPathFlowDTOS
                    .stream()
                    .map(CareerPathFlowDTO::getCareerPathId)
                    .collect(Collectors.toList());
            refreshCareerPathFlowEmp(cid, uid, careerPathFlowDTOS, careerPathFlowIds, positionDTOS);
            getDataDetail(careerPathFlowDTOS, careerPathDTO, positionDTOS, "refresh");
        }
        return careerPathDTO;
    }

    @Override
    public CareerPathDTO getDetail(Long cid, String uid, Long id) {
        List<CareerPathFlow> careerPathFlows = new ArrayList<>();
        CareerPathDTO careerPathDTO = validateDataDetail(cid, id);
        if (careerPathDTO.getId() != null) {
            careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathId(cid, careerPathDTO.getId());
        }
        if (careerPathFlows.isEmpty()) {
            throw new BusinessException("sd-career-path-flow-not-found");
        }
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, careerPathFlowDTOS, "getDetail");
        getDataDetail(careerPathFlowDTOS, careerPathDTO, positionDTOS, "getDetail");
        return careerPathDTO;
    }

    private void getDataDetail(List<CareerPathFlowDTO> careerPathFlowDTOS, CareerPathDTO careerPathDTO,
                               List<PositionDTO> positionDTOS, String type) {
        Map<Long, List<CareerPathFlowDTO>> mapTemp = careerPathFlowDTOS.stream()
                .collect(Collectors.groupingBy(e -> Optional.ofNullable(e.getParentId()).orElse(0L)));
        Integer level = 0;
        Set<Integer> totalStep = new HashSet<>();
        CareerPathFlowDTO childDataDTO = getDataChildren(0L, new CareerPathFlowDTO(), mapTemp, level, totalStep, positionDTOS);
        careerPathDTO.setCareerPathFlowDTOS(childDataDTO.getChildren());
        careerPathDTO.setTotalStep(totalStep.size());
    }

    private CareerPathFlowDTO getDataChildren(Long id, CareerPathFlowDTO dto, Map<Long, List<CareerPathFlowDTO>> mapTemp,
                                              Integer level, Set<Integer> totalStep, List<PositionDTO> positionDTOS) {
        List<CareerPathFlowDTO> listDTOs = new ArrayList<>();
        if (id != SDUtils.Number._0) {
            level += 1;
            dto.setLevel(level);
            totalStep.add(level);
        }
        if (mapTemp.get(id) != null) {
            listDTOs = new ArrayList<>();
            for (CareerPathFlowDTO item : mapTemp.get(id)) {
                listDTOs.add(getDataChildren(item.getId(), item, mapTemp, level, totalStep, positionDTOS));
            }
        }
        if (!listDTOs.isEmpty()) {
            dto.setExpanded(true);
        }
        if (!positionDTOS.isEmpty() && !SDUtils.Symbol.EMPTY.equals(dto.getCode())) {
            positionDTOS.stream()
                    .filter(o -> o.getCode().equals(dto.getCode()) && o.getId().equals(dto.getPositionId()))
                    .findFirst()
                    .ifPresent(positionDTO -> {
                        dto.setEmployeeDTOS(positionDTO.getEmployeeDTOS());
                        dto.setTotalEmployee(positionDTO.getTotalEmployee());
                    });
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

        String textPath = getPathAndName(type, path, String.valueOf(entity.getId()), SDUtils.Symbol.DOT);
        entity.setPath(textPath);

        String textPathName = getPathAndName(type, pathName, entity.getName(), SDUtils.Symbol.SLASH);
        entity.setPathName(textPathName);

        for (CareerPathFlowDTO item : dtos) {
            getDataFromChildren(
                    cid, uid, entity.getId(), careerPathId, textPath, textPathName, level,
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
                validateInput(careerPathFlows, "update");
                cpFlowRepo.saveAll(careerPathFlows);
                createCareerPathFlowEmpAndQualified(cid, uid, empIds, qualifiedIds, "update");
            }
        }
    }

    @Transactional
    void createCareerPathFlowEmpAndQualified(Long cid, String uid, Map<Long, List<Long>> empIds,
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

    private void refreshCareerPathFlowEmp(Long cid, String uid, List<CareerPathFlowDTO> careerPathFlowDTOS,
                                          List<Long> careerPathFlowIds, List<PositionDTO> positionDTOS) {
        List<CareerPathFlowEmp> listEntities = new ArrayList<>();
        List<CareerPathFlowEmp> careerPathFlowEmps =
                cpFlowEmpRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, careerPathFlowIds);
        List<Long> flowEmpIds = careerPathFlowEmps.stream().map(CareerPathFlowEmp::getEmpId).collect(Collectors.toList());
        careerPathFlowDTOS.forEach(item -> {
            List<EmployeeDTO> employeeDTOS = new ArrayList<>();
            PositionDTO positionDTO = positionDTOS
                    .stream()
                    .filter(o -> o.getCode().equals(item.getCode()) && o.getId().equals(item.getPositionId()))
                    .findFirst()
                    .orElse(null);
            if (positionDTO != null && !positionDTO.getEmployeeDTOS().isEmpty()) {
                employeeDTOS = positionDTO.getEmployeeDTOS();
            }
            if (!employeeDTOS.isEmpty()) {
                employeeDTOS.forEach(employeeDTO -> {
                    if (!flowEmpIds.contains(employeeDTO.getId())) {
                        CareerPathFlowEmp flowEmp = new CareerPathFlowEmp();
                        flowEmp.setCareerPathFlowId(item.getId());
                        flowEmp.setEmpId(employeeDTO.getId());
                        flowEmp.setStatus(Constants.STATE_ACTIVE);
                        flowEmp.setCompanyId(cid);
                        flowEmp.setCreateBy(uid);
                        flowEmp.setUpdateBy(uid);
                        listEntities.add(flowEmp);
                    }
                });
            }
        });
        if (!listEntities.isEmpty()) {
            cpFlowEmpRepo.saveAll(listEntities);
        }
    }

    private String getPathAndName(String type, String pathAndName, String value, String symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        String textPathAndName;
        if (type.equalsIgnoreCase(SDUtils.NameMethod.UPDATE) && !SDUtils.Symbol.EMPTY.equals(pathAndName)) {
            stringBuilder.append(pathAndName).append(symbol).append(value);
            textPathAndName = stringBuilder.toString();
        } else {
            if (SDUtils.Symbol.EMPTY.equals(pathAndName)) {
                stringBuilder.append(pathAndName).append(value).append(symbol);
                textPathAndName = stringBuilder.substring(SDUtils.Number._0, stringBuilder.length() - SDUtils.Number._1);
            } else {
                stringBuilder.append(pathAndName).append(symbol).append(value);
                textPathAndName = stringBuilder.toString();
            }
        }
        return textPathAndName;
    }

    private List<PositionDTO> getPositionDTOs(Long cid, String uid, List<CareerPathFlowDTO> careerPathFlowDTOS, String type) {
        Set<Long> empIds = new HashSet<>();
        if (!type.equalsIgnoreCase(SDUtils.NameMethod.REFRESH)) {
            List<Long> careerPathFlowIds = careerPathFlowDTOS.stream().map(CareerPathFlowDTO::getId).collect(Collectors.toList());
            if (!careerPathFlowIds.isEmpty()) {
                empIds = cpFlowEmpRepo.getEmpIdInCareerPathFlow(cid, careerPathFlowIds);
            }
        }
        // String[] states = {"official", "ctv", "working", "trainee", "unpaid", "quit", "maternity", "intern", "probation", "candidate"};
        List<String> states = List.of("official", "ctv", "working", "trainee", "unpaid", "quit", "maternity", "intern", "probation", "candidate");
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "employee_position");
        filter.put("empIds", empIds);
        filter.put("state", states);
        return executeHcmService.getDataPositionAndEmployee(cid, uid, filter);
    }

    private CareerPathDTO validateDataDetail(Long cid, Long id) {
        Optional<CareerPath> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isEmpty()) {
            throw new BusinessException("sd-career-path-not-found");
        }
        CareerPath careerPath = entityOptional.get();
        return CareerPath.toDTO(careerPath);
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

    private void validateInput(List<CareerPathFlow> careerPathFlows, String type) {
        for (CareerPathFlow item : careerPathFlows) {
            if (item.getCareerPathId() == null) {
                throw new BusinessException("sd-career-path-in-flow-not-found");
            }
            if (item.getPositionId() == null || SDUtils.Symbol.EMPTY.equals(item.getCode())) {
                throw new BusinessException("sd-career-path-in-flow-position-code-not-found");
            }
        }
    }

}