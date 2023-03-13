package vn.ngs.nspace.hcm.sd.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.sd.share.dto.FuncVariableDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VariableResponse {

    private List<FuncVariableDTO> function;
    private List<FuncVariableDTO> variable;

}
