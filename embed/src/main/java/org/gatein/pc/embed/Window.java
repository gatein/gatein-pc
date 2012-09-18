/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.embed;

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.WindowState;

import java.util.LinkedHashMap;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class Window
{

   /** . */
   final String id;

   /** The porlet name. */
   final String name;

   /** The navigation parameters. */
   final Map<String, String[]> parameters;

   /** . */
   final Mode mode;

   /** . */
   final WindowState state;

   /** The related portlet. */
   final Portlet portlet;

   Window(String id, Portlet portlet, String name, Mode mode, WindowState state, LinkedHashMap<String, String[]> parameters)
   {
      this.id = id;
      this.portlet = portlet;
      this.name = name;
      this.mode = mode;
      this.state = state;
      this.parameters = parameters;
   }
}
