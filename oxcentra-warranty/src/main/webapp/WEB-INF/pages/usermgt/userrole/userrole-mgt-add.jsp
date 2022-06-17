<%--
  Created by IntelliJ IDEA.
  User: Satheesh_M
  Date: 2/5/2021
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalAddUserrole" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAddUserroleLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAddUserroleLabel">Insert Userrole</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="addUserroleForm" modelAttribute="userrole" method="post"
                       name="addUserroleForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>
                    <div class="form-group row">
                        <label for="addUserroleCode" class="col-sm-3 col-form-label">Userrole Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="userroleCode" name="userroleCode" type="text"
                                        class="form-control form-control-sm" id="addUserroleCode" maxlength="16"
                                        placeholder="Userrole Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="addDescription" class="col-sm-3 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="64"
                                        id="addDescription" placeholder="Description"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="addUserroleType" class="col-sm-3 col-form-label">Userrole Type<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="userroleType" name="userroleType"
                                         class="form-control form-control-sm" id="addUserroleType">
                                <option selected value="">Select Userrole Type</option>
                                <c:forEach items="${userrole.userroleTypeActList}" var="userroleType">
                                    <form:option
                                            value="${userroleType.userRoleTypeCode}">${userroleType.description}
                                    </form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="addStatus" class="col-sm-3 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="status" name="status"
                                         class="form-control form-control-sm" id="addStatus"
                                         readonly="true">
                                <c:forEach items="${userrole.statusActList}" var="status">
                                    <form:option
                                            value="${status.statusCode}">${status.description}</form:option>
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
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${userrole.vadd}">
                        <button id="addBtn" type="button" onclick="add()" class="btn btn-primary">
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

    function resetAdd() {
        $('form[name=addUserroleForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddUserRoleFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addUserRole.json',
            data: $('form[name=addUserroleForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                if (res.flag) {
                    //success
                    $('#responseMsgAdd').show();
                    $('#responseMsgAdd').addClass('success-response').text(res.successMessage);
                    $('form[name=addTaskForm]').trigger("reset");
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

    function resetAddUserRoleFormData() {
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

