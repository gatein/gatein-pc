<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<portlet:actionURL windowState="maximized" portletMode="edit" secure="true"/>

<test_result_separator/>

<portlet:actionURL windowState="maximized" portletMode="edit" secure="true" var="testVar"/>
<%
   //Put url placed as page variable
   out.print(pageContext.getAttribute("testVar"));
%>

<test_result_separator/>

<portlet:actionURL secure="false">

   <portlet:param name="testParam" value="testParamValue"/>
   <portlet:param name="testParam" value="testParamValue2"/>
   <portlet:param name="secondParam" value="testParamValue"/>

</portlet:actionURL>
