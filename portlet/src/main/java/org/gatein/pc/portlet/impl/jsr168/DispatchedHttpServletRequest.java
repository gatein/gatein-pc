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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.common.util.Tools;
import org.gatein.common.util.ParameterMap;
import org.gatein.common.http.QueryStringParser;
import org.gatein.pc.portlet.impl.jsr168.api.PortletRequestImpl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.portlet.PortletSession;
import javax.portlet.PortletRequest;
import javax.portlet.ClientDataRequest;
import javax.portlet.ActionRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.EventRequest;
import javax.portlet.RenderRequest;
import javax.portlet.PortletConfig;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6903 $
 */
public abstract class DispatchedHttpServletRequest extends HttpServletRequestWrapper
{

   /** . */
   private static final int REQUEST_URI = 0;

   /** . */
   private static final int SERVLET_PATH = 1;

   /** . */
   private static final int PATH_INFO = 2;

   /** . */
   private static final int QUERY_STRING = 3;

   /** . */
   private static final int CONTEXT_PATH = 4;

   /** . */
   private static final String[] INCLUDE_KEYS = {
      "javax.servlet.include.request_uri",
      "javax.servlet.include.servlet_path",
      "javax.servlet.include.path_info",
      "javax.servlet.include.query_string",
      "javax.servlet.include.context_path"
   };

   /** . */
   private static final String[] FORWARD_KEYS = {
      "javax.servlet.forward.request_uri",
      "javax.servlet.forward.servlet_path",
      "javax.servlet.forward.path_info",
      "javax.servlet.forward.query_string",
      "javax.servlet.forward.context_path"
   };

   /** . */
   private static final Set<String> ALL_CONTAINER_ATTRIBUTES = new HashSet<String>();

   static
   {
      ALL_CONTAINER_ATTRIBUTES.addAll(Tools.toSet(INCLUDE_KEYS));
      ALL_CONTAINER_ATTRIBUTES.addAll(Tools.toSet(FORWARD_KEYS));
   }

   /** The initial dispatch. */
   final DispatchType dispatchType;

   /** . */
   private final PortletRequest preq;

   /** . */
   private final HttpServletRequestWrapper dreq;

   /** . */
   private final Map<String, String[]> parameters;

   /** . */
   private final int sessionScope;

   /** . */
   private final LinkedList<Dispatch> dispatches;

   /** . */
   private String[] infos;

   /** . */
   private final ServletContext servletContext;

   /** . */
   private DispatchedHttpSession session;

   public DispatchedHttpServletRequest(
      Dispatch dispatch,
      PortletRequest preq,
      HttpServletRequestWrapper dreq,
      ServletContext servletContext)
   {
      super(dreq);

      PortletConfig config = (PortletConfig)dreq.getAttribute(Constants.JAVAX_PORTLET_CONFIG);
      Map<String, String[]> options = config.getContainerRuntimeOptions();
      String[] sessionScopeOption = options.get("javax.portlet.servletDefaultSessionScope");
      int sessionScope = PortletSession.APPLICATION_SCOPE;
      if (sessionScopeOption != null && sessionScopeOption.length > 0 && "PORTLET_SCOPE".equals(sessionScopeOption[0]))
      {
         sessionScope = PortletSession.PORTLET_SCOPE;
      }

      //
      this.dispatchType = dispatch.getType();
      this.preq = preq;
      this.dreq = dreq;
      this.servletContext = servletContext;
      this.dispatches = new LinkedList<Dispatch>();
      this.sessionScope = sessionScope;

      // Push dispatch
      /*this.infos =*/ pushDispatch(dispatch);

      //
      String queryString = null;
      String path = dispatch.getPath();
      if (path != null)
      {
         int index = path.indexOf('?');
         if (index > -1)
         {
            queryString = path.substring(index + 1);
         }
      }

      //
      Map<String, String[]> parameters;
      if (queryString != null && queryString.length() > 0)
      {
         ParameterMap tmp = new ParameterMap();
         tmp.putAll(QueryStringParser.getInstance().parseQueryString(queryString));
         tmp.append(preq.getParameterMap());
         parameters = tmp;
      }
      else
      {
         parameters = preq.getParameterMap();
      }

      //
      this.parameters = parameters;
      this.infos = null;
   }

   // Must return null

   public final String getRemoteAddr()
   {
      return null;
   }

   public final String getRemoteHost()
   {
      return null;
   }

   public final String getRealPath(String s)
   {
      return null;
   }

   public final String getLocalAddr()
   {
      return null;
   }

   public final String getLocalName()
   {
      return null;
   }

   public final StringBuffer getRequestURL()
   {
      return null;
   }

   // Must return 0

   public final int getRemotePort()
   {
      return 0;
   }

   public final int getLocalPort()
   {
      return 0;
   }

   // Must return the path and query string information used to obtain the PortletRequestDispatcher object

   public final String getPathTranslated()
   {
      String pathInfo = getPathInfo();

      //
      return pathInfo == null ? null : servletContext.getRealPath(pathInfo);
   }

   public final String getPathInfo()
   {
      if (infos != null)
      {
         return infos[PATH_INFO];
      }
      else
      {
         return (String)preq.getAttribute(INCLUDE_KEYS[PATH_INFO]);
      }
   }

   public final String getQueryString()
   {
      if (infos != null)
      {
         return infos[QUERY_STRING];
      }
      else
      {
         return (String)preq.getAttribute(INCLUDE_KEYS[QUERY_STRING]);
      }
   }

   public final String getRequestURI()
   {
      if (infos != null)
      {
         return infos[REQUEST_URI];
      }
      else
      {
         return (String)preq.getAttribute(INCLUDE_KEYS[REQUEST_URI]);
      }
   }

   public final String getServletPath()
   {
      if (infos != null)
      {
         return infos[SERVLET_PATH];
      }
      else
      {
         return (String)preq.getAttribute(INCLUDE_KEYS[SERVLET_PATH]);
      }
   }

   // Must be equivalent to the method of the PortletRequest

   public final String getScheme()
   {
      return preq.getScheme();
   }

   public final String getServerName()
   {
      return preq.getServerName();
   }

   public final int getServerPort()
   {
      return preq.getServerPort();
   }

   public final Object getAttribute(String s)
   {
      if (s != null)
      {
//         Map<String, String> containerAttributes = containerAttributesStack.getLast();

         //
//         if (containerAttributes.containsKey(s))
//         {
//            return containerAttributes.get(s);
//         }

         if (dispatches.getLast().getType() == DispatchType.INCLUDE)
         {
            for (String key : INCLUDE_KEYS)
            {
               if (key.equals(s))
               {
                  return preq.getAttribute(key);
               }
            }
         }
         else
         {
            for (int i = 0;i < FORWARD_KEYS.length;i++)
            {
               if (FORWARD_KEYS[i].equals(s))
               {
                  if (infos != null)
                  {
                     return infos[i];
                  }
                  else
                  {
                     return preq.getAttribute(INCLUDE_KEYS[i]);
                  }
               }
            }
         }

         //
         String[] containerKeys = dispatches.getLast().getType() == DispatchType.FORWARD ? FORWARD_KEYS : INCLUDE_KEYS;

         //
         for (int i = 0;i < containerKeys.length;i++)
         {
            if (containerKeys[i].equals(s))
            {
               return preq.getAttribute(INCLUDE_KEYS[i]);
            }
         }

         //
         if (ALL_CONTAINER_ATTRIBUTES.contains(s))
         {
            return null;
         }

         //
         return preq.getAttribute(s);
      }
      else
      {
         return null;
      }
   }

   public final Enumeration getAttributeNames()
   {
      Set<String> names = Tools.toSet(preq.getAttributeNames());

      //
      names.removeAll(ALL_CONTAINER_ATTRIBUTES);

      //
      String[] containerKeys = dispatches.getLast().getType() == DispatchType.FORWARD ? FORWARD_KEYS : INCLUDE_KEYS;

      //
      for (String containerKey : containerKeys)
      {
         if (getAttribute(containerKey) != null)
         {
            names.add(containerKey);
         }
      }

      //
      return Collections.enumeration(names);
   }

   public final void setAttribute(String s, Object o)
   {
      if (s != null && ALL_CONTAINER_ATTRIBUTES.contains(s))
      {
         return;
      }

      //
      ((PortletRequestImpl)preq).getAttributes().setAttribute(s, o);
   }

   public final void removeAttribute(String s)
   {
      setAttribute(s, null);
   }

   public final Locale getLocale()
   {
      return preq.getLocale();
   }

   public final Enumeration getLocales()
   {
      return preq.getLocales();
   }

   public boolean isSecure()
   {
      return preq.isSecure();
   }

   public final String getAuthType()
   {
      return preq.getAuthType();
   }

   public final String getContextPath()
   {
      return preq.getContextPath();
   }

   public final String getRemoteUser()
   {
      return preq.getRemoteUser();
   }

   public final Principal getUserPrincipal()
   {
      return preq.getUserPrincipal();
   }

   public final String getRequestedSessionId()
   {
      return preq.getRequestedSessionId();
   }

   public final boolean isRequestedSessionIdValid()
   {
      return preq.isRequestedSessionIdValid();
   }

   public final Cookie[] getCookies()
   {
      return preq.getCookies();
   }

   // Must be equivalent to the method of the PortletRequest with the provision defined in PLT.16.1.1

   public final String getParameter(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Parameter name must not be null");
      }

      //
      String[] values = parameters.get(name);
      if (values != null)
      {
         return values[0];
      }

      //
      return null;
   }

   public final Enumeration getParameterNames()
   {
      return Tools.toEnumeration(parameters.keySet().iterator());
   }

   public final String[] getParameterValues(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Parameter name must not be null");
      }

      //
      String[] values = parameters.get(name);
      if (values != null)
      {
         return values.clone();
      }

      //
      return null;
   }

   public final Map getParameterMap()
   {
      return parameters;
   }

   // Defined by life cycle phase

   public abstract String getCharacterEncoding();

   public abstract void setCharacterEncoding(String s) throws UnsupportedEncodingException;

   public abstract String getContentType();

   public abstract ServletInputStream getInputStream() throws IOException;

   public abstract BufferedReader getReader() throws IOException;

   public abstract int getContentLength();

   public abstract String getMethod();

   // Must be based on properties provided by the getProperties method of the PortletRequest interface

   public final String getHeader(String s)
   {
      return preq.getProperty(s);
   }

   public final Enumeration getHeaders(String s)
   {
      return preq.getProperties(s);
   }

   public final Enumeration getHeaderNames()
   {
      return preq.getPropertyNames();
   }

   public final long getDateHeader(String s)
   {
      return 0; // WTF ????
   }

   public final int getIntHeader(String s)
   {
      return 0; // WTF ????
   }

   // Must provide the functionnalities provided by the servlet specification 2.3

   public final RequestDispatcher getRequestDispatcher(String s)
   {
      return new DispatchtedRequestDispatcher(dreq.getRequestDispatcher(s), s);
   }

   public final boolean isUserInRole(String s)
   {
      return preq.isUserInRole(s);
   }

   public final HttpSession getSession(boolean b)
   {
      return getSession(b, sessionScope);
   }

   public final HttpSession getSession()
   {
      return getSession(true);
   }

   public final boolean isRequestedSessionIdFromCookie()
   {
      return dreq.isRequestedSessionIdFromCookie();
   }

   public final boolean isRequestedSessionIdFromURL()
   {
      return dreq.isRequestedSessionIdFromURL();
   }

   public final boolean isRequestedSessionIdFromUrl()
   {
      return isRequestedSessionIdFromURL();
   }

   // The getProtocol method of the HttpServletRequest must always return ‘HTTP/1.1’

   public final String getProtocol()
   {
      return "HTTP/1.1";
   }

   // ******

   public final void setRequest(ServletRequest servletRequest)
   {
      // That's a trick
      dreq.setRequest(servletRequest);
   }

   public ServletRequest getRequest()
   {
      // That's a trick
      return dreq.getRequest();
   }

   void pushDispatch(Dispatch dispatch)
   {
      dispatches.addLast(dispatch);

      // We need to backup
      if (dispatches.size() == 2)
      {
         String[] infos = new String[INCLUDE_KEYS.length];
         for (int i = 0;i < INCLUDE_KEYS.length;i++)
         {
            infos[i] = (String)preq.getAttribute(INCLUDE_KEYS[i]);
         }
         this.infos = infos;
      }


//      String path = dispatch.getPath();
//
//      //
//      String[] infos;
//      if (path != null)
//      {
//         infos = build(path);
//
//         //
//         if (dispatch.getType() == DispatchType.INCLUDE)
//         {
//            Map<String, String> containerAttributes = new HashMap<String, String>();
//            for (int i = 0;i < infos.length;i++)
//            {
//               String value = infos[i];
//               if (value != null)
//               {
//                  containerAttributes.put(INCLUDE_KEYS[i], value);
//               }
//            }
//            containerAttributesStack.addLast(containerAttributes);
//         }
//         else
//         {
//            if (containerAttributesStack.size() == 0)
//            {
//               Map<String, String> containerAttributes = new HashMap<String, String>();
//               for (int i = 0;i < infos.length;i++)
//               {
//                  String value = infos[i];
//                  if (value != null)
//                  {
//                     containerAttributes.put(FORWARD_KEYS[i], value);
//                  }
//               }
//               containerAttributesStack.addLast(containerAttributes);
//            }
//            else
//            {
//               Map<String, String> containerAttributes = new HashMap<String, String>();
//               for (int i = 0;i < this.infos.length;i++)
//               {
//                  String value = this.infos[i];
//                  if (value != null)
//                  {
//                     containerAttributes.put(FORWARD_KEYS[i], value);
//                  }
//               }
//               containerAttributesStack.addLast(containerAttributes);
//            }
//         }
//      }
//      else
//      {
//         infos = new String[5];
//
//         //
//         Map<String, String> containerAttributes = Collections.emptyMap();
//         containerAttributesStack.addLast(containerAttributes);
//      }
//
//      //
//      return infos;
   }

//   private String[] build(String path)
//   {
//      if (path == null)
//      {
//         throw new IllegalArgumentException();
//      }
//
//      //
//      String servletPath;
//      String pathInfo;
//      String queryString;
//      int endOfServletPath = path.indexOf('/', 1);
//      if (endOfServletPath == -1)
//      {
//         endOfServletPath = path.indexOf('?', 1);
//         if (endOfServletPath == -1)
//         {
//            servletPath = path;
//            pathInfo = "";
//            queryString = "";
//         }
//         else
//         {
//            servletPath = path.substring(0, endOfServletPath);
//            pathInfo = null;
//            queryString = path.substring(endOfServletPath + 1);
//         }
//      }
//      else
//      {
//         servletPath = path.substring(0, endOfServletPath);
//         int endOfPathInfo = path.indexOf('?', endOfServletPath + 1);
//         if (endOfPathInfo == -1)
//         {
//            pathInfo = path.substring(endOfServletPath);
//            queryString = "";
//         }
//         else
//         {
//            pathInfo = path.substring(endOfServletPath, endOfPathInfo);
//            queryString = path.substring(endOfPathInfo + 1);
//         }
//      }
//
//      //
//      String[] infos = new String[5];
//
//      //
//      StringBuffer requestURI = new StringBuffer(preq.getContextPath());
//      requestURI.append(servletPath);
//      if (pathInfo != null)
//      {
//         requestURI.append(pathInfo);
//         infos[PATH_INFO] = pathInfo;
//      }
//
//      //
//      infos[SERVLET_PATH] = servletPath;
//      infos[QUERY_STRING] = queryString;
//      infos[REQUEST_URI] = requestURI.toString();
//      infos[CONTEXT_PATH] = preq.getContextPath();
//
//      //
//      return infos;
//   }

   void popDispatch()
   {
      if (dispatches.size() == 2)
      {
         infos = null; // We could remove that as it's not going to change, but for now...
      }

      //
      dispatches.removeLast();
   }

   /**
    * Returns the an implementation of <code>javax.servlet.http.HttpSession</code> that wraps this
    * portlet session and use the specified scope for manipulating attributes. This session is used
    * during the request dispatch to a servlet.
    *
    * @param scope the scope
    * @return the wrapped session
    */
   private HttpSession getSession(boolean create, int scope)
   {
      // Dereference an existing session if necessary
      if (session != null && !session.isValid())
      {
         session = null;
      }

      // If we have a session here we are sure it is valid and ok to return it
      if (session != null)
      {
         // So we do nothing
      }
      else if (create)
      {
         // For sure we need a session we will obtain a valid one
         PortletSession portletSession = preq.getPortletSession();
         session = new DispatchedHttpSession(this, portletSession, scope);
      }
      else
      {
         // Here we can try an existing session but it may return null
         PortletSession portletSession = preq.getPortletSession(false);

         //
         if (portletSession != null)
         {
            session = new DispatchedHttpSession(this, portletSession, scope);
         }
      }

      //
      return session;
   }

   // Subclasses

   public static abstract class ClientData extends DispatchedHttpServletRequest
   {

      /** . */
      private PortletServletInputStream in;

      /** . */
      private final ClientDataRequest cdreq;

      public ClientData(Dispatch dispatch, ClientDataRequest cdreq, HttpServletRequestWrapper dreq, ServletContext servletContext)
      {
         super(dispatch, cdreq, dreq, servletContext);

         //
         this.cdreq = cdreq;
      }

      public final String getCharacterEncoding()
      {
         return cdreq.getCharacterEncoding();
      }

      public final void setCharacterEncoding(String s) throws UnsupportedEncodingException
      {
         cdreq.setCharacterEncoding(s);
      }

      public final String getContentType()
      {
         return cdreq.getContentType();
      }

      public final ServletInputStream getInputStream() throws IOException
      {
         if (in == null)
         {
            in = new PortletServletInputStream(cdreq);
         }
         return in;
      }

      public final BufferedReader getReader() throws IOException
      {
         return cdreq.getReader();
      }

      public final int getContentLength()
      {
         return cdreq.getContentLength();
      }

      public final String getMethod()
      {
         return cdreq.getMethod();
      }

      private static final class PortletServletInputStream extends ServletInputStream
      {

         /** . */
         private final InputStream in;

         private PortletServletInputStream(ClientDataRequest req) throws IOException
         {
            in = req.getPortletInputStream();
         }

         public final int read() throws IOException
         {
            return in.read();
         }
      }
   }

   public static final class Action extends ClientData
   {

      public Action(Dispatch dispatch, ActionRequest areq, HttpServletRequestWrapper dreq, ServletContext servletContext)
      {
         super(dispatch, areq, dreq, servletContext);
      }
   }

   public static final class Resource extends ClientData
   {

      public Resource(Dispatch dispatch, ResourceRequest rreq, HttpServletRequestWrapper dreq, ServletContext servletContext)
      {
         super(dispatch, rreq, dreq, servletContext);
      }
   }

   public static abstract class Mime extends DispatchedHttpServletRequest
   {
      public Mime(Dispatch dispatch, PortletRequest preq, HttpServletRequestWrapper dreq, ServletContext servletContext)
      {
         super(dispatch, preq, dreq, servletContext);
      }

      public final String getCharacterEncoding()
      {
         return null;
      }

      public final void setCharacterEncoding(String s) throws UnsupportedEncodingException
      {
      }

      public final String getContentType()
      {
         return null;
      }

      public final ServletInputStream getInputStream() throws IOException
      {
         return null;
      }

      public final BufferedReader getReader() throws IOException
      {
         return null;
      }

      public final int getContentLength()
      {
         return 0;
      }
   }

   public static final class Event extends Mime
   {

      /** . */
      private final EventRequest ereq;

      public Event(Dispatch dispatch, EventRequest ereq, HttpServletRequestWrapper dreq, ServletContext servletContext)
      {
         super(dispatch, ereq, dreq, servletContext);

         //
         this.ereq = ereq;
      }

      public String getMethod()
      {
         return ereq.getMethod();
      }
   }

   public static final class Render extends Mime
   {

      public Render(Dispatch dispatch, RenderRequest rreq, HttpServletRequestWrapper dreq, ServletContext servletContext)
      {
         super(dispatch, rreq, dreq, servletContext);
      }

      public String getMethod()
      {
         return "GET";
      }
   }
}
