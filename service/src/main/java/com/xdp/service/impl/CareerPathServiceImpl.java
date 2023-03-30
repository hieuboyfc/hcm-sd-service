package com.xdp.service.impl;

import com.xdp.dto.CareerPathDTO;
import com.xdp.dto.CareerPathFlowDTO;
import com.xdp.dto.EmployeeDTO;
import com.xdp.dto.PositionDTO;
import com.xdp.entities.CareerPath;
import com.xdp.entities.CareerPathFlow;
import com.xdp.entities.CareerPathFlowEmp;
import com.xdp.entities.CareerPathFlowQualified;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import com.xdp.repository.CareerPathFlowEmpRepo;
import com.xdp.repository.CareerPathFlowQualifiedRepo;
import com.xdp.repository.CareerPathFlowRepo;
import com.xdp.repository.CareerPathRepo;
import com.xdp.service.CareerPathService;
import com.xdp.service.ExecuteHcmService;
import com.xdp.utils.DateTimeUtils;
import com.xdp.utils.SDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
            search = SDUtils.Symbol.PERCENT + dto.getSearch() + SDUtils.Symbol.PERCENT;
        } else {
            search = SDUtils.Symbol.PERCENT_X2;
        }
        Page<CareerPath> listPages = repo.search(
                cid, dto.getStatus(), dto.getOrgId(), dto.getStartDate(), dto.getEndDate(), search, pageable
        );

        List<CareerPathDTO> listDTOs = CareerPath.toDTOs(listPages.getContent());
        List<Long> ids = listDTOs.stream().map(CareerPathDTO::getId).collect(Collectors.toList());
        List<CareerPathFlow> careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathIdIn(cid, ids);
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        if (!careerPathFlowDTOS.isEmpty()) {
            List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, careerPathFlowDTOS, SDUtils.NameMethod.SEARCH);
            listDTOs.forEach(item -> {
                List<CareerPathFlowDTO> dtos = careerPathFlowDTOS
                        .stream()
                        .filter(o -> o.getCareerPathId().equals(item.getId()))
                        .collect(Collectors.toList());
                if (dtos.isEmpty()) return;
                getDataDetail(dtos, item, positionDTOS);
            });
        }
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public CareerPathDTO create(Long cid, String uid, CareerPathDTO dto) {
        CareerPathDTO dtoData = new CareerPathDTO();
        CareerPath entity = validateInput(cid, dto, false);
        if (entity.getId() == null) {
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
                        level, item, duplicateCodes, item.getChildren(), careerPathFlows, new ArrayList<>(),
                        empIds, qualifiedIds, SDUtils.NameMethod.CREATE);
            }
        }
        if (!careerPathFlows.isEmpty()) {
            validateInput(careerPathFlows);
            cpFlowRepo.saveAll(careerPathFlows);
            createCareerPathFlowEmpAndQualified(cid, uid, empIds, qualifiedIds);
            List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
            List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, careerPathFlowDTOS, SDUtils.NameMethod.CREATE);
            getDataDetail(careerPathFlowDTOS, dtoData, positionDTOS);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public CareerPathDTO update(Long cid, String uid, CareerPathDTO dto) {
        CareerPathDTO dtoData = new CareerPathDTO();
        CareerPath entity = validateInput(cid, dto, true);
        if (entity.getId() != null) {
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

        Map<Long, List<Long>> empIds = new HashMap<>();
        Map<Long, List<Long>> qualifiedIds = new HashMap<>();
        List<CareerPathFlow> careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathIdOrderByLevelDesc(cid, dto.getId());
        List<CareerPathFlow> updateCareerPathFlows = new ArrayList<>();
        if (!dto.getCareerPathFlowDTOS().isEmpty()) {
            Integer level = 0;
            for (CareerPathFlowDTO item : dto.getCareerPathFlowDTOS()) {
                List<String> duplicateCodes = new ArrayList<>();
                getDataFromChildren(cid, uid, 0L, dtoData.getId(), SDUtils.Symbol.EMPTY, SDUtils.Symbol.EMPTY,
                        level, item, duplicateCodes, item.getChildren(), careerPathFlows, updateCareerPathFlows,
                        empIds, qualifiedIds, SDUtils.NameMethod.UPDATE);
            }
        }
        if (!updateCareerPathFlows.isEmpty()) {
            List<CareerPathFlow> deleteCareerPathFlows = new ArrayList<>();
            List<Long> deleteIds = new ArrayList<>();
            List<Long> ids = updateCareerPathFlows.stream().map(CareerPathFlow::getId).collect(Collectors.toList());
            if (!careerPathFlows.isEmpty()) {
                for (CareerPathFlow item : careerPathFlows) {
                    if (!ids.contains(item.getId())) {
                        deleteCareerPathFlows.add(item);
                        deleteIds.add(item.getId());
                    }
                }
            }
            cpFlowRepo.deleteAll(deleteCareerPathFlows);

            List<CareerPathFlowEmp> careerPathFlowEmps = cpFlowEmpRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, deleteIds);
            cpFlowEmpRepo.deleteAll(careerPathFlowEmps);

            List<CareerPathFlowQualified> careerPathFlowQualifieds = cpFlowQualifiedRepo.findAllByCompanyIdAndCareerPathFlowIdIn(cid, deleteIds);
            cpFlowQualifiedRepo.deleteAll(careerPathFlowQualifieds);

            cpFlowRepo.saveAll(updateCareerPathFlows);
        }
        CareerPathDTO careerPathFlowDTO = getDetail(cid, uid, dto.getId());
        dtoData.setCareerPathFlowDTOS(careerPathFlowDTO.getCareerPathFlowDTOS());
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException(SDUtils.LanguageMessage.REQUIRED_FIELD);
        }
        List<CareerPath> careerPaths = repo.findAllByCompanyIdAndIdIn(cid, ids);
        if (careerPaths.isEmpty()) {
            throw new BusinessException(SDUtils.LanguageMessage.CareerPath.NOT_FOUND);
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
        List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, null, SDUtils.NameMethod.REFRESH);
        CareerPathDTO careerPathDTO = validateDataDetail(cid, id);
        List<CareerPathFlow> careerPathFlows = new ArrayList<>();
        if (careerPathDTO.getId() != null) {
            careerPathFlows = cpFlowRepo.findAllByCompanyIdAndCareerPathId(cid, careerPathDTO.getId());
        }
        if (careerPathFlows.isEmpty()) {
            throw new BusinessException(SDUtils.LanguageMessage.CareerPathFlow.NOT_FOUND);
        }

        int totalRecordChange = 0;
        for (CareerPathFlow item : careerPathFlows) {
            PositionDTO positionDTO = positionDTOS
                    .stream()
                    .filter(o -> o.getCode().equals(item.getCode()) && o.getId().equals(item.getPositionId()))
                    .findFirst()
                    .orElse(null);
            if (positionDTO != null
                    && (!item.getCode().equals(positionDTO.getCode())
                    || item.getName().equals(positionDTO.getName())
                    || !item.getStatus().equals(positionDTO.getStatus()))) {
                item.setCode(positionDTO.getCode());
                item.setName(positionDTO.getName());
                item.setStatus(positionDTO.getStatus());
                String pathName = item.getPathName().replace(item.getName(), positionDTO.getName());
                item.setPathName(pathName);
                totalRecordChange += 1;
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
            getDataDetail(careerPathFlowDTOS, careerPathDTO, positionDTOS);
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
            throw new BusinessException(SDUtils.LanguageMessage.CareerPathFlow.NOT_FOUND);
        }
        List<CareerPathFlowDTO> careerPathFlowDTOS = CareerPathFlow.toDTOs(careerPathFlows);
        List<PositionDTO> positionDTOS = getPositionDTOs(cid, uid, careerPathFlowDTOS, SDUtils.NameMethod.GET_DETAIL);
        getDataDetail(careerPathFlowDTOS, careerPathDTO, positionDTOS);
        return careerPathDTO;
    }

    private void getDataDetail(List<CareerPathFlowDTO> careerPathFlowDTOS, CareerPathDTO careerPathDTO,
                               List<PositionDTO> positionDTOS) {
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
        if (id != SDUtils.Number.N0) {
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
        /*if (!listDTOs.isEmpty()) {
            dto.setExpanded(true); // Expanded để ở đây mới đúng nhưng FE muốn chỗ nào Expanded cũng bằng True
        }*/
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
        dto.setExpanded(true);
        return dto;
    }

    @Transactional
    void getDataFromChildren(Long cid, String uid, Long parentId, Long careerPathId, String path,
                             String pathName, Integer level, CareerPathFlowDTO dto, List<String> duplicateCodes,
                             List<CareerPathFlowDTO> dtos, List<CareerPathFlow> careerPathFlows,
                             List<CareerPathFlow> updateCareerPathFlows, Map<Long, List<Long>> empIds,
                             Map<Long, List<Long>> qualifiedIds, String type) {
        if (dto.getCode().length() > SDUtils.Number.N8) {
            throw new BusinessException(SDUtils.Code.N5000, SDUtils.LanguageMessage.CareerPathFlow.CODE_EXCEED_THE_CHARACTER,
                    dto.getCode(), dto.getName(), SDUtils.Number.N8);
        }
        if (duplicateCodes.contains(dto.getCode())) {
            throw new BusinessException(SDUtils.LanguageMessage.CareerPathFlow.POSITION_EXIST);
        }
        duplicateCodes.add(dto.getCode());

        level += 1;
        CareerPathFlow entity;
        List<Long> employeeIds;
        List<Long> qualifitionIds;
        if (SDUtils.NameMethod.UPDATE.equalsIgnoreCase(type)) {
            entity = careerPathFlows
                    .stream()
                    .filter(o -> (o.getId() != null && o.getId().equals(dto.getId()))
                            && (o.getParentId() != null && o.getParentId().equals(dto.getParentId())))
                    .findFirst()
                    .orElse(new CareerPathFlow());
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setUpdateBy(uid);

            employeeIds = dto.getEmployeeDTOS().stream().map(EmployeeDTO::getId).collect(Collectors.toList());
            qualifitionIds = new ArrayList<>(); // Phần này chờ bên KNL làm xong mới lấy được dữ liệu
        } else {
            employeeIds = dto.getEmpIds();
            qualifitionIds = dto.getQualifiedIds();
            entity = CareerPathFlow.of(cid, dto);
        }
        entity.setCareerPathId(careerPathId);
        entity.setLevel(level);
        entity.setParentId(parentId);
        if (entity.getId() == null) {
            entity.setPositionId(dto.getPositionId());
            entity.setCompanyId(cid);
            entity.setCreateBy(uid);
        } else {
            entity.setUpdateBy(uid);
        }
        cpFlowRepo.save(entity);

        empIds.put(entity.getId(), employeeIds);
        qualifiedIds.put(entity.getId(), qualifitionIds);

        String textPath = getPathAndName(type, path, String.valueOf(entity.getId()), SDUtils.Symbol.DOT);
        entity.setPath(textPath);

        String textPathName = getPathAndName(type, pathName, entity.getName(), SDUtils.Symbol.SLASH);
        entity.setPathName(textPathName);

        for (CareerPathFlowDTO item : dtos) {
            getDataFromChildren(
                    cid, uid, entity.getId(), careerPathId, textPath, textPathName, level, item, duplicateCodes,
                    item.getChildren(), careerPathFlows, updateCareerPathFlows, empIds, qualifiedIds, type
            );
        }
        if (SDUtils.NameMethod.UPDATE.equalsIgnoreCase(type)) {
            updateCareerPathFlows.add(entity);
        } else {
            careerPathFlows.add(entity);
        }
    }

    @Transactional
    void createCareerPathFlowEmpAndQualified(Long cid, String uid, Map<Long, List<Long>> empIds,
                                             Map<Long, List<Long>> qualifiedIds) {
        if (!empIds.isEmpty()) {
            List<CareerPathFlowEmp> listEntities = new ArrayList<>();
            empIds.forEach((key, value) -> value.forEach(id -> {
                CareerPathFlowEmp flowEmp = new CareerPathFlowEmp();
                flowEmp.setCareerPathFlowId(key);
                flowEmp.setEmpId(id);
                flowEmp.setStatus(Constants.STATE_ACTIVE);
                flowEmp.setCompanyId(cid);
                flowEmp.setCreateBy(uid);
                flowEmp.setUpdateBy(uid);
                listEntities.add(flowEmp);
            }));
            cpFlowEmpRepo.saveAll(listEntities);
        }
        if (!qualifiedIds.isEmpty()) {
            List<CareerPathFlowQualified> listEntities = new ArrayList<>();
            qualifiedIds.forEach((key, value) -> value.forEach(id -> {
                CareerPathFlowQualified flowQualified = new CareerPathFlowQualified();
                flowQualified.setCareerPathFlowId(key);
                flowQualified.setQualifiedId(id);
                flowQualified.setStatus(Constants.STATE_ACTIVE);
                flowQualified.setCompanyId(cid);
                flowQualified.setCreateBy(uid);
                flowQualified.setUpdateBy(uid);
                listEntities.add(flowQualified);
            }));
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
                textPathAndName = stringBuilder.substring(SDUtils.Number.N0, stringBuilder.length() - SDUtils.Number.N1);
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
        List<String> states = SDUtils.State.Employee.DEFAULT;
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "employee_position");
        filter.put("empIds", empIds);
        filter.put("state", states);
        return executeHcmService.getDataPositionAndEmployee(cid, uid, filter);
    }

    private CareerPathDTO validateDataDetail(Long cid, Long id) {
        Optional<CareerPath> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isEmpty()) {
            throw new BusinessException(SDUtils.LanguageMessage.CareerPath.NOT_FOUND);
        }
        CareerPath careerPath = entityOptional.get();
        return CareerPath.toDTO(careerPath);
    }

    private CareerPath validateInput(Long cid, CareerPathDTO dto, boolean isEdit) {
        if (!StringUtils.hasLength(dto.getCode()) || !StringUtils.hasLength(dto.getName())) {
            throw new BusinessException(SDUtils.LanguageMessage.REQUIRED_FIELD);
        }

        CareerPath entity = new CareerPath();
        if (isEdit) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
            if (entity == null) {
                throw new BusinessException(SDUtils.LanguageMessage.CareerPath.NOT_FOUND);
            }
            if (!dto.getCode().equals(entity.getCode())) {
                throw new BusinessException(SDUtils.LanguageMessage.CareerPath.CODE_NOT_CHANGE);
            }
        } else {
            List<CareerPath> list = repo.findAllByCompanyIdAndCode(cid, dto.getCode());
            if (list.size() > SDUtils.Number.N0) {
                throw new BusinessException(SDUtils.LanguageMessage.CareerPath.EXIST);
            }
        }
        return entity;
    }

    private void validateInput(List<CareerPathFlow> careerPathFlows) {
        for (CareerPathFlow item : careerPathFlows) {
            if (item.getCareerPathId() == null) {
                throw new BusinessException(SDUtils.LanguageMessage.CareerPath.IN_FLOW_NOT_FOUND);
            }
            if (item.getPositionId() == null || SDUtils.Symbol.EMPTY.equals(item.getCode())) {
                throw new BusinessException(SDUtils.LanguageMessage.CareerPath.IN_FLOW_POSITION_CODE_NOT_FOUND);
            }
        }
    }
}
