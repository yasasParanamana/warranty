<%--
  Created by IntelliJ IDEA.
  User: dilanka_w
  Date: 1/11/2021
  Time: 6:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- add section modal popup start -->

<div class="modal fade " id="modalAddClaim" data-backdrop="static" tabindex="-1" role="dialog"
     aria-labelledby="modalAddTaskLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title" id="modalAddTaskLabel">Insert Claim</h6>
                <button type="button" id="addPopupClose" class="close" data-dismiss="modal" aria-label="Close"
                        onclick="resetAdd()">
                    <i aria-hidden="true" class="ki ki-close"></i>
                </button>
            </div>
            <form:form class="form-horizontal sm" id="addTaskForm" modelAttribute="claim" method="post"
                       name="addTaskForm">

                <div class="modal-body">
                    <div class="form-group"><span id="responseMsgAdd"></span>

                    </div>

                    <h5>Vehicle Details</h5>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="chassis">Chassis Number</label>

                            <form:input path="chassis" name="chassis" type="text"
                                        class="form-control form-control-sm" id="addchassisNumber" maxlength="16"
                                        placeholder="Chassis Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Model</label>
                            <form:input path="model" name="model" type="text"
                                        class="form-control form-control-sm" id="addmodel" maxlength="16"
                                        placeholder="Model"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <hr>
                    <h5>Customer Details</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="chassis">First Name</label>

                            <form:input path="firstName" name="firstName" type="text"
                                        class="form-control form-control-sm" id="addfirstName" maxlength="16"
                                        placeholder="First Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Last Name</label>
                            <form:input path="lastName" name="lastName" type="text"
                                        class="form-control form-control-sm" id="addlastName" maxlength="16"
                                        placeholder="Last Name"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Phone Number</label>
                            <form:input path="phone" name="phone" type="text"
                                        class="form-control form-control-sm" id="addphone" maxlength="16"
                                        placeholder="Phone Number"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Email</label>
                            <form:input path="email" name="email" type="text"
                                        class="form-control form-control-sm" id="addemail" maxlength="16"
                                        placeholder="Email"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="chassis">Address</label>

                            <form:input path="address" name="address" type="text"
                                        class="form-control form-control-sm" id="addaddress" maxlength="16"
                                        placeholder="Address"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">surburb</label>
                            <form:input path="surburb" name="surburb" type="text"
                                        class="form-control form-control-sm" id="addsurburb" maxlength="16"
                                        placeholder="Surburb"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">State</label>
                            <form:input path="state" name="state" type="text"
                                        class="form-control form-control-sm" id="addphone" maxlength="16"
                                        placeholder="State"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Post Code</label>
                            <form:input path="postcode" name="postcode" type="text"
                                        class="form-control form-control-sm" id="addpostcode" maxlength="16"
                                        placeholder="Post Code"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <hr>
                    <h5>Dealership & Purchase Deatils</h5>

                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label for="chassis">Dealership</label>

                            <form:input path="dealership" name="dealership" type="text"
                                        class="form-control form-control-sm" id="adddealership" maxlength="16"
                                        placeholder="Dealership"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="model">Purchasing Date</label>
                            <form:input path="purchasingDate" name="purchasingDate" type="text"
                                        class="form-control form-control-sm" id="addsurburb" maxlength="16"
                                        placeholder="purchasingDate"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>

                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-12">
                            <label for="chassis">Description</label>

                            <form:textarea path="description" name="description" type="text"
                                        class="form-control form-control-sm" id="adddescription" maxlength="16"
                                        placeholder="Description"
                                        onkeyup="this.value=this.value.toUpperCase(),$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g,''))"/>
                        </div>
                    </div>

                    <br/>
                    <div class="form-group">
                        <span class="text-danger">Required fields are marked by the '*'</span>
                    </div>
                    <!-- /.card-body -->
                </div>
                <div class="modal-footer justify-content-end">
                    <button id="addReset" type="button" class="btn btn-default" onclick="resetAdd()">Reset</button>
                    <c:if test="${claim.vadd}">
                        <button id="addBtn" type="button" onclick="add()" class="btn btn-primary">
                            Submit
                        </button>
                    </c:if>
                </div>
            </form:form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>

    function resetAdd() {
        $('form[name=addTaskForm]').trigger("reset");
        $('#responseMsgAdd').hide();
    }

    function add() {
        resetAddClaimFormData();
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/addTask.json',
            data: $('form[name=addTaskForm]').serialize(),
            beforeSend: function (xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {

                if (res.flag) { //success
                    $('#responseMsgAdd').show();
                    $('#responseMsgAdd').addClass('success-response').text(res.successMessage);
                    $('form[name=addTaskForm]').trigger("reset");
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

    function resetAddClaimFormData() {
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
