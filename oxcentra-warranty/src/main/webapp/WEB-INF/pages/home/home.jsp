<%--
  Created by IntelliJ IDEA.
  User: yasas_p
  Date: 2/11/2021
  Time: 11:17 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:set var="changePwdMode" value="${sessionBean.changePwdMode}"/>
<c:set var="sectionList" value="${sessionBean.sectionList}"/>
<c:set var="pageMap" value="${sessionBean.pageMap}"/>
<c:set var="daysToExpire" value="${sessionBean.daysToExpire}"/>


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
                    In Purchase Request Count <span class="badge badge-light"
                                                    id="purchaseCount">${homeform.countInPurchase}</span>
                </button>
            </div>
            <div class="d-flex align-items-baseline flex-wrap mr-5">
                <button type="button" class="btn btn-warning">
                    Noted Request Count <span class="badge badge-light" id="notedCount">${homeform.countNoted}</span>
                </button>
            </div>
        </div>
    </div>
    <!--begin::Container-->
    <div class="container">
        <!--begin::Search-->
        <div class="card">
            <div class="form-group row">
                <div class="col-lg-2">
                </div>
                <div class="col-lg-2">
                    <label for="searchFromDate">From Date :</label>
                    <div class="btn-group div-inline input-group input-group-sm input-append date">
                        <input path="fromDate" name="fromDate" id="searchFromDate"
                               class="form-control" readonly="true" onkeydown="return false"
                               autocomplete="off"/>
                    </div>
                </div>
                <div class="col-lg-2">
                    <label for="searchToDate">To Date :</label>
                    <div class="btn-group div-inline input-group input-group-sm input-append date">
                        <input path="toDate" name="toDate" id="searchToDate"
                               class="form-control" readonly="true" onkeydown="return false"
                               autocomplete="off"/>
                    </div>
                </div>
                <div class="col-lg-2">
                    <label></label>
                    <div class="btn-group div-inline input-group input-group-sm input-append date">
                        <button type="button" class="btn btn-primary mr-2 btn-sm" onclick="getSummary()"
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
        </div>
        <!--end::Search-->
        <!--begin::Charts-->
        <div class="card-columns">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Count of Unit State</h5>
                    <canvas id="myChartStatusCount"></canvas>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Total Count vs Failing Area</h5>
                    <canvas id="myChartFailingArea"></canvas>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Total Cost vs Failing Area</h5>
                    <canvas id="myChartFailingAreaCost"></canvas>
                </div>
            </div>
        </div>
        <!--end::Search-->
        <!--begin::DataCounts-->
        <div class="card-columns">
            <div class="card">
                <div class="card-body">
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">Total Count of Insidents</th>
                            <th scope="col" id="totalCount"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">Total Cost For Approved Warranty Claims </th>
                            <th scope="col" id="totalCost"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <!--end::Container-->
    <!--begin::EndDataCounts-->
</div>

<script type="text/javascript">

    /*reset Function*/

    function resetForm() {
        getSummary();
        setFromDate();
        setToDate();
    }

    /*summary Function*/

    function getSummary() {

        const fromDate = $('#searchFromDate').val();
        const toDate = $('#searchToDate').val();

        $.ajax({
            url: "${pageContext.request.contextPath}/getSummaryhome.json",
            data: {
                fromDate: fromDate,
                toDate: toDate
            },
            dataType: "json",
            type: 'GET',
            contentType: "application/json",
            success: function (data) {

                console.log(data);

                <!--summary status count-->
                $('span#pendingCount').html(data.countPending);
                $('span#purchaseCount').html(data.countInPurchase);
                $('span#notedCount').html(data.countNoted);

                // $('#totalCount').text(data.totalCount);
                let totalClaimCost =  data.totalCost;
                if(totalClaimCost != null){
                    $('#totalCost').text(data.totalCost+" $");
                }else{
                    $('#totalCost').text("0.00 $");
                }

                <!--summary chart status count-->

                var status = [];
                var statusCount = [];
                $.each(data.statusCountList, function (index, item) {
                    status.push(item.status);
                    statusCount.push(item.statusCount);
                });

                createStatusCountChart(status, statusCount);

                <!--summary chart failing Area count-->

                var failingArea = [];
                var failingAreaCount = [];
                $.each(data.failingAreaCountList, function (index, item) {
                    failingArea.push(item.failingArea);
                    failingAreaCount.push(item.failingAreaCount);
                });

                createFailingAreaChart(failingArea, failingAreaCount);

                <!--summary chart failing Area Cost-->

                var failingAreaCost = [];
                var failingAreaCostCount = [];
                $.each(data.failingAreaCostCountList, function (index, item) {
                    failingAreaCost.push(item.failingAreaCost);
                    failingAreaCostCount.push(item.failingAreaCostCount);
                });

                createFailingAreaCostChart(failingAreaCost, failingAreaCostCount);


            },
            error: function (data) {
                window.location = "${pageContext.request.contextPath}/logout.htm";
            }
        });

    }


    <!--summary chart status count-->

    function createStatusCountChart(status, statusCount) {

        let chartStatus = Chart.getChart("myChartStatusCount"); // <canvas> id
        if (chartStatus != undefined) {
            chartStatus.destroy();
        }

        const ctx = document.getElementById('myChartStatusCount').getContext('2d');
        var myChartStatusCount = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: status,
                datasets: [{
                    label: 'Count of Unit State',
                    data: statusCount,
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

    <!--summary chart failing Area count-->

    function createFailingAreaChart(failingArea, failingAreaCount) {

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

    <!--summary chart failing Area Cost-->
    function createFailingAreaCostChart(failingAreaCost, failingAreaCostCount) {

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

    jQuery(document).ready(function () {

        /*get all summary details*/
        getSummary()

    });

    <!--calendar date -->

    $(document).ready(function () {

        $('#searchFromDate').datepicker({
            format: 'yyyy-mm-dd',
            endDate: '+0d',
            setDate: new Date()+30,
            todayHighlight: true,
            forceParse: false,
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
