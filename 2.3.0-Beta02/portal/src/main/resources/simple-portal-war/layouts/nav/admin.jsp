<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="jbp" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
   PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

   <div class="nav full-width">
      <ul>
         <li>
            <a href="${pageContext.request.contextPath}/admin/index.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'admin/') == 'index.jsp' ? 'selected' : ''}">Portlet Manager</a>
         </li>
      </ul>
      <br class="clear"/>
   </div>
