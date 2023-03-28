package com.xdp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdp.entities.HeirSetup;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import com.xdp.repository.HeirSetupRepo;
import com.xdp.dto.HeirSetupDTO;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.xdp.service.HeirSetupService;
import com.xdp.utils.ObjectUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HeirSetupServiceImpl implements HeirSetupService {

    private final HeirSetupRepo repo;

    @Override
    public Map<String, Object> initSetup(Long cid, String uid) {
        List<HeirSetup> list = repo.findByCompanyIdAndStatusOrderByIdDesc(cid, Constants.ENTITY_ACTIVE);
        if (!list.isEmpty()) {
            HeirSetup entity = list.get(0);
            Map<String, Object> mapData = new HashMap<>();
            getData(entity, mapData);
            return mapData;
        } else {
            throw new BusinessException("sd-reason-leave-not-found");
        }
    }

    @Override
    public Map<String, Object> getDetail(Long cid, String uid, Long id) {
        Optional<HeirSetup> entityOptional = repo.findByCompanyIdAndId(cid, id);
        if (entityOptional.isPresent()) {
            HeirSetup entity = entityOptional.get();
            Map<String, Object> mapData = new HashMap<>();
            getData(entity, mapData);
            return mapData;
        } else {
            throw new BusinessException("sd-reason-leave-not-found");
        }
    }

    @Override
    public HeirSetupDTO update(Long cid, String uid, HeirSetupDTO dto) throws JsonProcessingException {
        Optional<HeirSetup> entityOptional = repo.findByCompanyIdAndId(cid, dto.getId());
        if (entityOptional.isPresent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            HeirSetup entity = entityOptional.get();

            // Xử lý dữ liệu ---> Cài đặt gợi ý người kế nhiệm
            Map<String, Object> curSuggestSuccessor = new HashMap<>();
            if (!entity.getSuggestSuccessor().isEmpty()) {
                Object object = ObjectUtils.getDataFromJson(entity.getSuggestSuccessor());
                curSuggestSuccessor = Objects.requireNonNull(JsonObject.mapFrom(object)).getMap();
            }

            if (curSuggestSuccessor != null) {
                Map<String, Object> newSuggestSuccessor = new HashMap<>();
                Map<String, Object> newSuggestSuccessorTwo = new HashMap<>();
                Map<String, Object> newSuggestSuccessorNext = new HashMap<>();
                Map<String, Object> curSuggestSuccessorNext;
                Map<String, Object> curSuggestSuccessorFT;

                newSuggestSuccessor.put("name", curSuggestSuccessor.get("name"));

                curSuggestSuccessorNext = JsonObject.mapFrom(curSuggestSuccessor.get("percentLevel")).getMap();
                newSuggestSuccessorTwo.put("name", curSuggestSuccessorNext.get("name"));
                newSuggestSuccessorTwo.put("checked", dto.getPercentLevel());

                curSuggestSuccessorFT = JsonObject.mapFrom(curSuggestSuccessorNext.get("percentLevelFrom")).getMap();
                newSuggestSuccessorNext.put("name", curSuggestSuccessorFT.get("name"));
                newSuggestSuccessorNext.put("value", dto.getPercentLevelFrom());
                newSuggestSuccessorTwo.put("percentLevelFrom", newSuggestSuccessorNext);
                newSuggestSuccessor.put("percentLevel", newSuggestSuccessorTwo);
                newSuggestSuccessorNext = new HashMap<>();

                curSuggestSuccessorFT = JsonObject.mapFrom(curSuggestSuccessorNext.get("percentLevelTo")).getMap();
                newSuggestSuccessorNext.put("name", curSuggestSuccessorFT.get("name"));
                newSuggestSuccessorNext.put("value", dto.getPercentLevelTo());
                newSuggestSuccessorTwo.put("percentLevelTo", newSuggestSuccessorNext);
                newSuggestSuccessor.put("percentLevel", newSuggestSuccessorTwo);

                String jsonData = objectMapper.writeValueAsString(newSuggestSuccessor);
                entity.setSuggestSuccessor(jsonData);
            }

            // Xử lý dữ liệu ---> Cài đặt hiển thị người kế nhiệm
            Map<String, Object> curDisplaySuccessor = new HashMap<>();
            if (!entity.getDisplaySuccessor().isEmpty()) {
                Object object = ObjectUtils.getDataFromJson(entity.getDisplaySuccessor());
                curDisplaySuccessor = Objects.requireNonNull(JsonObject.mapFrom(object)).getMap();
            }

            if (curDisplaySuccessor != null) {
                Map<String, Object> newDisplaySuccessor = new HashMap<>();
                Map<String, Object> newDisplaySuccessorNext = new HashMap<>();
                Map<String, Object> curDisplaySuccessorNext;

                newDisplaySuccessor.put("name", curDisplaySuccessor.get("name"));

                curDisplaySuccessorNext = JsonObject.mapFrom(curDisplaySuccessor.get("sortByRank")).getMap();
                newDisplaySuccessorNext.put("name", curDisplaySuccessorNext.get("name"));
                newDisplaySuccessorNext.put("checked", dto.getSortByRank());
                newDisplaySuccessor.put("sortByRank", newDisplaySuccessorNext);
                newDisplaySuccessorNext = new HashMap<>();

                curDisplaySuccessorNext = JsonObject.mapFrom(curDisplaySuccessor.get("sortByDegree")).getMap();
                newDisplaySuccessorNext.put("name", curDisplaySuccessorNext.get("name"));
                newDisplaySuccessorNext.put("checked", dto.getSortByDegree());
                newDisplaySuccessor.put("sortByDegree", newDisplaySuccessorNext);

                String jsonData = objectMapper.writeValueAsString(newDisplaySuccessor);
                entity.setDisplaySuccessor(jsonData);
            }

            // Xử lý dữ liệu ---> Cài đặt hiển thị thẻ tài năng
            Map<String, Object> curDisplayTalentCard = new HashMap<>();
            if (!entity.getDisplayTalentCard().isEmpty()) {
                Object object = ObjectUtils.getDataFromJson(entity.getDisplayTalentCard());
                curDisplayTalentCard = Objects.requireNonNull(JsonObject.mapFrom(object)).getMap();
            }

            if (curDisplayTalentCard != null) {
                Map<String, Object> newDisplayTalentCard = new HashMap<>();
                Map<String, Object> newDisplayTalentCardNext = new HashMap<>();
                Map<String, Object> curDisplayTalentCardNext;

                newDisplayTalentCard.put("name", curDisplayTalentCard.get("name"));

                curDisplayTalentCardNext = JsonObject.mapFrom(curDisplayTalentCard.get("qualification")).getMap();
                newDisplayTalentCardNext.put("name", curDisplayTalentCardNext.get("name"));
                newDisplayTalentCardNext.put("checked", dto.getQualification());
                newDisplayTalentCard.put("qualification", newDisplayTalentCardNext);
                newDisplayTalentCardNext = new HashMap<>();

                curDisplayTalentCardNext = JsonObject.mapFrom(curDisplayTalentCard.get("talentReview")).getMap();
                newDisplayTalentCardNext.put("name", curDisplayTalentCardNext.get("name"));
                newDisplayTalentCardNext.put("checked", dto.getTalentReview());
                newDisplayTalentCard.put("talentReview", newDisplayTalentCardNext);
                newDisplayTalentCardNext = new HashMap<>();

                curDisplayTalentCardNext = JsonObject.mapFrom(curDisplayTalentCard.get("workProcess")).getMap();
                newDisplayTalentCardNext.put("name", curDisplayTalentCardNext.get("name"));
                newDisplayTalentCardNext.put("checked", dto.getWorkProcess());
                newDisplayTalentCard.put("workProcess", newDisplayTalentCardNext);
                newDisplayTalentCardNext = new HashMap<>();

                curDisplayTalentCardNext = JsonObject.mapFrom(curDisplayTalentCard.get("literacy")).getMap();
                newDisplayTalentCardNext.put("name", curDisplayTalentCardNext.get("name"));
                newDisplayTalentCardNext.put("checked", dto.getLiteracy());
                newDisplayTalentCard.put("literacy", newDisplayTalentCardNext);
                newDisplayTalentCardNext = new HashMap<>();

                curDisplayTalentCardNext = JsonObject.mapFrom(curDisplayTalentCard.get("resultReview")).getMap();
                newDisplayTalentCardNext.put("name", curDisplayTalentCardNext.get("name"));
                newDisplayTalentCardNext.put("checked", dto.getResultReview());
                newDisplayTalentCard.put("resultReview", newDisplayTalentCardNext);
                newDisplayTalentCardNext = new HashMap<>();

                curDisplayTalentCardNext = JsonObject.mapFrom(curDisplayTalentCard.get("warehouseTalent")).getMap();
                newDisplayTalentCardNext.put("name", curDisplayTalentCardNext.get("name"));
                newDisplayTalentCardNext.put("checked", dto.getWarehouseTalent());
                newDisplayTalentCard.put("warehouseTalent", newDisplayTalentCardNext);

                String jsonData = objectMapper.writeValueAsString(newDisplayTalentCard);
                entity.setDisplayTalentCard(jsonData);
            }

            entity.setUpdateBy(uid);
            repo.save(entity);
            return dto;
        } else {
            throw new BusinessException("sd-reason-leave-not-found");
        }
    }

    public void getData(HeirSetup entity, Map<String, Object> mapData) {
        Object suggestSuccessor = new HashMap<>();
        if (!entity.getSuggestSuccessor().isEmpty()) {
            suggestSuccessor = ObjectUtils.getDataFromJson(entity.getSuggestSuccessor());
        }

        Object displaySuccessor = new HashMap<>();
        if (!entity.getDisplaySuccessor().isEmpty()) {
            displaySuccessor = ObjectUtils.getDataFromJson(entity.getDisplaySuccessor());
        }

        Object displayTalentCard = new HashMap<>();
        if (!entity.getDisplayTalentCard().isEmpty()) {
            displayTalentCard = ObjectUtils.getDataFromJson(entity.getDisplayTalentCard());
        }

        mapData.put("id", entity.getId());
        mapData.put("cid", entity.getCompanyId());
        mapData.put("status", entity.getStatus());
        mapData.put("createBy", entity.getCreateBy());
        mapData.put("createDate", entity.getCreateDate());
        mapData.put("updateBy", entity.getUpdateBy());
        mapData.put("modifiedDate", entity.getModifiedDate());
        mapData.put("suggestSuccessor", suggestSuccessor);
        mapData.put("displaySuccessor", displaySuccessor);
        mapData.put("displayTalentCard", displayTalentCard);
    }

}
