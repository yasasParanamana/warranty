<%--
  Created by IntelliJ IDEA.
  User: prathibha_w
  Date: 2/11/2021
  Time: 10:41 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:set var="changePwdMode" value="${sessionBean.changePwdMode}"/>
<c:set var="sectionList" value="${sessionBean.sectionList}"/>
<c:set var="pageMap" value="${sessionBean.pageMap}"/>
<c:set var="daysToExpire" value="${sessionBean.daysToExpire}"/>

<div>
    <c:if test="${sessionBean.daysToExpire gt 0}">
        <c:if test="${not changePwdMode}">
            <spring:message code="login.expirywarning" arguments="${sessionBean.daysToExpire}" htmlEscape="false"
                            argumentSeparator=";"/>
        </c:if>
    </c:if>
</div>

<div class="aside aside-left aside-fixed d-flex flex-column flex-row-auto" id="kt_aside">
    <!--begin::Brand-->
    <div class="brand flex-column-auto" id="kt_brand">
        <!--begin::Logo-->
        <a href="${pageContext.request.contextPath}/home.htm" class="brand-logo">
            <img alt="Logo" src="${pageContext.request.contextPath}/resources/assets/media/logos/rdb.png"/>
        </a>
        <!--end::Logo-->
        <!--begin::Toggle-->
        <button class="brand-toggle btn btn-sm px-0" id="kt_aside_toggle">
						<span class="svg-icon svg-icon svg-icon-xl">
							<!--begin::Svg Icon | path:assets/media/svg/icons/Navigation/Angle-double-left.svg-->
							<svg xmlns="http://www.w3.org/2000/svg"
                                 width="24px" height="24px" viewBox="0 0 24 24" version="1.1">
								<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
									<polygon points="0 0 24 0 24 24 0 24"/>
									<path
                                            d="M5.29288961,6.70710318 C4.90236532,6.31657888 4.90236532,5.68341391 5.29288961,5.29288961 C5.68341391,4.90236532 6.31657888,4.90236532 6.70710318,5.29288961 L12.7071032,11.2928896 C13.0856821,11.6714686 13.0989277,12.281055 12.7371505,12.675721 L7.23715054,18.675721 C6.86395813,19.08284 6.23139076,19.1103429 5.82427177,18.7371505 C5.41715278,18.3639581 5.38964985,17.7313908 5.76284226,17.3242718 L10.6158586,12.0300721 L5.29288961,6.70710318 Z"
                                            fill="#000000" fill-rule="nonzero"
                                            transform="translate(8.999997, 11.999999) scale(-1, 1) translate(-8.999997, -11.999999)"/>
									<path
                                            d="M10.7071009,15.7071068 C10.3165766,16.0976311 9.68341162,16.0976311 9.29288733,15.7071068 C8.90236304,15.3165825 8.90236304,14.6834175 9.29288733,14.2928932 L15.2928873,8.29289322 C15.6714663,7.91431428 16.2810527,7.90106866 16.6757187,8.26284586 L22.6757187,13.7628459 C23.0828377,14.1360383 23.1103407,14.7686056 22.7371482,15.1757246 C22.3639558,15.5828436 21.7313885,15.6103465 21.3242695,15.2371541 L16.0300699,10.3841378 L10.7071009,15.7071068 Z"
                                            fill="#000000" fill-rule="nonzero" opacity="0.3"
                                            transform="translate(15.999997, 11.999999) scale(-1, 1) rotate(-270.000000) translate(-15.999997, -11.999999)"/>
								</g>
							</svg>
                            <!--end::Svg Icon-->
						</span>
        </button>
        <!--end::Toolbar-->
    </div>
    <!--end::Brand-->
    <!--begin::Aside Menu-->
    <div class="aside-menu-wrapper flex-column-fluid" id="kt_aside_menu_wrapper">
        <!--begin::Menu Container-->
        <div id="kt_aside_menu" class="aside-menu my-4" data-menu-vertical="1" data-menu-scroll="1"
             data-menu-dropdown-timeout="500">
            <!--begin::Menu Nav-->
            <ul class="menu-nav">
                <li class="menu-item menu-item-active" aria-haspopup="true">
                    <a href="${pageContext.request.contextPath}/home.htm" class="menu-link">
                                    <span class="svg-icon menu-icon">
										<!--begin::Svg Icon | path:assets/media/svg/icons/Design/Layers.svg-->
										<svg xmlns="http://www.w3.org/2000/svg"
                                             width="24px" height="24px"
                                             viewBox="0 0 24 24" version="1.1">
											<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
												<polygon points="0 0 24 0 24 24 0 24"/>
												<path
                                                        d="M12.9336061,16.072447 L19.36,10.9564761 L19.5181585,10.8312381 C20.1676248,10.3169571 20.2772143,9.3735535 19.7629333,8.72408713 C19.6917232,8.63415859 19.6104327,8.55269514 19.5206557,8.48129411 L12.9336854,3.24257445 C12.3871201,2.80788259 11.6128799,2.80788259 11.0663146,3.24257445 L4.47482784,8.48488609 C3.82645598,9.00054628 3.71887192,9.94418071 4.23453211,10.5925526 C4.30500305,10.6811601 4.38527899,10.7615046 4.47382636,10.8320511 L4.63,10.9564761 L11.0659024,16.0730648 C11.6126744,16.5077525 12.3871218,16.5074963 12.9336061,16.072447 Z"
                                                        fill="#000000" fill-rule="nonzero"/>
												<path
                                                        d="M11.0563554,18.6706981 L5.33593024,14.122919 C4.94553994,13.8125559 4.37746707,13.8774308 4.06710397,14.2678211 C4.06471678,14.2708238 4.06234874,14.2738418 4.06,14.2768747 L4.06,14.2768747 C3.75257288,14.6738539 3.82516916,15.244888 4.22214834,15.5523151 C4.22358765,15.5534297 4.2250303,15.55454 4.22647627,15.555646 L11.0872776,20.8031356 C11.6250734,21.2144692 12.371757,21.2145375 12.909628,20.8033023 L19.7677785,15.559828 C20.1693192,15.2528257 20.2459576,14.6784381 19.9389553,14.2768974 C19.9376429,14.2751809 19.9363245,14.2734691 19.935,14.2717619 L19.935,14.2717619 C19.6266937,13.8743807 19.0546209,13.8021712 18.6572397,14.1104775 C18.654352,14.112718 18.6514778,14.1149757 18.6486172,14.1172508 L12.9235044,18.6705218 C12.377022,19.1051477 11.6029199,19.1052208 11.0563554,18.6706981 Z"
                                                        fill="#000000" opacity="0.3"/>
											</g>
										</svg>
                                        <!--end::Svg Icon-->
									</span>
                        <span class="menu-text">Dashboard</span>
                    </a>
                </li>

                <li class="menu-section">
                    <h4 class="menu-text">PAGES</h4>
                    <i class="menu-icon ki ki-bold-more-hor icon-md"></i>
                </li>
                <c:if test="${not changePwdMode}" var="condition">
                    <c:forEach items="${sectionList}" var="section">
                        <li class="menu-item menu-item-submenu" aria-haspopup="true" data-menu-toggle="hover">
                            <a href="javascript:;" class="menu-link menu-toggle" id="${section.sectionCode}">
                                                    <span class="svg-icon menu-icon">
                										<!--begin::Svg Icon | path:assets/media/svg/icons/Design/Bucket.svg-->
                										<svg xmlns="http://www.w3.org/2000/svg"
                                                             width="24px"
                                                             height="24px"
                                                             viewBox="0 0 24 24" version="1.1">
                											<g stroke="none" stroke-width="1" fill="none"
                                                               fill-rule="evenodd">
                												<rect x="0" y="0" width="24" height="24"/>
                												<path
                                                                        d="M5,5 L5,15 C5,15.5948613 5.25970314,16.1290656 5.6719139,16.4954176 C5.71978107,16.5379595 5.76682388,16.5788906 5.81365532,16.6178662 C5.82524933,16.6294602 15,7.45470952 15,7.45470952 C15,6.9962515 15,6.17801499 15,5 L5,5 Z M5,3 L15,3 C16.1045695,3 17,3.8954305 17,5 L17,15 C17,17.209139 15.209139,19 13,19 L7,19 C4.790861,19 3,17.209139 3,15 L3,5 C3,3.8954305 3.8954305,3 5,3 Z"
                                                                        fill="#000000" fill-rule="nonzero"
                                                                        transform="translate(10.000000, 11.000000) rotate(-315.000000) translate(-10.000000, -11.000000)"/>
                												<path
                                                                        d="M20,22 C21.6568542,22 23,20.6568542 23,19 C23,17.8954305 22,16.2287638 20,14 C18,16.2287638 17,17.8954305 17,19 C17,20.6568542 18.3431458,22 20,22 Z"
                                                                        fill="#000000" opacity="0.3"/>
                											</g>
                										</svg>
                                                        <!--end::Svg Icon-->
                									</span>
                                <span class="menu-text">${section.description}</span>
                                <i class="menu-arrow"></i>
                            </a>

                            <div class="menu-submenu">
                                <i class="menu-arrow"></i>
                                <ul class="menu-subnav">
                                    <li class="menu-item menu-item-parent" aria-haspopup="true">
                                                            <span class="menu-link">
                												<span class="menu-text">${section.description}</span>
                                                            </span>
                                    </li>
                                    <c:set var="sectionCode" value="${section.sectionCode}"/>
                                    <c:forEach items="${pageMap[sectionCode]}" var="page">
                                        <li class="menu-item" aria-haspopup="true">
                                            <a href="${pageContext.request.contextPath}/${page.url}" class="menu-link"
                                               id="${page.pageCode}">
                                                <i class="menu-bullet menu-bullet-dot">
                                                    <span></span>
                                                </i>
                                                <span class="menu-text">${page.description}</span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>

                        </li>
                    </c:forEach>
                </c:if>

                <%--                <li class="menu-item menu-item-submenu" aria-haspopup="true" data-menu-toggle="hover">--%>
                <%--                    <a href="javascript:;" class="menu-link menu-toggle">--%>
                <%--                                    <span class="svg-icon menu-icon">--%>
                <%--										<!--begin::Svg Icon | path:assets/media/svg/icons/Design/Bucket.svg-->--%>
                <%--										<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px"--%>
                <%--                                             viewBox="0 0 24 24" version="1.1">--%>
                <%--											<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">--%>
                <%--												<rect x="0" y="0" width="24" height="24" />--%>
                <%--												<path--%>
                <%--                                                        d="M5,5 L5,15 C5,15.5948613 5.25970314,16.1290656 5.6719139,16.4954176 C5.71978107,16.5379595 5.76682388,16.5788906 5.81365532,16.6178662 C5.82524933,16.6294602 15,7.45470952 15,7.45470952 C15,6.9962515 15,6.17801499 15,5 L5,5 Z M5,3 L15,3 C16.1045695,3 17,3.8954305 17,5 L17,15 C17,17.209139 15.209139,19 13,19 L7,19 C4.790861,19 3,17.209139 3,15 L3,5 C3,3.8954305 3.8954305,3 5,3 Z"--%>
                <%--                                                        fill="#000000" fill-rule="nonzero"--%>
                <%--                                                        transform="translate(10.000000, 11.000000) rotate(-315.000000) translate(-10.000000, -11.000000)" />--%>
                <%--												<path--%>
                <%--                                                        d="M20,22 C21.6568542,22 23,20.6568542 23,19 C23,17.8954305 22,16.2287638 20,14 C18,16.2287638 17,17.8954305 17,19 C17,20.6568542 18.3431458,22 20,22 Z"--%>
                <%--                                                        fill="#000000" opacity="0.3" />--%>
                <%--											</g>--%>
                <%--										</svg>--%>
                <%--                                        <!--end::Svg Icon-->--%>
                <%--									</span>--%>
                <%--                        <span class="menu-text">Themes</span>--%>
                <%--                        <i class="menu-arrow"></i>--%>
                <%--                    </a>--%>
                <%--                    <div class="menu-submenu">--%>
                <%--                        <i class="menu-arrow"></i>--%>
                <%--                        <ul class="menu-subnav">--%>
                <%--                            <li class="menu-item menu-item-parent" aria-haspopup="true">--%>
                <%--                                            <span class="menu-link">--%>
                <%--												<span class="menu-text">Themes</span>--%>
                <%--                                            </span>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/themes/aside-light.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Light Aside</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/themes/header-dark.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Dark Header</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                        </ul>--%>
                <%--                    </div>--%>
                <%--                </li>--%>
                <%--                <li class="menu-item menu-item-submenu" aria-haspopup="true" data-menu-toggle="hover">--%>
                <%--                    <a href="javascript:;" class="menu-link menu-toggle">--%>
                <%--                                    <span class="svg-icon menu-icon">--%>
                <%--										<!--begin::Svg Icon | path:assets/media/svg/icons/Code/Compiling.svg-->--%>
                <%--										<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px"--%>
                <%--                                             viewBox="0 0 24 24" version="1.1">--%>
                <%--											<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">--%>
                <%--												<rect x="0" y="0" width="24" height="24" />--%>
                <%--												<path--%>
                <%--                                                        d="M2.56066017,10.6819805 L4.68198052,8.56066017 C5.26776695,7.97487373 6.21751442,7.97487373 6.80330086,8.56066017 L8.9246212,10.6819805 C9.51040764,11.267767 9.51040764,12.2175144 8.9246212,12.8033009 L6.80330086,14.9246212 C6.21751442,15.5104076 5.26776695,15.5104076 4.68198052,14.9246212 L2.56066017,12.8033009 C1.97487373,12.2175144 1.97487373,11.267767 2.56066017,10.6819805 Z M14.5606602,10.6819805 L16.6819805,8.56066017 C17.267767,7.97487373 18.2175144,7.97487373 18.8033009,8.56066017 L20.9246212,10.6819805 C21.5104076,11.267767 21.5104076,12.2175144 20.9246212,12.8033009 L18.8033009,14.9246212 C18.2175144,15.5104076 17.267767,15.5104076 16.6819805,14.9246212 L14.5606602,12.8033009 C13.9748737,12.2175144 13.9748737,11.267767 14.5606602,10.6819805 Z"--%>
                <%--                                                        fill="#000000" opacity="0.3" />--%>
                <%--												<path--%>
                <%--                                                        d="M8.56066017,16.6819805 L10.6819805,14.5606602 C11.267767,13.9748737 12.2175144,13.9748737 12.8033009,14.5606602 L14.9246212,16.6819805 C15.5104076,17.267767 15.5104076,18.2175144 14.9246212,18.8033009 L12.8033009,20.9246212 C12.2175144,21.5104076 11.267767,21.5104076 10.6819805,20.9246212 L8.56066017,18.8033009 C7.97487373,18.2175144 7.97487373,17.267767 8.56066017,16.6819805 Z M8.56066017,4.68198052 L10.6819805,2.56066017 C11.267767,1.97487373 12.2175144,1.97487373 12.8033009,2.56066017 L14.9246212,4.68198052 C15.5104076,5.26776695 15.5104076,6.21751442 14.9246212,6.80330086 L12.8033009,8.9246212 C12.2175144,9.51040764 11.267767,9.51040764 10.6819805,8.9246212 L8.56066017,6.80330086 C7.97487373,6.21751442 7.97487373,5.26776695 8.56066017,4.68198052 Z"--%>
                <%--                                                        fill="#000000" />--%>
                <%--											</g>--%>
                <%--										</svg>--%>
                <%--                                        <!--end::Svg Icon-->--%>
                <%--									</span>--%>
                <%--                        <span class="menu-text">Subheaders</span>--%>
                <%--                        <i class="menu-arrow"></i>--%>
                <%--                    </a>--%>
                <%--                    <div class="menu-submenu">--%>
                <%--                        <i class="menu-arrow"></i>--%>
                <%--                        <ul class="menu-subnav">--%>
                <%--                            <li class="menu-item menu-item-parent" aria-haspopup="true">--%>
                <%--                                            <span class="menu-link">--%>
                <%--												<span class="menu-text">Subheaders</span>--%>
                <%--                                            </span>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/subheader/toolbar.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Toolbar Nav</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/subheader/actions.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Actions Buttons</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/subheader/tabbed.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Tabbed Nav</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/subheader/classic.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Classic</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/subheader/none.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">None</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                        </ul>--%>
                <%--                    </div>--%>
                <%--                </li>--%>
                <%--                <li class="menu-item menu-item-submenu" aria-haspopup="true" data-menu-toggle="hover">--%>
                <%--                    <a href="javascript:;" class="menu-link menu-toggle">--%>
                <%--                                    <span class="svg-icon menu-icon">--%>
                <%--										<!--begin::Svg Icon | path:assets/media/svg/icons/General/Settings-1.svg-->--%>
                <%--										<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px"--%>
                <%--                                             viewBox="0 0 24 24" version="1.1">--%>
                <%--											<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">--%>
                <%--												<rect x="0" y="0" width="24" height="24" />--%>
                <%--												<path--%>
                <%--                                                        d="M7,3 L17,3 C19.209139,3 21,4.790861 21,7 C21,9.209139 19.209139,11 17,11 L7,11 C4.790861,11 3,9.209139 3,7 C3,4.790861 4.790861,3 7,3 Z M7,9 C8.1045695,9 9,8.1045695 9,7 C9,5.8954305 8.1045695,5 7,5 C5.8954305,5 5,5.8954305 5,7 C5,8.1045695 5.8954305,9 7,9 Z"--%>
                <%--                                                        fill="#000000" />--%>
                <%--												<path--%>
                <%--                                                        d="M7,13 L17,13 C19.209139,13 21,14.790861 21,17 C21,19.209139 19.209139,21 17,21 L7,21 C4.790861,21 3,19.209139 3,17 C3,14.790861 4.790861,13 7,13 Z M17,19 C18.1045695,19 19,18.1045695 19,17 C19,15.8954305 18.1045695,15 17,15 C15.8954305,15 15,15.8954305 15,17 C15,18.1045695 15.8954305,19 17,19 Z"--%>
                <%--                                                        fill="#000000" opacity="0.3" />--%>
                <%--											</g>--%>
                <%--										</svg>--%>
                <%--                                        <!--end::Svg Icon-->--%>
                <%--									</span>--%>
                <%--                        <span class="menu-text">General</span>--%>
                <%--                        <i class="menu-arrow"></i>--%>
                <%--                    </a>--%>
                <%--                    <div class="menu-submenu">--%>
                <%--                        <i class="menu-arrow"></i>--%>
                <%--                        <ul class="menu-subnav">--%>
                <%--                            <li class="menu-item menu-item-parent" aria-haspopup="true">--%>
                <%--                                            <span class="menu-link">--%>
                <%--												<span class="menu-text">General</span>--%>
                <%--                                            </span>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/general/fluid-content.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Fluid Content</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/general/minimized-aside.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Minimized Aside</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/general/no-aside.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">No Aside</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/general/empty-page.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Empty Page</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/general/fixed-footer.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">Fixed Footer</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                            <li class="menu-item" aria-haspopup="true">--%>
                <%--                                <a href="layout/general/no-header-menu.html" class="menu-link">--%>
                <%--                                    <i class="menu-bullet menu-bullet-dot">--%>
                <%--                                        <span></span>--%>
                <%--                                    </i>--%>
                <%--                                    <span class="menu-text">No Header Menu</span>--%>
                <%--                                </a>--%>
                <%--                            </li>--%>
                <%--                        </ul>--%>
                <%--                    </div>--%>
                <%--                </li>--%>
                <%--                <li class="menu-item" aria-haspopup="true">--%>
                <%--                    <a target="_blank" href="https://preview.keenthemes.com/metronic/demo1/builder.html" class="menu-link">--%>
                <%--                                    <span class="svg-icon menu-icon">--%>
                <%--										<!--begin::Svg Icon | path:assets/media/svg/icons/Home/Library.svg-->--%>
                <%--										<svg xmlns="http://www.w3.org/2000/svg"--%>
                <%--                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px"--%>
                <%--                                             viewBox="0 0 24 24" version="1.1">--%>
                <%--											<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">--%>
                <%--												<rect x="0" y="0" width="24" height="24" />--%>
                <%--												<path--%>
                <%--                                                        d="M5,3 L6,3 C6.55228475,3 7,3.44771525 7,4 L7,20 C7,20.5522847 6.55228475,21 6,21 L5,21 C4.44771525,21 4,20.5522847 4,20 L4,4 C4,3.44771525 4.44771525,3 5,3 Z M10,3 L11,3 C11.5522847,3 12,3.44771525 12,4 L12,20 C12,20.5522847 11.5522847,21 11,21 L10,21 C9.44771525,21 9,20.5522847 9,20 L9,4 C9,3.44771525 9.44771525,3 10,3 Z"--%>
                <%--                                                        fill="#000000" />--%>
                <%--												<rect fill="#000000" opacity="0.3"--%>
                <%--                                                      transform="translate(17.825568, 11.945519) rotate(-19.000000) translate(-17.825568, -11.945519)"--%>
                <%--                                                      x="16.3255682" y="2.94551858" width="3" height="18" rx="1" />--%>
                <%--											</g>--%>
                <%--										</svg>--%>
                <%--                                        <!--end::Svg Icon-->--%>
                <%--									</span>--%>
                <%--                        <span class="menu-text">Builder</span>--%>
                <%--                    </a>--%>
                <%--                </li>--%>
            </ul>
            <!--end::Menu Nav-->
        </div>
        <!--end::Menu Container-->
    </div>
    <!--end::Aside Menu-->
</div>
