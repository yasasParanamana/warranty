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

<div class="modal fade" id="modalUpdateDealership" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalUpdateDealershipLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalUpdateDealershipLabel">Update dealership</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateDealershipForm" modelAttribute="dealership" method="post"
                       name="updateDealershipForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Dealership Code<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="dealershipCode" name="dealershipCode" type="text"
                                        class="form-control form-control-sm"
                                        id="eDealershipCode" maxlength="15" placeholder="Dealership Code" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Dealership Name<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="dealershipName" name="dealershipName" type="text"
                                        class="form-control form-control-sm"
                                        id="eDealershipName" maxlength="50" placeholder="Dealership Name" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Dealership Phone<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="dealershipPhone" name="dealershipPhone" type="text"
                                        class="form-control form-control-sm"
                                        id="eDealershipPhone" maxlength="50" placeholder="Dealership Phone" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Dealership Email<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="dealershipEmail" name="dealershipEmail" type="text"
                                        class="form-control form-control-sm"
                                        id="eDealershipEmail" maxlength="50" placeholder="Dealership Email" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Dealership Address<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="dealershipAddress" name="dealershipAddress" type="text"
                                        class="form-control form-control-sm"
                                        id="eDealershipAddress" maxlength="50" placeholder="Dealership Address" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${dealership.statusList}" var="status">
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
                    <button id="updateReset" type="button" class="btn btn-default" onclick="resetUpdate()">Reset
                    </button>
                    <c:if test="${dealership.vupdate}">
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
        resetUpdateDealershipForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateDealership.json',
            data: $('form[name=updateDealershipForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getDealership.json",
            data: {
                dealershipCode: $('#eDealershipCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eDealershipCode').val(data.dealershipCode);
                $('#eDealershipCode').attr('readOnly', true);

                $('#eDealershipName').val(data.dealershipName);
                $('#eDealershipPhone').val(data.dealershipPhone);
                $('#eDealershipEmail').val(data.dealershipEmail);
                $('#eDealershipAddress').val(data.dealershipAddress);
                $('#eStatus').val(data.status);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateDealershipForm();
    }

    function resetUpdateDealershipForm() {
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
