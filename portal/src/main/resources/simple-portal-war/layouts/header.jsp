<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" id="testreplace">
<head>
   <title>Portlet Container 2.0</title>
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/master.css" type="text/css"/>
   
   <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/shadowbox.css"/>
   <script type="text/javascript" src="${pageContext.request.contextPath}/js/prototype.js"></script>
   <script type="text/javascript"
           src="${pageContext.request.contextPath}/js/scriptaculous/scriptaculous.js?load=effects"></script>
   <script type="text/javascript" src="${pageContext.request.contextPath}/js/shadowbox-prototype.js"></script>
   <script type="text/javascript" src="${pageContext.request.contextPath}/js/shadowbox.js"></script>
   <script type="text/javascript">
      window.onload = function(){
      Shadowbox.init();
      };
   </script>
   <portal:headers/>
</head>
<body>

<div id="container" class="full-width">
   <div class="header full-width">
      <div class="float-left three-quarter-width logo"></div>
      <div class="float-right quarter-width pc20"><img src="${pageContext.request.contextPath}/images/pc20.gif"
                                                       alt="Portlet Container 2.0"/></div>
      <div class="float-right secondary-nav"><a href="${pageContext.request.contextPath}/admin/index.jsp">Admin</a></div>
      <br class="clear"/>
   </div>
   <%@ include file="nav/main.jsp" %>
   <div id="content">