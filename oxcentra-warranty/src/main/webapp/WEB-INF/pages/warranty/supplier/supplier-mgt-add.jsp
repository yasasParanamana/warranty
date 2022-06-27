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

<div class="modal fade" id="modalAddSupplier" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAddSupplierLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAddSupplierLabel">Add new supplier</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addSupplierForm" modelAttribute="supplier" method="post"
                       name="addSupplierForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>

                    <div class="form-group row">
                        <label for="supplierName" class="col-sm-4 col-form-label">Supplier Name<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="supplierName" name="supplierName" type="text"
                                        class="form-control form-control-sm"
                                        id="aSupplierName" maxlength="50" placeholder="Supplier Name"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="supplierPhone" class="col-sm-4 col-form-label">Supplier Phone<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="supplierPhone" name="supplierPhone" type="text"
                                        class="form-control form-control-sm"
                                        id="aSupplierPhone" maxlength="15" placeholder="Supplier Phone"
                                        onkeyup="$(this).val($(this).val().replace(/[^0-9]/g, ''))"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="supplierEmail" class="col-sm-4 col-form-label">Supplier Email<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="supplierEmail" name="supplierEmail" type="text"
                                        class="form-control form-control-sm"
                                        id="aSupplierEmail" maxlength="200" placeholder="Supplier Email"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="supplierAddress" class="col-sm-4 col-form-label">Supplier Address<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="supplierAddress" name="supplierAddress" type="text"
                                        class="form-control form-control-sm"
                                        id="aSupplierAddress" maxlength="200" placeholder="Supplier Address"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus"
                                         readonly="true">
                                <c:forEach items="${supplier.statusActList}" var="status">
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
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${supplier.vadd}">
                        <button id="addBtn" type="button" onclick="add()" class="btn btn-primary">
                            Submit
                        </button>
                    </c:if>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script>
    function resetAdd() {
        $('form[name=addSupplierForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function resetAddSupplierForm() {
        $(".validation-err").remove();
        if ($('#responseMsgAdd').hasClass('success-response')) {
            $('#responseMsgAdd').removeClass('success-response');
        }
        if ($('#responseMsgAdd').hasClass('error-response')) {
            $('#responseMsgAdd').removeClass('error-response');
        }
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddSupplierForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addSupplier.json',
            data: $('form[name=addSupplierForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                if (res.flag) {
                    // handle success response
                    $('#responseMsgAdd').show();
                    $('#responseMsgAdd').addClass('success-response').text(res.successMessage);
                    $('form[name=addSupplierForm]').trigger("reset");
                    search();
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
</script>

