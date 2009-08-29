<%@ page contentType="text/html" %>
<%@ page language="java" %>
<%@ taglib prefix="xportal" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>

<%@ include file="/layouts/header.jsp" %>

<portal:page>
   <portal:pageparam namespaceURI="urn:jboss:portal:simple:google" localName="zipcode" value="80201"/>
   <xportal:2columns>
      <jsp:attribute name="leftcol">
         <xportal:portlet name="Cart" applicationName="samples-shoppingcart-portlet" id="cart-portlet"/>
      </jsp:attribute>
      <jsp:attribute name="rightcol">
         <xportal:portlet name="Catalog" applicationName="samples-shoppingcart-portlet"/>
      </jsp:attribute>
   </xportal:2columns>
   <xportal:portlet name="DebuggerPortlet" applicationName="simple-portal"/>
</portal:page>

<%@ include file="/layouts/footer.jsp" %>