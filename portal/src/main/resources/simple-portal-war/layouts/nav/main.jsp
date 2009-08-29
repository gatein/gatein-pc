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
            <a href="${pageContext.request.contextPath}/demo/home.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'home.jsp' ? 'selected' : ''}">Home</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/wikipedia.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'wikipedia.jsp' ? 'selected' : ''}">Wikipedia</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/demo.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'demo.jsp' ? 'selected' : ''}">Demo Portlets</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/demo1.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'demo1.jsp' ? 'selected' : ''}">Demo1</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/demo2.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'demo2.jsp' ? 'selected' : ''}">Demo2</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/demo3.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'demo3.jsp' ? 'selected' : ''}">Demo3</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/demo4.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'demo4.jsp' ? 'selected' : ''}">Demo4</a>
         </li>
         <li>
            <a href="${pageContext.request.contextPath}/demo/jsr-301.jsp" class="${fn:substringAfter(pageContext.request.servletPath, 'demo/') == 'jsr-301.jsp' ? 'selected' : ''}">JSR-301 Demo</a>
         </li>
      </ul>
      <br class="clear"/>
   </div>
