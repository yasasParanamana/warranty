<%--
  Created by IntelliJ IDEA.
  User: Satheesh_M
  Date: 2/13/2021
  Time: 12:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalAssignTask" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAssignTaskLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAssignTaskLabel">Assign Task</h6>
                <button type="button" id="assignPagePopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAssignTask()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="assignTaskForm" modelAttribute="userrole" method="post" name="assignTaskForm">
                <div hidden>
                    <form:input path="userroleCode" id="assignTaskUserroleCode"/>
                </div>

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAssignTask"></span></div>
                    <div class="form-group row">
                        <label for="assignTaskUserRoleDescription" class="col-sm-3 col-form-label">User role : </label>
                        <div class="col-sm-5">
                            <form:input id="assignTaskUserRoleDescription" readonly="true"
                                        class="form-control form-control-sm" path="description" name="description"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="section" class="col-sm-3 col-form-label">Section : </label>
                        <div class="col-sm-5">
                            <form:select path="section" id="assignTaskSection"
                                         class="form-control form-control-sm"
                                         onchange="loadTaskAssignedPages(this.value)" name="section">
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="section" class="col-sm-3 col-form-label">Page : </label>
                        <div class="col-sm-5">
                            <form:select path="page" id="assignTaskPage"
                                         class="form-control form-control-sm"
                                         onchange="loadTasks(this.value)" name="page">
                            </form:select>
                        </div>
                    </div>


                    <div class="row">
                        <div class="col-sm-5">
                            <div class="form-group">
                                <label>Not assigned</label>
                                <form:select class="form-control form-control-sm"
                                             items="${userrole.notAssignedTaskList}"
                                             id="unassignedTasks"
                                             multiple="true"
                                             path="notAssignedTasks"
                                             name="notAssignedTasks"
                                             itemValue="taskCode"
                                             itemLabel="description"
                                             size="8"/>
                            </div>
                        </div>
                        <div class="col-sm-2 text-center">
                            <br/><br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="rightt"
                                    onclick="toRightTask()"><i
                                    class="fa fa-angle-right"></i></button>
                            <br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="leftt"
                                    onclick="toLeftTask()"><i
                                    class="fa fa-angle-left"></i></button>
                            <br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="righttall"
                                    onclick="toRightAllTask()"><i
                                    class="fa fa-angle-double-right"></i></button>
                            <br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="lefttall"
                                    onclick="toLeftAllTask()"><i
                                    class="fa fa-angle-double-left"></i></button>

                        </div>
                        <div class="col-sm-5">
                            <div class="form-group">
                                <label>Assigned</label>
                                <form:select class="form-control form-control-sm"
                                             items="${userrole.assignedTaskList}"
                                             id="assignedTasks"
                                             multiple="true"
                                             path="assignedTasks"
                                             name="assignedTasks"
                                             itemValue="taskCode"
                                             itemLabel="description"
                                             size="8"/>
                            </div>
                        </div>
                    </div>

                    <!-- /.card-body -->
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="assignTaskReset" type="button" class="btn btn-default" onclick="resetAssignTask()">Reset
                    </button>
                    <c:if test="${userrole.vassigntask}">
                        <button id="assignTaskBtn" type="button" onclick="assignTask()" class="btn btn-primary">
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

<script type="text/javascript">

    function loadTaskAssignedPages(section) {
        $("#assignTaskPage").empty();
        $("#assignTaskPage").append($('<option>', {
            value: "",
            text: "Select Page",
            selected: true
        }));
        $('#assignTaskPage').attr('readonly', true);

        $("#assignedTasks").empty();
        $("#unassignedTasks").empty();

        if (section) {
            var userroleCode = $('#assignTaskUserroleCode').val();
            $.ajax({
                url: "${pageContext.request.contextPath}/getAssignedPages.json",
                data: {
                    userroleCode: userroleCode,
                    sectionCode: section
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",

                success: function (data) {
                    $('#assignTaskPage').attr('readonly', false);
                    let assignTaskPage = document.getElementById("assignTaskPage");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.description;
                            option.value = obj.pageCode;
                            assignTaskPage.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }
    }

    function loadTasks(page) {
        $("#assignedTasks").empty();
        $("#unassignedTasks").empty();

        if (page) {
            var userroleCode = $('#assignTaskUserroleCode').val();
            $.ajax({
                url: "${pageContext.request.contextPath}/getAssignedTasks.json",
                data: {
                    userroleCode: userroleCode,
                    page: page
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    let assignedTasks = document.getElementById("assignedTasks");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.description;
                            option.value = obj.taskCode;
                            assignedTasks.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });


            $.ajax({
                url: "${pageContext.request.contextPath}/getUnAssignedTasks.json",
                data: {
                    userroleCode: userroleCode,
                    page: page
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    let unassignedTasks = document.getElementById("unassignedTasks");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.description;
                            option.value = obj.taskCode;
                            unassignedTasks.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }
    }


    function toRightTask() {
        $("#unassignedTasks option:selected").each(function () {
            $("#assignedTasks").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function toLeftTask() {
        $("#assignedTasks option:selected").each(function () {
            $("#unassignedTasks").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function toRightAllTask() {
        $("#unassignedTasks option").each(function () {
            $("#assignedTasks").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function toLeftAllTask() {
        $("#assignedTasks option").each(function () {
            $("#unassignedTasks").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function resetAssignTask() {
        resetAssignTaskUserRoleFormData();
        $('#responseMsgAssignTask').hide();
        $("#assignTaskSection").val("");

        $("#assignTaskPage").empty();
        $("#assignTaskPage").append($('<option>', {
            value: "",
            text: "Select Page",
            selected: true
        }));
        $('#assignTaskPage').attr('readonly', true);

        $("#assignedTasks").empty();
        $("#unassignedTasks").empty();
    }

    function assignTask() {
        resetAssignTaskUserRoleFormData();
        let assigned = [];
        $("#assignedTasks option").each(function () {
            assigned.push(this.value);
        });

        let unassigned = [];
        $("#unassignedTasks option").each(function () {
            unassigned.push(this.value);
        });

        $("#unassignedTasks").val(unassigned);
        $("#assignedTasks").val(assigned);

        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/assignTasks.json',
            data: $('form[name=assignTaskForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                if (res.flag) { //success
                    $('#responseMsgAssignTask').show();
                    $('#responseMsgAssignTask').addClass('success-response').text(res.successMessage);
                    searchStart();
                } else {
                    $('#responseMsgAssignTask').show();
                    $('#responseMsgAssignTask').addClass('error-response').text(res.errorMessage);
                }
            },
            error: function (jqXHR) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
    }

    function resetAssignTaskUserRoleFormData() {
        $(".validation-err").remove();

        if ($('#responseMsgAssignTask').hasClass('success-response')) {
            $('#responseMsgAssignTask').removeClass('success-response');
        }

        if ($('#responseMsgAssignTask').hasClass('error-response')) {
            $('#responseMsgAssignTask').removeClass('error-response');
        }

        $('#responseMsgAssignTask').hide();
    }
</script>
