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

<div class="modal fade" id="modalAddChannel" data-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Insert Channel</h6>

                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>

            <form:form class="form-horizontal sm" id="addChannelForm" modelAttribute="channel" method="post"
                       name="addChannelForm">
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
                        <label for="channelCode" class="col-sm-4 col-form-label">Channel Code<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="channelCode" name="channelCode" type="text"
                                        class="form-control form-control-sm"
                                        id="aChannelCode" maxlength="8" placeholder="Code"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Description<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="description" name="description" type="text"
                                        class="form-control form-control-sm"
                                        id="aDescription" maxlength="64" placeholder="Description"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="description" class="col-sm-4 col-form-label">Password<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:input path="password" name="password" type="text"
                                        class="form-control form-control-sm"
                                        id="aPassword" maxlength="64" placeholder="Password"
                                        onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                        autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="status" class="col-sm-4 col-form-label">Status<span
                                class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <form:select path="status" name="status" class="form-control form-control-sm" id="aStatus"
                                         readonly="true">
                                <c:forEach items="${channel.statusActList}" var="status">
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
                    <c:if test="${channel.vadd}">
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
        $('form[name=addChannelForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddChannelForm();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addChannel.json',
            data: $('form[name=addChannelForm]').serialize(),
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
                    $('form[name=addChannelForm]').trigger("reset");
                    searchStart();
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

    function resetAddChannelForm() {
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
