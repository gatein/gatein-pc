<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="jbp" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>




<%@ include file="/layouts/admin-header.jsp" %>
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" type="text/css"/>
   <script type="text/javascript" src="${pageContext.request.contextPath}/js/admin.js"></script>
<portal:page>

   <jbp:portlet name="AdminPortlet" applicationName="simple-portal"/>

</portal:page>
<%@ include file="/layouts/footer.jsp" %>