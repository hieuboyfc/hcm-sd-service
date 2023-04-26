package com.xdp.module_spfood.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.module_spfood.dto.SPFoodConfigDTO;
import com.xdp.module_spfood.dto.SPFoodTicketDTO;
import com.xdp.module_spfood.entity.SPFoodTicket;
import com.xdp.module_spfood.repo.SPFoodTicketRepo;
import com.xdp.module_spfood.response.FileResponse;
import com.xdp.module_spfood.response.UserResponse;
import com.xdp.module_spfood.service.*;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SPFoodTicketServiceImpl implements SPFoodTicketService {

    private final EntityManager entityManager;
    private final ExecutePowerAutomateService powerAutomateService;
    private final EmailService noticeService;
    private final ExecuteProjectService projectService;
    private final ExecuteUserService userService;
    private final SPFoodConfigService configService;
    private final SPFoodTicketRepo repo;

    @Override
    public Page<SPFoodTicketDTO> getList(Long cid, String uid, String search, Pageable pageable) {
        Page<SPFoodTicket> listPages = repo.getListSPFoodTicket(cid, search, pageable);
        List<SPFoodTicketDTO> listDTOs = SPFoodTicket.toDTOs(listPages.getContent());
        listDTOs.parallelStream().forEach(dtoData -> prepareData(cid, uid, dtoData, "getList"));
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public SPFoodTicketDTO create(Long cid, String uid, SPFoodTicketDTO dto) {
        SPFoodTicketDTO dtoData = new SPFoodTicketDTO();
        try {
            SPFoodTicket entity = validateInput(cid, dto, false, "create");
            if (entity == null) {
                entity = SPFoodTicket.of(cid, dto);
                entity.setCreateBy(uid);
                entity.setUpdateBy(uid);
                repo.save(entity);
                dtoData = SPFoodTicket.toDTO(entity);
                prepareData(cid, uid, dtoData, "create");
            }
            if (dto.getPhoto() != null && dto.getRequestType().equalsIgnoreCase("CASE_02")
                    && dto.getHandleType().equalsIgnoreCase("RESET_PASSWORD")) {
                String dataError = dto.getDescription();
                String stringReplace = dataError != null ? dataError
                        .replace("', ", "#")
                        .replace("'", StringUtils.EMPTY)
                        .replace("[", StringUtils.EMPTY)
                        .replace("]", StringUtils.EMPTY)
                        .replace("<p>", StringUtils.EMPTY)
                        .replace("</p>", StringUtils.EMPTY)
                        : StringUtils.EMPTY;
                List<String> listText = new ArrayList<>(Arrays.asList(stringReplace.split("#")));
                String email = StringUtils.EMPTY;
                int index;
                for (String item : listText) {
                    index = item.indexOf("@sicix");
                    if (index > 0) {
                        email = item.split("@")[0] + "@sicix.com.vn";
                        break;
                    }
                    index = item.indexOf("@ngs");
                    if (index > 0) {
                        email = item.split("@")[0] + "@ngs.com.vn";
                        break;
                    }
                }
                if (email.length() == 0) {
                    throw new BusinessException("Không tìm thấy địa chỉ Email để thực hiện Khôi phục Mật Khẩu");
                }
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> mapUser = repo.getUserByEmail(cid, email);
                UserResponse userResponse = mapper.convertValue(mapUser, UserResponse.class);
                if (userResponse.getId() != null && userResponse.getEmail() != null) {
                    Map<String, Object> bodyUser = new HashMap<>();
                    bodyUser.put("id", userResponse.getId());
                    bodyUser.put("password", "SiciX@2023");
                    boolean checkUser = userService.updatePassword(cid, uid, bodyUser);
                    if (Boolean.FALSE.equals(checkUser)) {
                        throw new BusinessException("Đã xảy ra sự cố khi Reset Password cho User có Email là [" + email + "]");
                    }
                    // noticeService.sendMail(email);
                } else {
                    throw new BusinessException("Không tìm thấy User có Email là [" + email + "] trong hệ thống để xử lý");
                }
            }
            if (dto.getRequestType().equalsIgnoreCase("CASE_01")
                    && dto.getHandleType().startsWith("SETUP_")) {
                if (dto.getEmail() == null) {
                    throw new BusinessException("Không tìm thấy địa chỉ Email để thực hiện Cài đặt Phần mềm");
                }
                SPFoodConfigDTO configDTO = initConfig(cid, uid, "setup-software");
                boolean checkPowerAutomate = powerAutomateService.setupSoftware(configDTO, dtoData);
                if (Boolean.FALSE.equals(checkPowerAutomate)) {
                    throw new BusinessException("Đã xảy ra sự cố");
                }
            }
            /*boolean checkPowerAutomate = powerAutomateService.createOrUpdate(configDTO, dtoData);
            if (Boolean.FALSE.equals(checkPowerAutomate)) {
                throw new BusinessException("Đã xảy ra sự cố");
            }*/
            return dtoData;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public SPFoodTicketDTO update(Long cid, String uid, SPFoodTicketDTO dto) {
        SPFoodTicketDTO dtoData = new SPFoodTicketDTO();
        try {
            SPFoodTicket entity = validateInput(cid, dto, true, "update");
            if (entity != null) {
                entity.setCode(dto.getCode().toUpperCase());
                entity.setName(dto.getName());
                entity.setDescription(dto.getDescription());
                entity.setType(dto.getType());
                entity.setEmail(dto.getEmail());
                entity.setPhone(dto.getPhone());
                entity.setResponseDeadline(dto.getResponseDeadline());
                entity.setDeadline(dto.getDeadline());
                entity.setProjectId(dto.getProjectId());
                entity.setRootPhaseId(dto.getRootPhaseId());
                entity.setSolution(dto.getSolution());
                entity.setRequestedBy(dto.getRequestedBy());
                entity.setResponsibleId(dto.getResponsibleId());
                entity.setStatus(dto.getStatus());
                entity.setUpdateBy(uid);
                repo.save(entity);
                dtoData = SPFoodTicket.toDTO(entity);
                prepareData(cid, uid, dtoData, "update");
            }
            /*SPFoodConfigDTO configDTO = initConfig(cid, uid, "update");
            boolean checkPowerAutomate = powerAutomateService.createOrUpdate(configDTO, dtoData);
            if (Boolean.FALSE.equals(checkPowerAutomate)) {
                throw new BusinessException("Đã xảy ra sự cố");
            }*/
            return dtoData;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, Long id) {
        try {
            SPFoodTicket entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
            if (entity == null) {
                throw new BusinessException("Không tìm thấy ID Ticket");
            }
            repo.delete(entity);
            SPFoodConfigDTO configDTO = initConfig(cid, uid, "delete");
            boolean checkPowerAutomate = powerAutomateService.delete(configDTO, entity.getId());
            if (Boolean.FALSE.equals(checkPowerAutomate)) {
                throw new BusinessException("Đã xảy ra sự cố");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public SPFoodTicketDTO getDetail(Long cid, String uid, Long id) {
        Optional<SPFoodTicket> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isPresent()) {
            SPFoodTicket entity = entityOptional.get();
            SPFoodTicketDTO dtoData = SPFoodTicket.toDTO(entity);
            prepareData(cid, uid, dtoData, "getDetail");
            return dtoData;
        } else {
            throw new BusinessException("Không tìm thấy Ticket trên hệ thống");
        }
    }

    @Override
    public FileResponse getDataFromFile(Long cid, String uid, MultipartFile file) {
        try {
            SPFoodConfigDTO configDTO = initConfig(cid, uid, "get-file");
            return powerAutomateService.getDataFromFile(configDTO, file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    private SPFoodConfigDTO initConfig(Long cid, String uid, String type) {
        List<SPFoodConfigDTO> configListDTOs = configService.initData(cid, uid);
        if (configListDTOs.isEmpty()) {
            throw new BusinessException("Không tìm thấy dánh sách cấu hình API của Ticket");
        }
        SPFoodConfigDTO configDTO = configListDTOs
                .stream()
                .filter(o -> o.getType().equalsIgnoreCase(type))
                .findFirst().orElse(null);
        if (configDTO == null) {
            throw new BusinessException("Không tìm thấy cấu hình API (" + type + ") Ticket");
        }
        return configDTO;
    }

    private void getInfoProject(Long cid, String uid, SPFoodTicketDTO dtoData, boolean checkCall) {
        List<Map<String, Object>> mapProject = projectService.getAllProject(cid, uid);
        Map<String, Object> projectMap = new HashMap<>();
        for (Map<String, Object> item : mapProject) {
            if (Long.valueOf(String.valueOf(item.get("id"))).equals(dtoData.getProjectId())) {
                projectMap.put("projectId", checkCall
                        ? String.valueOf(item.getOrDefault("id", 0))
                        : Long.valueOf(String.valueOf(item.getOrDefault("id", 0))));
                projectMap.put("projectName", item.getOrDefault("name", StringUtils.EMPTY));
                break;
            }
        }
        if (projectMap.isEmpty()) {
            projectMap.put("projectId", checkCall ? String.valueOf(dtoData.getProjectId()) : dtoData.getProjectId());
            projectMap.put("projectName", StringUtils.EMPTY);
        }
        dtoData.setProject(projectMap);
    }

    private void getInfoPhase(Long cid, String uid, SPFoodTicketDTO dtoData, boolean checkCall) {
        List<Map<String, Object>> mapPhase = projectService.getAllPhaseByProjectId(cid, uid, dtoData.getProjectId());
        Map<String, Object> phaseMap = new HashMap<>();
        for (Map<String, Object> item : mapPhase) {
            if (Long.valueOf(String.valueOf(item.get("rootId"))).equals(dtoData.getRootPhaseId())) {
                phaseMap.put("phaseId", checkCall
                        ? String.valueOf(item.getOrDefault("rootId", 0))
                        : Long.parseLong(String.valueOf(item.getOrDefault("rootId", 0))));
                phaseMap.put("phaseName", item.getOrDefault("name", StringUtils.EMPTY));
                break;
            }
        }
        if (phaseMap.isEmpty()) {
            phaseMap.put("phaseId", checkCall ? String.valueOf(dtoData.getRootPhaseId()) : dtoData.getRootPhaseId());
            phaseMap.put("phaseName", StringUtils.EMPTY);
        }
        dtoData.setPhase(phaseMap);
    }

    public List<?> getUserBy(Long cid, Set<String> userIds) {
        try {
            StringBuilder sqlQuery = new StringBuilder("SELECT ");
            sqlQuery.append(" u.id AS id, ");
            sqlQuery.append(" u.avatar AS avatar, ");
            sqlQuery.append(" CONCAT(u.first_name, ' ', u.middle_name, ' ', u.last_name) AS fullName, ");
            sqlQuery.append(" e.id AS empId, ");
            sqlQuery.append(" e.emp_no AS empNo, ");
            sqlQuery.append(" e.full_name as empName ");
            sqlQuery.append(" FROM user_service.user u ");
            sqlQuery.append(" JOIN hcm_service.employee e ON u.id = e.user_mapping_id ");
            sqlQuery.append(" WHERE u.company_id = :cid AND e.company_id = :cid ");
            sqlQuery.append(" AND u.status = 1 AND e.status = 1 ");
            sqlQuery.append(" AND u.id IN (:userIds) ");
            Query selectQuery = entityManager.createNativeQuery(sqlQuery.toString());
            selectQuery.setParameter("cid", cid);
            selectQuery.setParameter("userIds", userIds);
            selectQuery.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return selectQuery.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> findByUserMap(String userId, List<?> listUsers, boolean checkCall) {
        Map<String, Object> user = new HashMap<>();
        if (!listUsers.isEmpty() && (userId != null && userId.length() > 0)) {
            for (Object item : listUsers) {
                Map<String, Object> data = JsonObject.mapFrom(item).getMap();
                if (data.get("id").equals(userId)) {
                    user.put("id", data.getOrDefault("id", StringUtils.EMPTY));
                    user.put("avatar", data.getOrDefault("avatar", StringUtils.EMPTY));
                    user.put("fullName", data.getOrDefault("fullname", StringUtils.EMPTY));
                    user.put("empId", checkCall
                            ? String.valueOf(data.getOrDefault("empid", 0))
                            : Long.parseLong(String.valueOf(data.getOrDefault("empid", 0))));
                    user.put("empNo", data.getOrDefault("empno", StringUtils.EMPTY));
                    user.put("empName", data.getOrDefault("empname", StringUtils.EMPTY));
                    break;
                }
            }
        }
        if (user.isEmpty()) {
            user.put("id", StringUtils.EMPTY);
            user.put("avatar", StringUtils.EMPTY);
            user.put("fullName", StringUtils.EMPTY);
            user.put("empId", StringUtils.EMPTY);
            user.put("empNo", StringUtils.EMPTY);
            user.put("empName", StringUtils.EMPTY);
        }
        return user;
    }

    private void prepareData(Long cid, String uid, SPFoodTicketDTO dto, String type) {
        boolean checkCall = type.equalsIgnoreCase("create") || type.equalsIgnoreCase("update");
        getInfoProject(cid, uid, dto, checkCall);
        getInfoPhase(cid, uid, dto, checkCall);
        // List<?> listUsers = getUserBy(cid, getUserIds(dto));
        List<?> listUsers = repo.getListUserBy(cid, getUserIds(dto));
        dto.setRequested(findByUserMap(dto.getRequestedBy(), listUsers, checkCall));
        dto.setResponsible(findByUserMap(dto.getResponsibleId(), listUsers, checkCall));
        dto.setStatusName(getStatusName(dto.getStatus()));
    }

    private Set<String> getUserIds(SPFoodTicketDTO dto) {
        Set<String> userIds = new HashSet<>();
        try {
            if (dto.getRequestedBy() != null && dto.getRequestedBy().length() > 0) {
                userIds.add(dto.getRequestedBy());
            }
            if (dto.getResponsibleId() != null && dto.getResponsibleId().length() > 0) {
                userIds.add(dto.getResponsibleId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userIds;
    }

    private String getStatusName(Integer status) {
        String result = StringUtils.EMPTY;
        if (status == 1) {
            result = "Đang hoạt động";
        }
        if (status == 0) {
            result = "Ngừng hoạt động";
        }
        return result;
    }

    private SPFoodTicket validateInput(Long cid, SPFoodTicketDTO dto, Boolean isEdit, String type) {
        if (dto.getCode() == null || dto.getCode().length() == 0) {
            throw new BusinessException("Mã Code Ticket không được bỏ trống");
        }
        SPFoodTicket entity;
        if (Boolean.TRUE.equals(isEdit)) {
            Long id = dto.getId() != null ? dto.getId() : -1L;
            entity = repo.findByCompanyIdAndId(cid, id).orElse(null);
            if (entity == null) {
                throw new BusinessException("Không tìm thấy Ticket trên hệ thống");
            }
            if (!entity.getCode().equalsIgnoreCase(dto.getCode())) {
                throw new BusinessException("Mã Code của Ticket không được phép thay đổi");
            }
        } else {
            entity = repo.findByCompanyIdAndCode(cid, dto.getCode()).orElse(null);
            if (entity != null) {
                throw new BusinessException("Ticket đã tồn tại trên hệ thống");
            }
        }
        return entity;
    }
}
