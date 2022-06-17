<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 2/25/2021
  Time: 3:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateCategory" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update Category</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updateCategoryForm" modelAttribute="category" method="post"
                       name="updateCategoryForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="eUserTask" value="UPDATE" placeholder="User Task" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="categoryCode" class="col-sm-4 col-form-label">Category<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="categoryCode" name="categoryCode" type="text"
                                        class="form-control form-control-sm"
                                        id="eCategoryCode" maxlength="8" placeholder="Category code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>


                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="description" name="description" type="text"
                                        class="form-control form-control-sm"
                                        id="eDescription" maxlength="64" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Enable Bulk<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            Yes <form:radiobutton path="bulkEnable" id="eBulkEnableYes" class="eBulkEnableYes"
                                                  value="YES"/>
                            <span></span>
                            No <form:radiobutton path="bulkEnable" id="eBulkEnableNo" class="eBulkEnableNo" value="NO"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${category.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Priority<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="priority" name="status" class="form-control form-control-sm"
                                         id="ePriority"
                                         readonly="true">
                                <c:forEach items="${category.priorityList}" var="priority">
                                    <form:option value="${priority.priorityLevel}">${priority.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="categoryCode" class="col-sm-4 col-form-label">Time to live in Queue<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="ttlqueue" name="ttlqueue" type="text"
                                        class="form-control form-control-sm"
                                        id="eTelqueue" maxlength="8" placeholder="Time to live in Queue"
                                        onkeyup="$(this).val($(this).val().replace(/[^0-9]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Unsubscribe<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            Yes <form:radiobutton path="unsubscribe" id="eunsubscribeYes" class="eunsubscribeYes" value="YES"/>
                            <span></span>
                            No <form:radiobutton path="unsubscribe" id="eunsubscribeNo" class="eunsubscribeNo" value="NO"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Acknowledgment Wait<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            Yes <form:radiobutton path="ackwait" id="eackwaitYes" class="eackwaitYes" value="YES"/>
                            <span></span>
                            No <form:radiobutton path="ackwait" id="eackwaitNo" class="eackwaitYes" value="NO"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${category.vupdate}">
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
        resetUpdateCategoryForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateCategory.json',
            data: $('form[name=updateCategoryForm]').serialize(),
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
                    search();
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
            url: "${pageContext.request.contextPath}/getCategory.json",
            data: {
                categoryCode: $('#eCategoryCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eCategoryCode').val(data.category);
                $('#eCategoryCode').attr('readOnly', true);
                $('#eDescription').val(data.description);
                $('#eTelqueue').val(data.ttlqueue);

                //check check box
                if (data.isBulk === 'YES') {
                    $("#eBulkEnableYes").prop("checked", true);
                } else {
                    $("#eBulkEnableNo").prop("checked", true);
                }

                if (data.ackwait === 'YES') {
                    $("#eeackwaitYes").prop("checked", true);
                } else {
                    $("#eeackwaitNo").prop("checked", true);
                }

                if (data.unsubscribe === 'YES') {
                    $("#eunsubscribeYes").prop("checked", true);
                } else {
                    $("#eunsubscribeNo").prop("checked", true);
                }

                $('#eStatus').val(data.status);
                $('#ePriority').val(data.priority);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateCategoryForm();
    }

    function resetUpdateCategoryForm() {
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
