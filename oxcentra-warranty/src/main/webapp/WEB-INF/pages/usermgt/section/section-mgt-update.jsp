<%--
  Created by IntelliJ IDEA.
  User: maheshi_c
  Date: 3/23/2021
  Time: 10:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateSection" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateSectionLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateSectionLabel">Update Section</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateSectionForm" modelAttribute="section" method="post"
                       name="updateSectionForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>


                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Section Code<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="sectionCode" name="sectionCode" type="text"
                                        class="form-control form-control-sm"
                                        id="eSectionCode" maxlength="8" placeholder="Section Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="128"
                                        id="eDescription" placeholder="Description"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${section.statusList}" var="status">
                                    <form:option value="${status.statusCode}">${status.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Sort Key<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="sortKey" name="sortKey" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^0-9 ]/g, ''))"
                                        class="form-control form-control-sm" maxlength="3"
                                        id="eSortKey" placeholder="Sort Key"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                </div>


                <div class="modal-footer justify-content-end">
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${section.vupdate}">
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
        resetUpdateSectionForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateSection.json',
            data: $('form[name=updateSectionForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getSection.json",
            data: {
                sectionCode: $('#eSectionCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eSectionCode').val(data.sectionCode);
                $('#eSectionCode').attr('readOnly', true);
                $('#eDescription').val(data.description);
                $('#eStatus').val(data.status);
                $('#eSortKey').val(data.sortKey);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateSectionForm();
    }

    function resetUpdateSectionForm() {
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
