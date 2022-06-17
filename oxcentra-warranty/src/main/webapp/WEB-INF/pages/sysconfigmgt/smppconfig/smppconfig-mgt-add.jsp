<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 3/15/2021
  Time: 1:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalAddSmppConfiguration" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert SMPP Configuration</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addSmppConfigurationForm" modelAttribute="smppconfig"
                       method="post" name="addSmppConfigurationForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="aUserTask" value="ADD" placeholder="User Task"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="smppCode" class="col-sm-4 col-form-label">SMPP Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="smppCode" name="smppCode" type="text" class="form-control form-control-sm"
                                        id="aSmppCode" maxlength="16" placeholder="SMPP Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="description" name="description" type="text"
                                        class="form-control form-control-sm"
                                        id="aDescription" maxlength="16" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus"
                                         readonly="true">
                                <c:forEach items="${smppconfig.statusActList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Max Tps<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="maxTps" name="maxTps" type="text"
                                        class="form-control form-control-sm"
                                        id="aMaxTps" maxlength="4" placeholder="Max Tps"
                                        onkeypress="return isNumber(event)" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="primaryIp" class="col-sm-4 col-form-label">Primary IP<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="primaryIp" name="primaryIp" type="text"
                                        class="form-control form-control-sm"
                                        id="aPrimaryIp" maxlength="20" placeholder="Primary IP"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9. ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="secondaryIp" class="col-sm-4 col-form-label">Secondary IP<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="secondaryIp" name="secondaryIp" type="text"
                                        class="form-control form-control-sm"
                                        id="aSecondaryIp" maxlength="20" placeholder="Secondary IP"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9. ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="systemId" class="col-sm-4 col-form-label">System Id<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="systemId" name="systemId" type="text"
                                        class="form-control form-control-sm"
                                        id="aSystemId" maxlength="10" placeholder="System Id"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="password" class="col-sm-4 col-form-label">Password<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="password" name="password" type="password"
                                        class="form-control form-control-sm"
                                        id="aPassword" maxlength="10" placeholder="Password" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="bindPort" class="col-sm-4 col-form-label">Bind Port<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="bindPort" name="bindPort" type="text"
                                        class="form-control form-control-sm"
                                        id="aBindPort" maxlength="5" placeholder="Bind Port"
                                        onkeypress="return isNumber(event)" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="bindMode" class="col-sm-4 col-form-label">Bind Mode<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="bindMode" name="bindMode" type="text"
                                        class="form-control form-control-sm"
                                        id="aBindMode" maxlength="3" placeholder="Bind Mode"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="mtPort" class="col-sm-4 col-form-label">MT Port<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="mtPort" name="mtPort" type="text"
                                        class="form-control form-control-sm"
                                        id="aMtPort" maxlength="20" placeholder="MT Port"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="moPort" class="col-sm-4 col-form-label">MO Port<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="moPort" name="moPort" type="text"
                                        class="form-control form-control-sm"
                                        id="aMoPort" maxlength="20" placeholder="MO Port"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="maxBulkTps" class="col-sm-4 col-form-label">Max Bulk TPS<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="maxBulkTps" name="maxBulkTps" type="text"
                                        class="form-control form-control-sm"
                                        id="aMaxBulkTps" maxlength="5" placeholder="Max Bulk TPS"
                                        onkeypress="return isNumber(event)" autocomplete="off"/>
                        </div>
                    </div>


                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${smppconfig.vadd}">
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
    function isNumber(evt) {
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode > 31 && (charCode < 48 || charCode > 57)) {
            return false;
        }
        return true;
    }

    function resetAdd() {
        $('form[name=addSmppConfigurationForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddSmppConfigurationForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addSmppConfiguration.json',
            data: $('form[name=addSmppConfigurationForm]').serialize(),
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
                    $('form[name=addSmppConfigurationForm]').trigger("reset");
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

    function resetAddSmppConfigurationForm() {
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
