<%--
  Created by IntelliJ IDEA.
  User: suren_v
  Date: 1/12/2021
  Time: 5:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdatePasswordParam" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update Password Param</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updatePasswordParamForm" modelAttribute="passwordParam"
                       method="post"
                       name="updatePasswordParamForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Password Param Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="passwordparam" name="passwordparam" type="text"
                                        class="form-control form-control-sm" id="editPasswordParam" maxlength="16"
                                        placeholder="Password Param Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Value<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="value" name="value" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="64"
                                        id="editValue" placeholder="Value"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">UserRole Type<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="userroletype" name="userroletype" class="form-control form-control-sm"
                                         id="editUserRole">
                                <c:forEach items="${passwordparamviewform.userRoleTypeBeanList}" var="passwordparam">
                                    <form:option
                                            value="${passwordparam.userroletypecode}">${passwordparam.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <br/>
                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>
                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${passwordparamviewform.vupdate}">
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
        resetUpdatePasswordParamFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updatePasswordParam.json',
            data: $('form[name=updatePasswordParamForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getPasswordParam.json",
            data: {
                passwordParam: $('#editPasswordParam').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",

            success: function (data) {
                console.log(JSON.stringify(data));
                $('#editPasswordParam').val(data.passwordparam);
                $('#editPasswordParam').attr('readOnly', true);
                $('#editUserRole').val(data.userroletype);
                $('#editValue').val(data.value);

            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });

        resetUpdatePasswordParamFormData();

    }

    function resetUpdatePasswordParamFormData() {
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
