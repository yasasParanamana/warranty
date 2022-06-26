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

<link rel="stylesheet" type="text/css"
      href="${pageContext.request.contextPath}/resources/css/usermgt/usermgt.css?${initParam['version']}"/>
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listSection.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'supplierCode', 'value': $('#supplierCode').val()},
                        {'name': 'supplierName', 'value': $('#supplierName').val()},
                        {'name': 'supplierPhone', 'value': $('#supplierPhone').val()},
                        {'name': 'supplierEmail', 'value': $('#supplierEmail').val()},
                        {'name': 'supplierAddress', 'value': $('#supplierAddress').val()},
                        {'name': 'status', 'value': $('#status').val()},
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listSection.json",
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
                        title: "Section Code",
                        targets: 0,
                        mDataProp: "supplierCode",
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
                        defaultContent: "--"
                    },
                    {
                        title: "Sort Key",
                        targets: 3,
                        mDataProp: "sortKey",
                        defaultContent: "--"
                    },
                    {
                        label: 'Created Time',
                        name: 'createdTime',
                        targets: 4,
                        mDataProp: "createdTime",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated Time",
                        targets: 5,
                        mDataProp: "lastUpdatedTime",
                        defaultContent: "--",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD hh:mm a")
                        }
                    },
                    {
                        title: "Last Updated User",
                        targets: 6,
                        mDataProp: "lastUpdatedUser",
                        defaultContent: "--"
                    },
                    {
                        visible: ${section.vupdate},
                        title: "Update",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="editBtn" class="btn btn-default btn-sm"  onclick="editSection(\'' + full.supplierCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-edit.svg" alt=""></button>';
                        },
                        targets: 7,
                        defaultContent: "--"
                    },
                    {
                        visible: ${section.vdelete},
                        title: "Delete",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="deleteBtn" class="btn btn-default btn-sm"  onclick="deleteSection(\'' + full.supplierCode + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-delete.svg" alt=""></button>';
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listDualSection.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'supplierCode', 'value': $('#supplierCode').val()},
                        {'name': 'description', 'value': $('#description').val()},
                        {'name': 'status', 'value': $('#status').val()},
                        {'name': 'sortKey', 'value': $('#sortKey').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listDualSection.json",
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
                        title: "Section Code",
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
                        visible: ${section.vconfirm},
                        title: "Confirm",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="confirmBtn" class="btn btn-default btn-sm"  onclick="confirmSection(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-confirm.svg" alt=""></button>';
                        },
                        targets: 8,
                        defaultContent: "--"
                    },
                    {
                        visible: ${section.vreject},
                        title: "Reject",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="rejectBtn" class="btn btn-default btn-sm" onclick="rejectSection(\'' + full.id + '\');"><img src="${pageContext.request.contextPath}/resources/images/action-reject.svg" alt=""></button>';
                        },
                        targets: 9,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function editSection(supplierCode) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getSection.json",
                data: {
                    supplierCode: supplierCode
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $('#responseMsgUpdate').hide();

                    $('#eSectionCode').val(data.supplierCode);
                    $('#eSectionCode').attr('readOnly', true);

                    $('#eDescription').val(data.description);
                    $('#eStatus').val(data.status);
                    $('#eSortKey').val(data.sortKey);
                    $('#modalUpdateSection').modal('toggle');
                    $('#modalUpdateSection').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function deleteSection(keyval) {
            $('#deleteCodeCommon').val(keyval);
            $('#modalDeleteCommon').modal('toggle');
            $('#modalDeleteCommon').modal('show');
        }

        function confirmSection(keyval) {
            $('#idConfirm').val(keyval);
            $('#modalConfirmCommon').modal('toggle');
            $('#modalConfirmCommon').modal('show');
        }

        function rejectSection(keyval) {
            $('#idReject').val(keyval);
            $('#modalRejectCommon').modal('toggle');
            $('#modalRejectCommon').modal('show');
        }

        function search() {
            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function resetSearch() {
            $('#supplierCode').val("");
            $('#description').val("");
            $('#status').val("");
            $('#sortKey').val("");

            oTable.fnDraw();
            oTableDual.fnDraw();
        }

        function openAddModal() {
            $('#modalAddSection').modal('toggle');
            $('#modalAddSection').modal('show');
        }

        function deleteCommon() {
            $.ajax({
                type: 'POST',
                url: '${pageContext.request.contextPath}/deleteSection.json',
                data: {supplierCode: $('#deleteCodeCommon').val()},
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
                        search();
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
                url: '${pageContext.request.contextPath}/confirmSection.json',
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
                        $('form[name=addSectionForm]').trigger("reset");
                        search();
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
                url: '${pageContext.request.contextPath}/rejectSection.json',
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
                        $('form[name=addSectionForm]').trigger("reset");
                        search();
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
                <!-- start search box -->
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Search Section</h3>
                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse" data-toggle="tooltip"
                                    title="Collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                        </div>
                    </div>

                    <div class="card-body">
                        <form:form class="form" id="sectionform" name="sectionform" action="addSection"
                                   theme="simple" method="post" modelAttribute="section">

                            <div class="row">
                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="supplierCode">Section Code</label>
                                        <input id="supplierCode" name="supplierCode" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                               maxlength="8" class="form-control form-control-sm"
                                               placeholder="Section Code">
                                    </div>
                                </div>

                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="description">Description</label>
                                        <input id="description" name="description" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                               maxlength="128" class="form-control form-control-sm"
                                               placeholder="Description">
                                    </div>
                                </div>

                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="status">Status</label>
                                        <select id="status" name="status" class="form-control form-control-sm">
                                            <option selected value="">Select Status</option>
                                            <c:forEach items="${section.statusList}" var="status">
                                                <option value="${status.statusCode}">${status.description}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="sortKey">Sort Key</label>
                                        <input id="sortKey" name="sortKey" type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^0-9 ]/g, ''))"
                                               maxlength="128" class="form-control form-control-sm"
                                               placeholder="Sort Key">
                                    </div>
                                </div>
                            </div>

                            <div class="row justify-content-between">
                                <div class="col-md-10 col-sm-8">
                                    <div class="row">
                                        <div class="col-md-2 col-sm-4">
                                            <button type="button" class="btn btn-block btn-primary btn-sm"
                                                    onclick="search()">Search
                                            </button>
                                        </div>

                                        <div class="col-md-2 col-sm-4">
                                            <button type="button" class="btn btn-block btn-secondary btn-sm"
                                                    onclick="resetSearch()">Reset
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${section.vadd}">
                                    <div class="col-md-2 col-sm-4">
                                        <button type="button" class="btn btn-block btn-primary btn-sm"
                                                onclick="openAddModal()">Add Section
                                        </button>
                                    </div>
                                </c:if>
                            </div>
                        </form:form>
                    </div>
                </div>

                <!-- start data table -->
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Section</h3>
                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse" data-toggle="tooltip"
                                    title="Collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                        </div>
                    </div>

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
                                        <th>Section Code</th>
                                        <th>Description</th>
                                        <th>Status</th>
                                        <th>Sort Key</th>
                                        <th>Created Time</th>
                                        <th>Last Updated Time</th>
                                        <th>Last Updated User</th>
                                        <th>Update</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <c:if test="${section.vdualauth}">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Sections to be confirmed</h3>
                            <div class="card-tools">
                                <button type="button" class="btn btn-tool" data-card-widget="collapse"
                                        data-toggle="tooltip" title="Collapse">
                                    <i class="fas fa-minus"></i>
                                </button>
                            </div>
                        </div>
                    </div>

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
                                        <th>Section Code</th>
                                        <th>Description</th>
                                        <th>Status</th>
                                        <th>Sort Key</th>
                                        <th>Task</th>
                                        <th>Created Time</th>
                                        <th>Last Updated Time</th>
                                        <th>Last Updated User</th>
                                        <th>Confirm</th>
                                        <th>Reject</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</section>
<!-- start include jsp files -->
<jsp:include page="section-mgt-add.jsp"/>
<jsp:include page="section-mgt-update.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->
</body>
</html>
