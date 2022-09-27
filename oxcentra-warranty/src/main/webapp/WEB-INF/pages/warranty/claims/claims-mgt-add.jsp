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

<link rel="stylesheet" type="text/css"
      href="${pageContext.request.contextPath}/resources/css/warrenty/emailcss.css?${initParam['version']}"/>

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
                       name="addClaimForm" enctype="multipart/form-data">

                <div class="modal-body">
                    <div class="form-group" style="text-align: center"><span id="responseMsgAdd"></span>

                    </div>
                    <h5>Vehicle Details</h5>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="chassis">Chassis Number<span
                                    class="text-danger">*</span></label>

                            <form:input path="chassis" name="chassis" type="text"
                                        class="form-control form-control-sm" id="addchassisNumber" maxlength="50"
                                        placeholder="Chassis Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Model<span
                                    class="text-danger">*</span></label>
                            <form:select path="model" name="model"
                                         class="form-control form-control-sm" id="addmodel">
                                <option selected value="">Select Model</option>
                                <c:forEach items="${claim.modelActList}" var="modelType">
                                    <form:option
                                            value="${modelType.id}">${modelType.model}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <hr>
                    <h5>Customer Details</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="firstName">First Name<span
                                    class="text-danger">*</span></label>

                            <form:input path="firstName" name="firstName" type="text"
                                        class="form-control form-control-sm" id="addfirstName" maxlength="20"
                                        placeholder="First Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z ]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="lastName">Last Name<span
                                    class="text-danger">*</span></label>
                            <form:input path="lastName" name="lastName" type="text"
                                        class="form-control form-control-sm" id="addlastName" maxlength="30"
                                        placeholder="Last Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z ]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="phone">Phone Number<span
                                    class="text-danger">*</span></label>
                            <form:input path="phone" name="phone" type="text"
                                        class="form-control form-control-sm" id="addphone" maxlength="15"
                                        placeholder="Phone Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9() ]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="email">Email<span
                                    class="text-danger">*</span></label>
                            <form:input path="email" name="email" type="text"
                                        class="form-control form-control-sm" id="addemail" maxlength="50"
                                        placeholder="Email"
                                        onkeyup="$(this).val($(this).val().replace(/[^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="address">Address<span
                                    class="text-danger">*</span></label>

                            <form:input path="address" name="address" type="text"
                                        class="form-control form-control-sm" id="addaddress" maxlength="50"
                                        placeholder="Address"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="surburb">surburb<span
                                    class="text-danger">*</span></label>
                            <form:input path="surburb" name="surburb" type="text"
                                        class="form-control form-control-sm" id="addsurburb" maxlength="20"
                                        placeholder="Surburb"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="state">State<span
                                    class="text-danger">*</span></label>
                            <form:select path="state" name="state"
                                         class="form-control form-control-sm" id="addstate">
                                <option selected value="">Select State</option>
                                <c:forEach items="${claim.stateActList}" var="stateType">
                                    <form:option
                                            value="${stateType.state_id}">${stateType.state_name}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="postcode">Post Code<span
                                    class="text-danger">*</span></label>
                            <form:input path="postcode" name="postcode" type="text"
                                        class="form-control form-control-sm" id="addpostcode" maxlength="10"
                                        placeholder="Post Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                    </div>

                    <hr>
                    <h5>Dealership & Purchase Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="dealership">Dealership<span
                                    class="text-danger">*</span></label>

                            <form:input path="dealership" name="dealership" type="text"
                                        class="form-control form-control-sm" id="adddealership" maxlength="10"
                                        placeholder="Dealership" readonly="true" value="${claim.dealership}"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>

                        <div class="form-group col-md-3">
                            <label for="claimType">Claim Type<span
                                    class="text-danger">*</span></label>
                            <form:select path="claimType" name="claimType"
                                         class="form-control form-control-sm"
                                         id="claimType"
                                         onchange="setPurchaseDateDiv()" >
                                <option selected value="">Select Claim Type</option>
                                <c:forEach items="${claim.claimTypeList}" var="claimType">
                                    <form:option
                                            value="${claimType.key}">${claimType.value}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>

                        <div class="col-lg-3" id="purchasingDateDiv">
                            <label>Purchasing Date:<span
                                    class="text-danger">*</span></label>
                            <div class="btn-group div-inline input-group input-group-sm input-append date">
                                <input path="purchasingDate" name="purchasingDate" id="addpurchasingDate"
                                       class="form-control" readonly="true" onkeydown="return false"
                                       autocomplete="off"/>
                            </div>
                            <span class="form-text text-muted">Please select purchasing date</span>
                        </div>

                    </div>

                    <div id="claimTypeDocDiv">
                    <h5>Attachments</h5>
                    <div class="card">
                        <div class="card-body">
                            <div class="multiple-file_2" id="multiple_file_div_2">
                                <div class="fileuploadBtn_2" id="file_hide_1_2">
                                    <label id="filePin_2">
                                        <span class="glyphicon glyphicon-paperclip"></span>
                                        <form:input path="filesUpload_2" type="file" name="filesUpload_2"
                                                    multiple="multiple" id="file-upload-1_2" accept="jpg"
                                                    onchange="getNewFileInput_2(this)"/>
                                    </label>
                                </div>
                            </div>

                            <div style="display: none" id="file-upload-hide_2"></div>
                            <button id="Attachment_Reset_f3_2" type="button" class="btn btn-default"
                                    onclick="resetReplyDataAttach_2()" cssClass="sendbtn_2"
                                    cssStyle="position: absolute;margin-top: -50px;margin-left: 285px;">Attachment Reset
                            </button>
                            <div class="uploadFileNameList_2"></div>
                        </div>
                    </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-9">
                            <label for="description">Description<span
                                    class="text-danger">*</span></label>

                            <form:textarea path="description" name="description" type="text"
                                           class="form-control form-control-sm" id="adddescription" maxlength="256"
                                           onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>

                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="sparePartRequired1">Spare Part Required</label>

                            <form:input path="sparePartRequired1" name="sparePartRequired1" type="text"
                                        class="form-control form-control-sm" id="addsparePartRequired1" maxlength="20"
                                        placeholder="Spare Part Required"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                        <div class="form-group col-md-1">
                            <label for="quantity1">Quantity</label>

                            <form:input path="quantity1" name="quantity1" type="text"
                                        class="form-control form-control-sm" id="addquantity1" maxlength="3"
                                        placeholder="Quantity"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="sparePartRequired2">Spare Part Required</label>

                            <form:input path="sparePartRequired2" name="sparePartRequired2" type="text"
                                        class="form-control form-control-sm" id="addsparePartRequired2" maxlength="20"
                                        placeholder="Spare Part Required"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                        <div class="form-group col-md-1">
                            <label for="quantity2">Quantity</label>

                            <form:input path="quantity2" name="quantity2" type="text"
                                        class="form-control form-control-sm" id="addquantity2" maxlength="3"
                                        placeholder="Quantity"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9]/g,''))"/>
                        </div>
                    </div>
                        <%--Auto Increment--%>
                    <div id="newSparePart">

                    </div>
                    <div class="form-row">
                        <button id="addNewSparePart" type="button" class="btn btn-default" cssClass="sendbtn"
                                cssStyle="position: absolute;margin-top: -50px;margin-left: 285px;">Click Here to Add
                            New Spare Part
                        </button>
                    </div>

                    <hr>
                    <h5>Repair Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="failureType">Type of Failure<span
                                    class="text-danger">*</span></label>
                            <form:select path="failureType" name="failureType"
                                         class="form-control form-control-sm" id="addfailureType">
                                <option selected value="">Select Failure Type</option>
                                <c:forEach items="${claim.failureTypeActList}" var="failureType">
                                    <form:option
                                            value="${failureType.code}">${failureType.description}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>

                        <div class="form-group col-md-3">
                            <label for="failureArea">Area of Failure<span
                                    class="text-danger">*</span></label>
                            <form:select path="failureArea" name="failureArea"
                                         class="form-control form-control-sm" id="addfailureArea">
                                <option selected value="">Select Failure Area</option>
                                <c:forEach items="${claim.failureAreaActList}" var="failureArea">
                                    <form:option
                                            value="${failureArea.code}">${failureArea.description}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>

                        <div class="form-group col-md-3">
                            <label for="repairType">Type of Repair<span
                                    class="text-danger">*</span></label>
                            <form:select path="repairType" name="repairType"
                                         class="form-control form-control-sm" id="addrepairType">
                                <option selected value="">Select Type of Repair</option>
                                <c:forEach items="${claim.repairTypeActList}" var="repairType">
                                    <form:option
                                            value="${repairType.code}">${repairType.description}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-9">
                            <label for="repairDescription">Description of Repair<span
                                    class="text-danger">*</span></label>

                            <form:textarea path="repairDescription" name="repairDescription" type="text"
                                           class="form-control form-control-sm" id="addrepairDescription" maxlength="256"
                                           placeholder="Description Of Repair"
                                           onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9 -]/g,''))"/>
                        </div>
                    </div>
                    <h5>Attachments</h5>
                    <div class="card">
                        <div class="card-body">
                            <div class="form-row">
                            <div class="multiple-file" id="multiple_file_div">
                                <div class="fileuploadBtn" id="file_hide_1">
                                    <label id="filePin">
                                        <span class="glyphicon glyphicon-paperclip"></span>
                                        <form:input path="filesUpload" type="file" name="filesUpload"
                                                    multiple="multiple" id="file-upload-1" accept="jpg"
                                                    onchange="getNewFileInput(this)"/>
                                    </label>
                                </div>
                            </div>
                            <div style="display: none" id="file-upload-hide"></div>
                            <button id="Attachment_Reset_f3" type="button" class="btn btn-default"
                                    onclick="resetReplyDataAttach()" cssClass="sendbtn"
                                    cssStyle="position: absolute;margin-top: -50px;margin-left: 285px;">Attachment Reset
                            </button>
                        </div>
                            <div class="uploadFileNameList"></div>
                        </div>
                    </div>

                    <hr>
                    <h5>Cost Of Estimation</h5>

                    <div class="form-row">

                        <div class="form-group col-md-3">
                            <label for="costType">Type of Cost<span
                                    class="text-danger">*</span></label>
                            <form:select path="costType" name="costType"
                                         class="form-control form-control-sm"
                                         id="addCostType"
                                         onchange="setOtherCostDeatils()" >
                                <option selected value="">Select Type of Cost</option>
                                <c:forEach items="${claim.costTypeList}" var="costType">
                                    <form:option
                                            value="${costType.key}">${costType.value}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>

                        <div class="form-group col-md-3" id="addHoursDiv">
                            <label for="hours">Hours<span
                                    class="text-danger">*</span></label>
                            <form:input path="hours" name="hours" type="text"
                                        class="form-control form-control-sm" id="addhours" maxlength="4"
                                        placeholder="Hours"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9.]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3" id="addLabourRateDiv">
                            <label for="labourRate">Labour Rate<span
                                    class="text-danger">*</span></label>
                            <form:input path="labourRate" name="labourRate" type="text"
                                        class="form-control form-control-sm" id="addlabourRate" maxlength="5"
                                        placeholder="Labour Rate"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9 .]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3" id="addTotalCostDiv">
                            <label for="totalCost">Total Cost<span
                                    class="text-danger">*</span></label>
                            <form:input path="totalCost" name="totalCost" type="text"
                                        class="form-control form-control-sm" id="addtotalCost" maxlength="10"
                                        placeholder="Total Cost"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9 .]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-9">
                            <label for="costDescription">Description<span
                                    class="text-danger">*</span></label>

                            <form:textarea path="costDescription" name="costDescription" type="text"
                                           class="form-control form-control-sm" id="addcostDescription" maxlength="256"
                                           placeholder="Description Of Cost"
                                           onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"
                                           data-toggle="tooltip" data-placement="top" data-html="true" title="<b>Input the reason for final cost<b> "
                            />
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
        $('#addHoursDiv').hide();
        $('#addLabourRateDiv').hide();
        $('#addTotalCostDiv').hide();

        $('#purchasingDateDiv').hide();
        $('#claimTypeDocDiv').hide();

        resetReplyDataAttach();
        resetReplyDataAttach_2();

    });

    function setOtherCostDeatils(){

        const costType = $('#addCostType').val();

        if(costType === 'LABOUR'){

            $('#addHoursDiv').show();
            $('#addLabourRateDiv').show();
            $('#addTotalCostDiv').show();

        }else if (costType === 'MATERIALS' ){
            $('#addHoursDiv').hide();
            $('#addLabourRateDiv').hide();
            $('#addTotalCostDiv').show();

        }else if(costType === 'SUBLET'){

            $('#addHoursDiv').hide();
            $('#addLabourRateDiv').hide();
            $('#addTotalCostDiv').show();
        }else if(costType === ''){

            $('#addHoursDiv').hide();
            $('#addLabourRateDiv').hide();
            $('#addTotalCostDiv').hide();
        }
    }

    $(document).ready(function () {
        let max_fields = 10;
        let wrapper = $("#newSparePart");
        let add_button = $("#addNewSparePart");

        let x = 2;

        $(add_button).click(function (e) {
            e.preventDefault();
            if (x < max_fields) {
                x++;
                $(wrapper).append('<div class="form-row" ><div class="form-group col-md-6"> <label>Spare Part Required</label><input type="text" class="form-control form-control-sm" maxlength="20" name="sparePartRequired'+x+'"  placeholder="Spare Part Required"/> </div> <div class="form-group col-md-1"><label>Quantity</label><input type="text" name="quantity'+x+'" class="form-control form-control-sm" maxlength="3" placeholder="Quantity"/></div><a href="#" class="delete">Delete</a></div>');
            } else {
                alert('You Reached the limits')
            }
        });

        $(wrapper).on("click", ".delete", function (e) {
            e.preventDefault();
            $(this).parent('div').remove();
            x--;
        })
    });

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
        let today = (date.getFullYear() + "-" + month + "-" + day);
        $('#addpurchasingDate').val(today);
    }

    function resetAdd() {
        $('form[name=addClaimForm]').trigger("reset");
        $('#responseMsgAdd').hide();

        $('.uploadFileNameList').empty();
        $('.uploadFileNameList_2').empty();
        resetReplyDataAttach();
        resetReplyDataAttach_2();
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
                    resetReplyDataAttach_2()
                    resetReplyDataAttach();
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

    let file_Location_count = 1;

    function getNewFileInput(fileUpload) {
        let file = fileUpload.files;

        if (file.length > 0) {

            /* $('.uploadFileNameList').empty();
            $('.uploadFileNameList').show();*/

            $('#file-upload-hide').empty();

            for (let i = 0; i < file.length; i++) {

                Base64Convert(file[i]);

                $('.uploadFileNameList').append('<a class="ufileName" style="text-decoration: none;width: auto;padding: 2px 7px 1px 7px;float: left;margin: 0 3px;border-radius: 15px;cursor: pointer;background: #607D8B;color: white;border: 0px solid white;" href="' + URL.createObjectURL(file[i]) + '" download="' + file[i].name + '" ><span class="glyphicon glyphicon-paperclip"></span>' + file[i].name + '</div>');
            }
        }
        $('#file_hide_' + file_Location_count).hide();

        /*increment file location*/
        file_Location_count++;

        $('#multiple_file_div').append("<div class='fileuploadBtn' id='file_hide_" + file_Location_count + "'>"
            + "<label id='filePin'>"
            + "<span class='glyphicon glyphicon-paperclip'></span>"
            + "<input type='file' name='filesUpload'  accept='jpg' id='file-upload-" + file_Location_count + "' multiple='multiple' onchange='getNewFileInput(this)' >"
            + "</label>"
            + "</div>");
    }

    $(document).ready(function () {

        $('#file-upload').change(function () {
            let file = $('#file-upload')[0].files;
            if (file.length > 0) {
                $('.uploadFileNameList').empty();
                for (let i = 0; i < file.length; i++) {
                    $('.uploadFileNameList').append('<div class="ufileName"><span class="glyphicon glyphicon-paperclip"></span>' + file[i].name + '</div>');
                }
            }
        });
    });

    function resetReplyDataAttach() {
        file_Location_count = 1;
        $('#multiple_file_div').html("");
        $('#multiple_file_div').append("<div class='fileuploadBtn' id='file_hide_" + file_Location_count + "'>"
            + "<label id='filePin'>"
            + "<span class='glyphicon glyphicon-paperclip'></span>"
            + "<input type='file' name='filesUpload'  accept='jpg' id='file-upload-" + file_Location_count + "' multiple='multiple' onchange='getNewFileInput(this)' >"
            + "</label>"
            + "</div>");
        $('.uploadFileNameList').empty();
        $("#messageError").empty();
    }

    function Base64Convert(file) {

        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = function () {

            /*base64encoded string*/

            const readerResult = reader.result;
            const resultSplit = readerResult.substring(readerResult.indexOf(',') + 1);
            const fileDetails = resultSplit + "FileDetails" + file.name + "|" + file.size + "|" + file.type+"|REPAIR";

            $('#file-upload-hide').append('<input type="hidden" name="file" value="' + fileDetails + '"/>')

        };
        reader.onerror = function (error) {
            console.log('Error: ', error);
        };
    };

    function setPurchaseDateDiv(){

        let failingArea = $('#claimType').val();

        if(failingArea === 'STOCK_VAN' || failingArea === ''){
            $('#purchasingDateDiv').hide();
            $('#claimTypeDocDiv').hide();
        }else{
            $('#purchasingDateDiv').show();
            $('#claimTypeDocDiv').show();
        }
    }

   /* Purchasing data related file upload*/

    let file_Location_count_2 = 1;

    function getNewFileInput_2(fileUpload) {
        let file = fileUpload.files;

        if (file.length > 0) {


            $('#file-upload-hide_2').empty();

            for (let i = 0; i < file.length; i++) {

                Base64Convert_2(file[i]);

                $('.uploadFileNameList_2').append('<a class="ufileName" style="text-decoration: none;width: auto;padding: 2px 7px 1px 7px;float: left;margin: 0 3px;border-radius: 15px;cursor: pointer;background: #607D8B;color: white;border: 0px solid white;" href="' + URL.createObjectURL(file[i]) + '" download="' + file[i].name + '" ><span class="glyphicon glyphicon-paperclip"></span>' + file[i].name + '</div>');
            }
        }
        $('#file_hide__2' + file_Location_count_2).hide();

        /*increment file location*/
        file_Location_count_2++;

        $('#multiple_file_div_2').append("<div class='fileuploadBtn' id='file_hide__2" + file_Location_count_2 + "'>"
            + "<label id='filePin_2'>"
            + "<span class='glyphicon glyphicon-paperclip'></span>"
            + "<input type='file' name='filesUpload_2'  accept='jpg' id='file-upload-_2" + file_Location_count_2 + "' multiple='multiple' onchange='getNewFileInput_2(this)' >"
            + "</label>"
            + "</div>");
    }

    $(document).ready(function () {

        $('#file-upload_2').change(function () {
            let file = $('#file-upload_2')[0].files;
            if (file.length > 0) {
                $('.uploadFileNameList_2').empty();
                for (let i = 0; i < file.length; i++) {
                    $('.uploadFileNameList_2').append('<div class="ufileName"><span class="glyphicon glyphicon-paperclip"></span>' + file[i].name + '</div>');
                }
            }
        });
    });

    function resetReplyDataAttach_2() {
        file_Location_count_2 = 1;
        $('#multiple_file_div_2').html("");
        $('#multiple_file_div_2').append("<div class='fileuploadBtn' id='file_hide__2" + file_Location_count_2+ "'>"
            + "<label id='filePin_2'>"
            + "<span class='glyphicon glyphicon-paperclip'></span>"
            + "<input type='file' name='filesUpload_2'  accept='jpg' id='file-upload-_2" + file_Location_count_2 + "' multiple='multiple' onchange='getNewFileInput_2(this)' >"
            + "</label>"
            + "</div>");
        $('.uploadFileNameList_2').empty();
        $("#messageError").empty();
    }

    function Base64Convert_2(file) {

        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = function () {

            /*base64encoded string*/

            const readerResult = reader.result;
            const resultSplit = readerResult.substring(readerResult.indexOf(',') + 1);
            const fileDetails = resultSplit + "FileDetails" + file.name + "|" + file.size + "|" + file.type+"|DEALER";

            $('#file-upload-hide_2').append('<input type="hidden" name="file" value="' + fileDetails + '"/>')

        };
        reader.onerror = function (error) {
            console.log('Error: ', error);
        };
    };

</script>
<style>
    hr {
        border: 1px solid #b9bbbe;
    }


</style>