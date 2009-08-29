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
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The <CODE>PortletConfig</CODE> interface provides the portlet with its configuration. The configuration holds
 * information about the portlet that is valid for all users. The configuration is retrieved from the portlet definition
 * in the deployment descriptor. The portlet can only read the configuration data.
 * <p/>
 * The configuration information contains the portlet name, the portlet initialization parameters, the portlet resource
 * bundle and the portlet application context.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 * @see Portlet
 */
public interface PortletConfig
{
   /**
    * Returns the name of the portlet.
    * <p/>
    * The name may be provided via server administration, assigned in the portlet application deployment descriptor with
    * the <code>portlet-name</code> tag.
    *
    * @return the portlet name
    */
   String getPortletName();

   /**
    * Returns the <code>PortletContext</code> of the portlet application the portlet is in.
    *
    * @return a <code>PortletContext</code> object, used by the caller to interact with its portlet container
    * @see PortletContext
    */
   PortletContext getPortletContext();

   /**
    * Gets the resource bundle for the given locale based on the resource bundle defined in the deployment descriptor
    * with <code>resource-bundle</code> tag or the inlined resources defined in the deployment descriptor.
    *
    * @param locale the locale for which to retrieve the resource bundle
    * @return the resource bundle for the given locale
    */
   ResourceBundle getResourceBundle(Locale locale);

   /**
    * Returns a String containing the value of the named initialization parameter, or null if the parameter does not
    * exist.
    *
    * @param name a <code>String</code> specifying the name of the initialization parameter
    * @return a <code>String</code> containing the value of the initialization parameter
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    */
   String getInitParameter(String name) throws IllegalArgumentException;

   /**
    * Returns the names of the portlet initialization parameters as an <code>Enumeration</code> of String objects, or an
    * empty <code>Enumeration</code> if the portlet has no initialization parameters.
    *
    * @return an <code>Enumeration</code> of <code>String</code> objects containing the names of the portlet
    * initialization parameters, or an empty <code>Enumeration</code> if the portlet has no initialization parameters.
    */
   Enumeration getInitParameterNames();
}
