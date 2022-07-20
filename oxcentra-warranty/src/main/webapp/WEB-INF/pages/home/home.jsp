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

            <!--end::Info-->
            <!--begin::Toolbar-->
            <%--            <div class="d-flex align-items-center">--%>
            <%--                <!--begin::Actions-->--%>
            <%--                <a href="#" class="btn btn-clean btn-sm font-weight-bold font-size-base mr-1">Today</a>--%>
            <%--                <a href="#" class="btn btn-clean btn-sm font-weight-bold font-size-base mr-1">Month</a>--%>
            <%--                <a href="#" class="btn btn-clean btn-sm font-weight-bold font-size-base mr-1">Year</a>--%>
            <%--                <!--end::Actions-->--%>
            <%--                <!--begin::Daterange-->--%>
            <%--                <a href="#" class="btn btn-sm btn-light font-weight-bold mr-2" id="kt_dashboard_daterangepicker"--%>
            <%--                   data-toggle="tooltip" title="Select dashboard daterange" data-placement="left">--%>
            <%--                    <span class="text-muted font-size-base font-weight-bold mr-2"--%>
            <%--                          id="kt_dashboard_daterangepicker_title">Today</span>--%>
            <%--                    <span class="text-primary font-size-base font-weight-bolder" id="kt_dashboard_daterangepicker_date">Aug 16</span>--%>
            <%--                </a>--%>
            <%--                <!--end::Daterange-->--%>
            <%--                <!--begin::Dropdowns-->--%>
            <%--                <div class="dropdown dropdown-inline" data-toggle="tooltip" title="Quick actions" data-placement="left">--%>
            <%--                    <a href="#" class="btn btn-sm btn-clean btn-icon" data-toggle="dropdown" aria-haspopup="true"--%>
            <%--                       aria-expanded="false">--%>
            <%--                                        <span class="svg-icon svg-icon-success svg-icon-lg">--%>
            <%--											<!--begin::Svg Icon | path:assets/media/svg/icons/Files/File-plus.svg-->--%>
            <%--											<svg xmlns="http://www.w3.org/2000/svg"--%>
            <%--                                                 xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px"--%>
            <%--                                                 viewBox="0 0 24 24" version="1.1">--%>
            <%--												<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">--%>
            <%--													<polygon points="0 0 24 0 24 24 0 24"/>--%>
            <%--													<path--%>
            <%--                                                            d="M5.85714286,2 L13.7364114,2 C14.0910962,2 14.4343066,2.12568431 14.7051108,2.35473959 L19.4686994,6.3839416 C19.8056532,6.66894833 20,7.08787823 20,7.52920201 L20,20.0833333 C20,21.8738751 19.9795521,22 18.1428571,22 L5.85714286,22 C4.02044787,22 4,21.8738751 4,20.0833333 L4,3.91666667 C4,2.12612489 4.02044787,2 5.85714286,2 Z"--%>
            <%--                                                            fill="#000000" fill-rule="nonzero" opacity="0.3"/>--%>
            <%--													<path--%>
            <%--                                                            d="M11,14 L9,14 C8.44771525,14 8,13.5522847 8,13 C8,12.4477153 8.44771525,12 9,12 L11,12 L11,10 C11,9.44771525 11.4477153,9 12,9 C12.5522847,9 13,9.44771525 13,10 L13,12 L15,12 C15.5522847,12 16,12.4477153 16,13 C16,13.5522847 15.5522847,14 15,14 L13,14 L13,16 C13,16.5522847 12.5522847,17 12,17 C11.4477153,17 11,16.5522847 11,16 L11,14 Z"--%>
            <%--                                                            fill="#000000"/>--%>
            <%--												</g>--%>
            <%--											</svg>--%>
            <%--                                            <!--end::Svg Icon-->--%>
            <%--										</span>--%>
            <%--                    </a>--%>
            <%--                    <div class="dropdown-menu p-0 m-0 dropdown-menu-md dropdown-menu-right py-3">--%>
            <%--                        <!--begin::Navigation-->--%>
            <%--                        <ul class="navi navi-hover py-5">--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-drop"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">New Group</span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-list-3"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">Contacts</span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-rocket-1"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">Groups</span>--%>
            <%--                                    <span class="navi-link-badge">--%>
            <%--														<span--%>
            <%--                                                                class="label label-light-primary label-inline font-weight-bold">new</span>--%>
            <%--                                                    </span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-bell-2"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">Calls</span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-gear"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">Settings</span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                            <li class="navi-separator my-3"></li>--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-magnifier-tool"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">Help</span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                            <li class="navi-item">--%>
            <%--                                <a href="#" class="navi-link">--%>
            <%--                                                    <span class="navi-icon">--%>
            <%--														<i class="flaticon2-bell-2"></i>--%>
            <%--													</span>--%>
            <%--                                    <span class="navi-text">Privacy</span>--%>
            <%--                                    <span class="navi-link-badge">--%>
            <%--														<span--%>
            <%--                                                                class="label label-light-danger label-rounded font-weight-bold">5</span>--%>
            <%--                                                    </span>--%>
            <%--                                </a>--%>
            <%--                            </li>--%>
            <%--                        </ul>--%>
            <%--                        <!--end::Navigation-->--%>
            <%--                    </div>--%>
            <%--                </div>--%>
            <%--                <!--end::Dropdowns-->--%>
            <%--            </div>--%>
            <!--end::Toolbar-->
        </div>
    </div>
    <!--end::Subheader-->
    <!--begin::Entry-->
    <div class="d-flex flex-column-fluid">
        <!--begin::Container-->
        <div class="container">
            <!--begin::Dashboard-->
            <!--begin::Row-->
            <div class="row">
                <c:if test="${not changePwdMode}" var="condition">
                    <c:forEach items="${sectionList}" var="section">
                        <div class="col-xl-4">
                            <div class="card card-custom card-stretch gutter-b">
                                <div class="card-body d-flex p-0">
                                    <div class="flex-grow-1 p-8 card-rounded flex-grow-1 bgi-no-repeat ${section.sectionCode}"
                                         style="background-position: calc(100% + 0.5rem) bottom; background-size: auto 70%; ">
                                        <h4 class="text-inverse-danger mt-2 font-weight-bolder">${section.description}</h4>
                                        <c:if test="${not changePwdMode}" var="condition">
                                            <c:set var="sectionCode" value="${section.sectionCode}"/>
                                            <c:forEach items="${pageMap[sectionCode]}" var="page">
                                                <ul class="ul-dashboard">
                                                    <li>
                                                        <a href="${pageContext.request.contextPath}/${page.url}"
                                                           id="${section.sectionCode}-${page.pageCode}">${page.description}</a>
                                                    </li>
                                                </ul>
                                            </c:forEach>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            <!--end::Engage Widget 2-->
                        </div>
                    </c:forEach>
                </c:if>


                <%--                <div class="col-xl-4">--%>
                <%--                    <!--begin::Engage Widget 1-->--%>
                <%--                    <div class="card card-custom card-stretch gutter-b">--%>
                <%--                        <div class="card-body d-flex p-0">--%>
                <%--                            <div class="flex-grow-1 p-8 card-rounded bgi-no-repeat d-flex align-items-center"--%>
                <%--                                 style="background-color: #FFF4DE; background-position: left bottom; background-size: auto 100%; background-image: url(assets/media/svg/humans/custom-2.svg)">--%>
                <%--                                <div class="row">--%>
                <%--                                    <div class="col-12 col-xl-5"></div>--%>
                <%--                                    <div class="col-12 col-xl-7">--%>
                <%--                                        <h4 class="text-danger font-weight-bolder">Join SAP now to get 35% off</h4>--%>
                <%--                                        <p class="text-dark-50 my-5 font-size-xl font-weight-bold">Offering discounts--%>
                <%--                                            for your online store can be a powerful weapon in to drive loyalty</p>--%>
                <%--                                        <a href="#" class="btn btn-danger font-weight-bold py-2 px-6">Join SaaS</a>--%>
                <%--                                    </div>--%>
                <%--                                </div>--%>
                <%--                            </div>--%>
                <%--                        </div>--%>
                <%--                    </div>--%>
                <%--                    <!--end::Engage Widget 1-->--%>
                <%--                </div>--%>
                <%--                <div class="col-xl-4">--%>
                <%--                    <!--begin::Engage Widget 2-->--%>
                <%--                    <div class="card card-custom card-stretch gutter-b">--%>
                <%--                        <div class="card-body d-flex p-0">--%>
                <%--                            <div class="flex-grow-1 bg-danger p-8 card-rounded flex-grow-1 bgi-no-repeat"--%>
                <%--                                 style="background-position: calc(100% + 0.5rem) bottom; background-size: auto 70%; background-image: url(assets/media/svg/humans/custom-3.svg)">--%>
                <%--                                <h4 class="text-inverse-danger mt-2 font-weight-bolder">User Confidence</h4>--%>

                <%--                                <c:if test="${not changePwdMode}" var="condition">--%>
                <%--                                    <c:forEach items="${sectionList}" var="section">--%>
                <%--                                        <ul class="ul-dashboard">--%>
                <%--                                            <li>${section.description}</li>--%>
                <%--                                        </ul>--%>
                <%--                                    </c:forEach>--%>
                <%--                                </c:if>--%>
                <%--                            </div>--%>
                <%--                        </div>--%>
                <%--                    </div>--%>
                <%--                    <!--end::Engage Widget 2-->--%>
                <%--                </div>--%>
                <%--                <div class="col-xl-4">--%>
                <%--                    <!--begin::Engage Widget 3-->--%>
                <%--                    <div class="card card-custom card-stretch gutter-b">--%>
                <%--                        <div class="card-body d-flex p-0 card-rounded">--%>
                <%--                            <div class="flex-grow-1 p-10 card-rounded flex-grow-1 bgi-no-repeat"--%>
                <%--                                 style="background-color: #663259; background-position: calc(100% + 0.5rem) bottom; background-size: auto 75%; background-image: url(assets/media/svg/humans/custom-4.svg)">--%>
                <%--                                <h4 class="text-inverse-danger mt-2 font-weight-bolder">Based On</h4>--%>
                <%--                                <div class="mt-5">--%>
                <%--                                    <div class="d-flex mb-5">--%>
                <%--															<span class="svg-icon svg-icon-md svg-icon-white flex-shrink-0 mr-3">--%>
                <%--																<!--begin::Svg Icon | path:assets/media/svg/icons/Navigation/Arrow-right.svg-->--%>
                <%--																<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                                                     xmlns:xlink="http://www.w3.org/1999/xlink"--%>
                <%--                                                                     width="24px" height="24px" viewBox="0 0 24 24"--%>
                <%--                                                                     version="1.1">--%>
                <%--																	<g stroke="none" stroke-width="1" fill="none"--%>
                <%--                                                                       fill-rule="evenodd">--%>
                <%--																		<polygon points="0 0 24 0 24 24 0 24"/>--%>
                <%--																		<rect fill="#000000" opacity="0.3"--%>
                <%--                                                                              transform="translate(12.000000, 12.000000) rotate(-90.000000) translate(-12.000000, -12.000000)"--%>
                <%--                                                                              x="11" y="5" width="2" height="14"--%>
                <%--                                                                              rx="1"/>--%>
                <%--																		<path d="M9.70710318,15.7071045 C9.31657888,16.0976288 8.68341391,16.0976288 8.29288961,15.7071045 C7.90236532,15.3165802 7.90236532,14.6834152 8.29288961,14.2928909 L14.2928896,8.29289093 C14.6714686,7.914312 15.281055,7.90106637 15.675721,8.26284357 L21.675721,13.7628436 C22.08284,14.136036 22.1103429,14.7686034 21.7371505,15.1757223 C21.3639581,15.5828413 20.7313908,15.6103443 20.3242718,15.2371519 L15.0300721,10.3841355 L9.70710318,15.7071045 Z"--%>
                <%--                                                                              fill="#000000" fill-rule="nonzero"--%>
                <%--                                                                              transform="translate(14.999999, 11.999997) scale(1, -1) rotate(90.000000) translate(-14.999999, -11.999997)"/>--%>
                <%--																	</g>--%>
                <%--																</svg>--%>
                <%--                                                                <!--end::Svg Icon-->--%>
                <%--															</span>--%>
                <%--                                        <span class="text-white">Activities</span>--%>
                <%--                                    </div>--%>
                <%--                                    <div class="d-flex mb-5">--%>
                <%--															<span class="svg-icon svg-icon-md svg-icon-white flex-shrink-0 mr-3">--%>
                <%--																<!--begin::Svg Icon | path:assets/media/svg/icons/Navigation/Arrow-right.svg-->--%>
                <%--																<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                                                     xmlns:xlink="http://www.w3.org/1999/xlink"--%>
                <%--                                                                     width="24px" height="24px" viewBox="0 0 24 24"--%>
                <%--                                                                     version="1.1">--%>
                <%--																	<g stroke="none" stroke-width="1" fill="none"--%>
                <%--                                                                       fill-rule="evenodd">--%>
                <%--																		<polygon points="0 0 24 0 24 24 0 24"/>--%>
                <%--																		<rect fill="#000000" opacity="0.3"--%>
                <%--                                                                              transform="translate(12.000000, 12.000000) rotate(-90.000000) translate(-12.000000, -12.000000)"--%>
                <%--                                                                              x="11" y="5" width="2" height="14"--%>
                <%--                                                                              rx="1"/>--%>
                <%--																		<path d="M9.70710318,15.7071045 C9.31657888,16.0976288 8.68341391,16.0976288 8.29288961,15.7071045 C7.90236532,15.3165802 7.90236532,14.6834152 8.29288961,14.2928909 L14.2928896,8.29289093 C14.6714686,7.914312 15.281055,7.90106637 15.675721,8.26284357 L21.675721,13.7628436 C22.08284,14.136036 22.1103429,14.7686034 21.7371505,15.1757223 C21.3639581,15.5828413 20.7313908,15.6103443 20.3242718,15.2371519 L15.0300721,10.3841355 L9.70710318,15.7071045 Z"--%>
                <%--                                                                              fill="#000000" fill-rule="nonzero"--%>
                <%--                                                                              transform="translate(14.999999, 11.999997) scale(1, -1) rotate(90.000000) translate(-14.999999, -11.999997)"/>--%>
                <%--																	</g>--%>
                <%--																</svg>--%>
                <%--                                                                <!--end::Svg Icon-->--%>
                <%--															</span>--%>
                <%--                                        <span class="text-white">Sales</span>--%>
                <%--                                    </div>--%>
                <%--                                    <div class="d-flex">--%>
                <%--															<span class="svg-icon svg-icon-md svg-icon-white flex-shrink-0 mr-3">--%>
                <%--																<!--begin::Svg Icon | path:assets/media/svg/icons/Navigation/Arrow-right.svg-->--%>
                <%--																<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                                                     xmlns:xlink="http://www.w3.org/1999/xlink"--%>
                <%--                                                                     width="24px" height="24px" viewBox="0 0 24 24"--%>
                <%--                                                                     version="1.1">--%>
                <%--																	<g stroke="none" stroke-width="1" fill="none"--%>
                <%--                                                                       fill-rule="evenodd">--%>
                <%--																		<polygon points="0 0 24 0 24 24 0 24"/>--%>
                <%--																		<rect fill="#000000" opacity="0.3"--%>
                <%--                                                                              transform="translate(12.000000, 12.000000) rotate(-90.000000) translate(-12.000000, -12.000000)"--%>
                <%--                                                                              x="11" y="5" width="2" height="14"--%>
                <%--                                                                              rx="1"/>--%>
                <%--																		<path d="M9.70710318,15.7071045 C9.31657888,16.0976288 8.68341391,16.0976288 8.29288961,15.7071045 C7.90236532,15.3165802 7.90236532,14.6834152 8.29288961,14.2928909 L14.2928896,8.29289093 C14.6714686,7.914312 15.281055,7.90106637 15.675721,8.26284357 L21.675721,13.7628436 C22.08284,14.136036 22.1103429,14.7686034 21.7371505,15.1757223 C21.3639581,15.5828413 20.7313908,15.6103443 20.3242718,15.2371519 L15.0300721,10.3841355 L9.70710318,15.7071045 Z"--%>
                <%--                                                                              fill="#000000" fill-rule="nonzero"--%>
                <%--                                                                              transform="translate(14.999999, 11.999997) scale(1, -1) rotate(90.000000) translate(-14.999999, -11.999997)"/>--%>
                <%--																	</g>--%>
                <%--																</svg>--%>
                <%--                                                                <!--end::Svg Icon-->--%>
                <%--															</span>--%>
                <%--                                        <span class="text-white">Releases</span>--%>
                <%--                                    </div>--%>
                <%--                                </div>--%>
                <%--                            </div>--%>
                <%--                        </div>--%>
                <%--                    </div>--%>
                <%--                    <!--end::Engage Widget 3-->--%>
                <%--                </div>--%>
                <%--            </div>--%>
                <%--            <!--end::Row-->--%>
                <%--            <!--begin::Row-->--%>
                <%--            <div class="row">--%>
                <%--                <div class="col-xl-4">--%>
                <%--                    <!--begin::Engage Widget 4-->--%>
                <%--                    <div class="card card-custom card-stretch gutter-b">--%>
                <%--                        <div class="card-body d-flex p-0">--%>
                <%--                            <div class="flex-grow-1 bg-light-success p-12 pb-40 card-rounded flex-grow-1 bgi-no-repeat"--%>
                <%--                                 style="background-position: calc(100% + 0.5rem) bottom; background-size: 35% auto; background-image: url(assets/media/svg/humans/custom-5.svg)">--%>
                <%--                                <p class="text-success pt-10 pb-5 font-size-h3 font-weight-bolder line-height-lg">Start--%>
                <%--                                    with a branding--%>
                <%--                                    <br/>site design modern--%>
                <%--                                    <br/>content creation</p>--%>
                <%--                                <a href="#" class="btn btn-success font-weight-bold py-2 px-6">Join Now</a>--%>
                <%--                            </div>--%>
                <%--                        </div>--%>
                <%--                    </div>--%>
                <%--                    <!--end::Engage Widget 4-->--%>
                <%--                </div>--%>
                <%--                <div class="col-xl-4">--%>
                <%--                    <!--begin::Engage Widget 5-->--%>
                <%--                    <div class="card card-custom card-stretch gutter-b">--%>
                <%--                        <div class="card-body d-flex p-0">--%>
                <%--                            <div class="flex-grow-1 bg-info p-12 pb-40 card-rounded flex-grow-1 bgi-no-repeat"--%>
                <%--                                 style="background-position: right bottom; background-size: 55% auto; background-image: url(assets/media/svg/humans/custom-6.svg)">--%>
                <%--                                <h3 class="text-inverse-info pb-5 font-weight-bolder">Start Now</h3>--%>
                <%--                                <p class="text-inverse-info pb-5 font-size-h6">Offering discounts for better--%>
                <%--                                    <br/>online a store can loyalty--%>
                <%--                                    <br/>weapon into driving</p>--%>
                <%--                                <a href="#" class="btn btn-success font-weight-bold py-2 px-6">Join Now</a>--%>
                <%--                            </div>--%>
                <%--                        </div>--%>
                <%--                    </div>--%>
                <%--                    <!--end::Engage Widget 5-->--%>
                <%--                </div>--%>
                <%--                <div class="col-xl-4">--%>
                <%--                    <!--begin::Engage Widget 6-->--%>
                <%--                    <div class="card card-custom card-stretch gutter-b">--%>
                <%--                        <div class="card-body d-flex p-0">--%>
                <%--                            <div class="flex-grow-1 bg-danger p-12 pb-40 card-rounded flex-grow-1 bgi-no-repeat"--%>
                <%--                                 style="background-position: calc(100% + 0.5rem) bottom; background-size: 35% auto; background-image: url(assets/media/svg/humans/custom-7.svg)">--%>
                <%--                                <p class="text-inverse-danger pt-10 pb-5 font-size-h3 font-weight-bolder line-height-lg">--%>
                <%--                                    Start with a branding--%>
                <%--                                    <br/>site design modern--%>
                <%--                                    <br/>content creation</p>--%>
                <%--                                <a href="#" class="btn btn-warning font-weight-bold py-2 px-6">Join Now</a>--%>
                <%--                            </div>--%>
                <%--                        </div>--%>
                <%--                    </div>--%>
                <%--                    <!--end::Engage Widget 6-->--%>
                <%--                </div>--%>
            </div>
        </div>
        <!--end::Container-->
    </div>
    <!--end::Entry-->
</div>
