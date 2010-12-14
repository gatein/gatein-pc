<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%--
  ~ JBoss, a division of Red Hat
  ~ Copyright 2010, Red Hat Middleware, LLC, and individual
  ~ contributors as indicated by the @authors tag. See the
  ~ copyright.txt in the distribution for a full listing of
  ~ individual contributors.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  --%>

<portlet:defineObjects/>
<%@ page import="javax.portlet.ResourceURL" %>

<portlet:resourceURL secure="true" escapeXml="true" cacheability="<%= ResourceURL.FULL%>"/>

<test_result_separator/>

<portlet:resourceURL secure="true" escapeXml="true" id="testId1" var="testVar"/>
<%
   //Put url placed as page variable
   out.print(pageContext.getAttribute("testVar"));
%>

<test_result_separator/>

<portlet:resourceURL secure="false" escapeXml="false">
   <portlet:property name="testProperty" value="testPropValue"/>
   <portlet:property name="testProperty" value="testPropValue2"/>
   <portlet:property name="secondProperty" value="testPropValue"/>

   <portlet:param name="testParam" value="testParamValue"/>
   <portlet:param name="testParam" value="testParamValue2"/>
   <portlet:param name="secondParam" value="testParamValue"/>
</portlet:resourceURL>
