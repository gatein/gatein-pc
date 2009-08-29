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
package org.gatein.pc.api;

import org.gatein.common.io.Serialization;
import org.gatein.common.util.ParameterMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * A set of parameters.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6549 $
 */
public class ParametersStateString extends StateString implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -8529807471117491810L;

   public static ParametersStateString create()
   {
      return new ParametersStateString();
   }

   public static ParametersStateString create(Map<String, String[]> parameters)
   {
      return new ParametersStateString(parameters);
   }

   /**
    * Create a parameters state string. It assumes that the argument is either an instance of
    * <code>ParametersStateString</code> or that it is the string encoded value of a
    * <code>ParametersStateString</code>.
    *
    * @param stateString the state string
    * @return a new parameter state string
    * @throws IllegalArgumentException if the state string is opaque and does not represent parameters
    */
   public static ParametersStateString create(StateString stateString) throws IllegalArgumentException
   {
      if (stateString == null)
      {
         throw new IllegalArgumentException("No null state string accepted");
      }

      //
      if (stateString instanceof ParametersStateString)
      {
         // Use the copy constructor
         return new ParametersStateString(((ParametersStateString)stateString).parameters);
      }
      else
      {
         // Use the deserializing constructor
         return new ParametersStateString(stateString.getStringValue());
      }
   }

   /** The underlying map that does not have a copy read/write access mode. */
   private ParameterMap parameters;

   protected ParametersStateString(DataInputStream in) throws IOException
   {
      Map<String, String[]> tmp = Serialization.PARAMETER_MAP.unserialize(in);

      //
      parameters = ParameterMap.wrap(tmp);
   }

   protected ParametersStateString(String opaqueValue)
   {
      Map<String, String[]> params = StateString.decodeOpaqueValue(opaqueValue);
      if (!params.isEmpty())
      {
         parameters = ParameterMap.wrap(params);
      }
      else
      {
         parameters = new ParameterMap();
      }
   }

   /** Creates an empty parameter set. */
   protected ParametersStateString()
   {
      this.parameters = new ParameterMap();
   }

   /**
    * Copy the parameter map to initialize the object state.
    *
    * @param parameters the parameter map to clone
    * @throws IllegalArgumentException if the parameter map is null or not valid
    */
   private ParametersStateString(Map<String, String[]> parameters)
   {
      this.parameters = ParameterMap.clone(parameters);
   }

   /**
    * Return the parameter value or null if it does not exist.
    *
    * @param name the parameter name
    * @return the parameter value or null if it does not exist
    * @throws IllegalArgumentException if the name is null
    */
   public String getValue(String name) throws IllegalArgumentException
   {
      return parameters.getValue(name);
   }

   /**
    * Return the parameter values or null if it does not exist.
    *
    * @param name the value to get
    * @return the parameter values
    * @throws IllegalArgumentException if the name is null
    */
   public String[] getValues(String name) throws IllegalArgumentException
   {
      return parameters.get(name);
   }

   /** Clear all the parameters. */
   public void clear()
   {
      parameters.clear();
   }

   /**
    * Replace all the parameters.
    *
    * @param map the map to replace
    * @throws IllegalArgumentException if the map is not valid
    */
   public void replace(Map<String, String[]> map)
   {
      parameters.replace(map);
   }

   /**
    * Set the a parameter value.
    *
    * @param name  the parameter name
    * @param value the parameter value
    * @throws IllegalArgumentException if the name or the value is null
    */
   public void setValue(String name, String value)
   {
      parameters.setValue(name, value);
   }

   /**
    * Set the parameter values. This method does not make a defensive copy of the values.
    *
    * @param name   the parameter name
    * @param values the parameter values
    * @throws IllegalArgumentException if the name is null
    */
   public void setValues(String name, String[] values)
   {
      parameters.setValues(name, values);
   }

   /**
    * Remove a parameter.
    *
    * @param name the parameter name
    * @throws IllegalArgumentException if the name is null
    */
   public void remove(String name)
   {
      parameters.remove(name);
   }

   /**
    * Return the size.
    *
    * @return the size
    */
   public int getSize()
   {
      return parameters.size();
   }

   /**
    * Return the underlying parameter object.
    *
    * @return the parameter object
    */
   public Map<String, String[]> getParameters()
   {
      return parameters;
   }

   /**
    * Retrieves the opaque version associated with this navigational state.
    *
    * @return a URL-safe String representation of this navigational state.
    */
   public String getStringValue()
   {
      return StateString.encodeAsOpaqueValue(parameters);
   }

   public void writeTo(DataOutputStream out) throws IOException
   {
      if (parameters.isEmpty())
      {
         out.writeByte(StateString.EMPTY);
      }
      else
      {
         out.writeByte(StateString.SERIALIZED);
         Serialization.PARAMETER_MAP.serialize(parameters, out);
      }
   }

   public String toString()
   {
      return "StateString[" + parameters + "]";
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof ParametersStateString)
      {
         ParametersStateString that = (ParametersStateString)obj;
         return parameters.equals(that.parameters);
      }
      return false;
   }

   public int hashCode()
   {
      return parameters.hashCode();
   }
}
