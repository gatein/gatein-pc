<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="xportal" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>

<%@ include file="/layouts/header.jsp" %>

<portal:page>


   <xportal:2columns>
            <jsp:attribute name="leftcol">
               <xportal:portlet name="portletA" applicationName="samples-eventdebug-portlet" id="portlet-A"/>
            </jsp:attribute>
            <jsp:attribute name="rightcol">
               <xportal:portlet name="portletB" applicationName="samples-eventdebug-portlet" id="portlet-B"/>
               <xportal:portlet name="portletC" applicationName="samples-eventdebug-portlet" id="portlet-C"/>
            </jsp:attribute>

   </xportal:2columns>
</portal:page>
<%@ include file="/layouts/footer.jsp" %>