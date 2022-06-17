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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listPasswordParam.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userroletype', 'value': $('#searchUserRole').val()},
                        {'name': 'passwordparam', 'value': $('#searchCommonParam').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listPasswordParam.json",
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
                        title: "Password Param",
                        targets: 0,
                        mDataProp: "passwordparam",
                        defaultContent: "--"
                    },
                    {
                        title: "UserRole Type",
                        targets: 1,
                        mDataProp: "userroletype",
                        defaultContent: "--"
                    },
                    {
                        title: "Value",
                        targets: 2,
                        mDataProp: "value",
                        defaultContent: "--"
                    },
                    {
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        render: function (data, type, full, meta) {
                            return '<div>\n\
            <c:if test="${passwordparamviewform.vupdate}"><a title="Update" id=' + full.passwordparam + ' onclick="editPasswordParamInit(\'' + full.passwordparam + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></a>\n\
            </c:if>\</div>';
                        },
                        targets: 3,
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualPasswordParam.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userroletype', 'value': $('#searchUserRole').val()},
                        {'name': 'passwordparam', 'value': $('#searchCommonParam').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualPasswordParam.json",
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
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "Password Param Code",
                        targets: 1,
                        mDataProp: "key1",
                        defaultContent: "--"
                    },
                    {
                        title: "UserRole Type",
                        targets: 2,
                        mDataProp: "key2",
                        defaultContent: "--"
                    },
                    {
                        title: "Value",
                        targets: 3,
                        mDataProp: "key3",
                        defaultContent: "--"
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
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated Time",
                        targets: 6,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Updated User",
                        targets: 7,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {

                        visible: ${passwordparamviewform.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        render: function (data, type, full, meta) {
                            return '<div>\n\
            <c:if test="${passwordparamviewform.vconfirm}"><a title="Confirm" id=' + full.id + ' onclick="confirmPasswordParamInit(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></a>\n\
            </c:if>\</div>';
                        },
                        targets: 8,
                        defaultContent: "--"
                    },
                    {

                        visible: ${passwordparamviewform.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        render: function (data, type, full, meta) {
                            return '<div>\n\
            <c:if test="${passwordparamviewform.vreject}"><a title="Reject" id=' + full.id + ' onclick="rejectPasswordParamInit(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></a>\n\
            </c:if>\</div>';
                        },
                        targets: 9,
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
            $('#searchUserRole').val("");
            $('#searchCommonParam').val("");
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function editPasswordParamInit(passwordParam) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getPasswordParam.json",
                data: {
                    passwordParam: passwordParam
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $('#editPasswordParam').val(data.passwordparam);
                    $('#editPasswordParam').attr('readOnly', true);
                    $('#editUserRole').val(data.userroletype);
                    $('#editValue').val(data.value);

                    $('#modalUpdatePasswordParam').modal('toggle');
                    $('#modalUpdatePasswordParam').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/login.jsp";
                }
            });
        }

        function confirmPasswordParamInit(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectPasswordParamInit(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }

        function confirmCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/confirmPasswordParam.json',
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
                url: '${pageContext.request.contextPath}/rejectPasswordParam.json',
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
<body>
<section class="content">
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Search Password Parameters</h3>
                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse" data-toggle="tooltip"
                                    title="Collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                        </div>
                    </div>
                    <div class="card-body">
                        <form:form class="form" id="passwordparamviewform" name="passwordparamsearch"
                                   action="addPasswordParam" theme="simple"
                                   method="post" modelAttribute="passwordparamviewform">
                            <div class="row">
                                <div class="col-sm-3">
                                    <!-- select -->
                                    <div class="form-group">
                                        <label for="searchUserRole">UserRole</label>
                                        <select id="searchUserRole" class="form-control form-control-sm">
                                            <option selected value="">Select UserRole</option>
                                            <c:forEach items="${passwordparamviewform.userRoleTypeBeanList}"
                                                       var="passwordparam">
                                                <option value="${passwordparam.userroletypecode}">${passwordparam.description}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-sm-3">
                                    <!-- select -->
                                    <div class="form-group">
                                        <label for="searchCommonParam">Password Type</label>
                                        <select id="searchCommonParam" class="form-control form-control-sm">
                                            <option selected value="">Select Password Type</option>
                                            <c:forEach items="${passwordparamviewform.passwordParamBeanList}"
                                                       var="pwdtype">
                                                <option value="${pwdtype.paramcode}">${pwdtype.description}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="row justify-content-between">
                                <div class="col-md-10 col-sm-8">
                                    <div class="row">
                                        <div class="col-md-2 col-sm-4">
                                            <button type="button" class="btn btn-block btn-primary btn-sm"
                                                    onclick="searchStart()">Search
                                            </button>
                                        </div>
                                        <br/>
                                        <br/>
                                        <div class="col-md-2 col-sm-4">
                                            <button type="button" class="btn btn-block btn-secondary btn-sm"
                                                    onclick="resetSearch()">Reset
                                            </button>
                                        </div>
                                        <br/>
                                        <br/>
                                    </div>
                                </div>
                            </div>
                        </form:form>
                    </div>
                </div>
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Password Parameter</h3>
                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse" data-toggle="tooltip"
                                    title="Collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                        </div>
                    </div>

                    <!-- /.card-header -->
                    <div class="card-body">
                        <div id="data-table-loading" style="display: block;">
                            <div class="loader"></div>
                            <div class="loading-text">Loading..</div>
                        </div>
                        <div id="data-table-wrapper" style="display: none;">
                            <div id="tablediv">
                                <table id="table" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th>Password Param</th>
                                        <th>UserRole Type</th>
                                        <th>Value</th>
                                        <th class="all">Update</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- start data table dual -->
                <c:if test="${passwordparamviewform.vdualauth}">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Password parameters to be confirmed</h3>
                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse" data-toggle="tooltip"
                                    title="Collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                        </div>
                    </div>
                    <!-- /.card-header -->
                    <div class="card-body">
                        <div id="data-table-loading-dual" style="display: block;">
                            <div class="loader"></div>
                            <div class="loading-text">Loading..</div>
                        </div>
                        <div id="data-table-wrapper-dual" style="display: none;">
                            <div id="tabledivdual">
                                <table id="tabledual" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Password Param</th>
                                        <th>UserRole Type</th>
                                        <th>Value</th>
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
                            </div>
                        </div>
                    </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</section>
<jsp:include page="password-param-update.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
</body>
</html>
