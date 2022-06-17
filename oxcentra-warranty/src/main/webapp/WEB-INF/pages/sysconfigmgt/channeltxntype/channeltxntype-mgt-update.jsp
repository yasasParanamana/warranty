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
<div class="modal fade" id="modalUpdateChannelTxnType" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update Channel Transaction Type</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateChannelTxnTypeForm" name="updateChannelTxnTypeForm"
                       modelAttribute="channelTxnType" method="post">
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
                            Transaction Type<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="txntypeDescription" name="txntype" type="text"
                                        class="form-control form-control-sm"
                                        id="eTxnTypeDescription" readonly="true"/>

                            <form:input path="txntype" name="txntype" type="text" class="form-control form-control-sm"
                                        id="eTxnType" hidden="true" readonly="true"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">SMS Channel<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="channelDescription" name="channel" type="text"
                                        class="form-control form-control-sm"
                                        id="eChannelDescription" readonly="true"/>

                            <form:input path="channel" name="channel" type="text" class="form-control form-control-sm"
                                        id="eChannel" hidden="true" readonly="true"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">SMS Template<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="template" name="template" class="form-control form-control-sm" id="eTemplate">
                                <form:options items="${channelTxnType.templateList}" itemLabel="description"
                                              itemValue="templatecode"/>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus">
                                <form:options items="${channelTxnType.statusList}" itemLabel="description"
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
                    <c:if test="${channelTxnType.vupdate}">
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
        resetUpdateChannelTxnTypeForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateChannelTxnType.json',
            data: $('form[name=updateChannelTxnTypeForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getChannelTxnType.json",
            data: {
                txntype: $('#eTxnType').val(),
                channel: $('#eChannel').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eTxnType').val(data.txntype).attr('readOnly', true);
                $('#eTxnTypeDescription').val(data.txntypeDescription).attr('readOnly', true);

                $('#eChannel').val(data.channelcode).attr('readOnly', true);
                $('#eChannelDescription').val(data.channelDescription).attr('readOnly', true);

                $('#eTemplate').val(data.templatecode);
                $('#eStatus').val(data.status);

            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateChannelTxnTypeForm();
    }

    function resetUpdateChannelTxnTypeForm() {
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

