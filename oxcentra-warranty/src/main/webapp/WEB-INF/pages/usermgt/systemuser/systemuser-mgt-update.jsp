<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 2/15/2021
  Time: 5:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateSystemUser" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateSystemUserLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateSystemUserLabel">Update System User</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateSystemUserForm" modelAttribute="systemuser" method="post"
                       name="updateSystemUserForm">
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
                        <div class="col-lg-4">
                            <label>Username<span class="text-danger">*</span></label>
                            <form:input path="userName" name="userName" type="text" class="form-control form-control-sm"
                                        id="eUserName" maxlength="16" placeholder="Username"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                            <span class="form-text text-muted">Please enter username</span>
                        </div>
                        <div class="col-lg-4">
                            <label>Full Name<span class="text-danger">*</span></label>
                            <form:input path="fullName" name="fullName" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="128"
                                        id="eFullName" placeholder="Full Name"/>
                            <span class="form-text text-muted">Please enter fullname</span>
                        </div>
                        <div class="col-lg-4">
                            <label>Email<span class="text-danger">*</span></label>
                            <form:input path="email" name="email" type="text"
                                        class="form-control form-control-sm" maxlength="128"
                                        id="eEmail" placeholder="Email" onkeyup="ValidateEmailUpdate();"/>
                            <span class="form-text text-muted" id="email-default-msg-update">Please enter Email</span>
                            <span class="form-text text-danger" id="email-validation-msg-update"></span>
                        </div>
                    </div>

                    <div class="form-group row">
                        <div class="col-lg-4">
                            <label>User Role Code<span class="text-danger">*</span></label>
                            <form:select path="userRoleCode" name="userRoleCode" class="form-control form-control-sm"
                                         id="eUserRoleCode" readonly="true">
                                <c:forEach items="${systemuser.userRoleList}" var="userRoleList">
                                    <form:option
                                            value="${userRoleList.userroleCode}">${userRoleList.description}</form:option>
                                </c:forEach>
                            </form:select>
                            <span class="form-text text-muted">Please enter User Role Code</span>
                        </div>
                        <div class="col-lg-4">
                            <label>Status<span class="text-danger">*</span></label>
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${systemuser.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                            <span class="form-text text-muted">Please enter Status</span>
                        </div>
                        <div class="col-lg-4">
                            <label>Mobile Number<span class="text-danger">*</span></label>
                            <form:input path="mobileNumber" name="mobileNumber" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^\d]/ig, ''))"
                                        class="form-control form-control-sm" maxlength="10"
                                        id="eMobileNumber" placeholder="Mobile Number"/>
                            <span class="form-text text-muted">Please enter Mobile Number</span>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-lg-4">
                            <label>NIC<span class="text-danger">*</span></label>
                            <form:input path="nic" name="nic" type="text"
                                        class="form-control form-control-sm" maxlength="10"
                                        id="eNic" placeholder="NIC"
                                        onkeyup="$(this).val($(this).val().replace(/[^vVxX0-9 ]/g, ''));toUpperCase()"/>
                            <span class="form-text text-muted">Please enter NIC</span>
                        </div>


                        <div class="col-lg-4">
                            <label>User Role Code<span class="text-danger">*</span></label>
                            <form:select path="serviceId" name="serviceId" class="form-control form-control-sm"
                                         id="eServiceId" readonly="true">
                                <c:forEach items="${systemuser.dealershipList}" var="dealership">
                                    <form:option
                                            value="${dealership.dealershipCode}">${dealership.dealershipName}</form:option>
                                </c:forEach>
                            </form:select>
                            <span class="form-text text-muted">Please select Dealership</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>

                </div>
                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${systemuser.vupdate}">
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
    function toUpperCase() {
        var NIC = $("#eNic").val();
        if (NIC.length == 10) {
            if (NIC.charAt(9) == "v") {
                var n = NIC.replace("v", "V");
                //   alert(n);
                $("#eNic").val(n);
            } else if (NIC.charAt(9) == "x") {
                var n = NIC.replace("x", "X");
                //   alert(n);
                $("#eNic").val(n);
            }
        }
    }

    function ValidateEmailUpdate() {
        var email = document.getElementById("eEmail").value;
        var lblError = document.getElementById("email-validation-msg-update");
        lblError.innerHTML = "";
        var expr = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
        if ((!expr.test(email)) && (email != "")) {
            lblError.innerHTML = "Invalid email address.";
            document.getElementById("email-validation-msg-update").style.visibility = "visible";
            document.getElementById("email-default-msg-update").style.visibility = "hidden";
        } else {
            lblError.innerHTML = "";
            document.getElementById("email-validation-msg-update").style.visibility = "hidden";
            document.getElementById("email-default-msg-update").style.visibility = "visible";
        }
    }

    function update() {
        resetUpdateSystemUserForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateSystemUser.json',
            data: $('form[name=updateSystemUserForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getSystemUser.json",
            data: {
                userName: $('#eUserName').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eUserName').val(data.userName);
                $('#eUserName').attr('readOnly', true);
                $('#eFullName').val(data.fullName);
                $('#eEmail').val(data.email);
                $('#eUserRoleCode').val(data.userRoleCode);
                $('#eStatus').val(data.status);
                $('#eMobileNumber').val(data.mobileNumber);
                $('#eNic').val(data.nic);
                $('#eServiceId').val(data.serviceid);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateSystemUserForm();
    }

    function resetUpdateSystemUserForm() {
        $(".validation-err").remove();
        if ($('#responseMsgUpdate').hasClass('success-response')) {
            $('#responseMsgUpdate').removeClass('success-response');
        }
        if ($('#responseMsgUpdate').hasClass('error-response')) {
            $('#responseMsgUpdate').removeClass('error-response');
        }
        $('#responseMsgUpdate').hide();
        document.getElementById("email-default-msg-update").style.visibility = "visible";
        document.getElementById("email-validation-msg-update").style.visibility = "hidden";

    }
</script>
