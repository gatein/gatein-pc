<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="xportal" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>

<%@ include file="/layouts/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/debugger.css" type="text/css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/debugger.js"></script>
<portal:page>

   <xportal:2columns>
            <jsp:attribute name="leftcol">
               <xportal:portlet name="RandomEventPortlet1" applicationName="samples-basic-portlet"/>
               <xportal:portlet name="RandomEventPortlet2" applicationName="samples-basic-portlet"/>
               <xportal:portlet name="RandomEventPortlet3" applicationName="samples-basic-portlet"/>
            </jsp:attribute>
            <jsp:attribute name="rightcol">
               <xportal:portlet name="DebuggerPortlet" applicationName="simple-portal"/>
            </jsp:attribute>

   </xportal:2columns>
</portal:page>
<%@ include file="/layouts/footer.jsp" %>