<%--
  Created by IntelliJ IDEA.
  User: yasas paranamana
  Date: 03/08/2022
  Time: 6:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade " id="modalAddClaim" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAddClaimLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAddClaimLabel">Insert Claim</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="addClaimForm" modelAttribute="claim" method="post"
                       name="addClaimForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span>

                    </div>

                    <h5>Vehicle Details</h5>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="chassis">Chassis Number</label>

                            <form:input path="chassis" name="chassis" type="text"
                                        class="form-control form-control-sm" id="addchassisNumber" maxlength="50"
                                        placeholder="Chassis Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Model</label>
                            <form:input path="model" name="model" type="text"
                                        class="form-control form-control-sm" id="addmodel" maxlength="10"
                                        placeholder="Model"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <hr>
                    <h5>Customer Details</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="firstName">First Name</label>

                            <form:input path="firstName" name="firstName" type="text"
                                        class="form-control form-control-sm" id="addfirstName" maxlength="20"
                                        placeholder="First Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="lastName">Last Name</label>
                            <form:input path="lastName" name="lastName" type="text"
                                        class="form-control form-control-sm" id="addlastName" maxlength="30"
                                        placeholder="Last Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="phone">Phone Number</label>
                            <form:input path="phone" name="phone" type="text"
                                        class="form-control form-control-sm" id="addphone" maxlength="15"
                                        placeholder="Phone Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="email">Email</label>
                            <form:input path="email" name="email" type="text"
                                        class="form-control form-control-sm" id="addemail" maxlength="50"
                                        placeholder="Email"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="address">Address</label>

                            <form:input path="address" name="address" type="text"
                                        class="form-control form-control-sm" id="addaddress" maxlength="50"
                                        placeholder="Address"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="surburb">surburb</label>
                            <form:input path="surburb" name="surburb" type="text"
                                        class="form-control form-control-sm" id="addsurburb" maxlength="20"
                                        placeholder="Surburb"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="state">State</label>
                            <form:input path="state" name="state" type="text"
                                        class="form-control form-control-sm" id="addphone" maxlength="20"
                                        placeholder="State"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="postcode">Post Code</label>
                            <form:input path="postcode" name="postcode" type="text"
                                        class="form-control form-control-sm" id="addpostcode" maxlength="10"
                                        placeholder="Post Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <hr>
                    <h5>Dealership & Purchase Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="dealership">Dealership</label>

                            <form:input path="dealership" name="dealership" type="text"
                                        class="form-control form-control-sm" id="adddealership" maxlength="10"
                                        placeholder="Dealership"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>

                        <div class="col-lg-4">
                            <label>Purchasing Date:</label>
                            <div class="btn-group div-inline input-group input-group-sm input-append date">
                                <input path="purchasingDate" name="purchasingDate" id="addpurchasingDate"
                                       class="form-control" readonly="true" onkeydown="return false"
                                       autocomplete="off"/>
                            </div>
                            <span class="form-text text-muted">Please select purchasing date</span>
                        </div>

                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="description">Description</label>

                            <form:textarea path="description" name="description" type="text"
                                        class="form-control form-control-sm" id="adddescription" maxlength="50"
                                        placeholder="Description"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <hr>
                    <h5>Repair Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="failureType">Type Of Failure</label>

                            <form:input path="failureType" name="failureType" type="text"
                                        class="form-control form-control-sm" id="addfailureType" maxlength="10"
                                        placeholder="Failure Type"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="failureArea">Area Of Failure</label>
                            <form:input path="failureArea" name="failureArea" type="text"
                                        class="form-control form-control-sm" id="addfailureArea" maxlength="10"
                                        placeholder="Failure Area"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="repairType">Type Of Repair</label>
                            <form:input path="repairType" name="repairType" type="text"
                                        class="form-control form-control-sm" id="addrepairType" maxlength="10"
                                        placeholder="Repair Type"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-row">
                            <div class="form-group col-md-12">
                                <label for="repairDescription">Description Of Repair</label>

                                <form:textarea path="repairDescription" name="repairDescription" type="text"
                                               class="form-control form-control-sm" id="addrepairDescription" maxlength="50"
                                               placeholder="Description Of Repair"
                                               onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                            </div>
                    </div>

                    <hr>
                    <h5>Cost Of Estimation</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="costType">Type Of Cost</label>

                            <form:input path="costType" name="costType" type="text"
                                        class="form-control form-control-sm" id="addcostType" maxlength="50"
                                        placeholder="Type Of Cost"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="hours">Hours</label>
                            <form:input path="hours" name="hours" type="text"
                                        class="form-control form-control-sm" id="addhours" maxlength="50"
                                        placeholder="Hours"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="labourRate">Labour Rate</label>
                            <form:input path="labourRate" name="labourRate" type="text"
                                        class="form-control form-control-sm" id="addlabourRate" maxlength="50"
                                        placeholder="Labour Rate"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="totalCost">Total Cost</label>
                            <form:input path="totalCost" name="totalCost" type="text"
                                        class="form-control form-control-sm" id="addtotalCost" maxlength="10"
                                        placeholder="Total Cost"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="costDescription">Description</label>

                            <form:textarea path="costDescription" name="costDescription" type="text"
                                           class="form-control form-control-sm" id="addcostDescription" maxlength="50"
                                           placeholder="Description Of Cost"
                                           onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>


                    <br/>
                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                    <!-- /.card-body -->
                </div>
                <div class="modal-footer justify-content-end">
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${claim.vadd}">
                        <button id="addBtn" type="button" onclick="add()" class="btn btn-primary">
                            Submit
                        </button>
                    </c:if>
                </div>
            </form:form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>

    $(document).ready(function () {
        $('#addpurchasingDate').datepicker({
            format: 'yyyy-mm-dd',
            endDate: '+0d',
            setDate: new Date(),
            todayHighlight: true,
            forceParse: false
        });
        setPurchasingDate();
    });

    function setPurchasingDate() {
        var date = new Date();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        if (day < 10) {
            day = '0' + day;
        }
        if (month < 10) {
            month = '0' + month;
        }
        var today = (date.getFullYear() + "-" + month + "-" + day);
        $('#addpurchasingDate').val(today);
    }

    function resetAdd() {
        $('form[name=addClaimForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddClaimFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addWarrantyClaims.json',
            data: $('form[name=addClaimForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {

                if (res.flag) { //success
                    $('#responseMsgAdd').show();
                    $('#responseMsgAdd').addClass('success-response').text(res.successMessage);
                    $('form[name=addClaimForm]').trigger("reset");
                    searchStart();
                } else {
                    $('#responseMsgAdd').show();
                    $('#responseMsgAdd').addClass('error-response').text(res.errorMessage);
                }
            },
            error: function (jqXHR) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
    }

    function resetAddClaimFormData() {
        $(".validation-err").remove();

        if ($('#responseMsgAdd').hasClass('success-response')) {
            $('#responseMsgAdd').removeClass('success-response');
        }

        if ($('#responseMsgAdd').hasClass('error-response')) {
            $('#responseMsgAdd').removeClass('error-response');
        }

        $('#responseMsgAdd').hide();
    }

</script>
