<%@ tag body-content="scriptless" %>
<%@ attribute name="leftcol" fragment="true" required="false" %>
<%@ attribute name="rightcol" fragment="true" required="false" %>

<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>

 <div class="quarter-width float-left">
    <jsp:invoke fragment="leftcol"/>
 </div>
 <div class="three-quarter-width float-left">
    <jsp:invoke fragment="rightcol"/>
 </div>





