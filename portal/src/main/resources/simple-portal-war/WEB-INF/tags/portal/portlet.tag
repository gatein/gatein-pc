<%@ tag body-content="scriptless" %>
<%@ attribute name="name" rtexprvalue="true" required="true" %>
<%@ attribute name="applicationName" rtexprvalue="true" required="true" %>
<%@ attribute name="id" rtexprvalue="true" required="false" %>
<%@ attribute name="style" rtexprvalue="true" required="false" %>
<%@ attribute name="content" fragment="true" required="false" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<portal:portlet name="${name}" applicationName="${applicationName}" errorPage="/WEB-INF/jsp/error.jsp">
   <div class="portlet" id="${!empty id ? id : name}" style="${style}">
      <div class="portlet-frame">
         <div class="header full-width">
            <div class="header-layer full-width">
               <div class="title two-third-width float-left"><h2><portal:portlettitle/></h2></div>
               <div class="controls third-width float-right">
                  <span class="mode-button">
                     <a href="<portal:portleturl windowState='maximized'/>"><img
                        src="${pageContext.request.contextPath}/images/icon-maximize.gif" alt=""/></a>
                  </span>
                  <span class="mode-button">
                     <a href="<portal:portleturl windowState='normal'/>"><img
                        src="${pageContext.request.contextPath}/images/icon-normal.gif" alt=""/></a>
                  </span>
                  <span class="mode-button">
                     <a href="<portal:portleturl windowState='minimized'/>"><img
                        src="${pageContext.request.contextPath}/images/icon-minimize.gif" alt=""/></a>
                  </span>
                  <span class="mode-button">
                     <a title="Edit" href="<portal:portleturl mode='edit'/>">Edit</a>
                  </span>
               </div>
            </div>
         </div>
         <portal:portletmarkup/>
         <jsp:doBody/>
      </div>
   </div>
</portal:portlet>
