<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<portlet:renderURL secure="false">
   <portlet:param name="testParam" value="testParamValue"/>
   <portlet:param name="testParam" value="testParamValue2"/>
   <portlet:param name="secondParam" value="testParamValue"/>
</portlet:renderURL>