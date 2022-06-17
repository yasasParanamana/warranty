<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 2/22/2021
  Time: 1:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="modal fade" id="modalUpdateChannel" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Update Channel</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="updateChannelForm" modelAttribute="channel" method="post"
                       name="updateChannelForm">
                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgUpdate"></span></div>

                    <div class="form-group row" hidden="true">
                        <label for="userTask" class="col-sm-4 col-form-label">User Task<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="userTask" name="userTask" type="text" class="form-control form-control-sm"
                                        id="eUserTask" value="UPDATE" placeholder="User Task"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">
                            Code<span class="text-danger">*</span>
                        </label>
                        <div class="col-sm-8">
                            <form:input path="channelCode" name="channelCode" type="text"
                                        class="form-control form-control-sm"
                                        id="eChannelCode" maxlength="8" placeholder="Channel Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="description" name="description" type="text"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g,''))"
                                        class="form-control form-control-sm" maxlength="64"
                                        id="eDescription" placeholder="Description" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="eStatus"
                                         readonly="true">
                                <c:forEach items="${channel.statusList}" var="status">
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
                    <c:if test="${channel.vupdate}">
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
        resetUpdateChannelForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/updateChannel.json',
            data: $('form[name=updateChannelForm]').serialize(),
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
            url: "${pageContext.request.contextPath}/getChannel.json",
            data: {
                channelCode: $('#eChannelCode').val()
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {
                $('#eChannelCode').val(data.channelCode);
                $('#eChannelCode').attr('readOnly', true);
                $('#eDescription').val(data.description);
                $('#eStatus').val(data.status);
            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });
        resetUpdateChannelForm();
    }

    function resetUpdateChannelForm() {
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
