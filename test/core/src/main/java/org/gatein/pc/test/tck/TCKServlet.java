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

package org.gatein.pc.test.tck;

import org.gatein.pc.test.controller.Renderer;
import org.gatein.pc.test.controller.ControllerResponseRendererFactory;
import org.gatein.pc.test.controller.PageRenderer;
import org.gatein.pc.controller.impl.URLParameterConstants;
import org.gatein.pc.controller.impl.ControllerRequestFactory;
import org.gatein.pc.controller.PortletController;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.request.ControllerRequest;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.Portlet;
import org.gatein.wci.WebRequest;
import org.gatein.wci.WebResponse;
import org.gatein.wci.endpoint.EndPointServlet;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class TCKServlet extends EndPointServlet
{

   /** . */
   public static String TCK_PORTLET_NAME = "portletName";

   protected void service(WebRequest req, WebResponse resp) throws ServletException, IOException
   {
      try
      {
         _service(req, resp);
      }
      catch (Exception e)
      {
         if (e instanceof ServletException)
         {
            throw (ServletException)e;
         }
         if (e instanceof IOException)
         {
            throw (IOException)e;
         }
         if (e instanceof RuntimeException)
         {
            throw (RuntimeException)e;
         }
         throw new ServletException(e);
      }
   }

   protected void _service(WebRequest req, WebResponse resp) throws Exception
   {
      TCKPortletControllerContext context = new TCKPortletControllerContext(req, resp, getServletContext());

      //
      PortletController controller = new PortletController();

      //
      String type = req.getParameter(URLParameterConstants.TYPE);

      //
      if (URLParameterConstants.PORTLET_TYPE.equals(type))
      {
         ControllerRequestFactory factory = new ControllerRequestFactory(context.getPageNavigationalStateSerialization());
         ControllerRequest request = factory.decode(req);

         // We must have one!!!
         TCKPortletPageNavigationalState tckPageNavigationalState = (TCKPortletPageNavigationalState)request.getPageNavigationalState();

         //
         ControllerResponse response = controller.process(context, request);

         //
         ControllerResponseRendererFactory rendererFactory = new ControllerResponseRendererFactory(
            false,
            false,
            context.getStateControllerContext(),
            request.getPageNavigationalState());

         //
         Renderer renderer = rendererFactory.getRenderer(response);

         //
         renderer.render(new TCKRendererContext(context, tckPageNavigationalState));
      }
      else
      {
         String[] tckParamers = req.getParameterValues(TCK_PORTLET_NAME);
         Collection<Portlet> portlets = context.getPortlets();
         Set<String> involvedPortlets = new LinkedHashSet<String>();
         for (String tckParamer : tckParamers)
         {
            String[] parts = tckParamer.split("/");
            String portletAppName = parts[0];
            String portletName = parts[1];

            //
            for (Portlet portlet : portlets)
            {
               if (portlet.getInfo().getApplicationName().equals(portletAppName)
                  && portlet.getInfo().getName().equals(portletName))
               {
                  involvedPortlets.add(portlet.getContext().getId());
               }
            }
         }

         if (tckParamers.length > 0 && involvedPortlets.isEmpty())
         {
            throw new IllegalStateException("Portlets requested by TCK were not found");
         }

         TCKPortletPageNavigationalState tckPageNavigationalState = new TCKPortletPageNavigationalState(context.getStateControllerContext().createPortletPageNavigationalState(false), involvedPortlets);

         //
         PageRenderer renderer = new PageRenderer(new ResponseProperties(), tckPageNavigationalState);

         //
         renderer.render(new TCKRendererContext(context, tckPageNavigationalState));
      }
   }
}
