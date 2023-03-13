package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.response.VariableResponse;
import vn.ngs.nspace.hcm.sd.share.dto.FormulaSetupDTO;

import java.io.IOException;
import java.util.List;

public interface FormulaSetupService {

    Page<FormulaSetupDTO> search(Long cid, String uid, FormulaSetupDTO dto, Pageable pageable);

    FormulaSetupDTO create(Long cid, String uid, FormulaSetupDTO dto);

    FormulaSetupDTO update(Long cid, String uid, FormulaSetupDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    FormulaSetupDTO getDetail(Long cid, String uid, Long id);

    Object runFormula(Long cid, String uid, Long id, List<Object> listValue) throws IOException;

    FormulaSetupDTO checkSyntax(long cid, String uid, FormulaSetupDTO dto);

    VariableResponse getVariable(Long cid, String uid);
}
