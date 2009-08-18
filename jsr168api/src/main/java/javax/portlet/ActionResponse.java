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
import java.util.Map;

/**
 * The <CODE>ActionResponse</CODE> interface represents the portlet response to an action request. It extends the
 * <CODE>PortletResponse</CODE> interface to provide specific action response functionality to portlets.<br> The portlet
 * container creates an <CODE>ActionResponse</CODE> object and passes it as argument to the portlet's
 * <CODE>processAction</CODE> method.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 * @see ActionRequest
 * @see PortletResponse
 */
public interface ActionResponse extends PortletResponse
{
   /**
    * Sets the window state of a portlet to the given window state.
    * <p/>
    * Possible values are the standard window states and any custom window states supported by the portal and the
    * portlet. Standard window states are: <ul> <li>MINIMIZED <li>NORMAL <li>MAXIMIZED </ul>
    *
    * @param windowState the new portlet window state
    * @throws WindowStateException if the portlet cannot switch to the specified window state. To avoid this exception
    *                              the portlet can check the allowed window states with <code>Request.isWindowStateAllowed()</code>.
    * @throws java.lang.IllegalStateException
    *                              if the method is invoked after <code>sendRedirect</code> has been called.
    * @see WindowState
    */
   void setWindowState(WindowState windowState) throws WindowStateException;

   /**
    * Sets the portlet mode of a portlet to the given portlet mode.
    * <p/>
    * Possible values are the standard portlet modes and any custom portlet modes supported by the portal and the
    * portlet. Portlets must declare in the deployment descriptor the portlet modes they support for each markup type.
    * Standard portlet modes are: <ul> <li>EDIT <li>HELP <li>VIEW </ul>
    * <p/>
    * Note: The portlet may still be called in a different window state in the next render call, depending on the
    * portlet container / portal.
    *
    * @param portletMode the new portlet mode
    * @throws PortletModeException if the portlet cannot switch to this portlet mode, because the portlet or portal does
    *                              not support it for this markup, or the current user is not allowed to switch to this
    *                              portlet mode. To avoid this exception the portlet can check the allowed portlet modes
    *                              with <code>Request.isPortletModeAllowed()</code>.
    * @throws java.lang.IllegalStateException
    *                              if the method is invoked after <code>sendRedirect</code> has been called.
    */
   void setPortletMode(PortletMode portletMode) throws PortletModeException;

   /**
    * Instructs the portlet container to send a redirect response to the client using the specified redirect location
    * URL.
    * <p/>
    * This method only accepts an absolute URL (e.g. <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
    * or a full path URI (e.g. <code>/myportal/mywebap/myfolder/myresource.gif</code>). If required, the portlet
    * container may encode the given URL before the redirection is issued to the client.
    * <p/>
    * The sendRedirect method can not be invoked after any of the following methods of the ActionResponse interface has
    * been called: <ul> <li>setPortletMode <li>setWindowState <li>setRenderParameter <li>setRenderParameters </ul>
    *
    * @throws java.lang.IllegalStateException
    *          if the method is invoked after any of above mentioned methods of the ActionResponse interface has been
    *          called.
    * @param      location   the redirect location URL
    * @exception java.io.IOException if an input or output exception occurs.
    * @exception java.lang.IllegalArgumentException if a relative path URL is given
    */
   void sendRedirect(String location) throws IOException, IllegalArgumentException, IllegalStateException;

   /**
    * Sets a parameter map for the render request.
    * <p/>
    * All previously set render parameters are cleared.
    * <p/>
    * These parameters will be accessible in all sub-sequent render calls via the <code>PortletRequest.getParameter</code>
    * call until a new request is targeted to the portlet.
    * <p/>
    * The given parameters do not need to be encoded prior to calling this method.
    *
    * @param parameters Map containing parameter names for the render phase as keys and parameter values as map values.
    *                   The keys in the parameter map must be of type String. The values in the parameter map must be of
    *                   type String array (<code>String[]</code>).
    * @throws java.lang.IllegalStateException
    *          if the method is invoked after <code>sendRedirect</code> has been called.
    * @exception java.lang.IllegalArgumentException if parameters is <code>null</code>, if any of the key/values in the
    * Map are <code>null</code>, if any of the keys is not a String, or if any of the values is not a String array.
    */
   void setRenderParameters(Map parameters) throws IllegalArgumentException, IllegalStateException;

   /**
    * Sets a String parameter for the render request.
    * <p/>
    * These parameters will be accessible in all sub-sequent render calls via the <code>PortletRequest.getParameter</code>
    * call until a request is targeted to the portlet.
    * <p/>
    * This method replaces all parameters with the given key.
    * <p/>
    * The given parameter do not need to be encoded prior to calling this method.
    *
    * @param key   key of the render parameter
    * @param value value of the render parameter
    * @throws java.lang.IllegalStateException
    *          if the method is invoked after <code>sendRedirect</code> has been called.
    * @exception java.lang.IllegalArgumentException if key or value are <code>null</code>.
    */
   void setRenderParameter(String key, String value) throws IllegalArgumentException, IllegalStateException;

   /**
    * Sets a String array parameter for the render request.
    * <p/>
    * These parameters will be accessible in all sub-sequent render calls via the <code>PortletRequest.getParameter</code>
    * call until a request is targeted to the portlet.
    * <p/>
    * This method replaces all parameters with the given key.
    * <p/>
    * The given parameter do not need to be encoded prior to calling this method.
    *
    * @param key    key of the render parameter
    * @param values values of the render parameter
    * @throws java.lang.IllegalStateException
    *          if the method is invoked after <code>sendRedirect</code> has been called.
    * @exception java.lang.IllegalArgumentException if key or value are <code>null</code>.
    */
   void setRenderParameter(String key, String[] values) throws IllegalArgumentException, IllegalStateException;
}
