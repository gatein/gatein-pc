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
package org.gatein.pc.test.portlet.framework;

import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.JoinPointType;
import org.gatein.pc.test.unit.annotations.TestActor;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 5568 $
 */
@TestActor(id=UTP2.NAME)
public class UTP2 extends AbstractUniversalTestPortlet
{
   public static ThreadLocal local = new ThreadLocal();

   public static Object holder;

   public static final String NAME = "Portlet2";

   public final static JoinPoint RENDER_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_RENDER);

   public final static JoinPoint EVENT_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_EVENT);

   public final static JoinPoint ACTION_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_ACTION);

   public final static JoinPoint RESOURCE_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_RESOURCE);

   /** Resets helper variables */
   public void reset()
   {
      local.set(null);
      holder = null;
   }
}
