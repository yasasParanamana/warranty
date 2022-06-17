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

<div class="modal fade" id="modalAddSystemUser" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAddSystemUserLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAddSystemUserLabel">Insert System User </h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="addSystemUserForm" modelAttribute="systemuser" method="post"
                       name="addSystemUserForm">
                <div class="modal-body">
                    <div class="card-body">
                        <div class="form-group"><span id="responseMsgAdd"></span></div>
                        <div class="form-group row" hidden="true">
                            <label for="userName" class="col-sm-4 col-form-label">User Task<span
                                    class="text-danger">*</span></label>
                            <div class="col-sm-8">
                                <form:input path="userTask" name="userTask" type="text"
                                            class="form-control form-control-sm"
                                            id="aUserTask" value="ADD" placeholder="User Task"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="col-lg-4">
                                <label>Username<span class="text-danger">*</span></label>
                                <form:input path="userName" name="userName" type="text"
                                            class="form-control form-control-sm"
                                            id="aUserName" maxlength="16" placeholder="Username"
                                            onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                                <span class="form-text text-muted">Please enter username</span>
                            </div>
                            <div class="col-lg-4">
                                <label>Full Name<span class="text-danger">*</span></label>
                                <form:input path="fullName" name="fullName" type="text"
                                            onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                            class="form-control form-control-sm" maxlength="128"
                                            id="aFullName" placeholder="Full Name"/>
                                <span class="form-text text-muted">Please enter fullname</span>
                            </div>
                            <div class="col-lg-4">
                                <label>Email<span class="text-danger">*</span></label>
                                <form:input path="email" name="email" type="text"
                                            class="form-control form-control-sm" maxlength="128"
                                            id="aEmail" placeholder="Email" onkeyup="ValidateEmail();"/>
                                <span class="form-text text-muted" id="email-default-msg">Please enter Email</span>
                                <span class="form-text text-danger" id="email-validation-msg"></span>
                            </div>
                        </div>

                        <div class="form-group row">
                            <div class="col-lg-4">
                                <label>User Role Code<span class="text-danger">*</span></label>
                                <form:select path="userRoleCode" name="userRoleCode"
                                             class="form-control form-control-sm"
                                             id="aUserRoleCode" readonly="true">
                                    <c:forEach items="${systemuser.userRoleList}" var="userRoleList">
                                        <form:option
                                                value="${userRoleList.userroleCode}">${userRoleList.description}</form:option>
                                    </c:forEach>
                                </form:select>
                                <span class="form-text text-muted">Please enter User Role Code</span>
                            </div>

                            <div class="col-lg-4">
                                <label>Status<span class="text-danger">*</span></label>
                                <form:select path="status" name="status" class="form-control form-control-sm"
                                             id="aStatus"
                                             readonly="true">
                                    <c:forEach items="${systemuser.statusActList}" var="status">
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
                                            id="aMobileNumber" placeholder="Mobile Number"/>
                                <span class="form-text text-muted">Please enter Mobile Number</span>
                            </div>
                        </div>

                        <div class="form-group row">
                            <div class="col-lg-4">
                                <label>Password<span class="text-danger">*</span></label>
                                <form:input path="password" name="password" type="password"
                                            class="form-control form-control-sm" maxlength="128"
                                            id="aPassword" placeholder="Password"/>
                                <span class="form-text text-muted">Please enter Password</span>
                            </div>

                            <div class="col-lg-4">
                                <label>Confirm Password<span class="text-danger">*</span></label>
                                <form:input path="confirmPassword" name="confirmPassword" type="password"
                                            class="form-control form-control-sm" maxlength="128"
                                            id="aConfirmPassword" placeholder="Confirm Password"/>
                                <span class="form-text text-muted">Please enter Confirm Password</span>
                            </div>

                            <div class="col-lg-4">
                                <label>NIC<span class="text-danger">*</span></label>
                                <form:input path="nic" name="nic" type="text"
                                            class="form-control form-control-sm" maxlength="10"
                                            id="aNic" placeholder="NIC" onkeyup="$(this).val($(this).val().replace(/[^vVxX0-9 ]/g, ''));toUpperCaseAdd()"/>
                                <span class="form-text text-muted">Please enter NIC</span>
                            </div>
                        </div>

                        <div class="form-group row">
                            <div class="col-lg-4">
                                <label>Service ID<span class="text-danger">*</span></label>
                                <form:input path="serviceId" name="serviceId" type="text"
                                            onkeyup="$(this).val($(this).val().replace(/[^\d]/ig, ''))"
                                            class="form-control form-control-sm" maxlength="10"
                                            id="aServiceId" placeholder="Service Id"/>
                                <span class="form-text text-muted">Please enter Service Id</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <span class="text-danger">Required fields are marked by the '*'</span>
                        </div>
                    </div>

                </div>
                <div class="modal-footer justify-content-end">
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${systemuser.vadd}">
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
    function toUpperCaseAdd() {
        var NIC = $("#aNic").val();
        if (NIC.length == 10) {
            if (NIC.charAt(9) == "v") {
                var n = NIC.replace("v", "V");
                //   alert(n);
                $("#aNic").val(n);
            } else if (NIC.charAt(9) == "x") {
                var n = NIC.replace("x", "X");
                //   alert(n);
                $("#aNic").val(n);
            }
        }
    }
    function ValidateEmail() {
        var email = document.getElementById("aEmail").value;
        var lblError = document.getElementById("email-validation-msg");
        lblError.innerHTML = "";
        var expr = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;;
        if ((!expr.test(email)) && (email != "")) {
            lblError.innerHTML = "Invalid email address.";
            document.getElementById("email-default-msg").style.visibility = "hidden";
        } else{
            lblError.innerHTML = "";
            document.getElementById("email-default-msg").style.visibility = "visible";
        }
    }

    function resetAdd() {
        $('form[name=addSystemUserForm]').trigger("reset");
        $('#responseMsgAdd').hide();
        ValidateEmail();
    }

    function add() {
        resetAddSystemUserForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addSystemUser.json',
            data: $('form[name=addSystemUserForm]').serialize(),
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
                    $('form[name=addSystemUserForm]').trigger("reset");
                    searchStart();
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

    function resetAddSystemUserForm() {
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
