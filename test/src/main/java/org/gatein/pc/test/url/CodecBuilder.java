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
package org.gatein.pc.test.url;

import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class CodecBuilder
{

   /** . */
   final ArrayList<String> metaParameterNames;

   /** . */
   final String reservedParameterName;

   public CodecBuilder(CodecBuilder that)
   {
      this.metaParameterNames = new ArrayList<String>(that.metaParameterNames);
      this.reservedParameterName = that.reservedParameterName;
   }

   public CodecBuilder(String reservedParameter)
   {
      this.metaParameterNames = new ArrayList<String>();
      this.reservedParameterName = reservedParameter;
   }

   public CodecBuilder addMetaParameter(String name)
   {
      if (metaParameterNames.contains(name))
      {
         throw new IllegalStateException();
      }
      if (reservedParameterName.equals(name))
      {
         throw new IllegalStateException();
      }
      metaParameterNames.add(name);
      return this;
   }

   public ParameterEncoder createEncoder()
   {
      return new ParameterEncoder(this);
   }

   public ParameterDecoder createDecoder()
   {
      return new ParameterDecoder(this);
   }
}
