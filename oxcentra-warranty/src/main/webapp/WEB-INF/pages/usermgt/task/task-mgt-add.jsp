<%--
  Created by IntelliJ IDEA.
  User: yasas
  Date: 1/11/2021
  Time: 6:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalAddTask" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAddTaskLabel" aria-hidden="true">
    <div class="modal-dialog  modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAddTaskLabel">Insert Task</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="addTaskForm" modelAttribute="task" method="post"
                       name="addTaskForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>
                    <div class="form-group row">
                        <label for="addTaskCode" class="col-sm-3 col-form-label">Task Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="taskCode" name="taskCode" type="text"
                                        class="form-control form-control-sm" id="addTaskCode" maxlength="16"
                                        placeholder="Task Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="addDescription" class="col-sm-3 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="32"
                                        id="addDescription" placeholder="Description"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="addStatus" class="col-sm-3 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="status" name="status"
                                         class="form-control form-control-sm" id="addStatus"
                                         readonly="true">
                                <c:forEach items="${task.statusActList}" var="status">
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
                    <c:if test="${task.vadd}">
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
        $('form[name=addTaskForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddTaskFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addTask.json',
            data: $('form[name=addTaskForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {

                if (res.flag) { //success
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

    function resetAddTaskFormData() {
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
