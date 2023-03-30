package com.xdp.api;

import com.xdp.dto.CareerPathDTO;
import com.xdp.lib.annotation.ActionMapping;
import com.xdp.lib.utils.ResponseUtils;
import com.xdp.policy.utils.Permission;
import com.xdp.service.CareerPathService;
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
@RequestMapping(value = "/career-path")
@Tag(name = "CareerPathApi", description = "SD Career Path Api")
public class CareerPathApi {

    private final CareerPathService service;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search - SD Career Path", description = "Search - SD Career Path", tags = {"CareerPath"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> search(@Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
                                    @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
                                    @Parameter(description = "Payload variable value") @RequestBody CareerPathDTO dto,
                                    Pageable pageable) {
        try {
            return ResponseUtils.handlerSuccess(service.search(cid, uid, dto, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Create - SD Career Path", description = "Create - SD Career Path", tags = {"CareerPath"})
    @ActionMapping(action = Permission.CREATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> create(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Create") @RequestBody CareerPathDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update - SD Career Path", description = "Update - SD Career Path", tags = {"CareerPath"})
    @ActionMapping(action = Permission.UPDATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> update(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Update") @RequestBody CareerPathDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete - SD Career Path", description = "Delete - SD Career Path", tags = {"CareerPath"})
    @ActionMapping(action = Permission.DELETE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> delete(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload variable value") @RequestBody List<Long> ids) {
        service.delete(cid, uid, ids);
        try {
            return ResponseUtils.handlerSuccess();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/refresh")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Refresh - SD Career Path", description = "Refresh - SD Career Path", tags = {"CareerPath"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    protected ResponseEntity<?> refresh(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                        @Parameter(description = "Id of User") @RequestHeader String uid,
                                        @Parameter(description = "Payload variable value") @RequestParam(name = "id") Long id) {
        try {
            return ResponseUtils.handlerSuccess(service.refresh(cid, uid, id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/get-detail")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Detail - SD Career Path", description = "Get Detail - SD Career Path", tags = {"CareerPath"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    protected ResponseEntity<?> getDetail(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                          @Parameter(description = "Id of User") @RequestHeader String uid,
                                          @Parameter(description = "Payload variable value") @RequestParam(name = "id") Long id) {
        try {
            return ResponseUtils.handlerSuccess(service.getDetail(cid, uid, id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
