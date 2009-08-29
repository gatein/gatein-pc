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
package org.gatein.pc.samples.basic;

import javax.portlet.GenericPortlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.PortletRequest;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.ActionResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ExceptionPortlet extends GenericPortlet
{

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      throwException(req);
   }

   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      throwException(req);

      //
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();
      PortletURL renderURL = resp.createRenderURL();
      PortletURL actionURL = resp.createActionURL();

      //
      writer.write("<p>");
      writer.write("<div class=\"portlet-section-header\">Throw :</div>");
      writer.write("<div class=\"portlet-section-body\">");
      renderURL.setParameter("op", "portletexception");
      writer.write("<div><a href=\"" + renderURL + "\">render PortletException</a></div>");
      renderURL.setParameter("op", "portletsecurityexception");
      writer.write("<div><a href=\"" + renderURL + "\">render PortletSecurityException</a></div>");
      renderURL.setParameter("op", "ioexception");
      writer.write("<div><a href=\"" + renderURL + "\">render IOException</a></div>");
      renderURL.setParameter("op", "runtimeexception");
      writer.write("<div><a href=\"" + renderURL + "\">render RuntimeException</a></div>");
      actionURL.setParameter("op", "error");
      writer.write("<div><a href=\"" + renderURL + "\">render Error</a></div>");
      actionURL.setParameter("op", "portletexception");
      writer.write("<div><a href=\"" + actionURL + "\">action PortletException</a></div>");
      actionURL.setParameter("op", "portletsecurityexception");
      writer.write("<div><a href=\"" + actionURL + "\">action PortletSecurityException</a></div>");
      actionURL.setParameter("op", "ioexception");
      writer.write("<div><a href=\"" + actionURL + "\">action IOException</a></div>");
      actionURL.setParameter("op", "runtimeexception");
      writer.write("<div><a href=\"" + actionURL + "\">action RuntimeException</a></div>");
      actionURL.setParameter("op", "error");
      writer.write("<div><a href=\"" + actionURL + "\">action Error</a></div>");
      writer.write("</div>");
      writer.write("</p>");
   }

   private void throwException(PortletRequest req) throws PortletException, IOException
   {
      String op = req.getParameter("op");

      // Throw any required exception
      if ("portletexception".equals(op))
      {
         throw new PortletException();
      }
      if ("portletsecurityexception".equals(op))
      {
         throw new PortletSecurityException("");
      }
      if ("ioexception".equals(op))
      {
         throw new IOException();
      }
      if ("runtimeexception".equals(op))
      {
         throw new RuntimeException();
      }
      if ("error".equals(op))
      {
         throw new Error();
      }
   }
}
