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

/**
 * The <CODE>PortletResponse</CODE> defines the base interface to assist a portlet in creating and sending a response to
 * the client. The portlet container uses two specialized versions of this interface when invoking a portlet,
 * <CODE>ActionResponse</CODE> and <CODE>RenderResponse</CODE>. The  portlet container creates these objects and passes
 * them as arguments to the portlet's <CODE>processAction</CODE> and <CODE>render</CODE> methods.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 * @see ActionResponse
 * @see RenderResponse
 */
public interface PortletResponse
{
   /**
    * Adds a String property to an existing key to be returned to the portal.
    * <p/>
    * This method allows response properties to have multiple values.
    * <p/>
    * Properties can be used by portlets to provide vendor specific information to the portal.
    *
    * @param key   the key of the property to be returned to the portal
    * @param value the value of the property to be returned to the portal
    * @throws java.lang.IllegalArgumentException
    *          if key is <code>null</code>.
    */
   void addProperty(String key, String value) throws IllegalArgumentException;

   /**
    * Sets a String property to be returned to the portal.
    * <p/>
    * Properties can be used by portlets to provide vendor specific information to the portal.
    * <p/>
    * This method resets all properties previously added with the same key.
    *
    * @param key   the key of the property to be returned to the portal
    * @param value the value of the property to be returned to the portal
    * @throws java.lang.IllegalArgumentException
    *          if key is <code>null</code>.
    */
   void setProperty(String key, String value) throws IllegalArgumentException;

   /**
    * Returns the encoded URL of the resource, like servlets, JSPs, images and other static files, at the given path.
    * <p/>
    * Some portal/portlet-container implementation may require those URLs to contain implementation specific data
    * encoded in it. Because of that, portlets should use this method to create such URLs.
    * <p/>
    * The <code>encodeURL</code> method may include the session ID and other portal/portlet-container specific
    * information into the URL. If encoding is not needed, it returns the URL unchanged.
    *
    * @param path the URI path to the resource. This must be either an absolute URL (e.g.
    *             <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>) or a full path URI (e.g.
    *             <code>/myportal/mywebap/myfolder/myresource.gif</code>).
    * @return the encoded resource URL as string
    * @throws java.lang.IllegalArgumentException
    *          if path doesn't have a leading slash or is not an absolute URL
    */
   String encodeURL(String path) throws IllegalArgumentException;
}
