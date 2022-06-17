<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="col-sm-9">
    <h4>
        <small>Change Password</small>
    </h4>
    <hr>

    <c:set var="msg" value="${msg}"/>
    <c:if test="${not empty msg}">
        <div>
            <div class="icon">
                <div class="tile-title message-error"><span class="text-danger">
                    <c:out value="${msg}"/></span>
                </div>
            </div>
        </div>
    </c:if>

    <form:form method="post" modelAttribute="passwordchangeform" action="passwordchange.htm">
        <div class="form-group">
            <div class="input-group">
                <label>Current Password<span
                        class="text-danger">*</span></label>
            </div>
            <div class="input-group">
                <form:input path="oldPassword" name="oldPassword" class="form-control" placeholder="Current Password"
                            type="password"/>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group">
                <label>New Password<span
                        class="text-danger">*</span></label>
            </div>
            <div class="input-group">
                <form:input path="newPassword" name="newPassword" class="form-control" placeholder="New Password"
                            type="password"/>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group">
                <label>Confirm New Password<span
                        class="text-danger">*</span></label>
            </div>
            <div class="input-group">
                <form:input path="confirmNewPassword" name="confirmNewPassword" class="form-control"
                            placeholder="Re-enter New Password" type="password"/>
            </div>
        </div>
        <div class="form-group">
            <span class="text-danger">Required fields are marked by the '*'</span>
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block"> Submit</button>
        </div>
    </form:form>
</div>
