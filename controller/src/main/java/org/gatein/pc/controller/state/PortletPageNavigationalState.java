/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.controller.state;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Set;

/**
 * Defines the page navigational state view that the controller needs to operate on. Its name begins with page
 * however it does not mandate that the represented context  to be a page. I.E it could represent a set
 * of physical pages or something else.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public interface PortletPageNavigationalState
{

   /**
    * Returns the portlet window ids referenced.
    *
    * @return a set of window id
    */
   Set<String> getPortletWindowIds();

   /**
    * Returns the navigational state of a portlet window or null if it does not exist.
    *
    * @param portletWindowId the portlet window id
    * @return the portlet window navigational state
    * @throws IllegalArgumentException if an argument is not valid
    */
   PortletWindowNavigationalState getPortletWindowNavigationalState(String portletWindowId) throws IllegalArgumentException;

   /**
    * Update the navigational state of a portlet window.
    *
    * @param portletWindowId the portlet window id
    * @param portletWindowState the portlet window state
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   void setPortletWindowNavigationalState(String portletWindowId, PortletWindowNavigationalState portletWindowState) throws IllegalArgumentException, IllegalStateException;

   /**
    * Obtain the public navigational state of a portlet window. The interpretation of what should be retrieved is left up
    * to the implementor. An example of implementation would use the mapping between qnames and name provided by the
    * referenced portlet info.
    *
    * @param portletWindowId the portlet window id
    * @return the portlet public navigational state
    * @throws IllegalArgumentException if an argument is not valid
    */
   Map<String, String[]> getPortletPublicNavigationalState(String portletWindowId) throws IllegalArgumentException;

   /**
    * <p>Update the public navigational state of a portlet window. The interpretation of what should be updated is left up
    * to the implementor. An example of implementation would use the mapping between qname and name provided by the referenced
    * portlet info.</p>
    *
    * <p>The update argument values with a length of zero should be treated as removals.</p>
    *
    * @param portletWindowId the portlet window id
    * @param update the updates
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   void setPortletPublicNavigationalState(String portletWindowId, Map<String, String[]> update) throws IllegalArgumentException, IllegalStateException;

   /**
    * Returns the set of public names.
    *
    * @return the public names
    */
   Set<QName> getPublicNames();

   /**
    * Returns a public navigational state entry or null if it is not found.
    *
    * @param name the name
    * @return the entry value
    * @throws IllegalArgumentException if an argument is not valid
    */
   String[] getPublicNavigationalState(QName name) throws IllegalArgumentException;

   /**
    * Sets a public navigational state entry.
    *
    * @param name the name
    * @param value the new value
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   void setPublicNavigationalState(QName name, String[] value) throws IllegalArgumentException, IllegalStateException;

   /**
    * Removes a public navigational state entry.
    *
    * @param name the name
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   void removePublicNavigationalState(QName name) throws IllegalArgumentException, IllegalStateException;
}
