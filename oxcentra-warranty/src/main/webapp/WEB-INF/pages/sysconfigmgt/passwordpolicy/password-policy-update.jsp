<%--
  Created by IntelliJ IDEA.
  User: suren_v
  Date: 2/2/2021
  Time: 2:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalUpdateTask" data-backdrop="static">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update Password Policy</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetSearch()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updatePasswordPolicyForm" modelAttribute="passwordPolicy"
                       method="post"
                       name="updatePasswordPolicyForm" autocomplete="false">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Policy ID<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="passwordPolicyId" name="passwordPolicyId" type="text"
                                        class="form-control form-control-sm" id="editPolicyId" maxlength="3"
                                        onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">Minimum Length<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="minimumLength" name="minimumLength" type="text"
                                        class="form-control form-control-sm" id="editMinimumLength" maxlength="3"
                                        onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Maximum Length<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="maximumLength" name="maximumLength" type="text"
                                        class="form-control form-control-sm" id="editMaximumLength" maxlength="3"
                                        onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">No of Invalid Login Attempt<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="noOfInvalidLoginAttempt" name="noOfInvalidLoginAttempt" type="text"
                                        class="form-control form-control-sm" id="editNoOfInvalidLoginAttempt"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Minimum Special Characters Length<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="minimumSpecialCharacters" name="minimumSpecialCharacters" type="text"
                                        class="form-control form-control-sm" id="editMinimumSpecialCharacters"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">Minimum Uppercase Characters<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="minimumUpperCaseCharacters" name="minimumUpperCaseCharacters" type="text"
                                        class="form-control form-control-sm" id="editMinimumUpperCaseCharacters"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Minimum Numerical Characters<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="minimumNumericalCharacters" name="minimumNumericalCharacters" type="text"
                                        class="form-control form-control-sm" id="editMinimumNumericalCharacters"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">Minimum Lowercase Characters<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="minimumLowerCaseCharacters" name="minimumLowerCaseCharacters" type="text"
                                        class="form-control form-control-sm" id="editMinimumLowerCaseCharacters"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Repeat Characters Allowed<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="repeatCharactersAllow" name="repeatCharactersAllow" type="text"
                                        class="form-control form-control-sm" id="editRepeatCharactersAllow"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">Initial Password Expiry Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="initialPasswordExpiryStatus" name="initialPasswordExpiryStatus"
                                        type="text"
                                        class="form-control form-control-sm" id="editInitialPasswordExpiryStatus"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Password Expiry Period<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="passwordExpiryPeriod" name="passwordExpiryPeriod" type="text"
                                        class="form-control form-control-sm" id="editPasswordExpiryPeriod"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">No of History Password<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="noOfHistoryPassword" name="noOfHistoryPassword" type="text"
                                        class="form-control form-control-sm" id="editNoOfHistoryPassword"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                    </div>

                    <div class="form-group row">

                        <label class="col-sm-3 col-form-label">Idle Account Expiry Period<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="idleAccountExpiryPeriod" name="idleAccountExpiryPeriod" type="text"
                                        class="form-control form-control-sm" id="editIdleAccountExpiryPeriod"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                        <label class="col-sm-3 col-form-label">Minimum Password Change Period<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="minimumPasswordChangePeriod" name="minimumPasswordChangePeriod"
                                        type="text"
                                        class="form-control form-control-sm" id="editMinimumPasswordChangePeriod"
                                        maxlength="3" onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                                        onpaste="return false" autocomplete="false"/>
                        </div>

                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-3">
                            <form:input path="description" name="description" type="text"
                                        class="form-control form-control-sm" id="editDescription" maxlength="32"
                                        autocomplete="false"/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${passwordPolicy.vupdate}">
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
    $(document).ready(function () {
        $("#editMaximumLength").attr("autocomplete", "off");
        $("#editNoOfInvalidLoginAttempt").attr("autocomplete", "off");
        $("#editMinimumSpecialCharacters").attr("autocomplete", "off");
        $("#editMinimumUpperCaseCharacters").attr("autocomplete", "off");
        $("#editMinimumNumericalCharacters").attr("autocomplete", "off");
        $("#editMinimumLowerCaseCharacters").attr("autocomplete", "off");
        $("#editRepeatCharactersAllow").attr("autocomplete", "off");
        $("#editInitialPasswordExpiryStatus").attr("autocomplete", "off");
        $("#editPasswordExpiryPeriod").attr("autocomplete", "off");
        $("#editIdleAccountExpiryPeriod").attr("autocomplete", "off");
        $("#editMinimumPasswordChangePeriod").attr("autocomplete", "off");
    });

    function update() {
        resetUpdatePolicyFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updatePasswordPolicy.json',
            data: $('form[name=updatePasswordPolicyForm]').serialize(),
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

    function resetUpdate() {

        $.ajax({
            url: "${pageContext.request.contextPath}/getPasswordPolicy.json",
            data: {
                policyid: $('#editPolicyId').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#editPolicyId').val(data.passwordPolicyId);
                $('#editPolicyId').attr('readOnly', true);
                $('#editMinimumLength').val(data.minimumLength);
                $('#editMaximumLength').val(data.maximumLength);
                $('#editMinimumSpecialCharacters').val(data.minimumSpecialCharacters);
                $('#editMinimumUpperCaseCharacters').val(data.minimumUpperCaseCharacters);
                $('#editMinimumNumericalCharacters').val(data.minimumNumericalCharacters);
                $('#editMinimumLowerCaseCharacters').val(data.minimumLowerCaseCharacters);
                $('#editNoOfInvalidLoginAttempt').val(data.noOfInvalidLoginAttempt);
                $('#editRepeatCharactersAllow').val(data.repeatCharactersAllow);
                $('#editInitialPasswordExpiryStatus').val(data.initialPasswordExpiryStatus);
                $('#editPasswordExpiryPeriod').val(data.passwordExpiryPeriod);
                $('#editNoOfHistoryPassword').val(data.noOfHistoryPassword);
                $('#editMinimumPasswordChangePeriod').val(data.minimumPasswordChangePeriod);
                $('#editIdleAccountExpiryPeriod').val(data.idleAccountExpiryPeriod);
                $('#editDescription').val(data.description);
                $('#editLastUpdatedUser').val(data.lastUpdatedUser);
                $('#editLastUpdatedUser').attr('readOnly', true);
                $('#responseMsgUpdate').val("");

            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/login.jsp";
            }
        });

        resetUpdatePolicyFormData();
    }

    function resetUpdatePolicyFormData() {
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
