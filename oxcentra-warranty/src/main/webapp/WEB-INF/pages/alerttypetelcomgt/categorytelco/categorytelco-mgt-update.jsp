<%--
  Created by IntelliJ IDEA.
  User: akila_s
  Date: 3/29/2021
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="modal fade" id="modalUpdateCategoryTelco" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update Category Telco</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateCategoryTelcoForm" name="updateCategoryTelcoForm"
                       modelAttribute="categoryTelco" method="post">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="eUserTask" value="UPDATE"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Category<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="categoryDescription" name="category" type="text"
                                        class="form-control form-control-sm"
                                        id="eCategoryDescription" readonly="true"/>

                            <form:input path="category" name="category" type="text" class="form-control form-control-sm"
                                        id="eCategory" hidden="true" readonly="true"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Telco<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="telco" name="telco" class="form-control form-control-sm" id="eTelco">
                                <form:options items="${categoryTelco.telcoList}" itemLabel="description"
                                              itemValue="code"/>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">MT Port<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="mtPort" name="mtPort" class="form-control form-control-sm" id="eMtPort">
                                <c:forEach items="${categoryTelco.mtPortList}" var="mtPort">
                                    <option value="${mtPort}">${mtPort}</option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus">
                                <form:options items="${categoryTelco.statusList}" itemLabel="description"
                                              itemValue="statusCode"/>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>


                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${categoryTelco.vupdate}">
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
        resetUpdateCategoryTelcoForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateCategoryTelco.json',
            data: $('form[name=updateCategoryTelcoForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getCategoryTelco.json",
            data: {
                category: $('#eCategory').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eCategory').val(data.category);
                $('#eCategory').attr('readOnly', true);
                $('#eCategoryDescription').val(data.categoryDescription);
                $('#eCategoryDescription').attr('readOnly', true);
                $('#eTelco').val(data.telco);
                $('#eMtPort').val(data.mtPort);
                $('#eStatus').val(data.status);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateCategoryTelcoForm();
    }

    function resetUpdateCategoryTelcoForm() {
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
