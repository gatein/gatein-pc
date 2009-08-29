<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="xportal" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>

<%@ include file="/layouts/header.jsp" %>

<portal:page>
   <portal:pageparam namespaceURI="urn:jboss:portal:simple:google" localName="zipcode" value="80201"/>

   <xportal:2columns>
            <jsp:attribute name="leftcol">
               <xportal:portlet name="Foo" applicationName="bar"/>
            </jsp:attribute>
            <jsp:attribute name="rightcol">

               <xportal:portlet name="ExceptionPortlet" applicationName="samples-basic-portlet"/>
               <xportal:portlet name="AJAXResourcePortlet" applicationName="samples-basic-portlet" id="header-portlet"/>
               <xportal:portlet name="FailDuringInitPortlet" applicationName="samples-basic-portlet"/>
               <xportal:portlet name="ExceptionPortlet" applicationName="samples-basic-portlet"/>
            </jsp:attribute>

   </xportal:2columns>
</portal:page>
<%@ include file="/layouts/footer.jsp" %>