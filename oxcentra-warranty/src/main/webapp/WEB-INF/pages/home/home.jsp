<%--
  Created by IntelliJ IDEA.
  User: prathibha_w
  Date: 2/11/2021
  Time: 11:17 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:set var="changePwdMode" value="${sessionBean.changePwdMode}"/>
<c:set var="sectionList" value="${sessionBean.sectionList}"/>
<c:set var="pageMap" value="${sessionBean.pageMap}"/>
<c:set var="daysToExpire" value="${sessionBean.daysToExpire}"/>

<head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
</head>
<body>
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
    <!--begin::Subheader-->
    <div class="subheader py-2 py-lg-4 subheader-solid" id="kt_subheader">
        <div class="container-fluid d-flex align-items-center justify-content-between flex-wrap flex-sm-nowrap">
            <!--begin::Info-->
            <div class="d-flex align-items-center flex-wrap mr-2">
                <!--begin::Page Title-->
                <h5 class="text-dark font-weight-bold mt-2 mb-2 mr-5">Home</h5>
                <!--end::Page Title-->
                <!--begin::Actions-->
                <div class="subheader-separator subheader-separator-ver mt-2 mb-2 mr-4 bg-gray-200">
                </div>
                <span class="text-muted font-weight-bold mr-4">#WARRANTY</span>
                <%--                <a href="#" class="btn btn-light-warning font-weight-bolder btn-sm">Add New</a>--%>
                <!--end::Actions-->
            </div>
            <div class="d-flex align-items-baseline flex-wrap mr-5">
                <button type="button" class="btn btn-danger">
                    Pending Approvals <span class="badge badge-light" id="pendingCount">${homeform.countPending}</span>
                </button>
            </div>
            <div class="d-flex align-items-baseline flex-wrap mr-5">
                <button type="button" class="btn btn-success">
                    In Purchase Request Count <span class="badge badge-light" id="purchaseCount">${homeform.countInPurchase}</span>
                </button>
            </div>
            <div class="d-flex align-items-baseline flex-wrap mr-5">
                <button type="button" class="btn btn-warning" >
                    Noted Request Count <span class="badge badge-light" id="notedCount">${homeform.countNoted}</span>
                </button>
            </div>


        </div>

    </div>
    <div class="card-body">
        <div class="form-group row">
            <div class="col-lg-3">
                <label>From Date:</label>
                <div class="btn-group div-inline input-group input-group-sm input-append date">
                    <input path="fromDate" name="fromDate" id="searchFromDate"
                           class="form-control" readonly="true" onkeydown="return false"
                           autocomplete="off"/>
                </div>
                <span class="form-text text-muted">Please select From date</span>
            </div>

            <div class="col-lg-3">
                <label>To Date:</label>
                <div class="btn-group div-inline input-group input-group-sm input-append date">
                    <input path="toDate" name="toDate" id="searchToDate"
                           class="form-control" readonly="true" onkeydown="return false"
                           autocomplete="off"/>
                </div>
                <span class="form-text text-muted" id="todate-default-msg">Please select To date</span>
                <span class="form-text text-danger" id="todate-validation-msg"></span>
            </div>
            <div class="col-lg-3">
                <button type="button" class="btn btn-primary mr-2 btn-sm" onclick="getStatusCount()"
                        id="btnSearch">
                    Search
                </button>
                <button type="button" class="btn btn-secondary btn-sm" onclick="resetForm()"
                        id="btnReset">
                    Reset
                </button>
            </div>
        </div>
    </div>
    <!--end::Subheader-->
    <!--begin::Entry-->

    <div class="card-columns">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Count of Unit State</h5>
                <canvas id="myChart" ></canvas>
            </div>
        </div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Total Count vs Failing Area</h5>
                <canvas id="myChartFailingArea" ></canvas>
            </div>
        </div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Total Cost vs Failing Area</h5>
                <canvas id="myChartFailingAreaCost" ></canvas>
            </div>
        </div>


    </div>

</div>
</body>
<script type="text/javascript">


    function resetForm(){

        setFromDate();
        setToDate();

    }

    function getStatusCount() {

        const supplierId = "2";

        $.ajax({
            url: "${pageContext.request.contextPath}/getSummaryhome.json",
            data: {
                supplierId: supplierId
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {

                console.log(data);

                $('span#pendingCount').html( data.countPending );
                $('span#purchaseCount').html(data.countInPurchase);
                $('span#notedCount').html(data.countNoted);

                var monthlyData = [];
                var monthlyCount = [];
                $.each(data.statusCountList, function (index, item) {
                    monthlyData .push(item.status);
                    monthlyCount.push(item.statusCount)
                });

                createChart(monthlyData,monthlyCount);


                var failingArea = [];
                var failingAreaCount = [];
                $.each(data.failingAreaCountList, function (index, item) {
                    failingArea .push(item.failingArea);
                    failingAreaCount.push(item.failingAreaCount)
                });

                createFailingAreaChart(failingArea,failingAreaCount);

                var failingAreaCost = [];
                var failingAreaCostCount = [];
                $.each(data.failingAreaCostCountList, function (index, item) {
                    failingAreaCost .push(item.failingAreaCost);
                    failingAreaCostCount.push(item.failingAreaCostCount)
                });

                createFailingAreaCostChart(failingAreaCost,failingAreaCostCount);


            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });

    }

    function createFailingAreaChart (failingArea,failingAreaCount){

        let chartStatus = Chart.getChart("myChartFailingArea");
        if (chartStatus != undefined) {
            chartStatus.destroy();
        }


        const ctx = document.getElementById('myChartFailingArea').getContext('2d');
        var myChartFailingArea = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: failingArea,
                datasets: [{
                    label: 'Total Count vs Failing Area  ',
                    data: failingAreaCount,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                title: {
                    display: true,
                    text: 'Total Count vs Failing Area'
                },
                responsive: true,
                maintainAspectRatio: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function createFailingAreaCostChart (failingAreaCost,failingAreaCostCount){

        let chartStatus = Chart.getChart("myChartFailingAreaCost");
        if (chartStatus != undefined) {
            chartStatus.destroy();
        }


        const ctx = document.getElementById('myChartFailingAreaCost').getContext('2d');
        var myChartFailingAreaCost = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: failingAreaCost,
                datasets: [{
                    label: 'Total Cost vs Failing Area  ',
                    data: failingAreaCostCount,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                title: {
                    display: true,
                    text: 'Total Cost vs Failing Area'
                },
                responsive: true,
                maintainAspectRatio: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }





    function createChart (strArrayStatus,strCount){

        let chartStatus = Chart.getChart("myChart"); // <canvas> id
        if (chartStatus != undefined) {
            chartStatus.destroy();
        }


        alert(strArrayStatus);
        alert(strCount);

    const ctx = document.getElementById('myChart').getContext('2d');
     var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: strArrayStatus,
            datasets: [{
                label: 'Count of Unit State',
                data: strCount,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            title: {
                display: true,
                text: 'Count of Unit State'
            },
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
    }

    window.addEventListener('beforeprint', () => {
        myChart.resize(600, 600);
    });
    window.addEventListener('afterprint', () => {
        myChart.resize();
    });

    $(document).ready(function () {

        alert("sdfdsfsdf");

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


</script>
