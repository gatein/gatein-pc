/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.gatein.pc.test.tck;

import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class TCKPortletPageNavigationalState implements PortletPageNavigationalState
{

   /** . */
   final PortletPageNavigationalState defaultState;

   /** . */
   final Set<String> involvedPortlets;

   public TCKPortletPageNavigationalState(PortletPageNavigationalState defaultState, Set<String> involvedPortlets)
   {
      this.defaultState = defaultState;
      this.involvedPortlets = involvedPortlets;
   }

   public Set<String> getInvolvedPortlets()
   {
      return involvedPortlets;
   }

   public Set<String> getPortletWindowIds()
   {
      return defaultState.getPortletWindowIds();
   }

   public PortletWindowNavigationalState getPortletWindowNavigationalState(String portletWindowId) throws IllegalArgumentException
   {
      return defaultState.getPortletWindowNavigationalState(portletWindowId);
   }

   public void setPortletWindowNavigationalState(String portletWindowId, PortletWindowNavigationalState portletWindowState) throws IllegalArgumentException, IllegalStateException
   {
      defaultState.setPortletWindowNavigationalState(portletWindowId, portletWindowState);
   }

   public Map<String, String[]> getPortletPublicNavigationalState(String portletWindowId) throws IllegalArgumentException
   {
      return defaultState.getPortletPublicNavigationalState(portletWindowId);
   }

   public Set<QName> getPublicNames()
   {
      return defaultState.getPublicNames();
   }

   public void setPortletPublicNavigationalState(String portletWindowId, Map<String, String[]> update) throws IllegalArgumentException, IllegalStateException
   {
      defaultState.setPortletPublicNavigationalState(portletWindowId, update);
   }

   public String[] getPublicNavigationalState(QName name) throws IllegalArgumentException
   {
      return defaultState.getPublicNavigationalState(name);
   }

   public void setPublicNavigationalState(QName name, String[] value) throws IllegalArgumentException, IllegalStateException
   {
      defaultState.setPublicNavigationalState(name, value);
   }

   public void removePublicNavigationalState(QName name) throws IllegalArgumentException, IllegalStateException
   {
      defaultState.removePublicNavigationalState(name);
   }
}
