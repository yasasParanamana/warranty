<%--
  Created by IntelliJ IDEA.
  User: shalika_w
  Date: 3/16/2021
  Time: 3:33 PM
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

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $("#fromDate").datepicker();
        $("#toDate").datepicker();

        $(document).ready(function () {
            $('#fromDate').datepicker({
                format: 'yyyy-mm-dd',
                endDate: '+0d',
                setDate: new Date(),
                autoclose: true,
                todayHighlight: true,
                forceParse: false
            });

            $('#toDate').datepicker({
                format: 'yyyy-mm-dd',
                endDate: '+0d',
                setDate: new Date(),
                autoclose: true,
                todayHighlight: true,
                forceParse: false
            });

            setFromDate();
            setToDate();
            loadDataTable();
        });

        function getFromDate() {
            var date = new Date();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            if (day < 10) {
                day = '0' + day;
            }
            if (month < 10) {
                month = '0' + month;
            }
            return (date.getFullYear() + "-" + month + "-" + day);
        }

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
            $('#fromDate').val(today);
        }

        function getToDate() {
            var date = new Date();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            if (day < 10) {
                day = '0' + day;
            }
            if (month < 10) {
                month = '0' + month;
            }
            return (date.getFullYear() + "-" + month + "-" + day);
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
            $('#toDate').val(today);
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listSmsOutBox.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'fromDate', 'value': $('#fromDate').val()},
                        {'name': 'toDate', 'value': $('#toDate').val()},
                        {'name': 'telco', 'value': $('#telco').val()},
                        {'name': 'department', 'value': $('#department').val()},
                        {'name': 'channel', 'value': $('#smschannel').val()},
                        {'name': 'category', 'value': $('#category').val()},
                        {'name': 'delstatus', 'value': $('#deliverystatus').val()},
                        {'name': 'mobileno', 'value': $('#mobileno').val()},
                        {'name': 'txnType', 'value': $('#txntype').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listSmsOutBox.json",
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
                    var info = this.api().page.info();
                    if (info.recordsTotal > 0) {
                        $("#viewCSV").attr("disabled", false);
                        $("#viewPDF").attr("disabled", false);
                        $("#viewExcel").attr("disabled", false);
                    } else {
                        $("#viewCSV").attr("disabled", true);
                        $("#viewPDF").attr("disabled", true);
                        $("#viewExcel").attr("disabled", true);
                    }
                },
                columnDefs: [
                    {
                        title: "ID",
                        targets: 0,
                        mDataProp: "id",
                        defaultContent: "--"
                    },
                    {
                        title: "Reference Number",
                        targets: 1,
                        mDataProp: "referenceNo",
                        defaultContent: "--"
                    },
                    {
                        title: "Mobile Number",
                        targets: 2,
                        mDataProp: "mobileNumber",
                        defaultContent: "--"
                    },
                    {
                        title: "Message",
                        targets: 3,
                        mDataProp: "message",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 4,
                        mDataProp: "status",
                        defaultContent: "--"
                    },
                    {
                        title: "Delivery Status",
                        targets: 5,
                        mDataProp: "deleteStatus",
                        defaultContent: "--"
                    },
                    {
                        title: "Telco Service Provider",
                        targets: 6,
                        mDataProp: "telco",
                        defaultContent: "--"
                    },
                    {
                        title: "Channel",
                        targets: 7,
                        mDataProp: "channel",
                        defaultContent: "--"
                    },
                    {
                        title: "SMS Category",
                        targets: 8,
                        mDataProp: "category",
                        defaultContent: "--"
                    },
                    {
                        title: "Transaction Type",
                        targets: 9,
                        mDataProp: "trnType",
                        defaultContent: "--"
                    },
                    {
                        label: 'Created Date & Time',
                        name: 'createdTime',
                        targets: 10,
                        mDataProp: "createdTime",
                        render: function (data) {
                            return moment(data).format("YYYY-MM-DD")
                        }
                    }
                ]
            });
        }

        function search() {
            var fromdate = $('#fromDate').val();
            var todate = $('#toDate').val();

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

        function resetSearch() {
            //reset the fields
            $('#fromDate').val(getFromDate()).datepicker("update");
            $('#toDate').val(getToDate()).datepicker("update");
            $('#telco').val("");
            $('#department').val("");
            $('#smschannel').val("");
            $('#category').val("");
            $('#deliverystatus').val("");
            $('#mobileno').val("");
            $('#txntype').val("");
            //reset the data table
            oTable.fnDraw();
            document.getElementById("todate-validation-msg").style.visibility = "hidden";
            document.getElementById("todate-default-msg").style.visibility = "visible";
        }

        function viewPDFReport() {
            form = document.getElementById('smsoutboxreportview');
            form.action = 'downloadPdfReportSmsOutBox.htm';
            form.submit();
        }

        function viewExcelReport() {
            form = document.getElementById('smsoutboxreportview');
            form.action = 'downloadExcelReportSmsOutBox.htm';
            form.submit();
        }

        function viewCsvReport() {
            form = document.getElementById('smsoutboxreportview');
            form.action = 'downloadCsvReportSmsOutBox.htm';
            form.submit();
        }
    </script>
</head>
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
    <!--begin::Subheader-->
    <div class="subheader py-2 py-lg-6 subheader-solid" id="kt_subheader">
        <div class="container-fluid d-flex align-items-center justify-content-between flex-wrap flex-sm-nowrap">
            <!--begin::Info-->
            <div class="d-flex align-items-center flex-wrap mr-1">
                <!--begin::Page Heading-->
                <div class="d-flex align-items-baseline flex-wrap mr-5">
                    <!--begin::Page Title-->
                    <h5 class="text-dark font-weight-bold my-1 mr-5">Sms Outbox Report</h5>
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
                            <h3 class="card-title">Search Sms Outbox</h3>
                        </div>

                        <!--begin::Form-->
                        <form:form class="form" id="smsoutboxreportview" name="smsoutboxreportview" action="SmsOutBox"
                                   theme="simple" method="post" modelAttribute="smsoutputreport">

                            <div class="card-body">
                                <div class="form-group row">
                                    <div class="col-lg-4">
                                        <label>From Date:</label>
                                        <div class="btn-group div-inline input-group input-group-sm input-append date">
                                            <input path="fromDate" name="fromDate" id="fromDate"
                                                   class="form-control" readonly="true" onkeydown="return false"
                                                   autocomplete="off"/>
                                        </div>
                                        <span class="form-text text-muted">Please select From date</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>To Date:</label>
                                        <div class="btn-group div-inline input-group input-group-sm input-append date">
                                            <input path="toDate" name="toDate" id="toDate"
                                                   class="form-control" readonly="true" onkeydown="return false"
                                                   autocomplete="off"/>
                                        </div>
                                        <span class="form-text text-muted" id="todate-default-msg">Please select To date</span>
                                        <span class="form-text text-danger" id="todate-validation-msg"></span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label for="telco">Telco</label>
                                        <select id="telco" name="telco" class="form-control form-control-sm">
                                            <option selected value="">Select Telco</option>
                                            <c:forEach items="${smsoutputreport.telcoList}" var="telco">
                                                <option value="${telco.code}">${telco.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please select Telco</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label for="smschannel">SMS Channel</label>
                                        <select id="smschannel" name="channel" class="form-control form-control-sm">
                                            <option selected value="">Select SMS Channel</option>
                                            <c:forEach items="${smsoutputreport.smsChannelList}" var="smschannel">
                                                <option value="${smschannel.channelCode}">${smschannel.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please select SMS Channel</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>SMS Category:</label>
                                        <select path="category" name="category" id="category"
                                                class="form-control form-control-sm">
                                            <option selected value="">Select SMS Category</option>
                                            <c:forEach items="${smsoutputreport.categoryList}" var="categoryList">
                                                <option value="${categoryList.category}">${categoryList.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please select SMS Category</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Delivery Status:</label>
                                        <select path="deliverystatus" name="delstatus" id="deliverystatus"
                                                class="form-control form-control-sm">
                                            <option selected value="">Select Delivery Status</option>
                                            <c:forEach items="${smsoutputreport.delStatusList}" var="delStatusList">
                                                <option value="${delStatusList.code}">${delStatusList.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please enter Delivery Status</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Mobile Number:</label>
                                        <input path="mobileno" name="mobileno" id="mobileno"
                                               type="text"
                                               onkeyup="$(this).val($(this).val().replace(/[^0-9 ]/g,''))" onmouseout="$(this).val($(this).val().replace(/[^0-9 ]/g,''))" maxlength="11"
                                               class="form-control form-control-sm" placeholder="Mobile Number">
                                        <span class="form-text text-muted">Please enter Mobile Number</span>
                                    </div>

                                    <div class="col-lg-4">
                                        <label>Transaction Type:</label>
                                        <select path="txntype" name="txnType" id="txntype"
                                                class="form-control form-control-sm">
                                            <option selected value="">Select Transaction Type</option>
                                            <c:forEach items="${smsoutputreport.txnTypeList}" var="txnList">
                                                <option value="${txnList.txntype}">${txnList.description}</option>
                                            </c:forEach>
                                        </select>
                                        <span class="form-text text-muted">Please enter Transaction Type</span>
                                    </div>

                                </div>
                            </div>

                            <div class="card-footer">
                                <div class="row">
                                    <div class="col-lg-3">
                                        <button type="button" class="btn btn-primary mr-2 btn-sm" onclick="search()"
                                                id="btnSearch">
                                            Search
                                        </button>

                                        <button type="button" class="btn btn-secondary btn-sm" onclick="resetSearch()"
                                                id="btnReset">
                                            Reset
                                        </button>
                                    </div>

                                    <div class="col-lg-5"></div>

                                    <div class="col-lg-4">
                                        <c:if test="${smsoutputreport.vdownload}">
                                            <button id="viewExcel" type="button" class="btn btn-primary mr-2 btn-sm"
                                                    onclick="viewExcelReport()">
                                                View Excel
                                            </button>
                                        </c:if>
                                        <c:if test="${smsoutputreport.vdownload}">
                                            <button id="viewPDF" type="button" class="btn btn-primary mr-2 btn-sm"
                                                    onclick="viewPDFReport()">
                                                View PDF
                                            </button>
                                        </c:if>
                                        <c:if test="${smsoutputreport.vdownload}">
                                            <button id="viewCSV" type="button" class="btn btn-primary mr-2 btn-sm"
                                                    onclick="viewCsvReport()">
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
                                <th>Reference No</th>
                                <th>Mobile Number</th>
                                <th>Message</th>
                                <th>Status</th>
                                <th>Delete Status</th>
                                <th>Telco Service Provider</th>
                                <th>Channel</th>
                                <th>SMS Category</th>
                                <th>Transaction Type</th>
                                <th>Created Time</th>
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
</div>
</html>
