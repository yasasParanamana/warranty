<%--
  Created by IntelliJ IDEA.
  User: maheshi_c
  Date: 3/23/2021
  Time: 10:39 AM
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualCustomerSearch.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'identification', 'value': $('#identification').val()},
                        {'name': 'customerid', 'value': $('#customerid').val()},
                        {'name': 'accountno', 'value': $('#accountno').val()},
                        {'name': 'mobileno', 'value': $('#mobileno').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualCustomerSearch.json",
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
                        title: "Identification",
                        targets: 1,
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "Customer ID",
                        targets: 2,
                        mDataProp: "key2",
                        defaultContent: "--"
                    },
                    {
                        title: "Primary Account Number",
                        targets: 3,
                        mDataProp: "key3",
                        defaultContent: "--"
                    },
                    {
                        title: "Mobile Number",
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
                        title: "Task",
                        targets: 6,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 7,
                        mDataProp: "createdTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated Time",
                        targets: 8,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 9,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        visible: ${cussearch.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full, meta) {
                            return '<button id="confirmBtn" class="btn btn-default btn-sm"  onclick="confirmTxnType(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 10,
                        defaultContent: "--"
                    },
                    {
                        visible: ${cussearch.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full, meta) {
                            return '<button id="rejectBtn" class="btn btn-default btn-sm" onclick="rejectTxnType(\'' + full.id + '\');"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 11,
                        defaultContent: "--"
                    }

                ]
            });
        }

        function confirmTxnType(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectTxnType(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }

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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listCustomerInfo.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'identification', 'value': $('#identification').val()},
                        {'name': 'customerid', 'value': $('#customerid').val()},
                        {'name': 'accountno', 'value': $('#accountno').val()},
                        {'name': 'mobileno', 'value': $('#mobileno').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listCustomerInfo.json",
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
                initComplete: function (settings, json) {
                    document.getElementById('data-table-loading').style.display = "none";
                    document.getElementById('data-table-wrapper').style.display = "block";
                },
                fnDrawCallback: function (oSettings) {
                    $(".table ").css({"width": "100%"});
                },
                columnDefs: [
                    {
                        title: "Customer ID",
                        targets: 0,
                        mDataProp: "customerId",
                        defaultContent: "--"
                    },
                    {
                        title: "Identification",
                        targets: 1,
                        mDataProp: "identification",
                        defaultContent: "--"
                    },
                    {
                        title: "Primary Account Number",
                        targets: 2,
                        mDataProp: "accountNo",
                        defaultContent: "--"
                    },
                    {
                        title: "Customer Category",
                        targets: 3,
                        mDataProp: "customerCategory",
                        defaultContent: "--"
                    },
                    {
                        title: "Customer Name",
                        targets: 4,
                        mDataProp: "customerName",
                        defaultContent: "--"
                    },
                    {
                        label: 'DOB',
                        targets: 5,
                        mDataProp: "dob",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    },
                    {
                        title: "Mobile Number",
                        targets: 6,
                        mDataProp: "mobileNo",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 7,
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
                        title: "Waive-off Status",
                        targets: 8,
                        mDataProp: "waiveoffstatus",
                        defaultContent: "--"
                    },
                    {
                        title: "Branch",
                        targets: 9,
                        mDataProp: "branch",
                        defaultContent: "--"
                    },
                    {
                        title: "Account Type",
                        targets: 10,
                        mDataProp: "accountType",
                        defaultContent: "--"
                    },
                    {
                        label: 'Created Time',
                        name: 'createdTime',
                        targets: 11,
                        mDataProp: "createdTime",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
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
                        title: "View",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id=' + full.customerId + ' class="btn btn-default btn-sm"  onclick="viewCustomerSearch(\'' + full.customerId + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-view.svg" alt=""></button>';
                        },
                        targets: 13,
                        defaultContent: "--"
                    },
                    {
                        visible: ${cussearch.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="editBtn" class="btn btn-default btn-sm"  onclick="editCustomerSearch(\'' + full.customerId + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 14,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function search() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function downloadCSVReport() {
            form = document.getElementById('cussearch');
            form.action = 'csvCustomerSearch.htm';
            form.submit();
        }

        function resetSearch() {
            $('#identification').val("");
            $('#customerid').val("");
            $('#accountno').val("");
            $('#mobileno').val("");

            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function searchStart() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function confirmCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/confirmCustomer.json',
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
                url: '${pageContext.request.contextPath}/rejectCustomer.json',
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

        function editCustomerSearch(id) {

            $.ajax({
                url: "${pageContext.request.contextPath}/getCustomerSearch.json",
                data: {
                    customerId: id
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {

                    $('#editcustomerId').val(data.customerId);
                    $('#editcustomerId').attr('readOnly', true);
                    $('#editidentification').val(data.identification);
                    $('#editidentification').attr('readOnly', true);
                    $('#editaccountNo').val(data.accountNo);
                    $('#editaccountNo').attr('readOnly', true);
                    $('#editcustomerCategory').val(data.customerCategory);
                    $('#editcustomerCategory').attr('readOnly', true);
                    $('#editcustomerName').val(data.customerName);
                    $('#editcustomerName').attr('readOnly', true);
                    $('#editmobileNo').val(data.mobileNo);
                    $('#editmobileNo').attr('readOnly', true);
                    $('#editstatus').val(data.statuscode);
                    $('#editwaiveoffstatus').val(data.waiveoffstatus);
                    $('#editbranch').val(data.branch);
                    $('#editbranch').attr('readOnly', true);
                    $('#editaccountType').val(data.accountType);
                    $('#editaccountType').attr('readOnly', true);
                    $('#editdob').val(moment(data.dob).format("YYYY-MM-DD"));
                    $('#editdob').attr('readOnly', true);
                    $('#remark').val("");

                    $('#modalUpdateCustomerSearch').modal('toggle');
                    $('#modalUpdateCustomerSearch').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }

            });
        }

        function viewCustomerSearch(id) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getCustomerSearch.json",
                data: {
                    customerId: id
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {

                    $('#viewcustomerId').text(":  " + data.customerId);
                    $('#viewidentification').text(":  " + data.identification);
                    $('#viewaccountNo').text(":  " + data.accountNo);

                    $('#viewcustomerCategory').text(":  " + data.customerCategory);
                    $('#viewcustomerName').text(":  " + data.customerName);
                    $('#viewmobileNo').text(":  " + data.mobileNo);

                    $('#viewstatus').text(":  " + data.status);
                    $('#viewwaiveoffstatus').text(":  " + data.waiveoffstatus);
                    $('#viewbranch').text(":  " + data.branch);
                    $('#viewaccountType').text(":  " + data.accountType);
                    $('#viewdob').text(":  " + moment(data.dob).format("YYYY-MM-DD"));
                    $('#viewCreatedTime').text(":  " + moment(data.createdtime).format("YYYY-MM-DD"));
                    $('#viewLastUpdatedTime').text(":  " + moment(data.lastupdatedtime).format("YYYY-MM-DD"));


                    $('#modalViewCustomerSearch').modal('toggle');
                    $('#modalViewCustomerSearch').modal('show');
                },
                error: function (data) {
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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">Customer Search</h5>
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
                            <h3 class="card-title">Search Customer</h3>
                        </div>
                        <!--begin::Form-->
                        <form:form class="form" id="cussearch" name="cussearch"  action="SmsOutBox"
                                   theme="simple" method="post" modelAttribute="cussearch">
                            <%--                        <form class="form">--%>
                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-3">
                                        <label>Identification:</label>
                                        <div class="input-group">
                                            <input id="identification" name="identification" type="text"
                                                   onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                                   maxlength="50" class="form-control"
                                                   placeholder="Identification">
                                        </div>
                                        <span class="form-text text-muted">Please enter Identification</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Customer ID:</label>
                                        <div class="input-group">
                                            <input id="customerid" name="customerid" type="text"
                                                   onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                                   maxlength="50" class="form-control"
                                                   placeholder="Customer ID">
                                        </div>
                                        <span class="form-text text-muted">Please enter customer Id</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Account No:</label>
                                        <input id="accountno" name="accountno" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9]/g, ''))"
                                               maxlength="50" class="form-control"
                                               placeholder="Account No">
                                        <span class="form-text text-muted">Please enter account No</span>
                                    </div>
                                    <div class="col-lg-3">
                                        <label>Mobile No:</label>
                                        <input id="mobileno" name="mobileno" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^0-9]/g, ''))"
                                               maxlength="20" class="form-control"
                                               placeholder="Mobile No">
                                        <span class="form-text text-muted">Please enter mobile No</span>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="row">
                                    <div class="col-lg-3">
                                        <button type="button" class="btn btn-sm btn-primary mr-2" onclick="search()">
                                            Search
                                        </button>
                                        <button type="reset" class="btn btn-sm btn-secondary" onclick="resetSearch()">
                                            Reset
                                        </button>
                                    </div>
                                    <div class="col-lg-7"></div>
                                    <div class="col-lg-2">
                                        <c:if test="${cussearch.vdownload}">
                                            <button id="viewCSV" type="button" class="btn btn-primary mr-2 btn-sm"
                                                    onclick="downloadCSVReport()">
                                                View CSV
                                            </button>
                                        </c:if>
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
                <div class="card-header flex-wrap border-0 pt-1 pb-0">
                    <div class="card-title">
                        <h3 class="card-label">Customer Search Management
                            <span class="d-block text-muted pt-2 font-size-sm">Customer Search list</span></h3>
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
                                <th>Customer ID</th>
                                <th>Identification</th>
                                <th>Primary Account Number</th>
                                <th>Customer Category</th>
                                <th>Customer Name</th>
                                <th>DOB</th>
                                <th>Mobile Number</th>
                                <th>Status</th>
                                <th>Waive-off Status</th>
                                <th>Branch</th>
                                <th>Account Type</th>
                                <th>Created Date</th>
                                <th>Last Updated Date</th>
                                <th>View</th>
                                <th>Update</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <!--end: Datatable-->
                    </div>
                </div>
            </div>
            <!--end::Card--><!--begin::Card-->
            <c:if test="${cussearch.vdualauth}">
                <div class="card card-custom gutter-b">
                    <div class="card-header flex-wrap border-0 pt-6 pb-0">
                        <div class="card-title">
                            <h3 class="card-label">Customer Search to be confirmed</h3>
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
                                    <th>Identification</th>
                                    <th>Customer ID</th>
                                    <th>Primary Account Number</th>
                                    <th>Mobile Number</th>
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
        <!--end::Container-->
    </div>
    <!--end::Entry-->
</div>
<!-- start include jsp files -->
<jsp:include page="customer-search-view.jsp"/>
<jsp:include page="customer-search-update.jsp"/>
<jsp:include page="../common/confirm-modal.jsp"/>
<jsp:include page="../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</html>
