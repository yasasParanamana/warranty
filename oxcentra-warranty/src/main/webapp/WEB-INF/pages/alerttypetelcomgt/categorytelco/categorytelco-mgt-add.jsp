<%--
  Created by IntelliJ IDEA.
  User: akila_s
  Date: 3/26/2021
  Time: 11:51 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalAddCategoryTelco" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert Category Telco</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addCategoryTelcoForm" modelAttribute="categoryTelco" method="post"
                       name="addCategoryTelcoForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="aUserTask" value="ADD" placeholder="User Task"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="category" class="col-sm-4 col-form-label">Category<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select id="category" name="category" class="form-control form-control-sm"
                                         path="category">
                                <form:option value="">Select Category</form:option>
                                <form:options items="${categoryTelco.categoryList}" itemValue="category"
                                              itemLabel="description"/>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="telco" class="col-sm-4 col-form-label">Telco<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select id="telco" name="telco" class="form-control form-control-sm" path="">
                                <form:option value="">Select Telco</form:option>
                                <form:options items="${categoryTelco.telcoList}" itemValue="code"
                                              itemLabel="description"/>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="mtPort" class="col-sm-4 col-form-label">MT Port<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <select id="mtPort" name="mtPort" class="form-control form-control-sm">
                                <option selected value="">Select MT Port</option>
                                <c:forEach items="${categoryTelco.mtPortList}" var="mtPort">
                                    <option value="${mtPort}">${mtPort}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus"
                                         readonly="true">
                                <c:forEach items="${categoryTelco.statusActList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>

                <div class="modal-footer justify-content-end">
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${categoryTelco.vadd}">
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
        $('form[name=addCategoryTelcoForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddCategoryTelcoForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addCategoryTelco.json',
            data: $('form[name=addCategoryTelcoForm]').serialize(),
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
                    $('form[name=addCategoryTelcoForm]').trigger("reset");
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

    function resetAddCategoryTelcoForm() {
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
