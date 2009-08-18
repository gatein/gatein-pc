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
package javax.portlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The <CODE>GenericPortlet</CODE> class provides a default implementation for the <CODE>Portlet</CODE> interface.
 * <p/>
 * It provides an abstract class to be subclassed to create portlets. A subclass of <CODE>GenericPortlet</CODE> should
 * override at least one method, usually one of the following: <ul> <li>processAction, to handle action requests</li>
 * <li>doView, to handle render requests when in VIEW mode</li> <li>doEdit, to handle render requests when in EDIT
 * mode</li> <li>doHelp, to handle render request when in HELP mode</li> <li>init and destroy, to manage resources that
 * are held for the life of the servlet</li> </ul>
 * <p/>
 * Normally there is no need to override the render or the doDispatch methods. Render handles render requests setting
 * the title of the portlet in the response and invoking doDispatch. doDispatch dispatches the request to one of the
 * doView, doEdit or doHelp method depending on the portlet mode indicated in the request.
 * <p/>
 * Portlets typically run on multithreaded servers, so please note that a portlet must handle concurrent requests and be
 * careful to synchronize access to shared resources.  Shared resources include in-memory data such as  instance or
 * class variables and external objects  such as files, database connections, and network  connections.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public abstract class GenericPortlet implements Portlet
{

   private PortletConfig config;

   /** Does nothing. */
   protected GenericPortlet()
   {
   }

   /**
    * A convenience method which can be overridden so that there's no need to call <code>super.init(config)</code>.
    * <p/>
    * <p>Instead of overriding {@link #init(PortletConfig)}, simply override this method and it will be called by
    * <code>GenericPortlet.init(PortletConfig config)</code>. The <code>PortletConfig</code> object can still be
    * retrieved via {@link #getPortletConfig}.
    *
    * @throws PortletException     if an exception has occurred that interferes with the portlet normal operation.
    * @throws UnavailableException if the portlet is unavailable to perform init
    */
   public void init() throws PortletException
   {
   }

   /**
    * Returns the PortletConfig object of this portlet.
    *
    * @return the PortletConfig object of this portlet
    */
   public PortletConfig getPortletConfig()
   {
      return config;
   }

   /**
    * Returns the name of this portlet.
    *
    * @return the portlet name
    * @see PortletConfig#getPortletName()
    */
   public String getPortletName()
   {
      return config.getPortletName();
   }

   /**
    * Returns the <code>PortletContext</code> of the portlet application the portlet is in.
    *
    * @return the portlet application context
    */
   public PortletContext getPortletContext()
   {
      return config.getPortletContext();
   }

   /**
    * The default implementation of this method routes the render request to a set of helper methods depending on the
    * current portlet mode the portlet is currently in. These methods are: <ul> <li><code>doView</code> for handling
    * <code>view</code> requests <li><code>doEdit</code> for handling <code>edit</code> requests <li><code>doHelp</code>
    * for handling <code>help</code> requests </ul>
    * <p/>
    * If the window state of this portlet is <code>minimized</code>, this method does not invoke any of the portlet mode
    * rendering methods.
    * <p/>
    * For handling custom portlet modes the portlet should override this method.
    *
    * @param request  the render request
    * @param response the render response
    * @throws PortletException         if the portlet cannot fulfilling the request
    * @throws UnavailableException     if the portlet is unavailable to perform render at this time
    * @throws PortletSecurityException if the portlet cannot fullfill this request because of security reasons
    * @throws java.io.IOException      if the streaming causes an I/O problem
    * @see #doView(RenderRequest,RenderResponse)
    * @see #doEdit(RenderRequest,RenderResponse)
    * @see #doHelp(RenderRequest,RenderResponse)
    */
   protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException
   {
      if (!WindowState.MINIMIZED.equals(request.getWindowState()))
      {
         PortletMode portletMode = request.getPortletMode();
         if (PortletMode.VIEW.equals(portletMode))
         {
            doView(request, response);
         }
         else if (PortletMode.HELP.equals(portletMode))
         {
            doHelp(request, response);
         }
         else if (PortletMode.EDIT.equals(portletMode))
         {
            doEdit(request, response);
         }
      }
   }

   /**
    * Helper method to serve up the mandatory <code>view</code> mode.
    * <p/>
    * The default implementation throws an exception.
    *
    * @param request  the portlet request
    * @param response the render response
    * @throws PortletException         if the portlet cannot fulfilling the request
    * @throws UnavailableException     if the portlet is unavailable to perform render at this time
    * @throws PortletSecurityException if the portlet cannot fullfill this request because of security reasons
    * @throws java.io.IOException      if the streaming causes an I/O problem
    */
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException
   {
      throw new PortletException();
   }

   /**
    * Helper method to serve up the <code>help</code> mode.
    * <p/>
    * The default implementation throws an exception.
    *
    * @param request  the portlet request
    * @param response the render response
    * @throws PortletException         if the portlet cannot fulfilling the request
    * @throws UnavailableException     if the portlet is unavailable to perform render at this time
    * @throws PortletSecurityException if the portlet cannot fullfill this request because of security reasons
    * @throws java.io.IOException      if the streaming causes an I/O problem
    */
   protected void doHelp(RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException
   {
      throw new PortletException();
   }

   /**
    * Helper method to serve up the <code>edit</code> mode.
    * <p/>
    * The default implementation throws an exception.
    *
    * @param request  the portlet request
    * @param response the render response
    * @throws PortletException         if the portlet cannot fulfilling the request
    * @throws UnavailableException     if the portlet is unavailable to perform render at this time
    * @throws PortletSecurityException if the portlet cannot fullfill this request because of security reasons
    * @throws java.io.IOException      if the streaming causes an I/O problem
    */
   protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException
   {
      throw new PortletException();
   }

   /**
    * Gets the resource bundle for the given locale based on the resource bundle defined in the deployment descriptor
    * with <code>resource-bundle</code> tag or the inlined resources defined in the deployment descriptor.
    *
    * @return the resource bundle for the given locale
    */
   public ResourceBundle getResourceBundle(Locale locale)
   {
      return getPortletConfig().getResourceBundle(locale);
   }

   /**
    * Used by the render method to get the title.
    * <p/>
    * The default implementation gets the title from the ResourceBundle of the PortletConfig of the portlet. The title
    * is retrieved using the 'javax.portlet.title' resource name.
    * <p/>
    * Portlets can overwrite this method to provide dynamic titles (e.g. based on locale, client, and session
    * information). Examples are: <UL> <LI>language-dependant titles for multi-lingual portals <LI>shorter titles for
    * WAP phones <LI>the number of messages in a mailbox portlet </UL>
    *
    * @return the portlet title for this window
    */
   protected String getTitle(RenderRequest request)
   {
      ResourceBundle bundle = getResourceBundle(request.getLocale());
      return bundle.getString("javax.portlet.title");
   }

   /**
    * Returns a String containing the value of the named initialization parameter, or null if the parameter does not
    * exist.
    *
    * @param name a <code>String</code> specifying the name of the initialization parameter
    * @return a <code>String</code> containing the value of the initialization parameter
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    */
   public String getInitParameter(String name) throws IllegalArgumentException
   {
      return getPortletConfig().getInitParameter(name);
   }

   /**
    * Returns the names of the portlet initialization parameters as an Enumeration of String objects, or an empty
    * Enumeration if the portlet has no initialization parameters.
    *
    * @return an <code>Enumeration</code> of <code>String</code> objects containing the names of the portlet
    * initialization parameters, or an empty Enumeration if the portlet has no initialization parameters.
    */
   public Enumeration getInitParameterNames()
   {
      return getPortletConfig().getInitParameterNames();
   }

   /**
    * Called by the portlet container to indicate to a portlet that the portlet is being placed into service.
    * <p/>
    * The default implementation just stores the <code>PortletConfig</code> object. <p>The portlet container calls the
    * <code>init</code> method exactly once after instantiating the portlet. The <code>init</code> method must complete
    * successfully before the portlet can receive any requests.
    * <p/>
    * <p>The portlet container cannot place the portlet into service if the <code>init</code> method does one of the
    * following: <ol> <li>it throws a <code>PortletException</code> <li>it does not return within a time period defined
    * by the Web server </ol>
    *
    * @param config a <code>PortletConfig</code> object containing the portlet configuration and initialization
    *               parameters
    * @throws PortletException     if an exception has occurred that interferes with the portlet normal operation.
    * @throws UnavailableException if the portlet cannot perform the initialization at this time.
    */
   public void init(PortletConfig config) throws PortletException
   {
      this.config = config;
      init();
   }

   /**
    * Called by the portlet container to allow the portlet to process an action request. This method is called if the
    * client request was originated by a URL created (by the portlet) with the <code>RenderResponse.createActionURL()</code>
    * method.
    * <p/>
    * The default implementation throws an exception.
    *
    * @param request  the action request
    * @param response the action response
    * @throws PortletException         if the portlet cannot fulfilling the request
    * @throws UnavailableException     if the portlet is unavailable to process the action at this time
    * @throws PortletSecurityException if the portlet cannot fullfill this request because of security reasons
    * @throws java.io.IOException      if the streaming causes an I/O problem
    */
   public void processAction(ActionRequest request, ActionResponse response) throws PortletException, PortletSecurityException, IOException
   {
      throw new PortletException();
   }

   /**
    * The default implementation of this method sets the title using the <code>getTitle</code> method and invokes the
    * <code>doDispatch</code> method.
    *
    * @param request  the render request
    * @param response the render response
    * @throws PortletException         if the portlet cannot fulfilling the request
    * @throws UnavailableException     if the portlet is unavailable to perform render at this time
    * @throws PortletSecurityException if the portlet cannot fullfill this request because of security reasons
    * @throws java.io.IOException      if the streaming causes an I/O problem
    */
   public void render(RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException
   {
      response.setTitle(getTitle(request));
      doDispatch(request, response);
   }

   /**
    * Called by the portlet container to indicate to a portlet that the portlet is being taken out of service.
    * <p/>
    * The default implementation does nothing.
    */
   public void destroy()
   {
   }
}
