<%@ page %>
<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ JBoss, a division of Red Hat                                             ~
~ Copyright 2006, Red Hat Middleware, LLC, and individual                  ~
~ contributors as indicated by the @authors tag. See the                   ~
~ copyright.txt in the distribution for a full listing of                  ~
~ individual contributors.                                                 ~
~                                                                          ~
~ This is free software; you can redistribute it and/or modify it          ~
~ under the terms of the GNU Lesser General Public License as              ~
~ published by the Free Software Foundation; either version 2.1 of         ~
~ the License, or (at your option) any later version.                      ~
~                                                                          ~
~ This software is distributed in the hope that it will be useful,         ~
~ but WITHOUT ANY WARRANTY; without even the implied warranty of           ~
~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU         ~
~ Lesser General Public License for more details.                          ~
~                                                                          ~
~ You should have received a copy of the GNU Lesser General Public         ~
~ License along with this software; if not, write to the Free              ~
~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA       ~
~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.                 ~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page isELIgnored="false" %>

<portlet:defineObjects/>

<table border="0" cellspacing="2" cellpadding="2">
   <tr>
      <td align="center"><a href="http://portal.demo.jboss.com" target="_blank"><img
         src="<%= renderRequest.getContextPath() %>/images/dodemo.gif" border="0"
         alt="Try the latest release of JBoss Portal, live and online."></a>
         <a href="http://labs.jboss.com/portal/jbossportal/download/index.html" target="_blank"><img
            src="<%= renderRequest.getContextPath() %>/images/getcode.gif" border="0"
            alt="Download JBoss Portal and have it up and running in minutes."></a>
         <a href="http://www.portletswap.com" target="_blank"><img
            src="<%= renderRequest.getContextPath() %>/images/accessorize.gif" border="0"
            alt="Download portlets and layouts for your new JBoss Portal installation."></a></td>
   </tr>
   <tr>
      <td></td>
   </tr>
   <tr>
      <td class="portlet-section-alternate">
         <font class="portlet-font">This is an installation of <b>JBoss Portlet Container 2.0
         </b>. You may
            log in at any time, using the <i>Login</i> link at the top-right of this page, with the following
            credentials:</font>
      </td>
   </tr>
   <tr>
      <td class="portlet-section-alternate" align="center">
         <b>user/user</b> or <b>admin/admin</b>
      </td>
   </tr>
   <tr>
      <td align="center">
         If you are in need of guidance with regards to navigating, configuring, or operating the portal, please view
         our <a href="http://labs.jboss.com/portal/jbossportal/docs/index.html" target="_blank">online documentation</a>.
      </td>
   </tr>
</table>
