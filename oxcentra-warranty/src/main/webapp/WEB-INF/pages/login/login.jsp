<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!--begin::Body-->
<body id="kt_body"
      class="header-fixed header-mobile-fixed subheader-enabled subheader-fixed aside-enabled aside-fixed aside-minimize-hoverable page-loading">
<!--begin::Main-->
<div class="d-flex flex-column flex-root">
    <!--begin::Login-->
    <div class="login login-1 login-signin-on d-flex flex-column flex-lg-row flex-column-fluid bg-white" id="kt_login">
        <!--begin::Aside-->
        <div class="login-aside d-flex flex-row-auto bgi-size-cover bgi-no-repeat p-10 p-lg-10"
             style="background-image: url(${pageContext.request.contextPath}/resources/assets/media/bg/bg-login.jpg);">
            <!--begin: Aside Container-->
            <div class="d-flex flex-row-fluid flex-column justify-content-between">
                <!--begin: Aside header-->
                <a href="#" class="flex-column-auto mt-5 pb-lg-0 pb-10">
                    <img src="${pageContext.request.contextPath}/resources/assets/media/logos/RDBNEWLOGIN.png"
                         class="max-h-70px" alt=""/>
                </a>
                <!--end: Aside header-->
                <!--begin: Aside content-->
                <div class="flex-column-fluid d-flex flex-column justify-content-center">
                    <h3 class="font-size-h1 mb-5 text-white text-combank">RDB SMS</h3>
                    <p class="font-weight-lighter text-white opacity-80">Having set a benchmark in banking in Sri Lanka
                        we have set standards, created an identity and forged an unsurpassable trend. Recognised as a
                        trend setter, we have maintained our cultural identity while providing a range of products and
                        services. Powered by state-of-the-art technology and driven by a team of highly motivated,
                        dynamic individuals we have become the leader in private banking in Sri Lanka.</p>
                </div>
                <!--end: Aside content-->
                <!--begin: Aside footer for desktop-->
                <div class="d-none flex-column-auto d-lg-flex justify-content-between mt-10">
                    <div class="opacity-70 font-weight-bold text-white">&copy; 2021 Epic Lanka (PVT) LTD</div>
                    <%--<div class="d-flex">--%>
                    <%--<a href="#" class="text-white">Privacy</a>--%>
                    <%--<a href="#" class="text-white ml-10">Legal</a>--%>
                    <%--<a href="#" class="text-white ml-10">Contact</a>--%>
                    <%--</div>--%>
                </div>
                <!--end: Aside footer for desktop-->
            </div>
            <!--end: Aside Container-->
        </div>
        <!--begin::Aside-->
        <!--begin::Content-->
        <div class="d-flex flex-column flex-row-fluid position-relative p-7 overflow-hidden">
            <!--begin::Content header-->
            <%--<div class="position-absolute top-0 right-0 text-right mt-5 mb-15 mb-lg-0 flex-column-auto justify-content-center py-5 px-10">--%>
            <%--<span class="font-weight-bold text-dark-50">Dont have an account yet?</span>--%>
            <%--<a href="javascript:;" class="font-weight-bold ml-2" id="kt_login_signup">Sign Up!</a>--%>
            <%--</div>--%>
            <!--end::Content header-->
            <!--begin::Content body-->
            <div class="d-flex flex-column-fluid flex-center mt-30 mt-lg-0">
                <!--begin::Signin-->
                <div class="login-form login-signin">
                    <div class="text-center mb-10 mb-lg-20">
                        <h3 class="font-size-h1">Log In</h3>
                        <p class="text-muted font-weight-bold">Enter your username and password</p>
                    </div>
                    <!--begin::Form-->
                    <form:form novalidate="novalidate" method="post" class="form" modelAttribute="loginform"
                               action="checkuser.htm" id="kt_login_signin_form">
                        <!-- Error Message -->
                        <c:set var="errorMessage" value="${msg}"/>
                        <c:set var="successMessage" value="${cmsg}"/>
                        <c:if test="${not empty errorMessage}">
                            <div id="msgerror" class="icon">
                            <span class="tile-title" style="color: red;">
                                <c:out value="${msg}"/>
                            </span>
                            </div>
                        </c:if>
                        <c:if test="${not empty successMessage}">
                            <div id="msgerror" class="icon">
                            <span class="tile-title" style="color: blue;">
                                <c:out value="${cmsg}"/>
                            </span>
                            </div>
                        </c:if>
                        <div class="form-group">
                            <form:input path="username" name="username"
                                        class="form-control form-control-solid h-auto py-5 px-6" placeholder="Username"
                                        type="text" autocomplete="off" onkeyup="convertToLowercase()"
                                        onfocus="convertToLowercase()"/>
                        </div>
                        <div class="form-group">
                            <form:input path="password" name="password" id="password"
                                        class="form-control form-control-solid h-auto py-5 px-6" placeholder="Password"
                                        type="password" autocomplete="off" onfocus="disablePaste()"/>
                        </div>
                        <!--begin::Action-->
                        <div class="form-group d-flex flex-wrap justify-content-between align-items-center">
                                <%--<a href="javascript:;" class="text-dark-50 text-hover-primary my-3 mr-2" id="kt_login_forgot">Forgot Password ?</a>--%>
                            <a class="text-dark-50 text-hover-primary my-3 mr-2" id="kt_login_forgot"></a>
                            <button type="submit" id="kt_login_signin_submit"
                                    class="btn btn-primary font-weight-bold px-9 py-4 my-3">LOGIN
                            </button>
                        </div>
                        <!--end::Action-->
                    </form:form>
                    <!--end::Form-->
                </div>
                <!--end::Signin-->
                <!--begin::Signup-->
                <div class="login-form login-signup">
                    <div class="text-center mb-10 mb-lg-20">
                        <h3 class="font-size-h1">Sign Up</h3>
                        <p class="text-muted font-weight-bold">Enter your details to create your account</p>
                    </div>
                    <!--begin::Form-->
                    <form class="form" novalidate="novalidate" id="kt_login_signup_form">
                        <div class="form-group">
                            <input class="form-control form-control-solid h-auto py-5 px-6" type="text"
                                   placeholder="Fullname" name="fullname" autocomplete="off"/>
                        </div>
                        <div class="form-group">
                            <input class="form-control form-control-solid h-auto py-5 px-6" type="email"
                                   placeholder="Email" name="email" autocomplete="off"/>
                        </div>
                        <div class="form-group">
                            <input class="form-control form-control-solid h-auto py-5 px-6" type="password"
                                   placeholder="Password" name="password" autocomplete="off"/>
                        </div>
                        <div class="form-group">
                            <input class="form-control form-control-solid h-auto py-5 px-6" type="password"
                                   placeholder="Confirm password" name="cpassword" autocomplete="off"/>
                        </div>
                        <div class="form-group">
                            <label class="checkbox mb-0">
                                <input type="checkbox" name="agree"/>
                                <span></span>I Agree the
                                <a href="#">terms and conditions</a></label>
                        </div>
                        <div class="form-group d-flex flex-wrap flex-center">
                            <button type="button" id="kt_login_signup_submit"
                                    class="btn btn-primary font-weight-bold px-9 py-4 my-3 mx-4">Submit
                            </button>
                            <button type="button" id="kt_login_signup_cancel"
                                    class="btn btn-light-primary font-weight-bold px-9 py-4 my-3 mx-4">Cancel
                            </button>
                        </div>
                    </form>
                    <!--end::Form-->
                </div>
                <!--end::Signup-->
                <!--begin::Forgot-->
                <%--<div class="login-form login-forgot">--%>
                <%--<div class="text-center mb-10 mb-lg-20">--%>
                <%--<h3 class="font-size-h1">Forgotten Password ?</h3>--%>
                <%--<p class="text-muted font-weight-bold">Enter your email to reset your password</p>--%>
                <%--</div>--%>
                <%--<!--begin::Form-->--%>
                <%--<form class="form" novalidate="novalidate" id="kt_login_forgot_form">--%>
                <%--<div class="form-group">--%>
                <%--<input class="form-control form-control-solid h-auto py-5 px-6" type="email" placeholder="Email" name="email" autocomplete="off" />--%>
                <%--</div>--%>
                <%--<div class="form-group d-flex flex-wrap flex-center">--%>
                <%--<button type="button" id="kt_login_forgot_submit" class="btn btn-primary font-weight-bold px-9 py-4 my-3 mx-4">Submit</button>--%>
                <%--<button type="button" id="kt_login_forgot_cancel" class="btn btn-light-primary font-weight-bold px-9 py-4 my-3 mx-4">Cancel</button>--%>
                <%--</div>--%>
                <%--</form>--%>
                <%--<!--end::Form-->--%>
                <%--</div>--%>
                <!--end::Forgot-->
            </div>
            <!--end::Content body-->
            <!--begin::Content footer for mobile-->
            <div class="d-flex d-lg-none flex-column-auto flex-column flex-sm-row justify-content-between align-items-center mt-5 p-5">
                <div class="text-dark-50 font-weight-bold order-2 order-sm-1 my-2">&copy; 2021 Epic Lanka (PVT) LTD
                </div>
                <%--<div class="d-flex order-1 order-sm-2 my-2">--%>
                <%--<a href="#" class="text-dark-75 text-hover-primary">Privacy</a>--%>
                <%--<a href="#" class="text-dark-75 text-hover-primary ml-4">Legal</a>--%>
                <%--<a href="#" class="text-dark-75 text-hover-primary ml-4">Contact</a>--%>
                <%--</div>--%>
            </div>
            <!--end::Content footer for mobile-->
        </div>
        <!--end::Content-->
    </div>
    <!--end::Login-->
    <script>
        function disablePaste() {
            var input = document.getElementById("password");
            if (input)
                input.onpaste = function () {
                    return false;
                };
        }

        function convertToLowercase() {
            let username = $('input[name="username"]').val();
            $('input[name="username"]').val(username !== '' ? username.toLowerCase() : '')
        }
    </script>
</div>
<!--end::Main-->
