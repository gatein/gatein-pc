<%@ tag body-content="scriptless" %>
<%@ attribute name="leftcol" fragment="true" required="false" %>
<%@ attribute name="rightcol" fragment="true" required="false" %>

<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>

 <div class="third-width float-left">
    <jsp:invoke fragment="leftcol"/>
 </div>
 <div class="two-third-width float-left">
    <jsp:invoke fragment="rightcol"/>
 </div>





