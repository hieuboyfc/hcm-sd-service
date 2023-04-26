package com.xdp.module_spfood.api;

import com.xdp.lib.annotation.ActionMapping;
import com.xdp.lib.utils.ResponseUtils;
import com.xdp.module_spfood.dto.SPFoodTicketDTO;
import com.xdp.module_spfood.service.SPFoodTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/sp-food-ticket")
@Tag(name = "SPFoodTicketApi", description = "SP Food Ticket Api")
public class SPFoodTicketApi {

    private final SPFoodTicketService service;

    @GetMapping("/list")
    @Operation(summary = "Get List - SP Food Ticket", description = "Get List - SP Food Ticket", tags = {"SPFoodTicket"})
    @ActionMapping(action = "view")
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
    @Operation(summary = "Create - SP Food Ticket", description = "Create - SP Food Ticket", tags = {"SPFoodTicket"})
    @ActionMapping(action = "create")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> create(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Create") @RequestBody SPFoodTicketDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update - SP Food Ticket", description = "Update - SP Food Ticket", tags = {"SPFoodTicket"})
    @ActionMapping(action = "update")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> update(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload Update") @RequestBody SPFoodTicketDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete - SP Food Ticket", description = "Delete - SP Food Ticket", tags = {"SPFoodTicket"})
    @ActionMapping(action = "delete")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity<?> delete(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                    @Parameter(description = "Id of User") @RequestHeader String uid,
                                    @Parameter(description = "Payload variable value") @RequestParam(name = "id") Long id) {
        service.delete(cid, uid, id);
        try {
            return ResponseUtils.handlerSuccess();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/get-detail")
    @ActionMapping(action = "view")
    @Operation(summary = "Get Detail - SP Food Ticket", description = "Get Detail - SP Food Ticket", tags = {"SPFoodTicket"})
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

    @PostMapping("/get-data-from-file")
    @ActionMapping(action = "view")
    @Operation(summary = "Get Data From File - SP Food Ticket", description = "Get Data From File - SP Food Ticket", tags = {"SPFoodTicket"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    protected ResponseEntity<?> getDataFromFile(@Parameter(description = "Id of Company") @RequestHeader Long cid,
                                                @Parameter(description = "Id of User") @RequestHeader String uid,
                                                @Parameter(description = "Payload variable value") @RequestParam(name = "file") MultipartFile file) {
        try {
            return ResponseUtils.handlerSuccess(service.getDataFromFile(cid, uid, file));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

}
