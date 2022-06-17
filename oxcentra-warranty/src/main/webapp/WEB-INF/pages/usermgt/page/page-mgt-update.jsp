<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade" id="modalUpdateTask" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateTaskLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateTaskLabel">Update Page</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetResponseMsg()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="updatePageForm" modelAttribute="page" method="post"
                       name="updatePageForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Page Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="pageCode" name="pageCode" type="text"
                                        class="form-control form-control-sm" id="editPageCode" maxlength="16"
                                        placeholder="Page Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Url<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="url" name="url" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="32"
                                        id="editUrl" placeholder="Url"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="64"
                                        id="editDescription" placeholder="Description"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Sort Key<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:input path="sortKey" name="sortKey" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^0-9]/g,''))"
                                        class="form-control form-control-sm" maxlength="3"
                                        id="editSortKey" placeholder="Sort Key"/>
                        </div>
                    </div>
                    <div class="form-group row" id="updateDualAuthFlag">
                        <label class="col-sm-3 col-form-label">Dual Auth<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <form:checkbox
                                    path="currentFlag" name="currentFlag"
                                    id="editDualauth"
                                    class="form-control form-control-sm" cssStyle="width: 20px"
                            />
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-9">
                            <select id="editStatus" class="form-control form-control-sm" name="status" path="status">
                                <option selected value="">Select Status</option>
                                <c:forEach items="${page.statusList}" var="status">
                                    <option value="${status.statusCode}">${status.description}</option>
                                </c:forEach>
                            </select>
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
                        <%--<c:if test="${userPrivileges.updateTask}">--%>
                    <button id="updateBtn" type="button" onclick="update()" class="btn btn-primary">
                        Submit
                    </button>
                        <%--</c:if>--%>
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
            url: '${pageContext.request.contextPath}/updatePage.json',
            data: $('form[name=updatePageForm]').serialize(),
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
                console.log(jqXHR);
                handleAjaxCallErrors(jqXHR.status);
            }
        });
    }


    function resetUpdate() {

        $.ajax({
            url: "${pageContext.request.contextPath}/getPage.json",
            data: {
                pageCode: $('#editPageCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",

            success: function (data) {
                $('#editPageCode').val(data.pageCode);
                $('#editPageCode').attr('readOnly', true);
                $('#editDescription').val(data.description);
                $('#editUrl').val(data.url);
                $('#editUrl').attr('readOnly', true);
                $('#editSortKey').val(data.sortKey);
                $('#editDualauth').prop("checked", data.cFlag);
                if (data.aFlag === true) {
                    $("#updateDualAuthFlag").attr("hidden", false);
                } else {
                    $("#updateDualAuthFlag").attr("hidden", true);
                }

                $('#editStatus').val(data.status);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/login.jsp";
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

    function resetResponseMsg() {
        $('#responseMsgUpdate').hide();
    }

</script>

