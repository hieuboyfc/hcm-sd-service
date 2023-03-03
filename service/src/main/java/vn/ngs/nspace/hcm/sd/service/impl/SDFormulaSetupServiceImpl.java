package vn.ngs.nspace.hcm.sd.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.sd.entities.SDFormulaSetup;
import vn.ngs.nspace.hcm.sd.repository.SDFormulaSetupRepo;
import vn.ngs.nspace.hcm.sd.service.SDFormulaSetupService;
import vn.ngs.nspace.hcm.sd.share.dto.SDFormulaSetupDTO;
import vn.ngs.nspace.hcm.sd.utils.DateTimeUtils;
import vn.ngs.nspace.hcm.sd.utils.SDUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SDFormulaSetupServiceImpl implements SDFormulaSetupService {

    private final SDFormulaSetupRepo repo;

    @Override
    public Page<SDFormulaSetupDTO> search(Long cid, String uid, SDFormulaSetupDTO dto, Pageable pageable) {
        String search;
        if (dto.getStartDate() == null) {
            dto.setStartDate(DateTimeUtils.minDate());
        }
        if (dto.getEndDate() == null) {
            dto.setEndDate(DateTimeUtils.maxDate());
        }
        if (dto.getSearch() != null) {
            search = "%" + dto.getSearch() + "%";
        } else {
            search = "%%";
        }
        Page<SDFormulaSetup> listPages = repo.search(
                cid, Constants.STATE_ACTIVE, dto.getStartDate(), dto.getEndDate(), search, pageable
        );
        List<SDFormulaSetupDTO> listDTOs = SDFormulaSetup.toDTOs(listPages.getContent());
        return new PageImpl<>(listDTOs, pageable, listPages.getTotalElements());
    }

    @Override
    @Transactional
    public SDFormulaSetupDTO create(Long cid, String uid, SDFormulaSetupDTO dto) {
        SDFormulaSetup entity = validateInput(cid, uid, dto, false, "create");
        repo.save(entity);
        dto = SDFormulaSetup.toDTO(entity);
        return dto;
    }

    @Override
    @Transactional
    public SDFormulaSetupDTO update(Long cid, String uid, SDFormulaSetupDTO dto) {
        SDFormulaSetupDTO dtoData = new SDFormulaSetupDTO();
        SDFormulaSetup entity = validateInput(cid, uid, dto, true, "update");
        if (entity != null) {
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setType(dto.getType());
            entity.setSyntax(dto.getSyntax());
            entity.setVariable(dto.getVariable());
            entity.setCreateDate(DateTimeUtils.getStartOfDate(dto.getStartDate()));
            entity.setEndDate(DateTimeUtils.getEndOfDate(dto.getEndDate()));
            entity.setStatus(dto.getStatus());
            entity.setUpdateBy(uid);
            repo.save(entity);
            dtoData = SDFormulaSetup.toDTO(entity);
        }
        return dtoData;
    }

    @Override
    @Transactional
    public void delete(Long cid, String uid, List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("required-field-null");
        }
        List<SDFormulaSetup> listData = repo.findAllByCompanyIdAndStatusAndIdIn(cid, Constants.STATE_ACTIVE, ids);
        if (listData.isEmpty()) {
            throw new BusinessException("sd-risk-leave-work-not-found");
        }
        listData.forEach(item -> {
            item.setUpdateBy(uid);
            item.setStatus(Constants.STATE_INACTIVE);
        });
        repo.saveAll(listData);
    }

    @Override
    public SDFormulaSetupDTO getDetail(Long cid, String uid, Long id) {
        Optional<SDFormulaSetup> entityOptional = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id);
        if (entityOptional.isPresent()) {
            SDFormulaSetup entity = entityOptional.get();
            return SDFormulaSetup.toDTO(entity);
        }
        return null;
    }

    @Override
    public Object runFormula(Long cid, String uid, Long id, List<Object> listValue) throws IOException {
        double result = 0;
        if (id == null) {
            throw new BusinessException("Request Invalid");
        }
        SDFormulaSetup entity = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id).orElse(null);
        if (entity != null) {
            String formula = entity.getSyntax();
            List<String> listVariable = new ArrayList<>(Arrays.asList(entity.getVariable().split(",")));
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Calculate");
            int rowCount = 0;
            for (int i = 0; i < listValue.size(); i++) {
                formula = formula.replace("{" + listVariable.get(i) + "}", "" + listValue.get(i));
            }
            Row rowFormula = sheet.createRow(rowCount);
            Cell cellFormula = rowFormula.createCell(0, CellType.FORMULA);
            cellFormula.setCellFormula(formula.substring(1));

            try {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                result = evaluator.evaluate(cellFormula).getNumberValue();
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            } finally {
                workbook.close();
            }

        }
        return result;
    }

    @Override
    public SDFormulaSetupDTO checkSyntax(long cid, String uid, SDFormulaSetupDTO dto) {
        Stack<Character> parentheses = new Stack<>(); // Ngoặc đơn
        Stack<Character> quotes = new Stack<>(); // Ngoặc kép
        if (dto.getVariable() != null) {
            List<String> variables = Stream.of(dto.getVariable().split(",")).collect(Collectors.toList());
            checkLength(variables);
        }
        if (dto.getSyntax() != null) {
            checkSyn(dto.getSyntax(), parentheses, quotes);
        }
        return dto;
    }

    private SDFormulaSetup validateInput(Long cid, String uid, SDFormulaSetupDTO dto, boolean isEdit, String type) {
        if (dto.getCode() == null || dto.getName() == null || dto.getStartDate() == null || dto.getEndDate() == null
                || dto.getType() == null || dto.getSyntax() == null || dto.getVariable() == null) {
            throw new BusinessException("require-field-empty");
        }

        Date startDate = DateTimeUtils.getStartOfDate(dto.getStartDate());
        Date endDate = DateTimeUtils.getEndOfDate(dto.getEndDate());
        if (startDate.after(endDate)) {
            throw new BusinessException("sd-formula-setup-check-from-to-date");
        }

        SDFormulaSetup sdFormulaSetup;
        if (isEdit) {
            sdFormulaSetup = repo
                    .findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, dto.getId())
                    .orElse(null);
            if (sdFormulaSetup == null) {
                throw new BusinessException("sd-formula-setup-not-found");
            }
            if (!sdFormulaSetup.getCode().equals(dto.getCode())) {
                throw new BusinessException("sd-formula-setup-code-not-changed");
            }
        } else {
            sdFormulaSetup = SDFormulaSetup.of(cid, uid, dto);
        }

        Long id = isEdit ? dto.getId() : -1L;
        List<SDFormulaSetup> listData = repo
                .getByCodeAndEffectiveDate(cid, id, Constants.STATE_ACTIVE, dto.getCode(), startDate, endDate);
        if (!listData.isEmpty()) {
            listData.forEach(item -> {
                String typeCode = item.getType();
                if (dto.getType().equals("03") && (typeCode.equals("01") || typeCode.equals("02"))) {
                    throw new BusinessException("5000", "sd-formula-setup-effect-exist", getTypeName(dto.getType()));
                }
                if (typeCode.equals("03") && (dto.getType().equals("01") || dto.getType().equals("02"))) {
                    throw new BusinessException("5000", "sd-formula-setup-effect-exist", getTypeName(dto.getType()));
                }
                if (!isEdit && dto.getCode().equals(item.getCode())) {
                    throw new BusinessException("5000", "sd-formula-setup-exist", dto.getCode());
                }
                if (DateTimeUtils.beforeOrEqual(startDate, item.getStartDate())
                        && DateTimeUtils.afterOrEqual(endDate, item.getEndDate())) {

                } else if (startDate.after(item.getStartDate()) && endDate.before(item.getEndDate())) {

                } else if (DateTimeUtils.afterOrEqual(startDate, item.getStartDate())
                        && DateTimeUtils.beforeOrEqual(startDate, item.getEndDate())
                        && DateTimeUtils.afterOrEqual(endDate, item.getEndDate())) {

                } else if (DateTimeUtils.afterOrEqual(endDate, item.getStartDate())
                        && DateTimeUtils.beforeOrEqual(endDate, item.getEndDate())
                        && DateTimeUtils.beforeOrEqual(startDate, item.getStartDate())) {

                }
            });
        }
        return sdFormulaSetup;
    }

    private String getTypeName(String type) {
        String result = "";
        switch (type) {
            case "01":
                result = "01 - Lộ trình công danh";
                break;
            case "02":
                result = "02 - Kế nhiệm";
                break;
            case "03":
                result = "01 - Lộ trình công danh; 02 - Kế nhiệm";
                break;
        }
        return result;
    }

    private void checkLength(List<String> variables) {
        for (String variable : variables) {
            List<String> codes = Stream.of(variable.split("\\.")).collect(Collectors.toList());
            if (codes.size() < 2) {
                throw new BusinessException("sd-formula-setup-code-not-enter");
            }
            String var = codes.get(0);
            if (variable.contains(SDUtils.RATE_EE) || variable.contains(SDUtils.RATE_POS)
                    || variable.contains(SDUtils.WEIGHT_POS) || variable.contains(SDUtils.LEVEL_EE)
                    || variable.contains(SDUtils.LEVEL_POS) || variable.contains(SDUtils.COM_POS)) {
                for (int i = 1; i < codes.size(); i++) {
                    String code = codes.get(i);
                    if (code.length() > 8) {
                        throw new BusinessException("5000", "sd-formula-setup-code-greater-than-8", code, var);
                    }
                }
            } else if (variable.contains(SDUtils.CHECK_CONTRACT) || variable.contains(SDUtils.CHECK_ACTION)) {
                for (int i = 1; i < codes.size(); i++) {
                    String code = codes.get(i);
                    if (code.length() > 255) {
                        throw new BusinessException("5000", "sd-formula-setup-code-greater-than-255", code, var);
                    }
                }
            }
        }
    }

    private void checkSyn(String formula, Stack<Character> parentheses, Stack<Character> quotes) {
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (c == '(') {
                parentheses.push(c);
            } else if (c == '{') {
                quotes.push(c);
            } else if (c == ')') {
                if (parentheses.isEmpty()) {
                    throw new BusinessException("sd-formula-setup-error-syntax");
                }
                parentheses.pop();
            } else if (c == '}') {
                if (quotes.isEmpty()) {
                    throw new BusinessException("sd-formula-setup-error-syntax");
                }
                quotes.pop();
            }
        }
        if (!quotes.isEmpty() || !parentheses.isEmpty()) {
            throw new BusinessException("sd-formula-setup-error-syntax");
        }
    }
}
