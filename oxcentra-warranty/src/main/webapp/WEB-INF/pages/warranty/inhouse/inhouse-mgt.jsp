                                                                                             <%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <style>
        hr {
            border: 1px solid #b9bbbe;
        }
    </style>
    <script type="text/javascript">

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var oTable;

        $(document).ready(function () {
            loadDataTable();
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
                sAjaxSource: "${pageContext.servletContext.contextPath}/listInHouse.json",
                fnServerData: function (sSource, aoData, fnCallback) {
                    aoData.push(
                        {'name': 'csrf_token', 'value': token},
                        {'name': 'header', 'value': header},
                        {'name': 'id', 'value': $('#searchId').val()}
                    );
                    $.ajax({
                        dataType: 'json',
                        type: 'POST',
                        url: "${pageContext.request.contextPath}/listInHouse.json",
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
                        title: "Warrenty ID",
                        targets: 0,
                        mDataProp: "id",
                        defaultContent: "--"
                    },
                    {
                        title: "First Name",
                        targets: 1,
                        mDataProp: "firstName",
                        defaultContent: "--"
                    },
                    {
                        title: "Last Name",
                        targets: 2,
                        mDataProp: "lastName",
                        defaultContent: "--"
                    },
                    {
                        title: "Phone Number",
                        targets: 3,
                        mDataProp: "phone",
                        defaultContent: "--"
                    },
                    {
                        title: "Email",
                        targets: 4,
                        mDataProp: "email",
                        defaultContent: "--"
                    },
                    {
                        title: "Delarship",
                        targets: 5,
                        mDataProp: "dealership",
                        defaultContent: "--"
                    },
                    {
                        title: "Status",
                        targets: 6,
                        mDataProp: "status",
                        defaultContent: "--",
                        render: function (data, type, full, meta) {
                            var status = {
                                'Approved': {
                                    'title': 'Approved',
                                    'class': ' label-light-info'
                                },
                                'Supplier Rejected': {
                                    'title': 'Supplier Rejected',
                                    'class': ' label-light-danger'
                                },
                                'Pending': {
                                    'title': 'Pending',
                                    'class': ' label-light-primary'
                                },
                                'Ackonwledged': {
                                    'title': 'Pre Approved',
                                    'class': ' label-light-success'
                                },
                                'Head Office Rejected': {
                                    'title': 'Head Office Rejected',
                                    'class': ' label-light-warning'
                                },
                                'Noted': {
                                    'title': 'Noted',
                                    'class': ' label-light-success'
                                },'In Purchase': {
                                    'title': 'In Purchase',
                                    'class': ' label-light-info'
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
                        visible: ${inHouse.vupdate},
                        title: "View",
                        sortable: false,
                        className: "dt-center",
                        mRender: function (data, type, full) {
                            return '<button id="editBtn" class="btn btn-default btn-sm"  onclick="editClaimInit(\'' + full.id + '\')"><img src="${pageContext.request.contextPath}/resources/images/action-view.svg" alt=""></button>';
                        },
                        targets: 11,
                        defaultContent: "--"
                    }
                ]
            });
        }

        function editClaimInit(id) {

            $.ajax({
                url: "${pageContext.request.contextPath}/getInHouse.json",
                data: {
                    id: id
                },
                dataType: "json",
                type: 'GET',
                contentType: "application/json",
                success: function (data) {
                    $('#responseMsgUpdate').hide();

                    $('#editId').val(data.id);
                    $('#editId').attr('readOnly', true);

                    $('#editModel').html(data.model);
                    $('#editChassisNumber').html(data.chassis);
                    $('#editFirstName').html(data.firstName);
                    $('#editLastName').html(data.lastName);
                    $('#editPhone').html(data.phone);
                    $('#editEmail').html(data.email);
                    $('#editAddress').html(data.address);
                    $('#editSurburb').html(data.surburb);
                    $('#editState').html(data.state);
                    $('#editPostcode').html(data.postcode);
                    $('#editDealership').html(data.dealership);
                    $('#editClaimType').html(data.claimType);
                    $('#editPurchasingDate').html(data.purchasingDate);
                    $('#editDescription').html(data.description);
                    $('#editFailureType').html(data.failureType);
                    $('#editFailureArea').html(data.failureArea);
                    $('#editRepairType').html(data.repairType);
                    $('#editRepairDescription').html(data.repairDescription);

                    $('#editCostType').val(data.costType);
                    $('#editHours').val(data.hours);
                    $('#editLabourRate').val(data.labourRate);
                    $('#editTotalCost').val(data.totalCost);
                    $('#editCostDescription').val(data.costDescription);

                    $('#editCusFirstName').html(data.firstName);
                    $('#editCusContactNo').html(data.phone);
                    $('#editCusEmail').html(data.email);
                    $('#editCusAddress').html(data.address);

                    $('#editContactDelarship').html(data.dealership);
                    $('#editDealershipName').html(data.dealershipName);
                    $('#editDealershipPhone').html(data.dealershipPhone);
                    $('#editDealershipEmail').html(data.dealershipEmail);
                    $('#editDealershipChassisNumber').html(data.chassis);
                    $('#editDealershipCaravanModel').html(data.model);
                    $('#editClaimType').html(data.claimType);
                    $('#editFailingArea').val(data.failingArea);

                    $('#editSupplier').val(data.supplier);
                    $('#editSupContactNumber').html(data.supplierPhone);
                    $('#editSupEmail').html(data.supplierEmail);
                    $('#editSupAddress').html(data.supplierAddress);
                    $('#editComment').val(data.comment);

                    if(data.inHouse === true ){
                        $('#isInHouse').attr('checked', true);
                        $('#nextBtn').hide();
                    }else{
                        $('#isInHouse').attr('checked', false);
                        $('#nextBtn').show();
                    }

                    $("#updateSparePartList").empty();
                    $("#viewSparePartList").empty();
                    $("#updatePdfFiletList").empty();

                    let sparePartLIst = data.sparePartList;
                    let table = $('<table/>').appendTo($('.sparePartList'));
                    let x = 0;

                    $('<tr/>').appendTo(table)
                        .append($('<td/>').html('<div class="form-row" ><div class="form-group col-md-8"> <label>Spare Part Required</label> </div> <div class="form-group col-md-1"><label>Quantity</label></div></div>'));

                    $(sparePartLIst).each(function (i, sparePartLIst) {
                        x++;
                        $('<tr/>').appendTo(table)
                            .append($('<td/>').html('<div class="form-row" ><div class="form-group col-md-8"> <input type="text" class="form-control form-control-sm" maxlength="20" name="sparePartRequired'+x+'"  value="'+sparePartLIst.sparePartType+'" placeholder="Spare Part Required"/> </div> <div class="form-group col-md-2"><input type="text" name="quantity'+x+'" class="form-control form-control-sm" maxlength="3" placeholder="Quantity" value="'+sparePartLIst.qty+'" /></div></div>'));
                    });

                    let sparePartSupList = data.sparePartList;
                    let tableSup = $('<table/>').appendTo($('.sparePartSupList'));
                    let y = 0;

                    $('<tr/>').appendTo(tableSup)
                        .append($('<td/>').html('<div class="form-row" ><div class="form-group col-md-8"> <label>Spare Part Required</label> </div> <div class="form-group col-md-1"><label>Quantity</label></div></div>'));

                    $(sparePartSupList).each(function (i, sparePartLIst) {
                        y++;
                        $('<tr/>').appendTo(tableSup)
                            .append($('<td/>').html('<div class="form-row" ><div class="form-group col-md-8"> <input readonly="true" type="text" class="form-control form-control-sm" maxlength="20" name="sparePartRequiredSup"  value="'+sparePartLIst.sparePartType+'" placeholder="Spare Part Required"/> </div> <div class="form-group col-md-2"><input readonly="true" type="text" name="quantitySup" class="form-control form-control-sm" maxlength="3" placeholder="Quantity" value="'+sparePartLIst.qty+'" /></div></div>'));
                    });


                    let repairFileList = data.repairFileList;
                    let tableAttachment = $('<table/>').appendTo($('#updateRepairFiletList'));

                    $.each(repairFileList, function (i, repairFileList) {

                        $('<tr/>').appendTo(tableAttachment)
                            .append($('<td/>').html('<img width="17" height="16"  src="${pageContext.request.contextPath}/resources/images/attachment.svg" />'))
                            .append($('<td/>').text(repairFileList.fileName))
                            .append($('<td/>').html('<a href="#" class="downloadImage" onClick="downloadFile(\'' + repairFileList.fileFormat +','+ repairFileList.base64value+','+repairFileList.fileName+'\')">Download</a>'));
                        ;
                    });


                    let claimTypeFileList = data.claimTypeFileList;
                    let tableClaimAttachment = $('<table/>').appendTo($('#updateClaimFiletList'));

                    $.each(claimTypeFileList, function (i, claimTypeFileList) {

                        $('<tr/>').appendTo(tableClaimAttachment)
                            .append($('<td/>').html('<img width="17" height="16"  src="${pageContext.request.contextPath}/resources/images/attachment.svg" />'))
                            .append($('<td/>').text(claimTypeFileList.fileName))
                            .append($('<td/>').html('<a href="#" class="downloadImage" onClick="downloadFile(\'' + claimTypeFileList.fileFormat +','+ claimTypeFileList.base64value+','+claimTypeFileList.fileName+'\')">Download</a>'));
                        ;
                    });


                    $('#firstTab').show();
                    $('#secondTab').hide();

                    $('#modalUpdateInHouseClaim').modal('toggle');
                    $('#modalUpdateInHouseClaim').modal('show');

                },
                error: function (data) {
                    window.location = "${pageContext.request.contextPath}/logout.htm";
                }
            });
        }

        function searchStart() {
            oTable.fnDraw();
        }

        function resetSearch() {
            $('#searchId').val("");
            $('#searchDescription').val("");
            $('#searchStatus').val("");
            oTable.fnDraw();
        }

        function search() {
            oTable.fnDraw();
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
                    <h5 class="text-dark font-weight-bold my-1 mr-5">In House Requests</h5>
                    <!--end::Page Title-->
                </div>
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
                <div class="card-header flex-wrap border-0 pt-1 pb-0">
                    <div class="card-title">
                        <div class="row">
                            <div class="col-lg-9">
                                <input id="searchId" name="searchId" type="text" maxlength="50" class="form-control">
                            </div>
                            <div class="col-lg-3">
                                <button type="button" class="btn btn-sm btn-primary mr-2" onclick="search()">
                                    Search
                                </button>
                            </div>
                        </div>
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
                                <th>Warrenty ID</th>
                                <th>First Name</th>
                                <th>Last Name</th>
                                <th>Phone Number</th>
                                <th>Email</th>
                                <th>Delarship</th>
                                <th>Status</th>
                                <th>Created User</th>
                                <th>Created Time</th>
                                <th>Last Updated User</th>
                                <th>Last Updated Time</th>
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
    </div>
</div>
<!-- start include jsp files -->
<jsp:include page="inhouse-mgt-view.jsp"/>
<jsp:include page="../../common/delete-modal.jsp"/>
<jsp:include page="../../common/confirm-modal.jsp"/>
<jsp:include page="../../common/reject-modal.jsp"/>
<!-- end include jsp files -->

</html>