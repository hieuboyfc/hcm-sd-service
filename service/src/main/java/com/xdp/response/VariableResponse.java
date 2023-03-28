package com.xdp.response;

import com.xdp.dto.FuncVariableDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VariableResponse {

    private List<FuncVariableDTO> function;
    private List<FuncVariableDTO> variable;

}
