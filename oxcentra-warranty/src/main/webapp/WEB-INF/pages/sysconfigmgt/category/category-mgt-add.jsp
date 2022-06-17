<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 2/25/2021
  Time: 3:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalAddCategory" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert Category</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addCategoryForm" modelAttribute="category" method="post"
                       name="addCategoryForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="aUserTask" value="ADD" placeholder="User Task" autocomplete="off"/>
                        </div>
                    </div>


                    <div class="form-group row">
                        <label for="categoryCode" class="col-sm-4 col-form-label">Category<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="categoryCode" name="categoryCode" type="text"
                                        class="form-control form-control-sm"
                                        id="aCategoryCode" maxlength="8" placeholder="Category code"
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
                                        id="aDescription" maxlength="64" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Enable Bulk<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            Yes <form:radiobutton path="bulkEnable" id="bulkEnableYes" value="YES"/>
                            <span></span>
                            No <form:radiobutton path="bulkEnable" id="bulkEnableNo" value="NO"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus"
                                         readonly="true">
                                <c:forEach items="${category.statusActList}" var="status">
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
                                         id="aPriority"
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
                                        id="aTelqueue" maxlength="8" placeholder="Time to live in Queue"
                                        onkeyup="$(this).val($(this).val().replace(/[^0-9]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Unsubscribe<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            Yes <form:radiobutton path="unsubscribe" id="unsubscribeYes" value="YES"/>
                            <span></span>
                            No <form:radiobutton path="unsubscribe" id="unsubscribeNo" value="NO"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Acknowledgment Wait<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            Yes <form:radiobutton path="ackwait" id="ackwaitYes" value="YES"/>
                            <span></span>
                            No <form:radiobutton path="ackwait" id="ackwaitNo" value="NO"/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${category.vadd}">
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
    function resetAdd() {
        $('form[name=addCategoryForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddCategoryForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addCategory.json',
            data: $('form[name=addCategoryForm]').serialize(),
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
                    $('form[name=addCategoryForm]').trigger("reset");
                    search();
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

    function resetAddCategoryForm() {
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
