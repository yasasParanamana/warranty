package com.oxcentra.warranty.bean.audit;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AuditValueBean {
    private String field;
    private String oldValue;
    private String newValue;
}
