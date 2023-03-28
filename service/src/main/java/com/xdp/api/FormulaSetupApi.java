package com.xdp.api;

import com.xdp.lib.annotation.ActionMapping;
import com.xdp.lib.utils.ResponseUtils;
import com.xdp.policy.utils.Permission;
import com.xdp.response.VariableResponse;
import com.xdp.service.FormulaSetupService;
import com.xdp.dto.FormulaSetupDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/formula-setup")
@Tag(name = "FormulaSetupApi", description = "SD Formula Setup Api")
public class FormulaSetupApi {

    private final FormulaSetupService service;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search - SD Formula Setup", description = "Search - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> search(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                    @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                    @Parameter(description = "Payload variable value") @RequestBody FormulaSetupDTO dto,
                                    Pageable pageable) {
        try {
            return ResponseUtils.handlerSuccess(service.search(cid, uid, dto, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/create")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create - SD Formula Setup", description = "Create - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> create(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                    @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                    @Parameter(description = "Payload Create") @RequestBody FormulaSetupDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/update")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update - SD Formula Setup", description = "Update - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> update(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                    @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                    @Parameter(description = "Payload Update") @RequestBody FormulaSetupDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/delete")
    @ActionMapping(action = Permission.DELETE)
    @Operation(summary = "Delete - SD Formula Setup", description = "Delete - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> delete(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                    @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                    @Parameter(description = "Payload variable value") @RequestBody List<Long> ids) {
        service.delete(cid, uid, ids);
        try {
            return ResponseUtils.handlerSuccess();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/get-detail")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Detail - SD Formula Setup", description = "Get Detail - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> getDetail(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                       @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                       @Parameter(description = "Payload variable value") @RequestParam(name = "id") Long id) {
        try {
            return ResponseUtils.handlerSuccess(service.getDetail(cid, uid, id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/run-formula/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Run Formula - SD Formula Setup", description = "Run Formula - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> runPayrollFormula(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                               @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                               @Parameter(description = "Payload variable value") @RequestBody List<Object> listValue,
                                               @PathVariable("id") Long id) {
        try {
            return ResponseUtils.handlerSuccess(service.runFormula(cid, uid, id, listValue));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/check-syntax")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Check Syntax - SD Formula Setup", description = "Check Syntax - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> checkSyntax(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                         @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                         @Parameter(description = "Payload variable value") @RequestBody FormulaSetupDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.checkSyntax(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/load-variable")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Load Variable - SD Formula Setup", description = "Load Variable - SD Formula Setup", tags = {"FormulaSetup"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> getVariable(@Parameter(description = "Id of Company") @RequestHeader("cid") Long cid,
                                         @Parameter(description = "Id of User") @RequestHeader("uid") String uid) {
        try {
            VariableResponse variableResponse = service.getVariable(cid, uid);
            return ResponseUtils.handlerSuccess(variableResponse);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

}
