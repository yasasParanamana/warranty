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

                <div class="modal-body">
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
                </div>
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
            </form:form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>

    function approve() {
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

</script>
