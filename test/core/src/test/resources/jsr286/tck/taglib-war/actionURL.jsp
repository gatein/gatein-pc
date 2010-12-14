<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<portlet:actionURL windowState="maximized" portletMode="edit" secure="true" copyCurrentRenderParameters="true"
                   escapeXml="true" name="testAction"/>

<test_result_separator/>

<portlet:actionURL windowState="maximized" portletMode="edit" secure="true" copyCurrentRenderParameters="true"
                   escapeXml="true" name="testAction" var="testVar"/>
<%
   //Put url placed as page variable
   out.print(pageContext.getAttribute("testVar"));
%>

<test_result_separator/>

<portlet:actionURL secure="false" name="testAction" escapeXml="false">

   <portlet:property name="testProperty" value="testPropValue"/>
   <portlet:property name="testProperty" value="testPropValue2"/>
   <portlet:property name="secondProperty" value="testPropValue"/>

   <portlet:param name="testParam" value="testParamValue"/>
   <portlet:param name="testParam" value="testParamValue2"/>
   <portlet:param name="secondParam" value="testParamValue"/>

</portlet:actionURL>
