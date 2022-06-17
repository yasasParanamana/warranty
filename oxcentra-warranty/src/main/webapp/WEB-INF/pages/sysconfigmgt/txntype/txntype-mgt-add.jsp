<%--
  Created by IntelliJ IDEA.
  User: namila_w
  Date: 11/15/2021
  Time: 10:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalAddTxnType" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert Txn Type</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addTxnTypeForm" modelAttribute="txntype" method="post"
                       name="addTxnTypeForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>

                    <div class="form-group row">
                        <label for="txntype" class="col-sm-4 col-form-label">Transaction Type<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="txntype" name="txntype" type="text"
                                        class="form-control form-control-sm"
                                        id="aTxntype" maxlength="20" placeholder="Transaction Type"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="description" name="description" type="text"
                                        class="form-control form-control-sm"
                                        id="aDescription" maxlength="50" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus"
                                         readonly="true">
                                <c:forEach items="${txntype.statusActList}" var="status">
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
                    <c:if test="${txntype.vadd}">
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
        $('form[name=addTxnTypeForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddTxnTypeForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addTxnType.json',
            data: $('form[name=addTxnTypeForm]').serialize(),
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
                    $('form[name=addTxnTypeForm]').trigger("reset");
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

    function resetAddTxnTypeForm() {
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

