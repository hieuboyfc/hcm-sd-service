package com.xdp.module_spfood.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FileResponse {

    private String id;
    private String extract_text;
    private String message;
    private String errorcode;
    private String causes;
    private String solution;
    private String script;
    private String warning;

}
