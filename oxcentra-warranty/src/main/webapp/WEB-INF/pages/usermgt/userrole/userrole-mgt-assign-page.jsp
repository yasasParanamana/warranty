<%--
  Created by IntelliJ IDEA.
  User: Satheesh_M
  Date: 2/10/2021
  Time: 1:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- assign page modal popup start -->

<div class="modal fade" id="modalAssignPage" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAssignPageLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAssignPageLabel">Assign Page</h6>
                <button type="button" id="assignPagePopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAssignPage()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="assignPageForm" modelAttribute="userrole" method="post"
                       name="assignPageForm">
                <div hidden>
                    <form:input path="userroleCode" id="assignPageUserroleCode"/>
                </div>

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAssignPage"></span></div>
                    <div class="form-group row">
                        <label for="assignPageUserRoleDescription" class="col-sm-3 col-form-label">User role : </label>
                        <div class="col-sm-5">
                            <form:input id="assignPageUserRoleDescription" readonly="true"
                                        class="form-control form-control-sm"
                                        path="description" name="description"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="section" class="col-sm-3 col-form-label">Section : </label>
                        <div class="col-sm-5">
                            <form:select path="section" id="section"
                                         class="form-control form-control-sm"
                                         onchange="loadPages(this.value)" name="section">
                            </form:select>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-sm-5">
                            <div class="form-group">
                                <label>Not assigned</label>
                                <form:select class="form-control form-control-sm"
                                             items="${assignPageUserRole.notAssignedList}"
                                             id="unassignedPages"
                                             multiple="true"
                                             path="notAssignedPages"
                                             name="notAssignedPages"
                                             itemValue="pageCode"
                                             itemLabel="description"
                                             size="8"/>
                            </div>
                        </div>
                        <div class="col-sm-2 text-center">
                            <br/><br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="rightpt"
                                    onclick="toRight()"><i
                                    class="fa fa-angle-right"></i></button>
                            <br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="leftpt"
                                    onclick="toLeft()"><i
                                    class="fa fa-angle-left"></i></button>
                            <br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="rightptall"
                                    onclick="toRightAll()"><i
                                    class="fa fa-angle-double-right"></i></button>
                            <br/>
                            <button type="button" class="btn btn-default btn-xs mb-1" id="leftptall"
                                    onclick="toLeftAll()"><i
                                    class="fa fa-angle-double-left"></i></button>
                        </div>
                        <div class="col-sm-5">
                            <div class="form-group">
                                <label>Assigned</label>
                                <form:select class="form-control form-control-sm"
                                             items="${assignPageUserRole.assignedList}"
                                             id="assignedPages"
                                             multiple="true"
                                             path="assignedPages"
                                             name="assignedPages"
                                             itemValue="pageCode"
                                             itemLabel="description"
                                             size="8"/>
                            </div>
                        </div>
                    </div>
                    <!-- /.card-body -->
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="assignPageReset" type="button" class="btn btn-default" onclick="resetAssignPage()">Reset
                    </button>
                    <c:if test="${userrole.vassignpage}">
                        <button id="assignPageBtn" type="button" onclick="assignPage()" class="btn btn-primary">
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

    function loadPages(section) {
        $("#assignedPages").empty();
        $("#unassignedPages").empty();
        if (section) {
            var userroleCode = $('#assignPageUserroleCode').val();
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
                    let assignedPages = document.getElementById("assignedPages");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.description;
                            option.value = obj.pageCode;
                            assignedPages.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });


            $.ajax({
                url: "${pageContext.request.contextPath}/getUnAssignedPages.json",
                data: {
                    userroleCode: userroleCode
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    let unassignedPages = document.getElementById("unassignedPages");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.description;
                            option.value = obj.pageCode;
                            unassignedPages.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }
    }

    function toRight() {
        $("#unassignedPages option:selected").each(function () {
            $("#assignedPages").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function toLeft() {
        $("#assignedPages option:selected").each(function () {
            $("#unassignedPages").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function toRightAll() {
        $("#unassignedPages option").each(function () {
            $("#assignedPages").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function toLeftAll() {
        $("#assignedPages option").each(function () {
            $("#unassignedPages").append($('<option>', {
                value: $(this).val(),
                text: $(this).text()
            }));
            $(this).remove();
        });
    }

    function resetAssignPage() {
        resetAssignPageUserRoleFormData();
        $('#responseMsgAssignPage').hide();
        $("#section").val("");
        $("#assignedPages").empty();
        $("#unassignedPages").empty();
    }

    function assignPage() {
        resetAssignPageUserRoleFormData();
        let assigned = [];
        $("#assignedPages option").each(function () {
            assigned.push(this.value);
        });

        let unassigned = [];
        $("#unassignedPages option").each(function () {
            unassigned.push(this.value);
        });

        $("#unassignedPages").val(unassigned);
        $("#assignedPages").val(assigned);

        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/assignPages.json',
            data: $('form[name=assignPageForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                if (res.flag) { //success
                    $('#responseMsgAssignPage').show();
                    $('#responseMsgAssignPage').addClass('success-response').text(res.successMessage);
                    searchStart();
                } else {
                    $('#responseMsgAssignPage').show();
                    $('#responseMsgAssignPage').addClass('error-response').text(res.errorMessage);
                }
            },
            error: function (jqXHR) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
    }

    function resetAssignPageUserRoleFormData() {
        $(".validation-err").remove();

        if ($('#responseMsgAssignPage').hasClass('success-response')) {
            $('#responseMsgAssignPage').removeClass('success-response');
        }

        if ($('#responseMsgAssignPage').hasClass('error-response')) {
            $('#responseMsgAssignPage').removeClass('error-response');
        }

        $('#responseMsgAssignPage').hide();
    }
</script>
