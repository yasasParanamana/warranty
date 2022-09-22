<%--
  Created by IntelliJ IDEA.
  User: yasas
  Date: 1/12/2021
  Time: 5:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateTask" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateTaskLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalUpdateTaskLabel">Update Task</h5>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updateTaskForm" modelAttribute="task" method="post"
                       name="updateTaskForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Task Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="taskCode" name="taskCode" type="text"
                                        class="form-control form-control-sm" id="editTaskCode" maxlength="16"
                                        placeholder="Task Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="32"
                                        id="editDescription" placeholder="Description"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:select path="status" name="status" class="form-control form-control-sm"
                                         id="editStatus">
                                <c:forEach items="${task.statusList}" var="status">
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
                    <c:if test="${task.vupdate}">
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
        resetUpdateTaskFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateTask.json',
            data: $('form[name=updateTaskForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getTask.json",
            data: {
                taskCode: $('#editTaskCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",

            success: function (data) {

                $('#editTaskCode').val(data.taskCode);
                $('#editTaskCode').attr('readOnly', true);
                $('#editDescription').val(data.description);
                $('#editStatus').val(data.status);

            },
            error: function (data) {
                console.log(data);
                <%--window.location = "${pageContext.request.contextPath}/logout.htm";--%>
            }
        });

        resetUpdateTaskFormData();

    }

    function resetUpdateTaskFormData() {
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
