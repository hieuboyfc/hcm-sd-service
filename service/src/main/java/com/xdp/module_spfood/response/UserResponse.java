package com.xdp.module_spfood.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserResponse {

    private String id;
    private String avatar;
    private String email;
    private Integer status;
    private String fullname;

}
