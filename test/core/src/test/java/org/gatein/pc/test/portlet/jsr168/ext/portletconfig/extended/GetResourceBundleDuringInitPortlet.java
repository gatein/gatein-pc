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
package org.gatein.pc.test.portlet.jsr168.ext.portletconfig.extended;

import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.JoinPointType;
import org.gatein.pc.test.unit.annotations.TestActor;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 5572 $
 */
@TestActor(id=GetResourceBundleDuringInitPortlet.NAME)
public class GetResourceBundleDuringInitPortlet extends AbstractUniversalTestPortlet
{
   public static ResourceBundle bundle = null;

   public static ThreadLocal local = new ThreadLocal();

   public static final String NAME = "GetResourceBundleDuringInitPortlet";

   public final static JoinPoint RENDER_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_RENDER);

   public final static JoinPoint ACTION_JOIN_POINT = new JoinPoint(NAME, JoinPointType.PORTLET_ACTION);

   protected void reset()
   {
      local.set(null);
   }

   public void init(PortletConfig config) throws PortletException
   {
      super.init(config);

      //
      try
      {
         bundle = config.getResourceBundle(Locale.ENGLISH);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
