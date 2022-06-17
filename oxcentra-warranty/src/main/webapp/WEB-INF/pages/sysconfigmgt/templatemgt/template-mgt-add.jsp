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

<div class="modal fade" id="modalAddTemplatemgt" data-backdrop="static">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert SMS Template</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addSMSTemplateForm" modelAttribute="templatemgt" method="post"
                       name="addSMSTemplateForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span></div>

                    <div class="form-group row">
                        <div class="col-lg-4">
                            <label>Code<span
                                    class="text-danger">*</span></label>
                            <form:input path="code" name="code" type="text"
                                        class="form-control form-control-sm"
                                        id="aCode" maxlength="10" placeholder="Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"
                                        autocomplete="off"/>
                        </div>
                        <div class="col-lg-4">
                            <label>Description<span
                                    class="text-danger">*</span></label>
                                <form:input path="description" name="description" type="text"
                                            class="form-control form-control-sm"
                                            id="aDescription" maxlength="50" placeholder="Description"
                                            onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                            onmouseout="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                            autocomplete="off"/>
                        </div>
                        <div class="col-lg-4">
                            <label>Status<span
                                    class="text-danger">*</span></label>
                                <form:select path="status" name="status" class="form-control form-control-sm"
                                             id="aStatus" readonly="true">
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
                                        id="des1" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>
                        <div class="col-sm-4">
                            <form:select path="field1" name="field1" class="form-control form-control-sm" id="field1"
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
                                        id="des2" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field2" name="field2" class="form-control form-control-sm" id="field2"
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
                                        id="des3" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field3" name="field3" class="form-control form-control-sm" id="field3"
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
                                        id="des4" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field4" name="field4" class="form-control form-control-sm" id="field4"
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
                                        id="des5" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field5" name="field5" class="form-control form-control-sm" id="field5"
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
                                        id="des6" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field6" name="field6" class="form-control form-control-sm" id="field6"
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
                                        id="des7" maxlength="64"
                                        onkeyup="$(this).val($(this).val().replace(/[|]/g,''))"
                                        onmouseout="$(this).val($(this).val().replace(/[|]/g,''))"
                                        autocomplete="off"/>
                        </div>

                        <div class="col-sm-4">
                            <form:select path="field7" name="field7" class="form-control form-control-sm" id="field7"
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
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${templatemgt.vadd}">
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
        $('form[name=addSMSTemplateForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddSMSTemplateForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addSMSTemplateMgt.json',
            data: $('form[name=addSMSTemplateForm]').serialize(),
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
                    $('form[name=addSMSTemplateForm]').trigger("reset");
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

    function resetAddSMSTemplateForm() {
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
