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

import java.util.Enumeration;

/**
 * The <CODE>PortalContext</CODE> interface gives the portlet the ability to retrieve information about the portal
 * calling this portlet.
 * <p/>
 * The portlet can only read the <CODE>PortalContext</CODE> data.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public interface PortalContext
{
   /**
    * Returns information about the portal like vendor, version, etc.
    * <p/>
    * The form of the returned string is <I>servername/versionnumber</I>. For example, the reference implementation
    * Pluto may return the string <CODE>Pluto/1.0</CODE>.
    * <p/>
    * The portlet container may return other optional information  after the primary string in parentheses, for example,
    * <CODE>Pluto/1.0 (JDK 1.3.1; Windows NT 4.0 x86)</CODE>.
    *
    * @return a <CODE>String</CODE> containing at least the portal name and version number
    */
   String getPortalInfo();

   /**
    * Returns the portal property with the given name, or a <code>null</code> if there is no property by that name.
    *
    * @param name property name
    * @return portal property with key <code>name</code>
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    */
   String getProperty(String name);

   /**
    * Returns all portal property names, or an empty <code>Enumeration</code> if there are no property names.
    *
    * @return All portal property names as an <code>Enumeration</code> of <code>String</code> objects
    */
   Enumeration getPropertyNames();

   /**
    * Returns all supported portlet modes by the portal as an enumertation of <code>PorltetMode</code> objects.
    * <p/>
    * The portlet modes must at least include the standard portlet modes <code>EDIT, HELP, VIEW</code>.
    *
    * @return All supported portal modes by the portal as an enumertation of <code>PorltetMode</code> objects.
    */
   Enumeration getSupportedPortletModes();

   /**
    * Returns all supported window states by the portal as an enumertation of <code>WindowState</code> objects.
    * <p/>
    * The window states must at least include the standard window states <code> MINIMIZED, NORMAL, MAXIMIZED</code>.
    *
    * @return All supported window states by the portal as an enumertation of <code>WindowState</code> objects.
    */
   Enumeration getSupportedWindowStates();
}
