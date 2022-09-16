package com.oxcentra.warranty.bean.warranty.critical;

import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.sysconfigmgt.failurearea.FailureArea;
import com.oxcentra.warranty.bean.sysconfigmgt.failuretype.FailureType;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.sysconfigmgt.repairtype.RepairType;
import com.oxcentra.warranty.bean.sysconfigmgt.state.State;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
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
public class CriticalInputBean extends DataTablesRequest {
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
    private String isInHouse;
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
    private String createdUser;
    private String lastUpdatedUser;
    private Date createdTime;
    private Date lastUpdatedTime;
    private String dealershipName;
    private String dealershipPhone;
    private String dealershipEmail;
    private String comment;

    private String sparePartRequired1;
    private String sparePartRequired2;
    private String sparePartRequired3;
    private String sparePartRequired4;
    private String sparePartRequired5;
    private String sparePartRequired6;
    private String sparePartRequired7;
    private String sparePartRequired8;
    private String sparePartRequired9;
    private String sparePartRequired10;
    private String quantity1;
    private String quantity2;
    private String quantity3;
    private String quantity4;
    private String quantity5;
    private String quantity6;
    private String quantity7;
    private String quantity8;
    private String quantity9;
    private String quantity10;

    private String sparePartRequiredSup;
    private String quantitySup;
    private String[] sparePartRequired;
    private String[] quantityRequired;

    private String failingArea;
    private String purchasingStatus;
    private String countPending;
    private String countInPurchase;
    private String countNoted;

    /*-------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;

    /*-------for access control-----------*/
    private List<Status> statusList;
    private List<Status> statusActList;
    private List<Model> modelActList;
    private List<State> stateActList;
    private List<FailureType> failureTypeActList;
    private List<FailureArea> failureAreaActList;
    private List<RepairType> repairTypeActList;
    private List<Supplier> supplierActList;
    private List<SpareParts> sparePartList;


    /*-------file upload-----------*/
    private MultipartFile[] filesUploads;
    private List<File> filesUpload = new ArrayList<File>();
    private List<String> filesUploadContentType = new ArrayList<String>();
    private List<String> filesUploadFileName = new ArrayList<String>();

    private List<File> filesUpload_2 = new ArrayList<File>();

    private List<String> file = new ArrayList<>();

    private List<SpareParts> spareParts = new ArrayList<>();
    private List<CommonKeyVal> costTypeList;
    private List<CommonKeyVal> failingAreaList;
    private List<CommonKeyVal> claimTypeList;

    private List<File> filesUploadDealer = new ArrayList<File>();


}
