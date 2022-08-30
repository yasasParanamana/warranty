package com.oxcentra.warranty.bean.warranty.claim;

import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.sysconfigmgt.state.State;
import com.oxcentra.warranty.util.common.DataTablesRequest;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClaimInputBean extends DataTablesRequest {
    private String id;
    private String chassis;
    private String model;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String surburb;
    private String state;
    private String postcode;
    private String dealership;
    private String claimType;
    private String purchasingDate;
    private String purchasingAttachmentId;
    private String description;
    private String sparePartsId;
    private String failureType;
    private String failureArea;
    private String repairType;
    private String repairDescription;
    private String attachmentsId;
    private String costType;
    private String hours;
    private String labourRate;
    private String totalCost;
    private String costDescription;
    private String assignee;
    private String supplier;
    private String status;
    private boolean isInHouse;
    private String inHouseStatus;
    private BigDecimal claimOnSupplier;
    private String supplierUrlToken;
    private BigDecimal supplierAuthAmount;
    private String supplierTrackingNum;
    private String supplierComment;
    private String supplierInvoice;
    private Date acknowledgedDate;
    private Date lastRemindedDate;
    private boolean isCritical;

    /*-------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vconfirm;
    private boolean vreject;
    private boolean vdualauth;

    /*-------for access control-----------*/
    private List<Status> statusList;
    private List<Status> statusActList;
    private List<Model> modelActList;
    private List<State> stateActList;

    /*-------file upload-----------*/
    private MultipartFile[] filesUploads;
    private List<File> filesUpload = new ArrayList<File>();
    private List<String> filesUploadContentType = new ArrayList<String>();
    private List<String> filesUploadFileName = new ArrayList<String>();

    private List<String> file = new ArrayList<>();
//    private List<String> file = new ArrayList<String>();



    /* add getters setters */

}
