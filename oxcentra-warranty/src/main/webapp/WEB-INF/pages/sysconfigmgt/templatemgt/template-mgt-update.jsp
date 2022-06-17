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
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update SMS Template</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updateSMSTemplateForm" modelAttribute="templatemgt" method="post"
                       name="updateSMSTemplateForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row">
                        <div class="col-lg-4">
                            <label>Code<span
                                    class="text-danger">*</span></label>
                            <form:input path="code" name="code" type="text"
                                        class="form-control form-control-sm"
                                        id="eCode" maxlength="10" placeholder="Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"
                                        autocomplete="off"/>
                        </div>
                        <div class="col-lg-4">
                            <label>Description<span
                                    class="text-danger">*</span></label>
                            <form:input path="description" name="description" type="text"
                                        class="form-control form-control-sm"
                                        id="eDescription" maxlength="50" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        autocomplete="off"/>
                        </div>
                        <div class="col-lg-4">
                            <label>Status<span
                                    class="text-danger">*</span></label>
                            <form:select path="status" name="status" class="form-control form-control-sm"
                                         id="eStatus" readonly="true">
                                <c:forEach items="${templatemgt.statusActList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-12 col-form-label">Message Format<span
                                class="text-danger">*</span></label>
                    </div>

                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des1" name="des1" type="text"
                                        class="form-control form-control-sm"
                                        id="des1Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>
                        <div class="col-sm-4">
                            <form:select path="field1" name="field1" class="form-control form-control-sm" id="field1Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des2" name="des2" type="text"
                                        class="form-control form-control-sm"
                                        id="des2Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field2" name="field2" class="form-control form-control-sm" id="field2Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des3" name="des3" type="text"
                                        class="form-control form-control-sm"
                                        id="des3Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field3" name="field3" class="form-control form-control-sm" id="field3Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des4" name="des4" type="text"
                                        class="form-control form-control-sm"
                                        id="des4Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field4" name="field4" class="form-control form-control-sm" id="field4Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des5" name="des5" type="text"
                                        class="form-control form-control-sm"
                                        id="des5Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field5" name="field5" class="form-control form-control-sm" id="field5Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des6" name="des6" type="text"
                                        class="form-control form-control-sm"
                                        id="des6Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field6" name="field6" class="form-control form-control-sm" id="field6Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-8">
                            <form:input path="des7" name="des7" type="text"
                                        class="form-control form-control-sm"
                                        id="des7Edit" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field7" name="field7" class="form-control form-control-sm" id="field7Edit"
                                         readonly="true">
                                <option selected value="">Select Field</option>
                                <c:forEach items="${templatemgt.comparisonfieldList}" var="comparisonfield">
                                    <form:option value="${comparisonfield.comparisonfieldcode}">${comparisonfield.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                </div>
                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${templatemgt.vupdate}">
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
        resetUpdateSMSTemplateForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateSMSTemplateMgt.json',
            data: $('form[name=updateSMSTemplateForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getSMSTemplateMgt.json",
            data: {
                code: $('#eCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eCode').val(data.code);
                $('#eCode').attr('readOnly', true);
                $('#eDescription').val(data.description);
                $('#eStatus').val(data.status);
                $('#des1Edit').val(data.des1);
                $('#des2Edit').val(data.des2);
                $('#des3Edit').val(data.des3);
                $('#des4Edit').val(data.des4);
                $('#des5Edit').val(data.des5);
                $('#des6Edit').val(data.des6);
                $('#des7Edit').val(data.des7);
                $('#field1Edit').val(data.field1);
                $('#field2Edit').val(data.field2);
                $('#field3Edit').val(data.field3);
                $('#field4Edit').val(data.field4);
                $('#field5Edit').val(data.field5);
                $('#field6Edit').val(data.field6);
                $('#field7Edit').val(data.field7);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateSMSTemplateForm();
    }

    function resetUpdateSMSTemplateForm() {
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
