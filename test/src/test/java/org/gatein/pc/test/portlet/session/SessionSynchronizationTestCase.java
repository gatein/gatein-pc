/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.gatein.pc.test.portlet.session;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class SessionSynchronizationTestCase
{

   public SessionSynchronizationTestCase()
   {
      System.out.println("SessionSynchronizationTestCase");
   }

   public void testA()
   {

   }

//   public void testListener() throws Exception
//   {
//      // Only PortletScope attributes are replicated
//      HashSet attributeNames = new CollectionBuilder().add("javax.portlet.p.myportlet?a")
//         .add("javax.portlet.p.myportlet?b").toHashSet();
//
//      //
//      HttpSessionImpl dispatchedSession1 = new HttpSessionImpl();
//      HttpServletRequest dispatchedReq1 = new HttpServletRequestImpl(dispatchedSession1);
//      HttpSessionImpl portalSession1 = new HttpSessionImpl();
//      HttpServletRequest portalReq1 = new HttpServletRequestImpl(portalSession1);
//      SessionListener listener = new SessionListener();
//
//      //
//      SubSession ss1 = new SubSession("myportlet");
//      ss1.synchronizeWithDispatchedSession(dispatchedReq1);
//
//      //
//      SubSession.set(ss1);
//
//      //
//      HttpSessionEvent event = new HttpSessionEvent(dispatchedSession1);
//      listener.sessionCreated(event);
//      ReplicatedValue replicatedValueA = new ReplicatedValue("a");
//      HttpSessionBindingEvent aEvent = new HttpSessionBindingEvent(dispatchedSession1, "javax.portlet.p.myportlet?a", replicatedValueA);
//      listener.attributeAdded(aEvent);
//      ReplicatedValue replicatedValueB = new ReplicatedValue("b");
//      HttpSessionBindingEvent bEvent = new HttpSessionBindingEvent(dispatchedSession1, "javax.portlet.p.myportlet?b", replicatedValueB);
//      listener.attributeAdded(bEvent);
//      ReplicatedValue replicatedValueC = new ReplicatedValue("c");
//      HttpSessionBindingEvent cEvent = new HttpSessionBindingEvent(dispatchedSession1, "c", replicatedValueC);
//      listener.attributeAdded(cEvent);
//      ReplicatedValue replicatedValueD = new ReplicatedValue("d");
//      HttpSessionBindingEvent dEvent = new HttpSessionBindingEvent(dispatchedSession1, "d", replicatedValueD);
//      listener.attributeAdded(dEvent);
//
//      //
//      SubSession.set(null);
//
//      //
//      assertTrue(ss1.isChanged());
//      assertFalse(ss1.isActivated());
//      assertEquals(attributeNames, ss1.getAttributeNames());
//
//      //
//      ss1.synchronizeWithPortalSession(portalReq1, "myportlet");
//
//      //
//      assertFalse(ss1.isChanged());
//      assertFalse(ss1.isActivated());
//      assertEquals(attributeNames, ss1.getAttributeNames());
//
//      // check that attributes are marshalled after synchronizeWithPortalSession
//      assertEquals(new MarshalledValue(replicatedValueA), ss1.getAttribute("javax.portlet.p.myportlet?a"));
//      assertEquals(new MarshalledValue(replicatedValueB), ss1.getAttribute("javax.portlet.p.myportlet?b"));
//
//      //
//      SubSession ss2 = (SubSession)Tools.clone(ss1);
//
//      //
//      assertFalse(ss2.isChanged());
//      assertTrue(ss2.isActivated());
//      assertEquals(attributeNames, ss2.getAttributeNames());
//
//      //
//      HttpSessionImpl dispatchedSession2 = new HttpSessionImpl();
//      dispatchedSession2.setAttribute("javax.portlet.p.myportlet?b", new ReplicatedValue("_b"));
//      dispatchedSession2.setAttribute("javax.portlet.p.myportlet?other", new ReplicatedValue("other"));
//      dispatchedSession2.setAttribute("d", new ReplicatedValue("_d"));
//      dispatchedSession2.setAttribute("other", new ReplicatedValue("other"));
//      HttpServletRequest dispatchedReq2 = new HttpServletRequestImpl(dispatchedSession2);
//
//      //
//
//      //
//      TestClassLoader cl = new TestClassLoader();
//      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
//      try
//      {
//         Thread.currentThread().setContextClassLoader(cl);
//         ss2.synchronizeWithDispatchedSession(dispatchedReq2);
//      }
//      finally
//      {
//         Thread.currentThread().setContextClassLoader(tccl);
//      }
//
//      //
//      assertTrue(ss2.isChanged()); // SubSession is marked as changed after synchronizeWithDispatchedSession
//      assertFalse(ss2.isActivated());
//      assertEquals(attributeNames, ss2.getAttributeNames());
//      assertEquals(attributeNames, Tools.toSet(dispatchedSession2.getAttributeNames()));
//      assertEquals(ReplicatedValue.create(cl, "a"), dispatchedSession2.getAttribute("javax.portlet.p.myportlet?a"));
//      assertEquals(ReplicatedValue.create(cl, "b"), dispatchedSession2.getAttribute("javax.portlet.p.myportlet?b"));
//
//      // Only PortletScope attributes are replicated
//      assertNull(dispatchedSession2.getAttribute("c"));
//      assertNull(dispatchedSession2.getAttribute("d"));
//   }
//
//   private static class HttpSessionImpl implements HttpSession
//   {
//
//      private final Map map = new HashMap();
//
//      public void setAttribute(String name, Object object)
//      {
//         map.put(name, object);
//      }
//
//      public void removeAttribute(String name)
//      {
//         map.remove(name);
//      }
//
//      public Object getAttribute(String name)
//      {
//         return map.get(name);
//      }
//
//      public Enumeration getAttributeNames()
//      {
//         return Tools.toEnumeration(map.keySet().iterator());
//      }
//
//      public void putValue(String name, Object object)
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public void removeValue(String name)
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public long getCreationTime()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public String getId()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public long getLastAccessedTime()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public ServletContext getServletContext()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public void setMaxInactiveInterval(int i)
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public int getMaxInactiveInterval()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public HttpSessionContext getSessionContext()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public Object getValue(String name)
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public String[] getValueNames()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public void invalidate()
//      {
//         throw new UnsupportedOperationException();
//      }
//
//      public boolean isNew()
//      {
//         return false;
//      }
//   }
//
//   private static class HttpServletRequestImpl implements HttpServletRequest
//   {
//
//      private HttpSession session;
//
//      public HttpServletRequestImpl(HttpSession session)
//      {
//         this.session = session;
//      }
//
//      public HttpSession getSession()
//      {
//         return session;
//      }
//
//      public HttpSession getSession(boolean b)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getAuthType()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Cookie[] getCookies()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public long getDateHeader(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getHeader(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Enumeration getHeaders(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Enumeration getHeaderNames()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public int getIntHeader(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getMethod()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getPathInfo()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getPathTranslated()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getContextPath()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getQueryString()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getRemoteUser()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public boolean isUserInRole(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Principal getUserPrincipal()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getRequestedSessionId()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getRequestURI()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public StringBuffer getRequestURL()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getServletPath()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//
//      public boolean isRequestedSessionIdValid()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public boolean isRequestedSessionIdFromCookie()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public boolean isRequestedSessionIdFromURL()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public boolean isRequestedSessionIdFromUrl()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Object getAttribute(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Enumeration getAttributeNames()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getCharacterEncoding()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public void setCharacterEncoding(String name) throws UnsupportedEncodingException
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public int getContentLength()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getContentType()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public ServletInputStream getInputStream() throws IOException
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getParameter(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Enumeration getParameterNames()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String[] getParameterValues(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Map getParameterMap()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getProtocol()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getScheme()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getServerName()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public int getServerPort()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public BufferedReader getReader() throws IOException
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getRemoteAddr()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getRemoteHost()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public void setAttribute(String name, Object object)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public void removeAttribute(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Locale getLocale()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public Enumeration getLocales()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public boolean isSecure()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public RequestDispatcher getRequestDispatcher(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getRealPath(String name)
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public int getRemotePort()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getLocalName()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public String getLocalAddr()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//
//      public int getLocalPort()
//      {
//         throw new UnsupportedOperationException("Implement me");
//      }
//   }
}
