package com.xdp.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PositionDTO {
    private Long id;
    private String type;
    private String code;
    private String name;
    private Integer status;

    private List<EmployeeDTO> employeeDTOS = new ArrayList<>();
    private Integer totalEmployee;
}
