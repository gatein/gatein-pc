package org.gatein.pc.test.unit;

import org.gatein.pc.api.spi.ServerContext;
import org.gatein.pc.test.TestPortletApplicationDeployer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Properties;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class PortletTestServlet extends HttpServlet
{

   /** . */
   private static HashMap<String, PortletTestCase> testSuite;

   public static PortletTestCase getTestCase(String testName)
   {
      if (testName == null)
      {
         throw new IllegalArgumentException("Test name can't be null");
      }
      return testSuite != null ? testSuite.get(testName) : null;
   }

   @Override
   public void init() throws ServletException
   {
      ServletContext ctx = getServletContext();

      //
      InputStream in = ctx.getResourceAsStream("/WEB-INF/suite.properties");
      if (in != null)
      {
         try
         {
            HashMap<String, PortletTestCase> suite = new HashMap<String, PortletTestCase>();
            Properties props = new Properties();
            props.load(in);
            for (Object key : props.keySet())
            {
               ClassLoader loader = ctx.getClassLoader();
               Class<?> clazz = loader.loadClass(key.toString());
               Constructor ctor = clazz.getConstructor(PortletTestCase.class);
               String testCaseName = clazz.getSimpleName();
               PortletTestCase portletTestCase = new PortletTestCase(testCaseName, ctx);
               ctor.newInstance(portletTestCase);
               suite.put(testCaseName, portletTestCase);
            }
            testSuite = suite;
         }
         catch (Exception e)
         {
            throw new ServletException(e);
         }
      }

      //
      TestPortletApplicationDeployer.deploy(ctx);
   }

   @Override
   public void destroy()
   {
      TestPortletApplicationDeployer.undeploy(getServletContext());

      //
      super.destroy();
   }

   /** . */
   public static final ThreadLocal<ServerContext.Callable> callback = new ThreadLocal<ServerContext.Callable>();

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      ServerContext.Callable cb = callback.get();
      if (cb != null)
      {
         cb.call(getServletContext(), req, resp);
      }
   }
}
