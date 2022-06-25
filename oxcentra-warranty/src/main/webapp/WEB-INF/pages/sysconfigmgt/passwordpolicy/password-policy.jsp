<%--
  Created by IntelliJ IDEA.
  User: suren_v
  Date: 2/1/2021
  Time: 10:54 AM
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listPasswordPolicy.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listPasswordPolicy.json",
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
                        title: "Password Policy ID",
                        targets: 0,
                        mDataProp: "passwordPolicyId",
                        sortable: "true",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Length",
                        targets: 1,
                        mDataProp: "minimumLength",
                        defaultContent: "--"
                    },
                    {
                        title: "Maximum Length",
                        targets: 2,
                        sortable: "true",
                        mDataProp: "maximumLength",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Special Characters",
                        targets: 3,
                        mDataProp: "minimumSpecialCharacters",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Uppercase Characters",
                        targets: 4,
                        mDataProp: "minimumUpperCaseCharacters",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Numerical Characters",
                        targets: 5,
                        mDataProp: "minimumNumericalCharacters",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Lowercase Characters",
                        targets: 6,
                        mDataProp: "minimumLowerCaseCharacters",
                        defaultContent: "--"
                    },
                    {
                        title: "No of Invalid Login Attempts",
                        targets: 7,
                        mDataProp: "noOfInvalidLoginAttempt",
                        defaultContent: "--"
                    },
                    {
                        title: "Repeat Characters Allow",
                        targets: 8,
                        mDataProp: "repeatCharactersAllow",
                        defaultContent: "--"
                    },
                    {
                        title: "Initial Password Expiry Status",
                        targets: 9,
                        mDataProp: "initialPasswordExpiryStatus",
                        defaultContent: "--"
                    },
                    {
                        title: "Password Expiry Period",
                        targets: 10,
                        mDataProp: "passwordExpiryPeriod",
                        defaultContent: "--"
                    },
                    {
                        title: "No of History Password",
                        targets: 11,
                        mDataProp: "noOfHistoryPassword",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Password Change Period",
                        targets: 12,
                        mDataProp: "minimumPasswordChangePeriod",
                        defaultContent: "--"
                    },
                    {
                        title: "Idle Account Expiry Period",
                        targets: 13,
                        mDataProp: "idleAccountExpiryPeriod",
                        defaultContent: "--"
                    },
                    {
                        title: "Description",
                        targets: 14,
                        mDataProp: "description",
                        defaultContent: "--"
                    },
                    {
                        visible: ${passwordPolicy.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="editBtn" class="btn btn-default btn-sm"  onclick="editPasswordPolicyInit(\'' + full.passwordPolicyId + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 15,
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualPasswordPolicy.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualPasswordPolicy.json",
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
                        mDataProp: "key1",
                        defaultContent: "--"
                    }, {
                        title: "Password Policy ID",
                        targets: 1,
                        visible: false,
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Length",
                        targets: 2,
                        mDataProp: "key2",
                        defaultContent: "--"
                    },
                    {
                        title: "Maximum Length",
                        targets: 3,
                        mDataProp: "key3",
                        defaultContent: "--"
                    },
                    {
                        title: "Minimum Special Characters",
                        targets: 4,
                        mDataProp: "key4",
                        defaultContent: "--"
                    }, {
                        title: "Minimum Uppercase Characters",
                        targets: 5,
                        mDataProp: "key5",
                        defaultContent: "--"
                    }, {
                        title: "Minimum Numerical Characters",
                        targets: 6,
                        mDataProp: "key6",
                        defaultContent: "--"
                    }, {
                        title: "Minimum Lowercase Characters",
                        targets: 7,
                        mDataProp: "key7",
                        defaultContent: "--"
                    }, {
                        title: "No of Invalid Login Attempt",
                        targets: 8,
                        mDataProp: "key8",
                        defaultContent: "--"
                    }, {
                        title: "Repeat Characters Allow",
                        targets: 9,
                        mDataProp: "key9",
                        defaultContent: "--"
                    }, {
                        title: "Initial Password Expiry Status",
                        targets: 10,
                        mDataProp: "key10",
                        defaultContent: "--"
                    }, {
                        title: "Password Expiry Period",
                        targets: 11,
                        mDataProp: "key11",
                        defaultContent: "--"
                    }, {
                        title: "No of History Password",
                        targets: 12,
                        mDataProp: "key12",
                        defaultContent: "--"
                    }, {
                        title: "Minimum Password Change Period",
                        targets: 13,
                        mDataProp: "key13",
                        defaultContent: "--"
                    }, {
                        title: "Idle Account Expiry Period",
                        targets: 14,
                        mDataProp: "key14",
                        defaultContent: "--"
                    }, {
                        title: "Description",
                        targets: 15,
                        mDataProp: "key15",
                        defaultContent: "--"
                    },
                    {
                        title: "Task",
                        targets: 16,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 17,
                        mDataProp: "createdTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated Time",
                        targets: 18,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 19,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        visible: ${passwordPolicy.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="confirmBtn" class="btn btn-default btn-sm"  onclick="confirmPasswordPolicyInit(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 20,
                        defaultContent: "--"
                    },
                    {
                        visible: ${passwordPolicy.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="rejectBtn" class="btn btn-default btn-sm" onclick="rejectPasswordPolicyInit(\'' + full.id + '\');"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 21,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function searchStart() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function resetSearch() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function editPasswordPolicyInit(policyid) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getPasswordPolicy.json",
                data: {
                    policyid: policyid
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $('#editPolicyId').val(data.passwordPolicyId);
                    $('#editPolicyId').attr('readOnly', true);
                    $('#editMinimumLength').val(data.minimumLength);
                    $('#editMaximumLength').val(data.maximumLength);
                    $('#editMinimumSpecialCharacters').val(data.minimumSpecialCharacters);
                    $('#editMinimumUpperCaseCharacters').val(data.minimumUpperCaseCharacters);
                    $('#editMinimumNumericalCharacters').val(data.minimumNumericalCharacters);
                    $('#editMinimumLowerCaseCharacters').val(data.minimumLowerCaseCharacters);
                    $('#editNoOfInvalidLoginAttempt').val(data.noOfInvalidLoginAttempt);
                    $('#editRepeatCharactersAllow').val(data.repeatCharactersAllow);
                    $('#editInitialPasswordExpiryStatus').val(data.initialPasswordExpiryStatus);
                    $('#editPasswordExpiryPeriod').val(data.passwordExpiryPeriod);
                    $('#editNoOfHistoryPassword').val(data.noOfHistoryPassword);
                    $('#editMinimumPasswordChangePeriod').val(data.minimumPasswordChangePeriod);
                    $('#editIdleAccountExpiryPeriod').val(data.idleAccountExpiryPeriod);
                    $('#editDescription').val(data.description);
                    $('#editLastUpdatedUser').val(data.lastUpdatedUser);
                    $('#editLastUpdatedUser').attr('readOnly', true);

                    $('#modalUpdateTask').modal('toggle');
                    $('#modalUpdateTask').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/login.jsp";
                }
            });
        }

        function confirmPasswordPolicyInit(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectPasswordPolicyInit(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }

        function confirmCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/confirmPasswordPolicy.json',
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
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function rejectCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/rejectPasswordPolicy.json',
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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">Password Policy Management</h5>
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
            <!--begin::Card-->
            <div class="card card-custom gutter-b">
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
                                <th>Policy ID</th>
                                <th>Minimum Length</th>
                                <th>Maximum Length</th>
                                <th>Minimum Special Characters</th>
                                <th>Minimum Uppercase Characters</th>
                                <th>Minimum Numerical Characters</th>
                                <th>Minimum Lowercase Characters</th>
                                <th>No of Invalid Login Attempts</th>
                                <th>Repeat Characters Allowed</th>
                                <th>Initial Password Expiry Status</th>
                                <th>Password Expiry Period</th>
                                <th>No of History Password</th>
                                <th>Minimum Password Change Period</th>
                                <th>Idle Account Expiry Period</th>
                                <th>Description</th>
                                <th class="all">Update</th>
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
            <c:if test="${passwordPolicy.vdualauth}">
                <div class="card card-custom gutter-b">
                    <div class="card-header flex-wrap border-0 pt-6 pb-0">
                        <div class="card-title">
                            <h3 class="card-label">Password policies to be confirmed
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
                                    <th>Password Policy ID</th>
                                    <th>Minimum Length</th>
                                    <th>Maximum Length</th>
                                    <th>Minimum Special Characters</th>
                                    <th>Minimum Uppercase Characters</th>
                                    <th>Minimum Numerical Characters</th>
                                    <th>Minimum Lowercase Characters</th>
                                    <th>No of Invalid Login Attempt</th>
                                    <th>Repeat Characters Allow</th>
                                    <th>Initial Password Expiry Status</th>
                                    <th>Password Expiry Period</th>
                                    <th>No of History Password</th>
                                    <th>Minimum Password Change Period</th>
                                    <th>Idle Account Expiry Period</th>
                                    <th>Description</th>
                                    <th>Task</th>
                                    <th>Created Time</th>
                                    <th>Last Updated Time</th>
                                    <th>Last Updated User</th>
                                    <th class="all">Confirm</th>
                                    <th class="all">Reject</th>
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
<jsp:include page="password-policy-update.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</html>
