package vn.ngs.nspace.hcm.sd.api;

import com.xdp.lib.annotation.ActionMapping;
import com.xdp.lib.utils.ResponseUtils;
import com.xdp.policy.utils.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.sd.service.CareerPathSetupService;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathSetupDTO;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/sd-career-path-setup")
@Tag(name = "CareerPathSetupApi", description = "SD Career Path Setup Api")
public class CareerPathSetupApi {

    private final CareerPathSetupService service;

    @GetMapping("/list")
    @Operation(summary = "Get List - SD Career Path Setup", description = "Get List - SD Career Path Setup", tags = {"CareerPathSetup"})
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> getList(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                     @Parameter(description = "Id of User") @RequestHeader String uid,
                                     @Parameter(description = "Payload variable value")
                                     @RequestParam(name = "search", defaultValue = "") String search,
                                     Pageable pageable) {
        try {
            return ResponseUtils.handlerSuccess(service.getList(cid, uid, search, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update - SD Career Path Setup", description = "Update - SD Career Path Setup", tags = {"CareerPathSetup"})
    @ActionMapping(action = Permission.UPDATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> update(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Update") @RequestBody CareerPathSetupDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/get-detail")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Detail - SD Career Path Setup", description = "Get Detail - SD Career Path Setup", tags = {"CareerPathSetup"})
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
