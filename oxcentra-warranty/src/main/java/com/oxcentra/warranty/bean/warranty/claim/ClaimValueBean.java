package com.oxcentra.warranty.bean.warranty.claim;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClaimValueBean {

    private String id;
    private String warrantyId;
    private String fileName;
    private String fileFormat;
    private String attachmentFile;
    private String createdDate;
}
