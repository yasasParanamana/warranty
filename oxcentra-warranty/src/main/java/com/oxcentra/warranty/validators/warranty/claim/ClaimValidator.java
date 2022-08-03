package com.oxcentra.warranty.validators.warranty.claim;

import com.oxcentra.warranty.bean.usermgt.task.TaskInputBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.util.validation.Validation;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class ClaimValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return ClaimInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(ClaimInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (ClaimInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();


                    if (fieldName.equals("chassis")) {
                        //validate the null and empty in chassis
                        String chassis = ((ClaimInputBean) o).getChassis();
                        if (validation.isEmptyFieldValue(chassis)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_CHASSIS, MessageVarList.CLAIM_MGT_EMPTY_CHASSIS);
                        }
                    } else if (fieldName.equals("model")) {
                        //validate the null and empty in model
                        String model = ((ClaimInputBean) o).getModel();
                        if (validation.isEmptyFieldValue(model)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_MODEL, MessageVarList.CLAIM_MGT_EMPTY_MODEL);
                        }
                    } else if (fieldName.equals("firstName")) {
                        //validate the null and empty in firstName
                        String firstName = ((ClaimInputBean) o).getFirstName();
                        if (validation.isEmptyFieldValue(firstName)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_FIRSTNAME, MessageVarList.CLAIM_MGT_EMPTY_FIRSTNAME);
                        }
                    } else if (fieldName.equals("lastName")) {
                        //validate the null and empty in lastName
                        String lastName = ((ClaimInputBean) o).getLastName();
                        if (validation.isEmptyFieldValue(lastName)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_LASTNAME, MessageVarList.CLAIM_MGT_EMPTY_LASTNAME);
                        }
                    } else if (fieldName.equals("phone")) {
                        //validate the null and empty in phone
                        String phone = ((ClaimInputBean) o).getPhone();
                        if (validation.isEmptyFieldValue(phone)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_PHONE, MessageVarList.CLAIM_MGT_EMPTY_PHONE);
                        }
                    } else if (fieldName.equals("email")) {
                        //validate the null and empty in email
                        String email = ((ClaimInputBean) o).getEmail();
                        if (validation.isEmptyFieldValue(email)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_EMAIL, MessageVarList.CLAIM_MGT_EMPTY_EMAIL);
                        }
                    } else if (fieldName.equals("address")) {
                        //validate the null and empty in address
                        String address = ((ClaimInputBean) o).getAddress();
                        if (validation.isEmptyFieldValue(address)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_ADDRESS, MessageVarList.CLAIM_MGT_EMPTY_ADDRESS);
                        }
                    } else if (fieldName.equals("surburb")) {
                        //validate the null and empty in surburb
                        String surburb = ((ClaimInputBean) o).getSurburb();
                        if (validation.isEmptyFieldValue(surburb)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_SURBURB, MessageVarList.CLAIM_MGT_EMPTY_SURBURB);
                        }
                    } else if (fieldName.equals("state")) {
                        //validate the null and empty in state
                        String state = ((ClaimInputBean) o).getState();
                        if (validation.isEmptyFieldValue(state)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_STATE, MessageVarList.CLAIM_MGT_EMPTY_STATE);
                        }
                    } else if (fieldName.equals("postcode")) {
                        //validate the null and empty in postcode
                        String postcode = ((ClaimInputBean) o).getPostcode();
                        if (validation.isEmptyFieldValue(postcode)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_POSTCODE, MessageVarList.CLAIM_MGT_EMPTY_POSTCODE);
                        }
                    } else if (fieldName.equals("dealership")) {
                        //validate the null and empty in dealership
                        String dealership = ((ClaimInputBean) o).getDealership();
                        if (validation.isEmptyFieldValue(dealership)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_DEALERSHIP, MessageVarList.CLAIM_MGT_EMPTY_DEALERSHIP);
                        }
                    } else if (fieldName.equals("purchasingDate")) {
                        //validate the null and empty in purchasingDate
                        String purchasingDate = ((ClaimInputBean) o).getPurchasingDate();
                        if (validation.isEmptyFieldValue(purchasingDate)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_PURCHASING_DATE, MessageVarList.CLAIM_MGT_EMPTY_PURCHASING_DATE);
                        }
                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((ClaimInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_DESCRIPTION, MessageVarList.CLAIM_MGT_EMPTY_DESCRIPTION);
                        }
                    } else if (fieldName.equals("failureType")) {
                        //validate the null and empty in failureType
                        String failureType = ((ClaimInputBean) o).getFailureType();
                        if (validation.isEmptyFieldValue(failureType)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_FAILURE_TYPE, MessageVarList.CLAIM_MGT_EMPTY_FAILURE_TYPE);
                        }
                    } else if (fieldName.equals("failureArea")) {
                        //validate the null and empty in failureArea
                        String failureArea = ((ClaimInputBean) o).getFailureArea();
                        if (validation.isEmptyFieldValue(failureArea)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_FAILURE_AREA, MessageVarList.CLAIM_MGT_EMPTY_FAILURE_AREA);
                        }
                    } else if (fieldName.equals("repairType")) {
                        //validate the null and empty in repairType
                        String repairType = ((ClaimInputBean) o).getRepairType();
                        if (validation.isEmptyFieldValue(repairType)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_REPAIR_TYPE, MessageVarList.CLAIM_MGT_EMPTY_REPAIR_TYPE);
                        }
                    } else if (fieldName.equals("repairDescription")) {
                        //validate the null and empty in repairDescription
                        String repairDescription = ((ClaimInputBean) o).getRepairDescription();
                        if (validation.isEmptyFieldValue(repairDescription)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_REPAIR_DESCRIPTION, MessageVarList.CLAIM_MGT_EMPTY_REPAIR_DESCRIPTION);
                        }
                    } else if (fieldName.equals("costType")) {
                        //validate the null and empty in costType
                        String costType = ((ClaimInputBean) o).getCostType();
                        if (validation.isEmptyFieldValue(costType)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_COST_TYPE, MessageVarList.CLAIM_MGT_EMPTY_COST_TYPE);
                        }
                    } else if (fieldName.equals("hours")) {
                        //validate the null and empty in hours
                        String hours = ((ClaimInputBean) o).getHours();
                        if (validation.isEmptyFieldValue(hours)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_HOURS, MessageVarList.CLAIM_MGT_EMPTY_HOURS);
                        }
                    } else if (fieldName.equals("labourRate")) {
                        //validate the null and empty in labourRate
                        String labourRate = ((ClaimInputBean) o).getLabourRate();
                        if (validation.isEmptyFieldValue(labourRate)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_LABOUR_RATE, MessageVarList.CLAIM_MGT_EMPTY_LABOUR_RATE);
                        }
                    } else if (fieldName.equals("totalCost")) {
                        //validate the null and empty in totalCost
                        String totalCost = ((ClaimInputBean) o).getTotalCost();
                        if (validation.isEmptyFieldValue(totalCost)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_TOTAL_COST, MessageVarList.CLAIM_MGT_EMPTY_TOTAL_COST);
                        }

                    } else if (fieldName.equals("costDescription")) {
                        //validate the null and empty in costDescription
                        String costDescription = ((ClaimInputBean) o).getCostDescription();
                        if (validation.isEmptyFieldValue(costDescription)) {
                            errors.rejectValue(fieldName, MessageVarList.CLAIM_MGT_EMPTY_COST_DESCRIPTION, MessageVarList.CLAIM_MGT_EMPTY_COST_DESCRIPTION);
                        }
                    }
                }
            } else {
                errors.reject(commonVarList.COMMON_VALIDATION_INVALID_BEANTYPE);
            }
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            errors.reject(commonVarList.COMMON_VALIDATION_FAIL_CODE);
        }
    }

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, ClaimInputBean o) {
        return new Field[]{allFields.get("id"), allFields.get("description"), allFields.get("status")};
    }
}
