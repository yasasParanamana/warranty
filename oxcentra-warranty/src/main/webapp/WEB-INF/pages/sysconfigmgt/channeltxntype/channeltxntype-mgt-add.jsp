<%--
  Created by IntelliJ IDEA.
  User: namila_w
  Date: 11/21/2021
  Time: 2:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalAddChannelTxnType" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert Channel Transaction Type</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addChannelTxnTypeForm" modelAttribute="channelTxnType" method="post"
                       name="addChannelTxnTypeForm">
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
                        <label for="txntype" class="col-sm-4 col-form-label">Transaction Type<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select id="txntype" name="txntype" class="form-control form-control-sm" path="txntype">
                                <option selected value="">Select Transaction Type</option>
                                <c:forEach items="${channelTxnType.txnTypeList}" var="txntype">
                                    <form:option value="${txntype.txntype}">${txntype.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="channel" class="col-sm-4 col-form-label">SMS Channel<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select id="channel" name="channel" class="form-control form-control-sm" path="channel">
                                <option selected value="">Select SMS Channel</option>
                                <c:forEach items="${channelTxnType.channelList}" var="channel">
                                    <form:option value="${channel.channelCode}">${channel.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="template" class="col-sm-4 col-form-label">SMS Template<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select id="template" name="template" class="form-control form-control-sm" path="template">
                                <option selected value="">Select SMS Template</option>
                                <c:forEach items="${channelTxnType.templateList}" var="template">
                                    <form:option value="${template.templatecode}">${template.description}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus" readonly="true">
                                <c:forEach items="${channelTxnType.statusActList}" var="status">
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
                    <c:if test="${channelTxnType.vadd}">
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
        $('form[name=addChannelTxnTypeForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddChannelTxnTypeForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addChannelTxnType.json',
            data: $('form[name=addChannelTxnTypeForm]').serialize(),
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
                    $('form[name=addChannelTxnTypeForm]').trigger("reset");
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

    function resetAddChannelTxnTypeForm() {
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
