package com.oxcentra.warranty.mapping.warranty;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Scope("prototype")
public class RegWarrantyAttachments {

    private BigDecimal id;
    private String warrantyId;
    private String fileName;
    private String fileFormat;
    private Blob attachmentFile;
    private Date createdDate;


}
