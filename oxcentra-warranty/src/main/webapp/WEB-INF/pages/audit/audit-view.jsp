<%--
  Created by IntelliJ IDEA.
  User: dilanka_w
  Date: 1/25/2021
  Time: 4:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalViewAudit" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">View Audit Trace</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <div class="modal-body">
                <div class="form-group"><span id="responseMsgView"></span></div>
                <div class="row">
                    <label class="col-sm-4">AuditTrace Id</label>
                    <label class="col-sm-8" id="viewAuditId"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Section</label>
                    <label class="col-sm-8" id="viewSection"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Page</label>
                    <label class="col-sm-8" id="viewPage"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Task</label>
                    <label class="col-sm-8" id="viewTask"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">User Role</label>
                    <label class="col-sm-8" id="viewUserRole"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Description</label>
                    <label class="col-sm-8" id="viewDescription"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Remarks</label>
                    <label class="col-sm-8" id="viewRemarks"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">IP</label>
                    <label class="col-sm-8" id="viewIP"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Created Time</label>
                    <label class="col-sm-8" id="viewCreatedTime"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Last Updated Time</label>
                    <label class="col-sm-8" id="viewLastUpdatedTime"></label>
                </div>
                <div class="row">
                    <label class="col-sm-4">Last Updated User</label>
                    <label class="col-sm-8" id="viewLastUpdatedUser"></label>
                </div>
                <div>
                    <table id="fieldtable" class="table table-bordered table-hover">
                        <thead>
                        <tr>
                            <th onmouseover="this.style= 'cursor:pointer'">Field</th>
                            <th onmouseover="this.style= 'cursor:pointer'">Old Value</th>
                            <th onmouseover="this.style= 'cursor:pointer'">New Value</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
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

