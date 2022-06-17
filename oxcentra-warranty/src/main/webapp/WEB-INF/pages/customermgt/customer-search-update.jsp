<%--
  Created by IntelliJ IDEA.
  User: maheshi_c
  Date: 3/23/2021
  Time: 10:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateCustomerSearch" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateCustomerLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateCustomerLabel">Update Customer</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateCustomerForm" modelAttribute="cussearch" method="post"
                       name="updateCustomerForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>


                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Customer Id<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="customerid" name="customerid" type="text"
                                        class="form-control form-control-sm"
                                        id="editcustomerId" placeholder="Customer Id"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Identification<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="identification" name="identification" type="text"
                                        class="form-control form-control-sm"
                                        id="editidentification" placeholder="Identification"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Primary Account Number<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="accountno" name="accountno" type="text"
                                        class="form-control form-control-sm"
                                        id="editaccountNo" placeholder="Primary Account Number"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Customer Category<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="customerCategory" name="customerCategory" type="text"
                                        class="form-control form-control-sm"
                                        id="editcustomerCategory" placeholder="Customer Category"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Customer Name<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="customerName" name="customerName" type="text"
                                        class="form-control form-control-sm"
                                        id="editcustomerName" placeholder="Customer Name"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">DOB<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="dobSt" name="dobSt" type="text"
                                        class="form-control form-control-sm"
                                        id="editdob" placeholder="DOB"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Mobile Number<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="mobileno" name="mobileno" type="text"
                                        class="form-control form-control-sm"
                                        id="editmobileNo" placeholder="Mobile Number"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="editstatus"
                                         readonly="true">
                                <c:forEach items="${cussearch.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Waive-off Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="waiveoffstatus" name="waiveoffstatus" class="form-control form-control-sm" id="editwaiveoffstatus"
                                         readonly="true">
                                <form:option value="YES">YES</form:option>
                                <form:option value="NO">NO</form:option>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Branch<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="branch" name="branch" type="text"
                                        class="form-control form-control-sm"
                                        id="editbranch" placeholder="Branch"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Account Type<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="accountType" name="accountType" type="text"
                                        class="form-control form-control-sm"
                                        id="editaccountType" placeholder="Account Type"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Remark</label>
                        <div class="col-sm-8">
                            <form:input path="remark" name="remark" type="text"
                                        class="form-control form-control-sm"
                                        id="remark" placeholder="Remark"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>


                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${cussearch.vupdate}">
                        <button id="updateBtn" type="button" onclick="update()" class="btn btn-primary">
                            Submit
                        </button>
                    </c:if>
                </div>
            </form:form>
        </div>
    </div>
</div>
<script>
    function update() {
        resetUpdateCustomerForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateCustomer.json',
            data: $('form[name=updateCustomerForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                if (res.flag) {
                    //success
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


    function resetUpdate() {
        $.ajax({
            url: "${pageContext.request.contextPath}/getCustomerSearch.json",
            data: {
                customerId: $('#editcustomerId').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#editcustomerId').val(data.customerId);
                $('#editcustomerId').attr('readOnly', true);
                $('#editidentification').val(data.identification);
                $('#editidentification').attr('readOnly', true);
                $('#editaccountNo').val(data.accountNo);
                $('#editaccountNo').attr('readOnly', true);
                $('#editcustomerCategory').val(data.customerCategory);
                $('#editcustomerCategory').attr('readOnly', true);
                $('#editcustomerName').val(data.customerName);
                $('#editcustomerName').attr('readOnly', true);
                $('#editmobileNo').val(data.mobileNo);
                $('#editmobileNo').attr('readOnly', true);
                $('#editstatus').val(data.statuscode);
                $('#editwaiveoffstatus').val(data.waiveoffstatus);
                $('#editbranch').val(data.branch);
                $('#editbranch').attr('readOnly', true);
                $('#editaccountType').val(data.accountType);
                $('#editaccountType').attr('readOnly', true);
                $('#editdob').val(moment(data.dob).format("YYYY-MM-DD"));
                $('#editdob').attr('readOnly', true);
                $('#remark').val("");

            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateCustomerForm();
    }

    function resetUpdateCustomerForm() {
        $(".validation-err").remove();
        if ($('#responseMsgUpdate').hasClass('success-response')) {
            $('#responseMsgUpdate').removeClass('success-response');
        }
        if ($('#responseMsgUpdate').hasClass('error-response')) {
            $('#responseMsgUpdate').removeClass('error-response');
        }
        $('#responseMsgUpdate').hide();
    }
</script>
