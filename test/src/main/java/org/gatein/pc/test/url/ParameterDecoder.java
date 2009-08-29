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

import org.gatein.common.util.ParameterMap;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ParameterDecoder
{

   /** . */
   private final CodecBuilder builder;

   /** . */
   private final ParameterMap metaParameters;

   /** . */
   private final ParameterMap actualParameters;

   public ParameterDecoder(CodecBuilder builder)
   {
      this.builder = builder;
      this.actualParameters = new ParameterMap();
      this.metaParameters = new ParameterMap();
   }

   public ParameterMap getMetaParameters()
   {
      return metaParameters;
   }

   public ParameterMap getActualParameters()
   {
      return actualParameters;
   }

   public void decode(ParameterMap parameters)
   {
      actualParameters.clear();
      metaParameters.clear();

      // Get the meta parameter
      long reservedParameter;
      String[] reservedParameters = parameters.getValues(builder.reservedParameterName);
      if (reservedParameters != null)
      {
         if (reservedParameters.length < 1)
         {
            throw new IllegalArgumentException();
         }
         try
         {
            reservedParameter = Long.parseLong(reservedParameters[0], 2);
         }
         catch (NumberFormatException e)
         {
            throw new IllegalArgumentException();
         }
      }
      else
      {
         throw new IllegalArgumentException();
      }

      //
      for (int i = builder.metaParameterNames.size() - 1; reservedParameter > 0;)
      {
         if (i < 0)
         {
            throw new IllegalArgumentException();
         }

         //
         String parameterName = builder.metaParameterNames.get(i--);

         //
         if ((reservedParameter & 1) == 1)
         {
            String[] parameterValues = parameters.getValues(parameterName);

            //
            if (parameterValues == null)
            {
               throw new IllegalArgumentException();
            }
            if (parameterValues.length == 0)
            {
               throw new IllegalArgumentException();
            }

            //
            String metaParameterValue = parameterValues[0];
            metaParameters.setValue(parameterName, metaParameterValue);

            //
            if (parameterValues.length > 1)
            {
               String[] actualParameterValues = new String[parameterValues.length - 1];
               System.arraycopy(parameterValues, 1, actualParameterValues, 0, actualParameterValues.length);
               actualParameters.setValues(parameterName, actualParameterValues);
            }
         }

         //
         reservedParameter = reservedParameter >> 1;
      }

      //
      for (Iterator i = parameters.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String parameterName = (String)entry.getKey();
         if (!metaParameters.containsKey(parameterName))
         {
            if (builder.reservedParameterName.equals(parameterName))
            {
               String[] reservedParameterValues = (String[])entry.getValue();
               if (reservedParameterValues.length > 1)
               {
                  String[] parameterValues = new String[reservedParameterValues.length - 1];
                  System.arraycopy(reservedParameterValues, 1, parameterValues, 0, parameterValues.length);
                  actualParameters.setValues(parameterName, parameterValues);
               }
            }
            else
            {
               String[] parameterValues = (String[])entry.getValue();
               actualParameters.setValues(parameterName, parameterValues);
            }
         }
      }
   }
}
