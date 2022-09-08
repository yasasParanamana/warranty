package com.oxcentra.warranty.mapping.warranty;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Component
@Scope("prototype")
public class Claim {
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
    private String claimType; //
    private Date purchasingDate;
    private String purchasingAttachmentId;//
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
    private BigDecimal totalCost;
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
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private String dealershipName;
    private String dealershipPhone;
    private String dealershipEmail;

    private List<SpareParts> sparePartList;
    private List<WarrantyAttachments> pdfFileList;
}
