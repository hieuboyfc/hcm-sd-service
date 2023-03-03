package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.SDFormulaSetupDTO;

import java.io.IOException;
import java.util.List;

public interface SDFormulaSetupService {

    Page<SDFormulaSetupDTO> search(Long cid, String uid, SDFormulaSetupDTO dto, Pageable pageable);

    SDFormulaSetupDTO create(Long cid, String uid, SDFormulaSetupDTO dto);

    SDFormulaSetupDTO update(Long cid, String uid, SDFormulaSetupDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    SDFormulaSetupDTO getDetail(Long cid, String uid, Long id);

    Object runFormula(Long cid, String uid, Long id, List<Object> listValue) throws IOException;

    SDFormulaSetupDTO checkSyntax(long cid, String uid, SDFormulaSetupDTO dto);
}
