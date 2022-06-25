<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <script type="text/javascript">
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var oTable;
        var oTableDual;

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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listUserRole.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userroleCode', 'value': $('#searchCode').val()},
                        {'name': 'description', 'value': $('#searchDescription').val()},
                        {'name': 'userroleType', 'value': $('#searchUserroleType').val()},
                        {'name': 'status', 'value': $('#searchStatus').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listUserRole.json",
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
                lengthMenu: [10, 20, 50, 100],
                searching: false,
                initComplete: function (settings, json) {
                    document.getElementById('data-table-loading').style.display = "none";
                    document.getElementById('data-table-wrapper').style.display = "block";
                },
                fnDrawCallback: function (oSettings) {
                    $(".table ").css({"width": "100%"});
                },
                columnDefs: [
                    {
                        title: "Userrole Code",
                        targets: 0,
                        mDataProp: "userroleCode",
                        defaultContent: "--"
                    },
                    {
                        title: "Description",
                        targets: 1,
                        mDataProp: "description",
                        defaultContent: "--"
                    },
                    {
                        title: "Userrole Type",
                        targets: 2,
                        mDataProp: "userroleType",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 3,
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
                        targets: 4,
                        mDataProp: "createdUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 5,
                        mDataProp: "createdTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 6,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 7,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        visible: ${userrole.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.userroleCode + ' class="btn btn-default btn-sm"  onclick="editUserroleInit(\'' + full.userroleCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 8,
                        defaultContent: "--"
                    },
                    {

                        visible: ${userrole.vdelete},
                        title: "Delete",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.userroleCode + ' class="btn btn-default btn-sm"  onclick="deleteUserroleInit(\'' + full.userroleCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-delete.svg" alt=""></button>';
                        },
                        targets: 9,
                        defaultContent: "--"
                    },
                    {
                        visible: ${userrole.vassignpage},
                        title: "Assign Page",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.userroleCode + ' class="btn btn-default btn-sm"  onclick="assignPageInit(\'' + full.userroleCode + '\'\,\'' + full.description + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 10,
                        defaultContent: "--"
                    },
                    {
                        visible: ${userrole.vassigntask},
                        title: "Assign Task",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.userroleCode + ' class="btn btn-default btn-sm"  onclick="assignTaskInit(\'' + full.userroleCode + '\'\,\'' + full.description + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 11,
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualUserRole.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userroleCode', 'value': $('#searchCode').val()},
                        {'name': 'description', 'value': $('#searchDescription').val()},
                        {'name': 'userroleType', 'value': $('#searchUserroleType').val()},
                        {'name': 'status', 'value': $('#searchStatus').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualUserRole.json",
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
                lengthMenu: [10, 20, 50, 100],
                responsive: true,
                searching: false,
                initComplete: function (settings, json) {
                    document.getElementById('data-table-loading-dual').style.display = "none";
                    document.getElementById('data-table-wrapper-dual').style.display = "block";
                },
                columnDefs: [
                    {
                        title: "ID",
                        targets: 0,
                        visible: false,
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "Userrole Code",
                        targets: 1,
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "Description",
                        targets: 2,
                        mDataProp: "key2",
                        defaultContent: "--"
                    },
                    {
                        title: "Userrole Type",
                        targets: 3,
                        mDataProp: "key3",
                        defaultContent: "--"
                    },
                    {
                        title: "Assign Pages/Tasks",
                        targets: 4,
                        mDataProp: "tmpRecord",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 5,
                        mDataProp: "key4",
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
                        title: "Task",
                        targets: 6,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 7,
                        mDataProp: "createdTime",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 8,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated User",
                        targets: 9,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        visible: ${userrole.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.id + ' class="btn btn-default btn-sm"  onclick="confirmUserroleInit(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 10,
                        defaultContent: "--"
                    },
                    {
                        visible: ${userrole.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.id + ' class="btn btn-default btn-sm" onclick="rejectUserroleInit(\'' + full.id + '\');"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 11,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function editUserroleInit(userroleCode) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getUserRole.json",
                data: {
                    userRoleCode: userroleCode
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",

                success: function (data) {
                    $('#responseMsgUpdate').hide();

                    $('#editUserroleCode').val(data.userroleCode);
                    $('#editUserroleCode').attr('readOnly', true);
                    $('#editDescription').val(data.description);
                    $('#editUserroleType').val(data.userroleType);
                    $('#editStatus').val(data.status);

                    $('#modalUpdateUserrole').modal('toggle');
                    $('#modalUpdateUserrole').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function deleteUserroleInit(keyval) {
            $('#deleteCodeCommon').val(keyval);
            $('#modalDeleteCommon').modal('toggle');
            $('#modalDeleteCommon').modal('show');
        }

        function assignPageInit(userroleCode, description) {
            $('#assignPageUserroleCode').val(userroleCode);
            $('#assignPageUserRoleDescription').val(description);

            $('#responseMsgAssignPage').hide();

            $('#modalAssignPage').modal('toggle');
            $('#modalAssignPage').modal('show');

            $("#section").empty();
            $("#section").append($('<option>', {
                value: "",
                text: "Select Section",
                selected: true
            }));

            $("#assignedPages").empty();
            $("#unassignedPages").empty();

            $.ajax({
                url: "${pageContext.request.contextPath}/getAllSection.json",
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $.each(data, function (index, obj) {
                            $("#section").append($('<option>', {
                                value: obj.sectionCode,
                                text: obj.description
                            }));
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function assignTaskInit(userroleCode, description) {
            $('#modalAssignTask').modal('toggle');
            $('#modalAssignTask').modal('show');

            $('#responseMsgAssignTask').hide();

            $('#assignTaskUserroleCode').val(userroleCode);
            $('#assignTaskUserRoleDescription').val(description);

            $("#assignTaskSection").empty();
            $("#assignTaskSection").append($('<option>', {
                value: "",
                text: "Select Section",
                selected: true
            }));

            $("#assignTaskPage").empty();
            $("#assignTaskPage").append($('<option>', {
                value: "",
                text: "Select Page",
                selected: true
            }));

            $('#assignTaskPage').attr('readonly', true);
            $("#assignedTasks").empty();
            $("#unassignedTasks").empty();

            $.ajax({
                url: "${pageContext.request.contextPath}/getAssignedSection.json",
                data: {
                    userroleCode: userroleCode
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $.each(data, function (index, obj) {
                            $("#assignTaskSection").append($('<option>', {
                                value: obj.sectionCode,
                                text: obj.description
                            }));
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function confirmUserroleInit(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectUserroleInit(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }


        function searchStart() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function resetSearch() {
            $('#searchCode').val("");
            $('#searchDescription').val("");
            $('#searchUserroleType').val("");
            $('#searchStatus').val("");
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function openAddModal() {
            $('#modalAddUserrole').modal('toggle');
            $('#modalAddUserrole').modal('show');
        }

        function deleteCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/deleteUserRole.json',
                data: {userRoleCode: $('#deleteCodeCommon').val()},
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
                    if (res.flag) { //success
                        $('#responseMsgDelete').show();
                        $('#responseMsgDelete').addClass('success-response').text(res.successMessage);
                        searchStart();
                    } else {
                        //set error messages
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
                url: '${pageContext.request.contextPath}/confirmUserRole.json',
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
                        $('form[name=addTaskForm]').trigger("reset");
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
                url: '${pageContext.request.contextPath}/rejectUserRole.json',
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
                        $('form[name=addTaskForm]').trigger("reset");
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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">Userrole Management</h5>
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
                            <h3 class="card-title">Search Userrole</h3>
                        </div>
                        <!--begin::Form-->
                        <form:form class="form" id="userroleviewform" name="userrolesearch" action="addUserRole"
                                   theme="simple"
                                   method="post" modelAttribute="userrole">
                            <%--                        <form class="form">--%>
                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-3">
                                        <label>Userrole Code:</label>
                                        <input id="searchCode" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                               maxlength="16"
                                               class="form-control" placeholder="Userrole Code">
                                        <span class="form-text text-muted">Please enter task code</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Description:</label>
                                        <input id="searchDescription" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                               maxlength="64"
                                               class="form-control" placeholder="Description">
                                        <span class="form-text text-muted">Please enter description</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Userrole Type</label>
                                        <select id="searchUserroleType" class="form-control">
                                            <option selected value="">Select Userrole Type</option>
                                            <c:forEach items="${userrole.userroleTypeList}" var="userroleType">
                                                <option value="${userroleType.userRoleTypeCode}">${userroleType.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please select userrole Type</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Status:</label>
                                        <select id="searchStatus" class="form-control">
                                            <option selected value="">Select Status</option>
                                            <c:forEach items="${userrole.statusList}" var="status">
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
                        <h3 class="card-label">Userrole Management
                            <span class="d-block text-muted pt-2 font-size-sm">User role list</span></h3>
                    </div>
                    <div class="card-toolbar">
                        <!--begin::Button-->
                        <c:if test="${userrole.vadd}">
                            <a href="#" onclick="openAddModal()" class="btn btn-primary btn-sm font-weight-bolder">
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
                                <th>Userrole Code</th>
                                <th>Description</th>
                                <th>Userrole Type</th>
                                <th>Status</th>
                                <th>Created User</th>
                                <th>Created Time</th>
                                <th>Last Updated User</th>
                                <th>Last Updated Time</th>
                                <th>Update</th>
                                <th>Delete</th>
                                <th>Assign Page</th>
                                <th>Assign Task</th>
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
            <c:if test="${userrole.vdualauth}">
                <div class="card card-custom gutter-b">
                    <div class="card-header flex-wrap border-0 pt-6 pb-0">
                        <div class="card-title">
                            <h3 class="card-label">Userroles to be confirmed
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
                                    <th>Userrole Code</th>
                                    <th>Description</th>
                                    <th>Userrole Type</th>
                                    <th>Assign Pages</th>
                                    <th>Status</th>
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
<jsp:include page="userrole-mgt-add.jsp"/>
<jsp:include page="userrole-mgt-update.jsp"/>
<jsp:include page="userrole-mgt-assign-page.jsp"/>
<jsp:include page="userrole-mgt-assign-task.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</html>
