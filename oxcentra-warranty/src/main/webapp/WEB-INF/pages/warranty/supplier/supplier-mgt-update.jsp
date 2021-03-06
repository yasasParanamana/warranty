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

<div class="modal fade" id="modalUpdateSupplier" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateSupplierLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateSupplierLabel">Update supplier</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateSupplierForm" modelAttribute="supplier" method="post"
                       name="updateSupplierForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Supplier Code<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="supplierCode" name="supplierCode" type="text"
                                        class="form-control form-control-sm"
                                        id="eSupplierCode" maxlength="15" placeholder="Supplier Code" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Supplier Name<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="supplierName" name="supplierName" type="text"
                                        class="form-control form-control-sm"
                                        id="eSupplierName" maxlength="50" placeholder="Supplier Name" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Supplier Phone<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="supplierPhone" name="supplierPhone" type="text"
                                        class="form-control form-control-sm"
                                        id="eSupplierPhone" maxlength="50" placeholder="Supplier Phone" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Supplier Email<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="supplierEmail" name="supplierEmail" type="text"
                                        class="form-control form-control-sm"
                                        id="eSupplierEmail" maxlength="50" placeholder="Supplier Email" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Supplier Address<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="supplierAddress" name="supplierAddress" type="text"
                                        class="form-control form-control-sm"
                                        id="eSupplierAddress" maxlength="50" placeholder="Supplier Address" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${supplier.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>


                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>


                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${supplier.vupdate}">
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
        resetUpdateSupplierForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateSupplier.json',
            data: $('form[name=updateSupplierForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getSupplier.json",
            data: {
                supplierCode: $('#eSupplierCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eSupplierCode').val(data.supplierCode);
                $('#eSupplierCode').attr('readOnly', true);

                $('#eSupplierName').val(data.supplierName);
                $('#eSupplierPhone').val(data.supplierPhone);
                $('#eSupplierEmail').val(data.supplierEmail);
                $('#eSupplierAddress').val(data.supplierAddress);
                $('#eStatus').val(data.status);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateSupplierForm();
    }

    function resetUpdateSupplierForm() {
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
