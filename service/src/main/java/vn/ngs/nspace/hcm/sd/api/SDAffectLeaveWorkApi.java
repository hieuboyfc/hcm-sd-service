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
import vn.ngs.nspace.hcm.sd.service.SDAffectLeaveWorkService;
import vn.ngs.nspace.hcm.sd.share.dto.category.SDAffectLeaveWorkDTO;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/sd-affect-leave-work")
@Tag(name = "SDAffectLeaveWorkApi", description = "SD Affect Leave Work Api")
public class SDAffectLeaveWorkApi {

    private final SDAffectLeaveWorkService service;

    @GetMapping("/list")
    @Operation(summary = "Get List - SD Affect Leave Work", description = "Get List - SD Affect Leave Work", tags = {"SDAffectLeaveWork"})
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

    @PostMapping("/create")
    @Operation(summary = "Create - SD Affect Leave Work", description = "Create - SD Affect Leave Work", tags = {"SDAffectLeaveWork"})
    @ActionMapping(action = Permission.CREATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> create(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Create") @RequestBody SDAffectLeaveWorkDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update - SD Affect Leave Work", description = "Update - SD Affect Leave Work", tags = {"SDAffectLeaveWork"})
    @ActionMapping(action = Permission.UPDATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> update(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Update") @RequestBody SDAffectLeaveWorkDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete - SD Affect Leave Work", description = "Delete - SD Affect Leave Work", tags = {"SDAffectLeaveWork"})
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

    @GetMapping("/get-detail")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Detail - SD Affect Leave Work", description = "Get Detail - SD Affect Leave Work", tags = {"SDAffectLeaveWork"})
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
