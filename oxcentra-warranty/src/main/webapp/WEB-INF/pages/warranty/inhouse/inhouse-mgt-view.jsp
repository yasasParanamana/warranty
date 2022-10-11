responseBean<%--
  Created by IntelliJ IDEA.
  User: yasas
  Date: 1/12/2021
  Time: 5:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateInHouseClaim" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateTaskLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalUpdateTaskLabel">In House Request</h5>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updateClaimForm" modelAttribute="inHouse" method="post"
                       name="updateClaimForm">
                <div class="form-group" style="text-align: center"><span style="alignment: center"
                                                                         id="responseMsgUpdate"></span></div>
                <div class="form-group row" hidden="true">
                    <div class="col-sm-8">
                        <form:input path="id" name="id" type="text" id="editId"/>
                    </div>
                </div>

                <div class="modal-body" id="firstTab">
                    <h5>Vehicle Details</h5>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editChassisNumber">Chassis Number</label>
                            <label>:</label>
                            <label id="editChassisNumber"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editModel">Model</label>
                            <label>:</label>
                            <label id="editModel"></label>
                        </div>
                    </div>

                    <hr>

                    <h5>Customer Details</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editFirstName">First Name</label>
                            <label>:</label>
                            <label id="editFirstName"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editLastName">Last Name</label>
                            <label>:</label>
                            <label id="editLastName"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editPhone">Phone Number</label>
                            <label>:</label>
                            <label id="editPhone"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editEmail">Email</label>
                            <label>:</label>
                            <label id="editEmail"></label>
                        </div>
                    </div>

                    <hr>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editAddress">Address</label>
                            <label>:</label>
                            <label id="editAddress"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editSurburb">Surburb</label>
                            <label>:</label>
                            <label id="editSurburb"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editState">State</label>
                            <label>:</label>
                            <label id="editState"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editPostcode">Post Code</label>
                            <label>:</label>
                            <label id="editPostcode"></label>
                        </div>
                    </div>

                    <hr>

                    <h5>Dealership & Purchase Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editDealership">Dealership</label>
                            <label>:</label>
                            <label id="editDealership"></label>
                        </div>

                        <div class="form-group col-md-3">
                            <label for="editClaimType">Claim Type</label>
                            <label>:</label>
                            <label id="editClaimType"></label>
                        </div>

                        <div class="form-group col-md-3">
                            <label for="editPurchasingDate">Purchasing Date</label>
                            <label>:</label>
                            <label id="editPurchasingDate"></label>
                        </div>

                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isInHouse" onclick="return false;">
                            <label class="form-check-label" for="isInHouse">
                                Is In House
                            </label>
                        </div>

                    </div>

                    <h5>Attachments</h5>
                    <div class="card">
                        <div class="card-body">
                            <div class="updateClaimFiletList" id="updateClaimFiletList"></div>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="editDescription">Description</label>
                            <label>:</label>
                            <label id="editDescription"></label>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-body">
                            <div class="sparePartList" id="viewSparePartList"></div>
                        </div>
                    </div>

                    <hr>
                    <h5>Repair Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editFailureType">Type of Failure</label>
                            <label>:</label>
                            <label id="editFailureType"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editFailureArea">Area of Failure</label>
                            <label>:</label>
                            <label id="editFailureArea"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editRepairType">Type of Repair</label>
                            <label>:</label>
                            <label id="editRepairType"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="failingArea">Failing Area<span
                                    class="text-danger">*</span></label>
                            <form:select path="failingArea" name="failingArea"
                                         class="form-control form-control-sm"
                                         id="editFailingArea" disabled="true">
                                <option selected value="">Select Failing Area</option>
                                <c:forEach items="${inHouse.failingAreaList}" var="failingArea">
                                    <form:option
                                            value="${failingArea.key}">${failingArea.value}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="editRepairDescription">Description of Repair</label>
                            <label>:</label>
                            <label id="editRepairDescription"></label>
                        </div>
                    </div>

                    <h5>Attachments</h5>
                    <div class="card">
                        <div class="card-body">
                            <div class="updateRepairFiletList" id="updateRepairFiletList"></div>
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
                                         id="editCostType"
                                         onchange="setOtherCostDeatilsEdit()" disabled="true">
                                <option selected value="">Select Type of Cost</option>
                                <c:forEach items="${inHouse.costTypeList}" var="costType">
                                    <form:option
                                            value="${costType.key}">${costType.value}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>

                        <div class="form-group col-md-3" id="editHoursDiv">
                            <label for="hours">Hours<span
                                    class="text-danger">*</span></label>
                            <form:input path="hours" name="hours" type="text" readonly="true"
                                        class="form-control form-control-sm" id="editHours" maxlength="4"
                                        placeholder="Hours"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9.]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3" id="editLabourRateDiv">
                            <label for="labourRate">Labour Rate<span
                                    class="text-danger">*</span></label>
                            <form:input path="labourRate" name="labourRate" type="text" readonly="true"
                                        class="form-control form-control-sm" id="editLabourRate" maxlength="5"
                                        placeholder="Labour Rate"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9 .]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3" id="editTotalCostDiv">
                            <label for="totalCost">Total Cost<span
                                    class="text-danger">*</span></label>
                            <form:input path="totalCost" name="totalCost" type="text" readonly="true"
                                        class="form-control form-control-sm" id="editTotalCost" maxlength="10"
                                        placeholder="Total Cost"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^0-9 .]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-9">
                            <label for="costDescription">Description<span
                                    class="text-danger">*</span></label>

                            <form:textarea path="costDescription" name="costDescription" type="text"
                                           class="form-control form-control-sm" id="editCostDescription" maxlength="256"
                                           placeholder="Description Of Cost" readonly="true"
                                           onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"
                                           data-toggle="tooltip" data-placement="top" data-html="true"
                                           title="<b>Input the reason for final cost<b> "
                            />
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="supplierTrackingNum">Tracking Number<span
                                    class="text-danger"></span></label>
                            <form:input path="supplierTrackingNum" name="supplierTrackingNum" type="text" readonly="false"
                                        class="form-control form-control-sm" id="editSupplierTrackingNum" maxlength="50"
                                        placeholder="Tracking Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <br/>
                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                    <!-- /.card-body -->
                    <div class="modal-footer justify-content-end">
                        <c:if test="${inHouse.vupdate}">
                            <button id="updateBtn" type="button" onclick="updateTrack()" class="btn btn-primary">
                                Update
                            </button>
                        </c:if>
                    </div>
                </div>
            </form:form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>

    function downloadFile(contentType) {

        const arr = contentType.split(',');

        contentType = arr[0];
        let base64Data = arr[1];
        let fileName = arr[2];

        let a = document.createElement("a");
        a.href = "data:" + contentType + ";base64," + base64Data;
        a.download = fileName;
        a.click();
    }

    function updateTrack(){

        $('#responseMsgUpdate').empty();

        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateInHouse.json',
            data: $('form[name=updateClaimForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                if (res.flag) { //success
                    $('#responseMsgUpdate').show();
                    $('#responseMsgUpdate').addClass('success-response').text(res.successMessage);
                    searchStart();
                } else {
                    $('#responseMsgUpdate').show();
                    $('#responseMsgUpdate').addClass('error-response').text(res.errorMessage);
                }
            },
            error: function (jqXHR) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });

    }

</script>
