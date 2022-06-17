<%--
  Created by IntelliJ IDEA.
  User: Satheesh_M
  Date: 2/5/2021
  Time: 3:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalUpdateUserrole" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateUserroleLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateUserroleLabel">Update Userrole</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updateUserroleForm" modelAttribute="userrole" method="post"
                       name="updateUserroleForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Userrole Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="userroleCode" name="userroleCode" type="text"
                                        class="form-control form-control-sm" id="editUserroleCode" maxlength="16"
                                        placeholder="Userrole Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="64"
                                        id="editDescription" placeholder="Description"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Userrole Type<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="userroleType" name="userroleType" class="form-control form-control-sm"
                                         id="editUserroleType">
                                <option selected value="">Select Userrole Type</option>
                                <c:forEach items="${userrole.userroleTypeList}" var="userroleType">
                                    <form:option
                                            value="${userroleType.userRoleTypeCode}">${userroleType.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="status" name="status" class="form-control form-control-sm"
                                         id="editStatus">
                                <c:forEach items="${userrole.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <br/>
                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                    <!-- /.card-body -->
                </div>
                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${userrole.vupdate}">
                        <button id="updateBtn" type="button" onclick="update()" class="btn btn-primary">
                            Submit
                        </button>
                    </c:if>
                </div>
            </form:form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>
    function update() {
        resetUpdateUserRoleFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateUserRole.json',
            data: $('form[name=updateUserroleForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getUserRole.json",
            data: {
                userRoleCode: $('#editUserroleCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#editUserroleCode').val(data.userroleCode);
                $('#editUserroleCode').attr('readOnly', true);
                $('#editDescription').val(data.description);
                $('#editUserroleType').val(data.userroleType);
                $('#editStatus').val(data.status);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateUserRoleFormData();
    }

    function resetUpdateUserRoleFormData() {
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

