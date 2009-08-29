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
package org.gatein.pc.portlet.impl.jsr168.taglib;

import org.gatein.pc.portlet.impl.jsr168.api.PortletURLImpl;
import org.gatein.pc.portlet.impl.jsr168.api.PortletRequestImpl;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.BaseURL;
import javax.servlet.jsp.JspException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Superclass of the actionURL and renderURL tags for the JSR 168 Portlet specification.
 * <p/>
 * Creates a URL that must point to the current portlet and must trigger a render or action request with the supplied
 * parameters.
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @version $Revision: 5448 $
 */
public abstract class GenerateURLTag extends PortletTag
{
   private String windowState;
   private String portletMode;
   private String var;
   private String secure;
   private Map parameters = new HashMap(5);

   /**
    * Indicates the portlet mode that the portlet must have when this link is executed.
    * <p/>
    * Predefined states: edit, view, help
    * <p/>
    * Optional. Defaults to same as the portlet mode for the current request, by not being included as a parameter in
    * the URL.
    *
    * @return Returns the portletMode.
    * @jsp.attribute rtexprvalue="true"
    */
   public String getPortletMode()
   {
      return portletMode;
   }

   /** @param portletMode The portletMode to set. */
   public void setPortletMode(String portletMode)
   {
      this.portletMode = portletMode;
   }

   /**
    * Indicates whether the resulting URL should be a secure or insecure connection.
    * <p/>
    * "true" or "false"
    * <p/>
    * Optional.  Defaults to security setting for the current request, by not being included as a parameter in the URL.
    *
    * @return Returns the secure connection value.
    * @jsp.attribute rtexprvalue="true"
    */
   public String getSecure()
   {
      return secure;
   }

   /** @param secure The secure connection value to set. */
   public void setSecure(String secure)
   {
      this.secure = secure;
   }

   /**
    * @return Returns the var - name of the exported scope variable.
    * @jsp.attribute rtexprvalue="true"
    */
   public String getVar()
   {
      return var;
   }

   /** @param var The var to set. */
   public void setVar(String var)
   {
      this.var = var;
   }

   /**
    * Indicates the window state that the portlet should have when this link is executed.
    * <p/>
    * Predefined states: minimized, maximized, normal
    * <p/>
    * Optional. Defaults to same as the window state for the current request, by not being included as a parameter in
    * the URL.
    *
    * @return Returns the windowState.
    * @jsp.attribute rtexprvalue="true"
    */
   public String getWindowState()
   {
      return windowState;
   }

   /** @param windowState The windowState to set. */
   public void setWindowState(String windowState)
   {
      this.windowState = windowState;
   }

   /**
    * Convert working parameters to what is needed by PortletURL - a map with String[] values.
    *
    * @return Returns the parameters.
    */
   protected Map getURLParameters()
   {
      Map urlParameters = new HashMap(parameters.size());

      Iterator it = parameters.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry entry = (Map.Entry)it.next();
         ArrayList l = (ArrayList)entry.getValue();
         urlParameters.put(entry.getKey(), l.toArray(new String[l.size()]));
      }
      return urlParameters;
   }

   /**
    * Add a named parameter. Cater for multiple parameters with the same name by storing them in a list.
    *
    * @param name
    * @param value
    */
   public void addParameter(String name, String value)
   {
      List l;

      Object o = parameters.get(name);
      if (o == null)
      {
         l = new ArrayList(5);
         parameters.put(name, l);

      }
      else
      {
         l = (List)o;
      }
      l.add(value);
   }

   /**
    * Set up the environment for generating the PortletURL and get the base PortletURL to decorate. Includes setting the
    * "type" parameter from the implementing subclass.
    *
    * @return The PortelURL to decorate
    */
   protected BaseURL getBasePortletEnvironmentAndURL()
   {
      PortletRequestImpl preq = (PortletRequestImpl)getPortletRequest();

      // Create the URL
      if ("action".equals(getTypeValue()))
      {
         return PortletURLImpl.createActionURL(getInvocation(), preq);
      }
      else if ("render".equals(getTypeValue()))
      {
         return PortletURLImpl.createRenderURL(getInvocation(), preq);
      }
      else
      {
         throw new Error("Impossible - it's a bug");
      }
   }

   /**
    * Set the window state for the URL to be what was given in the tag. If not given, default to what was there
    * previously
    *
    * @param portletURL
    * @throws WindowStateException
    */
   protected void setWindowState(PortletURL portletURL) throws WindowStateException
   {
      if (getWindowState() != null && getWindowState().trim().length() > 0)
      {
         portletURL.setWindowState(new WindowState(getWindowState().trim()));
      }
   }

   /**
    * Set the portlet mode for the URL to be what was given in the tag. If not given, default to what was there
    * previously
    *
    * @param portletURL
    * @throws PortletModeException
    */
   protected void setPortletMode(PortletURL portletURL) throws PortletModeException
   {
      if (getPortletMode() != null && getPortletMode().trim().length() > 0)
      {
         portletURL.setPortletMode(new PortletMode(getPortletMode().trim()));
      }
   }

   /**
    * Set the secure/unsecure state for the URL to be what was given in the tag. If not given, default to what was there
    * previously
    *
    * @param portletURL
    * @throws PortletSecurityException
    */
   protected void setSecure(BaseURL portletURL) throws PortletSecurityException
   {
      if (getSecure() != null && getSecure().trim().length() > 0)
      {
         portletURL.setSecure(Boolean.valueOf(getSecure().trim()).booleanValue());
      }
      else
      {
         // Maybe we don't need to set this again
         portletURL.setSecure(getInvocation().getSecurityContext().isSecure());
      }
   }

   protected void clearPreviousTag()
   {
      parameters = new HashMap(5);
      // windowCtx = null;
      // preq = null;
   }

   /**
    * Get the type - action or render - from the implementing subclass
    *
    * @return the value for the "type" portal parameter
    */
   protected abstract String getTypeValue();

   /** Process the body, which may contain portlet:param tags */
   public int doStartTag() throws JspException
   {
      clearPreviousTag();
      return EVAL_BODY_INCLUDE;
   }

   protected BaseURL generateURL() throws Exception
   {
      BaseURL newPortletURL = getBasePortletEnvironmentAndURL();

      setSecure(newPortletURL);

      // Include parameters as part of the tag request.

      if (!parameters.isEmpty())
      {
         newPortletURL.setParameters(getURLParameters());
      }
      return newPortletURL;
   }

   protected void writeURL(BaseURL portletURL) throws Exception
   {

      // If a variable was given in the tag to be set in the pageContext,
      // do that. Otherwise, just write out the URL to the page.

      if (getVar() != null)
      {
         pageContext.setAttribute(getVar(), portletURL.toString());
      }
      else
      {
         pageContext.getOut().print(portletURL.toString());
      }
   }


   /** Generate the URL */
   public int doEndTag() throws JspException
   {
      try
      {
         writeURL(generateURL());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new JspException(e);
      }
      return EVAL_PAGE;
   }


}
