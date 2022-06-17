<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 2/8/2021
  Time: 10:02 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <script type="text/javascript">
        var oTable;
        var oTableDual;

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $(document).ready(function () {
            loadDataTable();
            loadDataTableDual();
        });

        function loadDataTable() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            var stringify_aoData = function (aoData) {
                var o = {};
                var modifiers = ['mDataProp_', 'sSearch_', 'iSortCol_', 'bSortable_', 'bRegex_', 'bSearchable_', 'sSortDir_'];
                jQuery.each(aoData, function (idx, obj) {
                    if (obj.name) {
                        for (var i = 0; i < modifiers.length; i++) {
                            if (obj.name.substring(0, modifiers[i].length) == modifiers[i]) {
                                var index = parseInt(obj.name.substring(modifiers[i].length));
                                var key = 'a' + modifiers[i].substring(0, modifiers[i].length - 1);
                                if (!o[key]) {
                                    o[key] = [];
                                }
                                o[key][index] = obj.value;
                                return;
                            }
                        }
                        o[obj.name] = obj.value;
                    } else {
                        o[idx] = obj;
                    }
                });
                return JSON.stringify(o);
            };

            oTable = $('#table').dataTable({
                bServerSide: true,
                sAjaxSource: "${pageContext.servletContext.contextPath}/listSystemUser.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userName', 'value': $('#userName').val()},
                        {'name': 'fullName', 'value': $('#fullName').val()},
                        {'name': 'email', 'value': $('#email').val()},
                        {'name': 'mobileNumber', 'value': $('#mobileNumber').val()},
                        {'name': 'userRoleCode', 'value': $('#userRoleCode').val()},
                        {'name': 'status', 'value': $('#status').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listSystemUser.json",
                        contentType: "application/json",
                        data: stringify_aoData(aoData),
                        success: fnCallback,
                        error: function (e) {
                            window.location = "${pageContext.request.contextPath}/logout.htm";
                        }
                    });
                },
                bJQueryUI: true,
                sPaginationType: "full_numbers",
                bDeferRender: true,
                responsive: true,
                lengthMenu: [5, 10, 20, 50, 100],
                searching: false,
                scrollY: '50vh',
                scrollX: true,
                scrollCollapse: true,
                initComplete: function (settings, json) {
                    document.getElementById('data-table-loading').style.display = "none";
                    document.getElementById('data-table-wrapper').style.display = "block";
                },
                fnDrawCallback: function (oSettings) {
                    $(".table ").css({"width": "100%"});
                },
                columnDefs: [
                    {
                        title: "User Name",
                        targets: 0,
                        mDataProp: "userName",
                        defaultContent: "--"
                    },
                    {
                        title: "Full Name",
                        targets: 1,
                        mDataProp: "fullName",
                        defaultContent: "--"
                    },
                    {
                        title: "User Role Code",
                        targets: 2,
                        mDataProp: "userRoleCode",
                        defaultContent: "--"
                    },
                    {
                        title: "Email",
                        targets: 3,
                        mDataProp: "email",
                        defaultContent: "--"
                    },
                    {
                        title: "Mobile Number",
                        targets: 4,
                        mDataProp: "mobileNumber",
                        defaultContent: "--"
                    },
                    {
                        title: "NIC",
                        targets: 5,
                        mDataProp: "nic",
                        defaultContent: "--"
                    },
                    {
                        title: "Service ID",
                        targets: 6,
                        mDataProp: "serviceid",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Logged Date",
                        targets: 7,
                        mDataProp: "lastLoggedDate",
                        defaultContent: "--",
                        render: function (data) {
                            if(data != null){
                                return moment(data).format("YYYY-MM-DD")
                            } else {
                                return "--"
                            }

                        }
                    },
                    {
                        title: "Status",
                        targets: 8,
                        mDataProp: "status",
                        defaultContent: "--",
                        render: function (data, type, full, meta) {
                            var status = {
                                'Active': {
                                    'title': 'Active',
                                    'class': ' label-light-info'
                                },
                                'Inactive': {
                                    'title': 'Inactive',
                                    'class': ' label-light-danger'
                                },
                                'New': {
                                    'title': 'New',
                                    'class': ' label-light-primary'
                                },
                                'Changed': {
                                    'title': 'Changed',
                                    'class': ' label-light-success'
                                },
                                'Reset': {
                                    'title': 'Reset',
                                    'class': ' label-light-warning'
                                }
                            };
                            if (typeof status[data] === 'undefined') {
                                return data;
                            }
                            return '<span class="label label-lg font-weight-bold' + status[data].class + ' label-inline">' + status[data].title + '</span>';
                        },
                    },
                    {
                        title: "Created User",
                        targets: 9,
                        mDataProp: "createdUser",
                        defaultContent: "--"
                    },
                    {
                        label: 'Created Time',
                        name: 'createdTime',
                        targets: 10,
                        mDataProp: "createdTime",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 11,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 12,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        visible: ${systemuser.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.userName + ' class="btn btn-default btn-sm" onclick="editSystemUser(\'' + full.userName + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 13,
                        defaultContent: "--"
                    },
                    {
                        visible: ${systemuser.vdelete},
                        title: "Delete",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.userName + ' class="btn btn-default btn-sm" onclick="deleteSystemUser(\'' + full.userName + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-delete.svg" alt=""></button>';
                        },
                        targets: 14,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function loadDataTableDual() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            var stringify_aoData = function (aoData) {
                var o = {};
                var modifiers = ['mDataProp_', 'sSearch_', 'iSortCol_', 'bSortable_', 'bRegex_', 'bSearchable_', 'sSortDir_'];
                jQuery.each(aoData, function (idx, obj) {
                    if (obj.name) {
                        for (var i = 0; i < modifiers.length; i++) {
                            if (obj.name.substring(0, modifiers[i].length) == modifiers[i]) {
                                var index = parseInt(obj.name.substring(modifiers[i].length));
                                var key = 'a' + modifiers[i].substring(0, modifiers[i].length - 1);
                                if (!o[key]) {
                                    o[key] = [];
                                }
                                o[key][index] = obj.value;
                                return;
                            }
                        }
                        o[obj.name] = obj.value;
                    } else {
                        o[idx] = obj;
                    }
                });
                return JSON.stringify(o);
            };

            oTableDual = $('#tabledual').dataTable({
                bServerSide: true,
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualSystemUser.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userName', 'value': $('#userName').val()},
                        {'name': 'fullName', 'value': $('#fullName').val()},
                        {'name': 'email', 'value': $('#email').val()},
                        {'name': 'userRoleCode', 'value': $('#userRoleCode').val()},
                        {'name': 'mobileNumber', 'value': $('#mobileNumber').val()},
                        {'name': 'status', 'value': $('#status').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualSystemUser.json",
                        contentType: "application/json",
                        data: stringify_aoData(aoData),
                        beforeSend: function (xhr) {
                            if (header && token) {
                                xhr.setRequestHeader(header, token);
                            }
                        },
                        success: fnCallback,
                        error: function (e) {
                            window.location = "${pageContext.request.contextPath}/logout.htm";
                        }
                    });
                },
                bJQueryUI: true,
                sPaginationType: "full_numbers",
                bDeferRender: true,
                responsive: true,
                lengthMenu: [5, 10, 20, 50, 100],
                searching: false,
                scrollY: '50vh',
                scrollX: true,
                scrollCollapse: true,
                initComplete: function (settings, json) {
                    document.getElementById('data-table-loading-dual').style.display = "none";
                    document.getElementById('data-table-wrapper-dual').style.display = "block";
                },
                fnDrawCallback: function (oSettings) {
                    $(".table ").css({"width": "100%"});
                },
                columnDefs: [
                    {
                        title: "ID",
                        targets: 0,
                        visible: false,
                        mDataProp: "id",
                        defaultContent: "--"
                    },
                    {
                        title: "Username",
                        targets: 1,
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "Full Name",
                        targets: 2,
                        mDataProp: "key2",
                        defaultContent: "--"
                    },
                    {
                        title: "Email",
                        targets: 3,
                        mDataProp: "key3",
                        defaultContent: "--"
                    },
                    {
                        title: "User Role Code",
                        targets: 4,
                        mDataProp: "key4",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 5,
                        mDataProp: "status",
                        defaultContent: "--",
                        render: function (data, type, full, meta) {
                            var status = {
                                'Active': {
                                    'title': 'Active',
                                    'class': ' label-light-info'
                                },
                                'Inactive': {
                                    'title': 'Inactive',
                                    'class': ' label-light-danger'
                                },
                                'New': {
                                    'title': 'New',
                                    'class': ' label-light-primary'
                                },
                                'Changed': {
                                    'title': 'Changed',
                                    'class': ' label-light-success'
                                },
                                'Reset': {
                                    'title': 'Reset',
                                    'class': ' label-light-warning'
                                }
                            };
                            if (typeof status[data] === 'undefined') {
                                return data;
                            }
                            return '<span class="label label-lg font-weight-bold' + status[data].class + ' label-inline">' + status[data].title + '</span>';
                        },
                    },
                    {
                        title: "Mobile Number",
                        targets: 6,
                        mDataProp: "key6",
                        defaultContent: "--"
                    },
                    {
                        title: "NIC",
                        targets: 7,
                        mDataProp: "key8",
                        defaultContent: "--"
                    },
                    {
                        title: "Service ID",
                        targets: 8,
                        mDataProp: "key9",
                        defaultContent: "--"
                    },
                    {
                        title: "Task",
                        targets: 9,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 10,
                        mDataProp: "createdTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 11,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 12,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        visible: ${systemuser.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.id + ' class="btn btn-default btn-sm"  onclick="confirmSystemUser(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 13,
                        defaultContent: "--"
                    },
                    {
                        visible: ${systemuser.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.id + ' class="btn btn-default btn-sm" onclick="rejectSystemUser(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 14,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function editSystemUser(username) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getSystemUser.json",
                data: {
                    userName: username
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $('#responseMsgUpdate').hide();

                    $('#eUserName').val(data.userName);
                    $('#eUserName').attr('readOnly', true);
                    $('#eFullName').val(data.fullName);
                    $('#eEmail').val(data.email);
                    $('#eUserRoleCode').val(data.userRoleCode);
                    $('#eStatus').val(data.status);
                    $('#eMobileNumber').val(data.mobileNumber);
                    $('#eNic').val(data.nic);
                    $('#eServiceId').val(data.serviceid);

                    $('#modalUpdateSystemUser').modal('toggle');
                    $('#modalUpdateSystemUser').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function deleteSystemUser(keyval) {
            $('#deleteCodeCommon').val(keyval);
            $('#modalDeleteCommon').modal('toggle');
            $('#modalDeleteCommon').modal('show');
        }

        function confirmSystemUser(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectSystemUser(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }

        function searchStart() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function resetSearch() {
            $('#userName').val("");
            $('#fullName').val("");
            $('#email').val("");
            $('#userRoleCode').val("");
            $('#status').val("");
            $('#mobileNumber').val("");

            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function openAddModal() {
            $('#modalAddSystemUser').modal('toggle');
            $('#modalAddSystemUser').modal('show');
        }

        function deleteCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/deleteSystemUser.json',
                data: {userName: $('#deleteCodeCommon').val()},
                beforeSend: function (xhr) {
                    if (header && token) {
                        xhr.setRequestHeader(header, token);
                    }
                },
                success: function (res) {
                    //close delete modal
                    $('#modalDeleteCommon').modal('toggle');
                    //open delete process modal
                    $('#modalDeleteProcessCommon').modal('toggle');
                    $('#modalDeleteProcessCommon').modal('show');
                    if (res.flag) {
                        //success
                        $('#responseMsgDelete').show();
                        $('#responseMsgDelete').addClass('success-response').text(res.successMessage);
                        searchStart();
                    } else {
                        //Set error messages
                        $('#responseMsgDelete').show();
                        $('#responseMsgDelete').addClass('error-response').text(res.errorMessage);
                    }
                },
                error: function (jqXHR) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function confirmCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/confirmSystemUser.json',
                data: {id: $('#idConfirm').val()},
                beforeSend: function (xhr) {
                    if (header && token) {
                        xhr.setRequestHeader(header, token);
                    }
                },
                success: function (res) {
                    //close delete modal
                    $('#modalConfirmCommon').modal('toggle');
                    //open delete process modal
                    $('#modalConfirmProcessCommon').modal('toggle');
                    $('#modalConfirmProcessCommon').modal('show');

                    if (res.flag) {
                        //success
                        $('#responseMsgConfirm').show();
                        $('#responseMsgConfirm').addClass('success-response').text(res.successMessage);
                        $('form[name=addSystemUserForm]').trigger("reset");
                        searchStart();
                    } else {
                        $('#responseMsgConfirm').show();
                        $('#responseMsgConfirm').addClass('error-response').text(res.errorMessage);
                    }
                },
                error: function (jqXHR) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function rejectCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/rejectSystemUser.json',
                data: {id: $('#idReject').val()},
                beforeSend: function (xhr) {
                    if (header && token) {
                        xhr.setRequestHeader(header, token);
                    }
                },
                success: function (res) {
                    //close delete modal
                    $('#modalRejectCommon').modal('toggle');
                    //open delete process modal
                    $('#modalRejectProcessCommon').modal('toggle');
                    $('#modalRejectProcessCommon').modal('show');

                    if (res.flag) {
                        //success
                        $('#responseMsgReject').show();
                        $('#responseMsgReject').addClass('success-response').text(res.successMessage);
                        $('form[name=addSystemUserForm]').trigger("reset");
                        searchStart();
                    } else {
                        $('#responseMsgReject').show();
                        $('#responseMsgReject').addClass('error-response').text(res.errorMessage);
                    }
                },
                error: function (jqXHR) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }
    </script>
</head>

<!--begin::Content-->
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
    <!--begin::Subheader-->
    <div class="subheader py-2 py-lg-6 subheader-solid" id="kt_subheader">
        <div class="container-fluid d-flex align-items-center justify-content-between flex-wrap flex-sm-nowrap">
            <!--begin::Info-->
            <div class="d-flex align-items-center flex-wrap mr-1">
                <!--begin::Page Heading-->
                <div class="d-flex align-items-baseline flex-wrap mr-5">
                    <!--begin::Page Title-->
                    <h5 class="text-dark font-weight-bold my-1 mr-5">System User Management</h5>
                    <!--end::Page Title-->
                </div>
                <!--end::Page Heading-->
            </div>
            <!--end::Info-->
        </div>
    </div>
    <!--end::Subheader-->
    <!--begin::Entry-->
    <div class="d-flex flex-column-fluid">
        <!--begin::Container-->
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <!--begin::Card-->
                    <div class="card card-custom gutter-b">
                        <div class="card-header">
                            <h3 class="card-title">Search System User</h3>
                        </div>
                        <!--begin::Form-->
                        <form:form class="form" id="systemuserform" name="systemuserform" action="addSystemUser"
                                   theme="simple" method="post" modelAttribute="systemuser">
                            <%--                        <form class="form">--%>
                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-3">
                                        <label>Username:</label>
                                        <input id="userName" name="userName" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                               maxlength="16" class="form-control"
                                               placeholder="Username">
                                        <span class="form-text text-muted">Please enter username</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Full Name:</label>
                                        <input id="fullName" name="fullName" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                               maxlength="128" class="form-control "
                                               placeholder="Full Name">
                                        <span class="form-text text-muted">Please enter full name</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Email:</label>
                                        <input id="email" name="email" type="text" onkeyup="" maxlength="128"
                                               class="form-control" placeholder="Email">
                                        <span class="form-text text-muted">Please enter email</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Mobile Number:</label>
                                        <input id="mobileNumber" name="mobileNumber" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^\d]/ig, ''))"
                                               maxlength="10"
                                               class="form-control form-control-sm" placeholder="Mobile Number">

                                        <span class="form-text text-muted">Please enter mobile number</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>User Role Code:</label>
                                        <select id="userRoleCode" name="userRoleCode"
                                                class="form-control">
                                            <option selected value="">Select User Role Code</option>
                                            <c:forEach items="${systemuser.userRoleList}" var="userRole">
                                                <option value="${userRole.userroleCode}">${userRole.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please select user role code</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Status:</label>
                                        <select id="status" name="status" class="form-control">
                                            <option selected value="">Select Status</option>
                                            <c:forEach items="${systemuser.statusList}" var="status">
                                                <option value="${status.statusCode}">${status.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please select status</span>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="row">
                                    <div class="col-lg-6">
                                        <button type="button" class="btn btn-primary mr-2 btn-sm"
                                                onclick="searchStart()">
                                            Search
                                        </button>
                                        <button type="reset" class="btn btn-secondary btn-sm" onclick="resetSearch()">
                                            Reset
                                        </button>
                                    </div>
                                </div>

                                <!--end::Form-->
                            </div>
                        </form:form>
                        <!--end::Card-->
                    </div>
                </div>
            </div>
            <!--begin::Card-->
            <div class="card card-custom gutter-b">
                <div class="card-header flex-wrap border-0 pt-6 pb-0">
                    <div class="card-title">
                        <h3 class="card-label">System User Management
                            <span class="d-block text-muted pt-2 font-size-sm">System User list</span></h3>
                    </div>
                    <div class="card-toolbar">
                        <!--begin::Button-->
                        <c:if test="${systemuser.vadd}">
                            <a href="#" onclick="openAddModal()" class="btn btn-sm btn-primary font-weight-bolder">
											<span class="svg-icon svg-icon-md">
												<!--begin::Svg Icon | path:assets/media/svg/icons/Design/Flatten.svg-->
												<svg xmlns="http://www.w3.org/2000/svg"
                                                     width="24px"
                                                     height="24px" viewBox="0 0 24 24" version="1.1">
													<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
														<rect x="0" y="0" width="24" height="24"/>
														<circle fill="#000000" cx="9" cy="15" r="6"/>
														<path d="M8.8012943,7.00241953 C9.83837775,5.20768121 11.7781543,4 14,4 C17.3137085,4 20,6.6862915 20,10 C20,12.2218457 18.7923188,14.1616223 16.9975805,15.1987057 C16.9991904,15.1326658 17,15.0664274 17,15 C17,10.581722 13.418278,7 9,7 C8.93357256,7 8.86733422,7.00080962 8.8012943,7.00241953 Z"
                                                              fill="#000000" opacity="0.3"/>
													</g>
												</svg>
                                                <!--end::Svg Icon-->
											</span>New Record</a>
                        </c:if>
                        <!--end::Button-->
                    </div>
                </div>
                <div class="card-body">
                    <!--begin: Datatable-->
                    <div id="data-table-loading" style="display: block;">
                        <div class="loader"></div>
                        <div class="loading-text">Loading..</div>
                    </div>
                    <div id="data-table-wrapper" style="display: none;">
                        <table class="table table-separate table-head-custom table-checkable" id="table">
                            <thead>
                            <tr>
                                <th>User Name</th>
                                <th>Full Name</th>
                                <th>User Role Code</th>
                                <th>Email</th>
                                <th>Mobile Number</th>
                                <th>NIC</th>
                                <th>Service ID</th>
                                <th>Last Logged Date</th>
                                <th>Status</th>
                                <th>Created User</th>
                                <th>Created Time</th>
                                <th>Last Updated User</th>
                                <th>Last Updated Time</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <!--end: Datatable-->
                    </div>
                </div>
            </div>
            <!--end::Card-->
            <!--begin::Card-->
            <c:if test="${systemuser.vdualauth}">
                <div class="card card-custom gutter-b">
                    <div class="card-header flex-wrap border-0 pt-6 pb-0">
                        <div class="card-title">
                            <h3 class="card-label">System users to be confirmed
                                <span class="d-block text-muted pt-2 font-size-sm">Authentication records list</span>
                            </h3>
                        </div>
                    </div>
                    <div class="card-body">
                        <!--begin: Datatable-->
                        <div id="data-table-loading-dual" style="display: block;">
                            <div class="loader"></div>
                            <div class="loading-text">Loading..</div>
                        </div>
                        <div id="data-table-wrapper-dual" style="display: none;">
                            <table class="table table-separate table-head-custom table-checkable" id="tabledual">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Username</th>
                                    <th>Full Name</th>
                                    <th>Email</th>
                                    <th>User Role Code</th>
                                    <th>Status</th>
                                    <th>Mobile Number</th>
                                    <th>NIC</th>
                                    <th>Service ID</th>
                                    <th>Task</th>
                                    <th>Created Time</th>
                                    <th>Last Updated Time</th>
                                    <th>Last Updated User</th>
                                    <th>Confirm</th>
                                    <th>Reject</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                            <!--end: Datatable-->
                        </div>
                    </div>
                    <!--end::Card-->

                    <!--end::Container-->
                </div>
            </c:if>
            <!--end::Entry-->
        </div>
    </div>
</div>
<!-- start include jsp files -->
<jsp:include page="systemuser-mgt-add.jsp"/>
<jsp:include page="systemuser-mgt-update.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</html>
