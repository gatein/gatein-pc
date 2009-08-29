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

package org.gatein.pc.portlet.impl.jsr286.taglib;

import org.gatein.pc.portlet.impl.jsr168.taglib.GenerateURLTag;
import org.gatein.pc.portlet.impl.jsr168.api.PortletRequestImpl;
import org.gatein.pc.portlet.impl.jsr168.api.ResourceURLImpl;

import javax.portlet.PortletURL;
import javax.portlet.BaseURL;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public abstract class GenerateURL286Tag extends GenerateURLTag
{
   private Map<String, List> properties;

   private String escapeXml;

   private String copyCurrentRenderParameters;

   private StringWriter stringWriter;

   public String getEscapeXml()
   {
      return escapeXml;
   }

   public void setEscapeXml(String escapeXml)
   {
      this.escapeXml = escapeXml;
   }

   public String getCopyCurrentRenderParameters()
   {
      return copyCurrentRenderParameters;
   }

   public void setCopyCurrentRenderParameters(String copyCurrentRenderParameters)
   {
      this.copyCurrentRenderParameters = copyCurrentRenderParameters;
   }

   protected void setCopyCurrentRenderParameters(PortletURL portletURL)
   {
      //default is false
      if (copyCurrentRenderParameters != null && copyCurrentRenderParameters.equalsIgnoreCase("true"))
      {
         // Parameters values specified in tag need to be pre-pended  

         Map<String, String[]> parameters = portletURL.getParameterMap();

         Map<String, String[]> privateParams = getPortletRequest().getPrivateParameterMap();

         for(String name : privateParams.keySet())
         {

            if (!parameters.containsKey(name))
            {
               parameters.put(name, privateParams.get(name));
            }
            else
            {
               String[] val1 = parameters.get(name);
               String[] val2 = privateParams.get(name);
               String[] newVal = new String[val1.length + val2.length];

               for (int i = 0; i < val1.length; i++)
               {
                  newVal[i] = val1[i];
               }
               for (int i = 0; i < val2.length; i++)
               {
                  newVal[val1.length + i] = val2[i];
               }
               parameters.put(name, newVal);
            }
         }

         portletURL.setParameters(parameters);
      }
   }

   protected void removeTagParametersWithEmptyValue(PortletURL portletURL)
   {
      // Introduced in jsr 286 - the empty param tag value removes the parameter

      Map<String, String[]> parameters = portletURL.getParameterMap();

      Map<String, String[]> tagParams = getURLParameters();

      for (String name : tagParams.keySet())
      {
         String[] values = tagParams.get(name);
         if (values.length > 0 && values[values.length - 1].equals(""))
         {
            parameters.remove(name);
         }
      }

      portletURL.setParameters(parameters);

   }

   protected boolean isEscapeXml()
   {

      boolean def = true;

      String[] runtime = getConfig().getContainerRuntimeOptions().get("javax.portlet.escapeXml");

      if (runtime != null && runtime.length > 0)
      {
         if (runtime[0].equalsIgnoreCase("true"))
         {
            def = true;
         }
         else if (runtime[0].equalsIgnoreCase("false"))
         {
            def = false;
         }
      }

      if(escapeXml != null)
      {
         return Boolean.parseBoolean(escapeXml);
      }

      return def;
   }

   /**
    * Add a named property. Cater for multiple properties with the same name by storing them in a list.
    *
    * @param name
    * @param value
    */
   public void addProperty(String name, String value)
   {
      List<String> l = properties.get(name);
      if (l == null)
      {
         l = new ArrayList<String>(5);
         properties.put(name, l);
      }

      l.add(value);
   }

   protected void clearPreviousTag()
   {
      stringWriter = new StringWriter();
      properties = new HashMap<String, List>();

      super.clearPreviousTag();
   }

   protected BaseURL getBasePortletEnvironmentAndURL()
   {
      PortletRequestImpl preq = (PortletRequestImpl)getPortletRequest();

      if ("resource".equals(getTypeValue()))
      {
         return ResourceURLImpl.createResourceURL(getInvocation(), preq);
      }
      else
      {
         return super.getBasePortletEnvironmentAndURL();
      }
   }

   protected BaseURL generateURL() throws Exception
   {
      BaseURL newPortletURL =  super.generateURL();

      if (!properties.isEmpty())
      {
         for (String name : properties.keySet())
         {
            List<String> props = properties.get(name);
            for (String prop : props)
            {
               newPortletURL.setProperty(name, prop);
            }
         }
      }

      return newPortletURL;
   }

   protected void writeURL(BaseURL portletURL) throws Exception
   {

      // If a variable was given in the tag to be set in the pageContext,
      // do that. Otherwise, just write out the URL to the page.

      portletURL.write(stringWriter, isEscapeXml());

      if (getVar() != null)
      {
         pageContext.setAttribute(getVar(), stringWriter.toString());
      }
      else
      {
         pageContext.getOut().print(stringWriter.toString());
      }
   }
}
