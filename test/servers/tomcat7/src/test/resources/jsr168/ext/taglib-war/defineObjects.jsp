<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.PortletConfig" %>
<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
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
   if (portletConfig != null && portletConfig instanceof PortletConfig)
   {
      out.print(",portletConfig");
   }

%>