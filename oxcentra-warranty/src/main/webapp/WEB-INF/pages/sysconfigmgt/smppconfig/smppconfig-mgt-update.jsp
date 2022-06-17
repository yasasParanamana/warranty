<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 3/15/2021
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateSmppConfiguration" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update SMPP Configuration</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateSmppConfigurationForm" modelAttribute="smppconfig"
                       method="post"
                       name="updateSmppConfigurationForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="eUserTask" value="UPDATE" placeholder="User Task"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Code<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="smppCode" name="smppCode" type="text" class="form-control form-control-sm"
                                        id="eSmppCode" maxlength="16" placeholder="SMPP Code"
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
                                        id="eDescription" maxlength="16" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${smppconfig.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="maxTps" class="col-sm-4 col-form-label">Max Tps<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="maxTps" name="maxTps" type="text"
                                        class="form-control form-control-sm"
                                        id="eMaxTps" maxlength="4" placeholder="Max Tps"
                                        onkeypress="return isNumber(event)" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="primaryIp" class="col-sm-4 col-form-label">Primary IP<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="primaryIp" name="primaryIp" type="text"
                                        class="form-control form-control-sm"
                                        id="ePrimaryIp" maxlength="20" placeholder="Primary IP"
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
                                        id="eSecondaryIp" maxlength="20" placeholder="Secondary IP"
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
                                        id="eSystemId" maxlength="10" placeholder="System Id"
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
                                        id="ePassword" maxlength="10" placeholder="Password" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="bindPort" class="col-sm-4 col-form-label">Bind Port<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="bindPort" name="bindPort" type="text"
                                        class="form-control form-control-sm"
                                        id="eBindPort" maxlength="5" placeholder="Bind Port"
                                        onkeypress="return isNumber(event)" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="bindMode" class="col-sm-4 col-form-label">Bind Mode<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="bindMode" name="bindMode" type="text"
                                        class="form-control form-control-sm"
                                        id="eBindMode" maxlength="3" placeholder="Bind Port"
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
                                        id="eMtPort" maxlength="20" placeholder="MT Port"
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
                                        id="eMoPort" maxlength="20" placeholder="MO Port"
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
                                        id="eMaxBulkTps" maxlength="5" placeholder="Max Bulk TPS"
                                        onkeypress="return isNumber(event)" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${smppconfig.vupdate}">
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
        resetUpdateSmppConfigurationForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateSmppConfiguration.json',
            data: $('form[name=updateSmppConfigurationForm]').serialize(),
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
                    search();
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
            url: "${pageContext.request.contextPath}/getSmppConfiguration.json",
            data: {
                smppCode: $('#eSmppCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eSmppCode').val(data.smppCode);
                $('#eSmppCode').attr('readOnly', true);

                $('#eDescription').val(data.description);
                $('#eStatus').val(data.status);
                $('#eMaxTps').val(data.maxTps);
                $('#ePrimaryIp').val(data.primaryIp);
                $('#eSecondaryIp').val(data.secondaryIp);
                $('#eSystemId').val(data.systemId);
                $('#ePassword').val(data.password);
                $('#eBindPort').val(data.bindPort);
                $('#eBindMode').val(data.bindMode);
                $('#eMtPort').val(data.mtPort);
                $('#eMoPort').val(data.moPort);
                $('#eMaxBulkTps').val(data.maxBulkTps);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateSmppConfigurationForm();
    }

    function resetUpdateSmppConfigurationForm() {
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
