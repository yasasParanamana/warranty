<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<link rel="stylesheet" type="text/css"
      href="${pageContext.request.contextPath}/resources/css/login/login.css?${initParam['version']}"/>

<div class="main">
    <div class="card">
        <h4 class="card-title text-center mb-4 mt-1">Sign in</h4>
        <form:form method="post" modelAttribute="loginform" action="checkuser.htm">
            <div class="form-group">
                <div class="input-group">
                    <form:input path="username" name="username" class="form-control" placeholder="Username"
                                type="text"/>
                </div>
            </div>
            <div class="form-group">
                <div class="input-group">
                    <form:input path="password" name="password" class="form-control" placeholder="Password"
                                type="password"/>
                </div>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary btn-block"> Login</button>
            </div>
        </form:form>
    </div>
</div>
