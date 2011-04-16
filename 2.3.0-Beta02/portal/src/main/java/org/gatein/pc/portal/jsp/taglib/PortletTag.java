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
package org.gatein.pc.portal.jsp.taglib;

import org.gatein.pc.portal.jsp.PortalRenderResponse;
import org.gatein.pc.portal.jsp.PortalPrepareResponse;
import org.gatein.pc.portal.jsp.PortalResponse;
import org.gatein.pc.portal.jsp.WindowDef;
import org.gatein.pc.portal.jsp.WindowResult;
import org.gatein.pc.portal.Constants;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.UnavailableResponse;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.common.util.Tools;

import javax.servlet.jsp.JspException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Collections;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletTag extends PortalBodyTagSupport
{

   /** . */
   private static final Set<Mode> DEFAULT_MODES = Collections.unmodifiableSet(Tools.toSet(Mode.VIEW, org.gatein.pc.api.Mode.EDIT, Mode.HELP));

   /** . */
   private static final Set<org.gatein.pc.api.WindowState> DEFAULT_WINDOW_STATES = Collections.unmodifiableSet(Tools.toSet(org.gatein.pc.api.WindowState.NORMAL, org.gatein.pc.api.WindowState.MAXIMIZED, WindowState.MINIMIZED));

   /** . */
   private String nameAttr;

   /** . */
   private String applicationNameAttr;

   /** . */
   private String supportedWindowStatesAttr;

   /** . */
   private String supportedModesAttr;

   /** . */
   private String initialModeAttr;

   /** . */
   private String errorPageAttr;

   private Set<org.gatein.pc.api.WindowState> supportedWindowStates;
   private Set<Mode> supportedModes;
   private Mode initialMode;
   private PageTag pageTag;
   private String windowId;

   WindowResult result;

   public String getName()
   {
      return nameAttr;
   }

   public void setName(String name)
   {
      this.nameAttr = name;
   }

   public String getApplicationName()
   {
      return applicationNameAttr;
   }

   public void setApplicationName(String applicationName)
   {
      this.applicationNameAttr = applicationName;
   }

   public String getSupportedWindowStates()
   {
      return supportedWindowStatesAttr;
   }

   public void setSupportedWindowStates(String supportedWindowStates)
   {
      this.supportedWindowStatesAttr = supportedWindowStates;
   }

   public String getSupportedModes()
   {
      return supportedModesAttr;
   }

   public void setSupportedModes(String supportedModes)
   {
      this.supportedModesAttr = supportedModes;
   }

   public String getInitialMode()
   {
      return initialModeAttr;
   }

   public void setInitialMode(String initialMode)
   {
      this.initialModeAttr = initialMode;
   }

   public String getErrorPage()
   {
      return errorPageAttr;
   }

   public void setErrorPage(String errorPage)
   {
      this.errorPageAttr = errorPage;
   }

   private void initState(PortalResponse portalResponse)
   {
      //
      Set<Mode> supportedModes = new LinkedHashSet<Mode>();
      if (supportedModesAttr != null)
      {
         for (String supportedModeValue : supportedModesAttr.split(","))
         {
            org.gatein.pc.api.Mode mode = Mode.create(supportedModeValue.trim());
            supportedModes.add(mode);
         }
      }
      else
      {
         supportedModes = DEFAULT_MODES;
      }

      //
      Set<org.gatein.pc.api.WindowState> supportedWindowStates = new LinkedHashSet<org.gatein.pc.api.WindowState>();
      if (supportedWindowStatesAttr != null)
      {
         for (String supportedWindowStateValue : supportedWindowStatesAttr.split(","))
         {
            org.gatein.pc.api.WindowState windowState = WindowState.create(supportedWindowStateValue.trim());
            supportedWindowStates.add(windowState);
         }
      }
      else
      {
         supportedWindowStates = DEFAULT_WINDOW_STATES;
      }

      //
      org.gatein.pc.api.Mode initialMode = Mode.VIEW;
      if (initialModeAttr != null)
      {
         initialMode = org.gatein.pc.api.Mode.create(initialModeAttr.trim());
      }

      //
      String windowId = portalResponse.nextId();

      //
      this.initialMode = initialMode;
      this.supportedModes = supportedModes;
      this.supportedWindowStates = supportedWindowStates;
      this.pageTag = (PageTag)findAncestorWithClass(this, PageTag.class);
      this.windowId = windowId;
   }

   private void destroyState()
   {
      this.initialMode = null;
      this.supportedModes = null;
      this.supportedWindowStates = null;
      this.pageTag = null;
      this.windowId = null;
   }

   public int doStartTag(PortalPrepareResponse prepareResponse) throws JspException
   {
      initState(prepareResponse);

      //
      WindowDef windowDef = new WindowDef(nameAttr, applicationNameAttr, windowId, initialMode, supportedModes, supportedWindowStates);

      //
      prepareResponse.addWindowDef(windowId, windowDef);

      //
      return SKIP_BODY;
   }

   public int doEndTag(PortalPrepareResponse prepareResponse) throws JspException
   {
      destroyState();

      //
      return EVAL_PAGE;
   }

   public int doStartTag(PortalRenderResponse renderResponse) throws JspException
   {
      initState(renderResponse);

      //
      WindowResult result = renderResponse.getWindowResult(windowId);

      //
      if (result == null || result.getResponse() == null)
      {
         handleError(renderResponse, Constants.NOT_FOUND, null, null);

         // We cannot show an absent portlet, no meta data
         return SKIP_BODY;
      }
      else
      {
         if (pageTag.maximizedId != null)
         {
            if (windowId.equals(pageTag.maximizedId))
            {
               return render(renderResponse, result, EVAL_BODY_BUFFERED);
            }
            else
            {
               return SKIP_BODY;
            }
         }
         else
         {
            return render(renderResponse, result, EVAL_BODY_INCLUDE);
         }
      }
   }

   public int doEndTag(PortalRenderResponse renderResponse) throws JspException
   {
      if (bodyContent != null)
      {
         pageTag.content = bodyContent.getString();
      }

      //
      destroyState();

      //
      return EVAL_PAGE;
   }

   public int render(PortalRenderResponse renderResponse, WindowResult result, int rt)
   {
      PortletInvocationResponse portletResponse = result.getResponse();

      //
      if (portletResponse instanceof ContentResponse)
      {
         this.result = result;
         //
         return rt;
      }
      else if (portletResponse instanceof ErrorResponse)
      {
         ErrorResponse error = (ErrorResponse)portletResponse;

         //
         handleError(renderResponse, Constants.PORTLET_ERROR, error.getCause(), error.getMessage());

         //
         return SKIP_BODY;
      }
      else if (portletResponse instanceof UnavailableResponse)
      {
         handleError(renderResponse, Constants.UNAVAILABLE, null, null);

         //
         return SKIP_BODY;
      }
      else
      {
         handleError(renderResponse, Constants.INTERNAL_ERROR, null, null);

         //
         return SKIP_BODY;
      }
   }

   private void handleError(PortalRenderResponse renderResponse, String errorStatus, Throwable cause, String message)
   {
      if (errorPageAttr != null)
      {
         RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher(errorPageAttr);
         if (rd != null)
         {
            ServletRequest request= pageContext.getRequest();

            //
            try
            {
               pageTag.status = PageTag.Status.SUSPENDED;
               request.setAttribute(Constants.ERROR_STATUS, errorStatus);
               request.setAttribute(Constants.ERROR_CAUSE, cause);
               request.setAttribute(Constants.ERROR_MESSAGE, message);
               request.setAttribute(Constants.ERROR_WINDOW_ID, windowId);
               request.setAttribute(Constants.ERROR_APPLICATION_NAME, applicationNameAttr);
               request.setAttribute(Constants.ERROR_PORTLET_NAME, nameAttr);

               //
               rd.include(pageContext.getRequest(), new JspWriterResponse(renderResponse, pageContext.getOut()));
            }
            catch (ServletException e)
            {
               // Shall we do smth else ?
               e.printStackTrace();
            }
            catch (IOException e)
            {
               // Shall we do smth else ?
               e.printStackTrace();
            }
            finally
            {
               pageTag.status = PageTag.Status.ACTIVE;
               request.removeAttribute(Constants.ERROR_STATUS);
               request.removeAttribute(Constants.ERROR_CAUSE);
               request.removeAttribute(Constants.ERROR_MESSAGE);
               request.removeAttribute(Constants.ERROR_WINDOW_ID);
               request.removeAttribute(Constants.ERROR_APPLICATION_NAME);
               request.removeAttribute(Constants.ERROR_PORTLET_NAME);
            }
         }
      }
   }

}
