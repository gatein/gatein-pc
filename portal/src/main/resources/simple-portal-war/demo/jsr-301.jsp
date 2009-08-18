<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="xportal" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>

<%@ include file="/layouts/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/debugger.css" type="text/css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/debugger.js"></script>
<portal:page>

   <xportal:1column>
            <jsp:attribute name="leftcol">
               <xportal:portlet name="riPortlet" applicationName="JSFRIPortlet" style="width:800px"/>
            </jsp:attribute>
   </xportal:1column>
</portal:page>
<%@ include file="/layouts/footer.jsp" %>