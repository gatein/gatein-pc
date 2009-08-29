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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class URLProperty286Tag extends TagSupport
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1432342354333218087L;
   private String name;
   private String value;

   /**
    * The name of the property to add to the URL.
    * <p/>
    * If null or empty, no action is performed.
    *
    * @return Returns the name.
    * @jsp.attribute required="true" rtexprvalue="true"
    */
   public String getName()
   {
      return name;
   }

   /** @param name The name to set. */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * The value of the property to add to the URL.
    * <p/>
    * If null, it is processed as an empty value.
    *
    * @return Returns the value.
    * @jsp.attribute required="true" rtexprvalue="true"
    */
   public String getValue()
   {
      return value;
   }

   /** @param value The value to set. */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * No body to process
    *
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      return SKIP_BODY;
   }

   /**
    * Include the property in the surround tag properties
    *
    * @see javax.servlet.jsp.tagext.Tag#doEndTag()
    */
   public int doEndTag() throws JspException
   {
      try
      {
         GenerateURL286Tag ancestorTag =
            (GenerateURL286Tag)findAncestorWithClass(this, GenerateURL286Tag.class);

         Property p = new Property(getName(), getValue());

         if (p.isValid())
         {
            ancestorTag.addProperty(p.getName(), p.getValue());
         }
         else
         {
            // Should throw a JspException?
            // Not according to the JSR 286 spec. Ignore it
         }

      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new JspException(e);
      }
      return EVAL_PAGE;
   }

   public class Property
   {
      private String name;
      private String value;

      /** @return Returns the name. */
      public String getName()
      {
         return name;
      }

      /**
       * Per JSR 286 spec, if null, return an empty value
       *
       * @return Returns the value.
       */
      public String getValue()
      {
         return value;
      }

      public Property(String name, String value)
      {
         this.name = name;
         this.value = value;
      }

      public boolean isValid()
      {
         return name != null && name.length() > 0;
      }
   }

}
