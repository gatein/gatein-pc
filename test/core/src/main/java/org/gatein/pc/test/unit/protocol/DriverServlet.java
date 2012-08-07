package org.gatein.pc.test.unit.protocol;

import org.gatein.common.io.IOTools;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.PortletTestServlet;
import org.gatein.pc.test.unit.protocol.response.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class DriverServlet extends HttpServlet
{

   /** . */
   private static PortletTestContext context;

   /**
    * The current portlet test context statically available.
    *
    * @return the portlet test context.
    */
   public static PortletTestContext getPortletTestContext()
   {
      return context;
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      Response response = context.getResponse();
      if (response != null)
      {
         resp.setStatus(200);
         IOTools.copy(new ByteArrayInputStream(IOTools.serialize(response)), resp.getOutputStream());
      }
      else
      {
         resp.setStatus(404);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      String testName;
      String step;
      HashMap<String, Serializable> payload;
      try
      {
         testName = req.getParameter("test");
         step = req.getParameter("step");
         byte[] body = IOTools.getBytes(req.getInputStream());
         payload = (HashMap<String, Serializable>)IOTools.unserialize(body, Thread.currentThread().getContextClassLoader());
      }
      catch (ClassNotFoundException e)
      {
         throw new ServletException(e);
      }

      // Setup context
      DriverContext ctx = new DriverContext(Integer.parseInt(step), payload);
      PortletTestCase testCase = PortletTestServlet.getTestCase(testName);
      context = new PortletTestContext(testName, testCase, ctx);

      //
      resp.setStatus(200);
   }
}
