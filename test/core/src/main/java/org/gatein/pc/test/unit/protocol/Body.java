package org.gatein.pc.test.unit.protocol;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public abstract class Body implements Serializable
{
   public static class Raw extends Body
   {

      /** . */
      private byte[] bytes;

      public byte[] getBytes()
      {
         return bytes;
      }

      public void setBytes(byte[] bytes)
      {
         this.bytes = bytes;
      }
   }

   public static class Form extends Body
   {

      /** . */
      private Map<String, String[]> parameters = new HashMap<String, String[]>();

      public void addParameter(String name, String[] values)
      {
         if (name == null)
         {
            throw new IllegalStateException();
         }
         if (values == null)
         {
            throw new IllegalStateException();
         }
         for (String value : values)
         {
            if (value == null)
            {
               throw new IllegalStateException();
            }
         }
         parameters.put(name, values.clone());
      }

      public void removeParameter(String name)
      {
         if (name == null)
         {
            throw new IllegalStateException();
         }
         parameters.remove(name);
      }

      public Set getParameterNames()
      {
         return Collections.unmodifiableSet(parameters.keySet());
      }

      public String[] getParameterValues(String name)
      {
         if (name == null)
         {
            throw new IllegalStateException();
         }
         String[] values = parameters.get(name);
         return values != null ? values.clone() : null;
      }
   }
}
