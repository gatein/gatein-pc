<%@ page import="org.gatein.common.util.Exceptions" %>
<%@ page language="java" %>

<div class="portlet">
   <div class="portlet-frame">
      <div class="header full-width">
         <div class="header-layer full-width">
            <div class="title two-third-width float-left">
               <h2>
                  ${requestScope['org.gatein.pc.portal.error.portlet_name']} ${requestScope['org.gatein.pc.portal.error.status'] == 'not_found' ? ' not found' : ''}
               </h2>
            </div>
         </div>
      </div>
      <h3 class="error-header">Portlet was not rendered</h3>

      <div class="error-container">
         <ul>
            <li>
               <span>Error status:</span> ${requestScope['org.gatein.pc.portal.error.status']}
            </li>
            <li>
               <span>Portlet name:</span> ${requestScope['org.gatein.pc.portal.error.portlet_name']}
            </li>
            <li>
               <span>Portlet application name:</span> ${requestScope['org.gatein.pc.portal.error.application_name']}
            </li>
            <li>
               <span>Exception:</span> ${!empty requestScope['org.gatein.pc.portal.error.cause'] ? requestScope['org.gatein.pc.portal.error.cause'] : 'Error cause unavailable'}
               <%
                  if (request.getAttribute("org.gatein.pc.portal.error.cause") != null)
                  {
               %>
               <a class="option" href="#exception-stack-trace"
                  title="${requestScope['org.gatein.pc.portal.error.cause']}"
                  rel="shadowbox;width=900;height=400">${!empty requestScope['org.gatein.pc.portal.error.cause'] ? 'View Stack Trace' : ''}</a>

               <div id="exception-stack-trace" class="hidden">
                  <div class="exception">
                     <%=Exceptions.toHTML((Throwable)request.getAttribute("org.gatein.pc.portal.error.cause"))%>
                  </div>
               </div>
               <%}%>
            </li>
            <li>
               <span>Window id:</span> ${requestScope['org.gatein.pc.portal.error.window_id']}
            </li>
         </ul>
      </div>
   </div>
</div>
