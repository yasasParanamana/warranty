<%--
  Created by IntelliJ IDEA.
  User: yasas_p
  Date: 3/17/2021
  Time: 1:08 PM
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listChannel.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'channelCode', 'value': $('#searchChannelCode').val()},
                        {'name': 'description', 'value': $('#searchDescription').val()},
                        {'name': 'status', 'value': $('#searchStatus').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listChannel.json",
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
                        title: "Channel Code",
                        targets: 0,
                        mDataProp: "channelCode",
                        defaultContent: "--"
                    },
                    {
                        title: "Description",
                        targets: 1,
                        mDataProp: "description",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 2,
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
                        label: 'Created User',
                        name: 'createdUser',
                        targets: 3,
                        mDataProp: "createdUser",
                        defaultContent: "--"
                    },
                    {
                        label: 'Created Time',
                        name: 'createdTime',
                        targets: 4,
                        mDataProp: "createdTime",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 5,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 6,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        visible: ${channel.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="editBtn" class="btn btn-default btn-sm"  onclick="editChannel(\'' + full.channelCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 7,
                        defaultContent: "--"
                    },
                    {
                        visible: ${channel.vdelete},
                        title: "Delete",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="deleteBtn" class="btn btn-default btn-sm"  onclick="deleteChannel(\'' + full.channelCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-delete.svg" alt=""></button>';
                        },
                        targets: 8,
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualChannel.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'channelCode', 'value': $('#channelCode').val()},
                        {'name': 'description', 'value': $('#description').val()},
                        {'name': 'status', 'value': $('#status').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualChannel.json",
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
                        mDataProp: "id",
                        defaultContent: "--"
                    },
                    {
                        title: "Channel Code",
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
                        title: "Task",
                        targets: 4,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 5,
                        mDataProp: "createdTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated Time",
                        targets: 6,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 7,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        visible: ${channel.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="confirmBtn" class="btn btn-default btn-sm"  onclick="confirmChannel(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 8,
                        defaultContent: "--"
                    },
                    {
                        visible: ${channel.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="rejectBtn" class="btn btn-default btn-sm" onclick="rejectChannel(\'' + full.id + '\');"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 9,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function editChannel(channelCode) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getChannel.json",
                data: {
                    channelCode: channelCode
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $('#responseMsgUpdate').hide();

                    $('#eChannelCode').val(data.channelCode);
                    $('#eChannelCode').attr('readOnly', true);

                    $('#eDescription').val(data.description);
                    $('#eStatus').val(data.status);

                    $('#modalUpdateChannel').modal('toggle');
                    $('#modalUpdateChannel').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function deleteChannel(keyval) {
            $('#deleteCodeCommon').val(keyval);
            $('#modalDeleteCommon').modal('toggle');
            $('#modalDeleteCommon').modal('show');
        }

        function confirmChannel(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectChannel(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }

        function searchStart() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function resetSearch() {
            $('#searchChannelCode').val("");
            $('#searchDescription').val("");
            $('#searchStatus').val("");

            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function openAddModal() {
            $('#modalAddChannel').modal('toggle');
            $('#modalAddChannel').modal('show');
        }

        function deleteCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/deleteChannel.json',
                data: {channelCode: $('#deleteCodeCommon').val()},
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
                url: '${pageContext.request.contextPath}/confirmChannel.json',
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
                        $('form[name=addChannelForm]').trigger("reset");
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
                url: '${pageContext.request.contextPath}/rejectChannel.json',
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
                        $('form[name=addChannelForm]').trigger("reset");
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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">Channel Management</h5>
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
                            <h3 class="card-title">Search Channel</h3>
                        </div>
                        <!--begin::Form-->
                        <form:form class="form" id="channelform" name="channelform" action="addChannel"
                                   theme="simple" method="post" modelAttribute="channel">
                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-3">
                                        <label>Channel Code:</label>
                                        <div class="input-group">
                                            <input id="searchChannelCode" type="text"
                                                   onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                                   maxlength="16"
                                                   class="form-control form-control" placeholder="Channel Code">
                                        </div>
                                        <span class="form-text text-muted">Please enter channel code</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Description:</label>
                                        <div class="input-group">
                                            <input id="searchDescription" type="text"
                                                   onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                                   maxlength="64"
                                                   class="form-control form-control" placeholder="Description">
                                        </div>
                                        <span class="form-text text-muted">Please enter description</span>
                                    </div>

                                    <div class="col-lg-3">
                                        <label>Status:</label>
                                        <select id="searchStatus" class="form-control form-control">
                                            <option selected value="">Select Status</option>
                                            <c:forEach items="${channel.statusList}" var="status">
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
                        <!--end::Card-->
                    </div>
                </div>
            </div>

            <!--begin::Card-->
            <div class="card card-custom gutter-b">
                <div class="card-header flex-wrap border-0 pt-1 pb-0">
                    <div class="card-title">
                        <h3 class="card-label">Channel Management
                            <span class="d-block text-muted pt-2 font-size-sm">Channel list</span></h3>
                    </div>
                    <div class="card-toolbar">
                        <!--begin::Button-->
                        <c:if test="${channel.vadd}">
                            <a href="javascript:void(0)" onclick="openAddModal()"
                               class="btn btn-sm btn-primary font-weight-bolder">
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
											</span>Add Channel</a>
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
                                <th>Channel Code</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th>Created Time</th>
                                <th>Created User</th>
                                <th>Last Updated Time</th>
                                <th>Last Updated User</th>
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
            <c:if test="${channel.vdualauth}">
                <div class="card card-custom gutter-b">
                    <div class="card-header flex-wrap border-0 pt-6 pb-0">
                        <div class="card-title">
                            <h3 class="card-label">Channels to be confirmed</h3>
                        </div>
                    </div>
                    <div class="card-body">
                        <!--begin: Datatable-->
                        <div id="data-table-loading-dual" style="display: block;">
                            <div class="loader"></div>
                            <div class="loading-text">Loading..</div>
                        </div>
                        <div id="data-table-wrapper-dual" style="display: none;">
                            <table class="table table-separate table-head-custom table-checkable"
                                   id="tabledual">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Channel Code</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Task</th>
                                    <th>Created Time</th>
                                    <th>Last Updated User</th>
                                    <th>Last Updated Time</th>
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
<jsp:include page="channel-mgt-add.jsp"/>
<jsp:include page="channel-mgt-update.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</html>

