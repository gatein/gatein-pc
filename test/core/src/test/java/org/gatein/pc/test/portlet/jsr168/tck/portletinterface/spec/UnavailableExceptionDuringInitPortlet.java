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
package org.gatein.pc.test.portlet.jsr168.tck.portletinterface.spec;

import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.JoinPointType;
import org.gatein.pc.test.unit.annotations.TestActor;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.UnavailableException;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 5572 $
 * @portlet.specification assert="SPEC:5 - During initialization, the portlet object may throw an UnavailableException
 * or a PortletException. In this case, the portlet container must not place the portlet object into active service and
 * it must release the portlet object."
 * @portlet.specification assert="SPEC:6 - The destroy method must not be called because the initialization is
 * considered unsuccessful."
 */
@TestActor(id=UnavailableExceptionDuringInitPortlet.NAME)
public class UnavailableExceptionDuringInitPortlet extends AbstractUniversalTestPortlet
{

   public static final String NAME = "UnavailableExceptionDuringInitPortlet";

   public final static JoinPoint RENDER_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_RENDER);

   public final static JoinPoint ACTION_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_ACTION);

   //This is static becouse value is shared beetween portlet instances
   public static boolean rendered;
   public static boolean destroyed;

   public void init(PortletConfig config) throws PortletException
   {
      super.init(config);

      //
      throw new UnavailableException("Unavailable for testing purposes");
   }

   public void destroy()
   {
      destroyed = true;
   }

   protected void reset()
   {
      rendered = false;
      destroyed = false;
   }
}
