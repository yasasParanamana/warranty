<%--
  Created by IntelliJ IDEA.
  User: dilanka_w
  Date: 1/22/2021
  Time: 3:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <script type="text/javascript">
        var oTable;

        $(document).ready(function () {
            $('#searchFromDate').datepicker({
                format: 'yyyy-mm-dd',
                endDate: '+0d',
                setDate: new Date(),
                todayHighlight: true,
                forceParse: false
            });

            $('#searchToDate').datepicker({
                format: 'yyyy-mm-dd',
                endDate: '+0d',
                setDate: new Date(),
                todayHighlight: true,
                forceParse: false
            });

            setFromDate();
            setToDate();
            loadDataTable();
        });

        function setFromDate() {
            var date = new Date();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            if (day < 10) {
                day = '0' + day;
            }
            if (month < 10) {
                month = '0' + month;
            }
            var today = (date.getFullYear() + "-" + month + "-" + day);
            $('#searchFromDate').val(today);
        }

        function setToDate() {
            var date = new Date();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            if (day < 10) {
                day = '0' + day;
            }
            if (month < 10) {
                month = '0' + month;
            }
            var today = (date.getFullYear() + "-" + month + "-" + day);
            $('#searchToDate').val(today);
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listAudit.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'userName', 'value': $('#searchUsername').val()},
                        {'name': 'section', 'value': $('#searchSection').val()},
                        {'name': 'page', 'value': $('#searchPage').val()},
                        {'name': 'task', 'value': $('#searchTask').val()},
                        {'name': 'description', 'value': $('#searchDescription').val()},
                        {'name': 'fromDate', 'value': $('#searchFromDate').val()},
                        {'name': 'toDate', 'value': $('#searchToDate').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listAudit.json",
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
                        title: "ID",
                        targets: 0,
                        mDataProp: "auditid",
                        defaultContent: "--"
                    },
                    {
                        title: "Description",
                        targets: 1,
                        mDataProp: "description",
                        defaultContent: "--"
                    },
                    {
                        title: "Section",
                        targets: 2,
                        mDataProp: "section",
                        defaultContent: "--"
                    },
                    {
                        title: "Page",
                        targets: 3,
                        mDataProp: "page",
                        defaultContent: "--"
                    },
                    {
                        title: "Task",
                        targets: 4,
                        mDataProp: "task",
                        defaultContent: "--"
                    },
                    {
                        title: "Username",
                        targets: 5,
                        mDataProp: "lastupdateduser",
                        defaultContent: "--"
                    },
                    {
                        title: "Created User",
                        targets: 6,
                        mDataProp: "lastupdateduser",
                        defaultContent: "--"
                    },
                    {
                        title: "Created Time",
                        targets: 7,
                        mDataProp: "createdtime",
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
                            return '<button id=' + full.id + ' class="btn btn-default btn-sm"  onclick="viewAuditRecord(\'' + full.auditid + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-view.svg" alt=""></button>';
                        },
                        targets: 8,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function viewAuditRecord(id) {
            $.ajax({
                url: "${pageContext.request.contextPath}/getAudit.json",
                data: {
                    auditId: id
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {

                    $('#viewAuditId').text(data.auditid);
                    $('#viewSection').text(data.section);
                    $('#viewPage').text(data.page);

                    $('#viewTask').text(data.task);
                    $('#viewUserRole').text(data.userrole);
                    $('#viewDescription').text(data.description);

                    $('#viewRemarks').text(data.remarks);
                    $('#viewIP').text(data.ip);
                    $('#viewCreatedTime').text(moment(data.createdtime).format("YYYY-MM-DD"));
                    $('#viewLastUpdatedTime').text(moment(data.lastupdatedtime).format("YYYY-MM-DD"));
                    $('#viewLastUpdatedUser').text(data.lastupdateduser);

                    if (data.valueBeanList != null) {
                        $("#fieldtable td").remove();
                        $('#fieldtable').parents('div.dataTables_wrapper').first().show();
                        for (var i = 0; i < data.valueBeanList.length; i++) {
                            $("#fieldtable tbody").append("<tr>" +
                                "<td>" + data.valueBeanList[i].field + "</td>" +
                                "<td>" + data.valueBeanList[i].oldValue + "</td>" +
                                "<td>" + data.valueBeanList[i].newValue + "</td>" +
                                "</tr>");
                        }
                        $("#fieldtable").show();
                    } else {
                        $("#fieldtable").hide();
                        $("#fieldtable td").remove();
                    }

                    $('#modalViewAudit').modal('toggle');
                    $('#modalViewAudit').modal('show');
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function searchStart() {
            var fromdate = $('#searchFromDate').val();
            var todate = $('#searchToDate').val();

            if ((fromdate && todate) && (process(fromdate) > process(todate))) {
                var text = document.getElementById('todate-validation-msg');
                document.getElementById("todate-default-msg").style.visibility = "hidden";
                text.innerHTML = "To date should be greater than or equal to from date";
                document.getElementById("todate-validation-msg").style.visibility = "visible";
                oTable.fnDraw();
            } else {
                oTable.fnDraw();
                document.getElementById("todate-validation-msg").style.visibility = "hidden";
                document.getElementById("todate-default-msg").style.visibility = "visible";
            }
        }

        function process(date) {
            var parts = date.split("-");
            return new Date(parts[0], parts[1] - 1 , parts[2]);
        }

        function downloadExcelReport() {
            form = document.getElementById('auditviewform');
            form.action = 'excelAudit.htm';
            form.submit();
        }

        function downloadPDFReport() {
            form = document.getElementById('auditviewform');
            form.action = 'pdfAudit.htm';
            form.submit();
        }

        function downloadCSVReport() {
            form = document.getElementById('auditviewform');
            form.action = 'csvAudit.htm';
            form.submit();
        }
        function setfielddate() {

            var date = new Date();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            if (day < 10) {
                day = '0' + day;
            }
            if (month < 10) {
                month = '0' + month;
            }
            var today = (date.getFullYear() + "-" + month + "-" + day);
            $('#searchFromDate').val(today);
            $('#searchToDate').val(today);

        }

        function resetForm() {
            $('#searchUsername').val("");
            $('#searchSection').val("");
            $('#searchPage').val("");
            $('#searchTask').val("");
            $('#searchDescription').val("");

            setfielddate();

            $('#searchFromDate').datepicker({
                format: 'yyyy-mm-dd',
                endDate: '+0d',
                setDate: new Date(),
                todayHighlight: true,
                forceParse: false
            });

            $('#searchToDate').datepicker({
                format: 'yyyy-mm-dd',
                endDate: '+0d',
                setDate: new Date(),
                todayHighlight: true,
                forceParse: false
            });

            oTable.fnDraw();
            document.getElementById("todate-validation-msg").style.visibility = "hidden";
            document.getElementById("todate-default-msg").style.visibility = "visible";

            selectedSection('');
            selectedPage('');
        }


        var selectedSection = (section) => {

            $("#searchPage").empty();
            $("#searchPage").append($('<option>', {
                value: "",
                text: "Select Page",
                selected: true
            }));

            $.ajax({
                url: "${pageContext.request.contextPath}/getPagesforSection.json",
                data: {
                    sectionCode: section
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",

                success: function (data) {
                    let assignTaskPage = document.getElementById("searchPage");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.value;
                            option.value = obj.key;
                            assignTaskPage.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });


        };

        var selectedPage = (page) => {

            $("#searchTask").empty();
            $("#searchTask").append($('<option>', {
                value: "",
                text: "Select Task",
                selected: true
            }));

            $.ajax({
                url: "${pageContext.request.contextPath}/getTasksforPage.json",
                data: {
                    pageCode: page
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",

                success: function (data) {
                    let assignTaskPage = document.getElementById("searchTask");
                    $.each(data, function (index, obj) {
                            let option = document.createElement("option");
                            option.text = obj.value;
                            option.value = obj.key;
                            assignTaskPage.add(option);
                        }
                    )
                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });

        };


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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">System Audit Management</h5>
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
                            <h3 class="card-title">Search Audit</h3>
                        </div>

                        <!--begin::Form-->
                        <form:form class="form" id="auditviewform" name="auditsearch" action="Audit" theme="simple"
                                   method="post" modelAttribute="audittrace">
                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-4">
                                        <label>From Date:</label>
                                        <div class="btn-group div-inline input-group input-group-sm input-append date">
                                            <input path="fromDate" name="fromDate" id="searchFromDate"
                                                   class="form-control" readonly="true" onkeydown="return false"
                                                   autocomplete="off"/>
                                        </div>
                                        <span class="form-text text-muted">Please select From date</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>To Date:</label>
                                        <div class="btn-group div-inline input-group input-group-sm input-append date">
                                            <input path="toDate" name="toDate" id="searchToDate"
                                                   class="form-control" readonly="true" onkeydown="return false"
                                                   autocomplete="off"/>
                                        </div>
                                        <span class="form-text text-muted" id="todate-default-msg">Please select To date</span>
                                        <span class="form-text text-danger" id="todate-validation-msg"></span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Username:</label>
                                        <div class="input-group">
                                            <input path="userName" name="userName" id="searchUsername" type="text"
                                                   onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                                   maxlength="64"
                                                   class="form-control form-control-sm" placeholder="Username">
                                        </div>
                                        <span class="form-text text-muted">Please enter username</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Section:</label>
                                        <div class="input-group">
                                            <select path="section" name="section" id="searchSection" onchange="selectedSection(this.value)"
                                                    class="form-control form-control-sm">
                                                <option selected value="">Select Section</option>
                                                <c:forEach items="${audittrace.sectionList}" var="section">
                                                    <option value="${section.key}">${section.value}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <span class="form-text text-muted">Please select section</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Page:</label>
                                        <div class="input-group">
                                            <select path="page" name="page" id="searchPage" onchange="selectedPage(this.value)"
                                                    class="form-control form-control-sm">
                                                <option selected value="">Select Page</option>
                                                <c:forEach items="${audittrace.pageList}" var="page">
                                                    <option value="${page.key}">${page.value}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <span class="form-text text-muted">Please select Page</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Task:</label>
                                        <div class="input-group">
                                            <select path="task" name="task" id="searchTask"
                                                    class="form-control form-control-sm">
                                                <option selected value="">Select Task</option>
                                                <c:forEach items="${audittrace.taskList}" var="task">
                                                    <option value="${task.key}">${task.value}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <span class="form-text text-muted">Please select Task</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Description:</label>
                                        <div class="input-group">
                                            <input path="description" name="description" id="searchDescription"
                                                   type="text"
                                                   onkeyup="$(this).val($(this).val().replace(/[^a-zA-Z0-9 ]/g, ''))"
                                                   maxlength="1024"
                                                   class="form-control form-control-sm" placeholder="Description">
                                        </div>
                                        <span class="form-text text-muted">Please enter description</span>
                                    </div>

                                </div>
                            </div>

                            <div class="card-footer">
                                <div class="row">
                                    <div class="col-lg-3">
                                        <button type="button" class="btn btn-primary mr-2 btn-sm" onclick="searchStart()"
                                                id="btnSearch">
                                            Search
                                        </button>

                                        <button type="button" class="btn btn-secondary btn-sm" onclick="resetForm()"
                                                id="btnReset">
                                            Reset
                                        </button>
                                    </div>

                                    <div class="col-lg-5"></div>

                                    <div class="col-lg-4">
                                        <c:if test="${audittrace.vdownload}">
                                            <button id="viewExcel" type="button" class="btn btn-primary mr-2 btn-sm"
                                                    onclick="downloadExcelReport()">
                                                View Excel
                                            </button>
                                        </c:if>
                                        <c:if test="${audittrace.vdownload}">
                                            <button id="viewPDF" type="button" class="btn btn-primary mr-2 btn-sm"
                                                    onclick="downloadPDFReport()">
                                                View PDF
                                            </button>
                                        </c:if>
                                        <c:if test="${audittrace.vdownload}">
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
                                <th>ID</th>
                                <th>Description</th>
                                <th>Section</th>
                                <th>Page</th>
                                <th>Task</th>
                                <th>Username</th>
                                <th>Created User</th>
                                <th>Created Time</th>
                                <th>View</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <!--end: Datatable-->
                    </div>
                </div>
            </div>
            <!--end::Card-->
        </div>
        <!--end::Container-->
    </div>
    <!--end::Entry-->
</div>
<!-- start include jsp files -->
<jsp:include page="audit-view.jsp"/>
<!-- end include jsp files -->
</html>
