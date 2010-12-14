<%@ page import="org.jboss.unit.api.Assert" %>
<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.ActionRequest" %>
<%@ page import="javax.portlet.ActionResponse" %>
<%@ page import="javax.portlet.EventRequest" %>
<%@ page import="javax.portlet.EventResponse" %>
<%@ page import="javax.portlet.ResourceRequest" %>
<%@ page import="javax.portlet.ResourceResponse" %>
<%@ page import="javax.portlet.PortletConfig" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="java.util.Map" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
   out.print("jspDispatch");

   if (renderRequest != null && renderRequest instanceof RenderRequest)
   {
      out.print(",renderRequest");
   }
   if (renderResponse != null && renderResponse instanceof RenderResponse)
   {
      out.print(",renderResponse");
   }
   if (actionRequest != null && actionRequest instanceof ActionRequest)
   {
      out.print(",actionRequest");
   }
   if (actionResponse != null && actionResponse instanceof ActionResponse)
   {
      out.print(",actionResponse");
   }
   if (eventRequest != null && eventRequest instanceof EventRequest)
   {
      out.print(",eventRequest");
   }
   if (eventResponse != null && eventResponse instanceof EventResponse)
   {
      out.print(",eventResponse");
   }
   if (resourceRequest != null && resourceRequest instanceof ResourceRequest)
   {
      out.print(",resourceRequest");
   }
   if (resourceResponse != null && resourceResponse instanceof ResourceResponse)
   {
      out.print(",resourceResponse");
   }
   if (portletConfig != null && portletConfig instanceof PortletConfig)
   {
      out.print(",portletConfig");
   }
   if (portletSession != null && portletSession instanceof PortletSession)
   {
      out.print(",portletSession");
   }
   if (portletSessionScope != null && portletSessionScope instanceof Map)
   {
      out.print(",portletSessionScope");
   }
   if (portletPreferences != null && portletPreferences instanceof PortletPreferences)
   {
      out.print(",portletPreferences");
   }
   if (portletPreferencesValues != null && portletPreferencesValues instanceof Map)
   {
      out.print(",portletPreferencesValues");
   }

%>