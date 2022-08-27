<%--
  Created by IntelliJ IDEA.
  User: dilanka_w
  Date: 1/12/2021
  Time: 5:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateClaim" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateTaskLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalUpdateTaskLabel">Update Claim</h5>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updateClaimForm" modelAttribute="claim" method="post"
                       name="updateClaimForm">

                <div class="modal-body" id="firstTab">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>
                    <div class="form-group row" hidden="true">
                        <div class="col-sm-8">
                            <form:input path="id" name="id" type="text" id="editId" />
                        </div>
                    </div>
                    <h5>Vehicle Details</h5>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label  for="editChassisNumber" >Chassis Number</label>
                            <label  >:</label>
                            <label  id="editChassisNumber"></label>
                        </div>
                            <div class="form-group col-md-3">
                            <label  for="editModel">Model</label>
                            <label  >:</label>
                            <label  id="editModel"></label>
                        </div>
                    </div>

                    <hr>

                    <h5>Customer Details</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editFirstName">First Name</label>
                            <label  >:</label>
                            <label  id="editFirstName"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editLastName">Last Name</label>
                            <label  >:</label>
                            <label  id="editLastName"></label>
                       </div>
                        <div class="form-group col-md-3">
                            <label for="editPhone">Phone Number</label>
                            <label  >:</label>
                            <label  id="editPhone"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editEmail">Email</label>
                            <label  >:</label>
                            <label  id="editEmail"></label>
                        </div>
                    </div>

                    <hr>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editAddress">Address</label>
                            <label  >:</label>
                            <label  id="editAddress"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editSurburb">surburb</label>
                            <label  >:</label>
                            <label  id="editSurburb"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editState">State</label>
                            <label  >:</label>
                            <label  id="editState"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editPostcode">Post Code</label>
                            <label  >:</label>
                            <label  id="editPostcode"></label>
                       </div>
                    </div>

                    <hr>

                    <h5>Dealership & Purchase Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editDealership">Dealership</label>
                            <label  >:</label>
                            <label  id="editDealership"></label>
                       </div>

                        <div class="col-lg-4">
                            <label for="editPurchasingDate">Purchasing Date</label>
                            <label  >:</label>
                            <label  id="editPurchasingDate"></label>
                        </div>

                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="" id="isInHouse">
                            <label class="form-check-label" for="isInHouse">
                                Is In House
                            </label>
                        </div>

                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="editDescription">Description</label>
                            <label  >:</label>
                            <label  id="editDescription"></label>
                        </div>
                    </div>

                    <hr>
                    <h5>Repair Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editFailureType">Type Of Failure</label>
                            <label  >:</label>
                            <label  id="editFailureType"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editFailureArea">Area Of Failure</label>
                            <label  >:</label>
                            <label  id="editFailureArea"></label>
                       </div>
                        <div class="form-group col-md-3">
                            <label for="editRepairType">Type Of Repair</label>
                            <label  >:</label>
                            <label  id="editRepairType"></label>
                      </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="editRepairDescription">Description Of Repair</label>
                            <label  >:</label>
                            <label  id="editRepairDescription"></label>
                       </div>
                    </div>

                    <hr>
                    <h5>Cost Of Estimation</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="editCostType">Type Of Cost</label>
                            <label  >:</label>
                            <label  id="editCostType"></label>
                       </div>
                        <div class="form-group col-md-3">
                            <label for="editHours">Hours</label>
                            <label  >:</label>
                            <label  id="editHours"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editLabourRate">Labour Rate</label>
                            <label  >:</label>
                            <label  id="editLabourRate"></label>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="editTotalCost">Total Cost</label>
                            <label  >:</label>
                            <label  id="editTotalCost"></label>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="editCostDescription">Description</label>
                            <label  >:</label>
                            <label  id="editCostDescription"></label>
                       </div>
                    </div>


                    <br/>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                    <!-- /.card-body -->
                    <div class="modal-footer justify-content-end">
                        <c:if test="${claim.vupdate}">
                            <button id="approveBtn" type="button" onclick="approve()" class="btn btn-primary">
                                Approve
                            </button>
                        </c:if>
                        <c:if test="${claim.vupdate}">
                            <button id="rejectBtn" type="button" onclick="reject()" class="btn btn-primary">
                                Reject
                            </button>
                        </c:if>
                    </div>
                </div>

                <div class="modal-body" id="secondTab">

                    <h5>Contact Details</h5>
                    <div class="card-deck">
                        <div class="card">
                            <div class="card-body">
                                <div class="form-row">
                                    <label for="editContactDelarship">Delarship</label>
                                    <label  >:</label>
                                    <label  id="editContactDelarship"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editDealershipName">Respective Contact</label>
                                    <label  >:</label>
                                    <label  id="editDealershipName"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editDealershipPhone">Contact Number</label>
                                    <label  >:</label>
                                    <label  id="editDealershipPhone"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editDealershipEmail">Email Address</label>
                                    <label  >:</label>
                                    <label  id="editDealershipEmail"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editChassisNumebr">Chassis Numebr</label>
                                    <label  >:</label>
                                    <label  id="editChassisNumebr"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editCaravanModel">Caravan Model</label>
                                    <label  >:</label>
                                    <label  id="editCaravanModel"></label>
                                </div>

                            </div>
                        </div>
                        <div class="card">
                            <div class="card-body">
                                <div class="form-row">
                                    <label for="editCusFirstName">Customer Name</label>
                                    <label  >:</label>
                                    <label  id="editCusFirstName"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editCusContactNo">Contact Number</label>
                                    <label  >:</label>
                                    <label  id="editCusContactNo"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editCusEmail">Email Address</label>
                                    <label  >:</label>
                                    <label  id="editCusEmail"></label>
                                </div>
                                <div class="form-row">
                                    <label for="editCusAddress">Address</label>
                                    <label  >:</label>
                                    <label  id="editCusAddress"></label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <h5>Spare Part Required</h5>

                    <h5>Complete Supplier Details</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="firstName">Supplier Name</label>

                            <form:input path="firstName" name="firstName" type="text"
                                        class="form-control form-control-sm" id="addfirstName" maxlength="20"
                                        placeholder="First Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                        <div class="form-row">
                            <div class="form-group col-md-3">
                                <label for="editSupContactNumber">Supplier Contact Number</label>
                                <label  >:</label>
                                <label  id="editSupContactNumber"></label>
                            </div>
                            <div class="form-group col-md-3">
                                <label for="editSupEmail">Supplier Email</label>
                                <label  >:</label>
                                <label  id="editSupEmail"></label>
                            </div>
                            <div class="form-group col-md-3">
                                <label for="editSupAddress">Supplier Address</label>
                                <label  >:</label>
                                <label  id="editSupAddress"></label>
                            </div>
                        </div>

                    <div class="modal-footer justify-content-end">
                        <c:if test="${claim.vupdate}">
                            <button id="approveBtn" type="button" onclick="approve()" class="btn btn-primary">
                                Send E-mail to Supplier
                            </button>
                        </c:if>
                        <c:if test="${claim.vupdate}">
                            <button id="rejectBtn" type="button" onclick="reject()" class="btn btn-primary">
                                Cancel
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

    function approve() {

        var isInHouse=$("#isInHouse").is(":checked");
        alert(isInHouse);

        if(isInHouse === true){

            $('#secondTab').show();
            $('#firstTab').hide();

        }else{

            $('#secondTab').hide();
            $('#firstTab').show();
        }

        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/approveWarrantyClaims.json',
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

    function reject() {
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/rejectWarrantyClaims.json',
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

    $(function() {
        $('#firstTab').show();
        $('#secondTab').hide();
    });

</script>
