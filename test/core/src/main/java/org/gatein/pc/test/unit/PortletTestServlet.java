package org.gatein.pc.test.unit;

import org.gatein.pc.test.TestPortletApplicationDeployer;
import org.gatein.wci.RequestDispatchCallback;
import org.gatein.wci.WebApp;

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

   /** . */
   private WebApp webApp;

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
      final ClassLoader loader = Thread.currentThread().getContextClassLoader();
      final ServletContext ctx = getServletContext();

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
            throw new RuntimeException(e);
         }
      }

      //
      webApp = new WebApp()
      {
         @Override
         public ServletContext getServletContext()
         {
            return ctx;
         }

         @Override
         public ClassLoader getClassLoader()
         {
            return loader;
         }

         @Override
         public String getContextPath()
         {
            return ctx.getContextPath();
         }

         @Override
         public boolean importFile(String parentDirRelativePath, String name, InputStream source, boolean overwrite) throws IOException
         {
            return false;
         }

         @Override
         public boolean invalidateSession(String sessId)
         {
            return false;
         }
      };

      //
      TestPortletApplicationDeployer.deploy(webApp);
   }

   @Override
   public void destroy()
   {
      TestPortletApplicationDeployer.undeploy(webApp);

      //
      super.destroy();
   }

   /** . */
   public static final ThreadLocal<RequestDispatchCallback> callback = new ThreadLocal<RequestDispatchCallback>();

   /** . */
   public static final ThreadLocal<Object> payload = new ThreadLocal<Object>();

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      RequestDispatchCallback cb = callback.get();
      if (cb != null)
      {
         Object ret = cb.doCallback(getServletContext(), req, resp, payload.get());
         payload.set(ret);
      }
   }
}
