<%--
  Created by IntelliJ IDEA.
  User: yasas
  Date: 1/25/2021
  Time: 4:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalViewCustomerSearch" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">View Customer</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <div class="modal-body">
                <div class="form-group"><span id="responseMsgView"></span></div>
                <div class="row">
                    <label class="col-sm-4">Customer Id</label>
                    <label class="col-sm-8" id="viewcustomerId">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Identification</label>
                    <label class="col-sm-8" id="viewidentification">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Primary Account Number</label>
                    <label class="col-sm-8" id="viewaccountNo">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Customer Category</label>
                    <label class="col-sm-8" id="viewcustomerCategory">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Customer Name</label>
                    <label class="col-sm-8" id="viewcustomerName">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">DOB</label>
                    <label class="col-sm-8" id="viewdob">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Mobile Number</label>
                    <label class="col-sm-8" id="viewmobileNo">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Status</label>
                    <label class="col-sm-8" id="viewstatus">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Waive-off Status</label>
                    <label class="col-sm-8" id="viewwaiveoffstatus">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Branch</label>
                    <label class="col-sm-8" id="viewbranch">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Account Type</label>
                    <label class="col-sm-8" id="viewaccountType">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Created Time</label>
                    <label class="col-sm-8" id="viewCreatedTime">: </label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Last Updated Time</label>
                    <label class="col-sm-8" id="viewLastUpdatedTime">: </label>
                </div>

                <!-- /.card-body -->
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>
</script>

