<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<portlet:renderURL windowState="normal" portletMode="edit" secure="true" copyCurrentRenderParameters="true"
                   escapeXml="true"/>

<test_result_separator/>

<portlet:renderURL windowState="normal" portletMode="edit" secure="true" copyCurrentRenderParameters="true"
                   escapeXml="true" var="testVar"/>
<%
   //Put url placed as page variable
   out.print(pageContext.getAttribute("testVar"));
%>

<test_result_separator/>

<portlet:renderURL secure="false" escapeXml="false">
   <portlet:property name="testProperty" value="testPropValue"/>
   <portlet:property name="testProperty" value="testPropValue2"/>
   <portlet:property name="secondProperty" value="testPropValue"/>

   <portlet:param name="testParam" value="testParamValue"/>
   <portlet:param name="testParam" value="testParamValue2"/>
   <portlet:param name="secondParam" value="testParamValue"/>
</portlet:renderURL>