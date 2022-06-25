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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listPage.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'pageCode', 'value': $('#searchCode').val()},
                        {'name': 'description', 'value': $('#searchDescription').val()},
                        {'name': 'status', 'value': $('#searchStatus').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listPage.json",
                        contentType: "application/json",
                        data: stringify_aoData(aoData),
                        success: fnCallback,
                        error: function (e) {
                            handleAjaxCallErrors(e.status);
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
                        title: "Page Code",
                        targets: 0,
                        mDataProp: "pageCode",
                        defaultContent: "--"
                    },
                    {
                        title: "Description",
                        targets: 1,
                        mDataProp: "description",
                        defaultContent: "--"
                    },
                    {
                        title: "Url",
                        targets: 2,
                        mDataProp: "url",
                        defaultContent: "--"
                    },
                    {
                        title: "Sort Key",
                        targets: 3,
                        mDataProp: "sortKey",
                        defaultContent: "--"
                    },
                    {
                        title: "Actual Flag",
                        targets: 4,
                        mDataProp: "actualFalg",
                        defaultContent: "--"
                    },
                    {
                        title: "Current Flag",
                        targets: 5,
                        mDataProp: "currentFlag",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 6,
                        mDataProp: "statusCode",
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
                        targets: 7,
                        mDataProp: "createdUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 8,
                        mDataProp: "createdTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 9,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 10,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        visible: ${page.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.pageCode + ' class="btn btn-default btn-sm"  onclick="editPageInit(\'' + full.pageCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualPage.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'pageCode', 'value': $('#searchCode').val()},
                        {'name': 'description', 'value': $('#searchDescription').val()},
                        {'name': 'status', 'value': $('#searchStatus').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualPage.json",
                        contentType: "application/json",
                        data: stringify_aoData(aoData),
                        beforeSend: function (xhr) {
                            if (header && token) {
                                xhr.setRequestHeader(header, token);
                            }
                        },
                        success: fnCallback,
                        error: function (e) {
                            handleAjaxCallErrors(e.status);
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
                        title: "Page Code",
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
                        title: "Url",
                        targets: 3,
                        mDataProp: "key3",
                        defaultContent: "--"
                    },
                    {
                        title: "Sort Key",
                        targets: 4,
                        mDataProp: "key4",
                        defaultContent: "--"
                    },
                    {
                        title: "Actual Flag",
                        targets: 5,
                        mDataProp: "key5",
                        defaultContent: "--"
                    },
                    {
                        title: "Current Flag",
                        targets: 6,
                        mDataProp: "key6",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 7,
                        mDataProp: "key7",
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
                        targets: 8,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 9,
                        mDataProp: "createdTime",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 10,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated User",
                        targets: 11,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        visible: ${page.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full, meta) {
                            return '<button id="confirmBtn" class="btn btn-default btn-sm"  onclick="confirmPageInit(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 12,
                        defaultContent: "--"
                    },
                    {
                        visible: ${page.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full, meta) {
                            return '<button id="rejectBtn" class="btn btn-default btn-sm" onclick="rejectPageInit(\'' + full.id + '\');"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 13,
                        defaultContent: "--"
                    }
                ]
            });
        }


        function editPageInit(pageCode) {

            $.ajax({
                url: "${pageContext.request.contextPath}/getPage.json",
                data: {
                    pageCode: pageCode
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",

                success: function (data) {
                    $('#editPageCode').val(data.pageCode);
                    $('#editPageCode').attr('readOnly', true);
                    $('#editUrl').val(data.url);
                    $('#editUrl').attr('readOnly', true);
                    $('#editDescription').val(data.description);
                    $('#editSortKey').val(data.sortKey);
                    $('#editDualauth').prop("checked", data.currentFlag);

                    if (data.actualFalg === true) {
                        $('#updateDualAuthFlag').show();
                    } else {
                        $('#updateDualAuthFlag').hide();
                    }

                    $('#editStatus').val(data.statusCode);
                    $('#modalUpdateTask').modal('toggle');
                    $('#modalUpdateTask').modal('show');

                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/login.jsp";
                }
            });
        }

        function confirmPageInit(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectPageInit(keyval) {
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
            $('#searchStatus').val("");
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function confirmCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/confirmPage.json',
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

                    if (res.flag) { //success
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
                    console.log(jqXHR);
                    handleAjaxCallErrors(jqXHR.status);
                }
            });
        }

        function rejectCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/rejectPage.json',
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

                    if (res.flag) { //success
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
                    console.log(jqXHR);
                    handleAjaxCallErrors(jqXHR.status);
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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">Page Management</h5>
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
                            <h3 class="card-title">Search Page</h3>
                        </div>
                        <!--begin::Form-->
                        <form:form class="form" id="pageviewform" name="pagesearch" action="searchPage" theme="simple"
                                   method="post" modelAttribute="page">
                            <%--                        <form class="form">--%>
                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-3">
                                        <label>Page Code:</label>
                                        <input id="searchCode" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                               maxlength="16"
                                               class="form-control" placeholder="Page Code">
                                        <span class="form-text text-muted">Please enter page code</span>
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
                                        <label>Status:</label>
                                        <select id="searchStatus" class="form-control">
                                            <option selected value="">Select Status</option>
                                            <c:forEach items="${page.statusList}" var="status">
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
                                        <button type="button" class="btn btn-sm btn-primary mr-2"
                                                onclick="searchStart()">
                                            Search
                                        </button>
                                        <button type="reset" class="btn btn-sm btn-secondary" onclick="resetSearch()">
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
                        <h3 class="card-label">Page Management
                            <span class="d-block text-muted pt-2 font-size-sm">User page list</span></h3>
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
                                <th>Page Code</th>
                                <th>Description</th>
                                <th>Url</th>
                                <th>Sort Key</th>
                                <th>Actual Flag</th>
                                <th>Current Flag</th>
                                <th>Status</th>
                                <th>Created User</th>
                                <th>Created Time</th>
                                <th>Last Updated User</th>
                                <th>Last Updated Time</th>
                                <th>Update</th>
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
            <c:if test="${page.vdualauth}">
                <div class="card card-custom gutter-b">
                    <div class="card-header flex-wrap border-0 pt-6 pb-0">
                        <div class="card-title">
                            <h3 class="card-label">Pages to be confirmed
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
                                    <th>Page Code</th>
                                    <th>Description</th>
                                    <th>Url</th>
                                    <th>Sort Key</th>
                                    <th>Actual Flag</th>
                                    <th>Current Flag</th>
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
<jsp:include page="page-mgt-update.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</html>
