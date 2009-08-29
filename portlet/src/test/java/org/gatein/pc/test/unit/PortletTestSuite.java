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
package org.gatein.pc.test.unit;

import org.apache.log4j.Logger;
import org.jboss.unit.remote.driver.RemoteTestDriverServer;
import org.gatein.common.net.URLNavigator;
import org.gatein.common.net.URLVisitor;
import org.gatein.common.net.URLFilter;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.File;

/**
 * Builder is invoked when web application initialization process is starting. It builds sequence of tests for this
 * action.
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6989 $
 */
public final class PortletTestSuite implements ServletContextListener, ServletContextAttributeListener
{

   /** . */
   private static final Logger log = Logger.getLogger(PortletTestSuite.class);

   /** . */
   private PortletTestDriver driver;

   /** . */
   private ClassLoader loader;
   
   protected boolean testDriverSetup = false;

   /**
    * Finds all methods matching pattern in current instance and invokes them. Methods add Sequence into
    * SequenceRegistry. Then SequenceRegistry is injected into ServletContext
    */
   public void attributeAdded(ServletContextAttributeEvent event)
   {
	   if (event.getName().equals("TestDriverServer") && event.getValue() instanceof RemoteTestDriverServer)
       {
		   RemoteTestDriverServer remoteDriver = (RemoteTestDriverServer)event.getValue();
		   ServletContext servletContext = event.getServletContext();
		   setupRemoteDriver(servletContext, remoteDriver);
	    }
   }
   
   /**
    */
   public void setupRemoteDriver(ServletContext servletContext, RemoteTestDriverServer remoteDriver)
   {

	   testDriverSetup = true;

	   // It is only in servlet 2.5, for now we use reflection
	   String name = null;
	   try
	   {
		   Method m = ServletContext.class.getMethod("getContextPath");
		   name = (String)m.invoke(servletContext);
		   name = name.substring(1); // We remove the leading '/'
	   }
	   catch (Exception e)
	   {
		   log.error("Cannot get application context path", e);
	   }

	   //
	   if (name != null)
	   {
		   driver = new PortletTestDriver(name, remoteDriver);

		   //
		   try
		   {
			   for (Class clazz : getClasses(servletContext))
			   {
				   TestCase testCaseAnnotation = ((AnnotatedElement)clazz).getAnnotation(TestCase.class);

				   //
				   if (testCaseAnnotation != null)
				   {
					   boolean active = true;

					   //
					   for (Assertion assertion : testCaseAnnotation.value())
					   {
						   if (assertion.getStatus() instanceof Assertion.Inactive)
						   {
							   active = false;
							   log.debug("Skip test case " + clazz.getName() + " because the assertion " + assertion.getRef() + " is not enabled");
						   }
					   }

					   //
					   if (active)
					   {
						   try
						   {
							   Constructor ctor = clazz.getConstructor(PortletTestCase.class);
							   PortletTestCase portletTestCase = new PortletTestCase();
							   ctor.newInstance(portletTestCase);
							   String testCaseName = clazz.getSimpleName();
							   driver.addTestCase(testCaseName, portletTestCase);
						   }
						   catch (Exception e)
						   {
							   log.error("Cannot obtain test case constructor " + clazz, e);
						   }
					   }
				   }
				   else
				   {
					   log.error("Skip test case " + clazz.getName() + " because it is not annotated as a test case");
				   }
			   }
		   }
		   catch (IOException e)
		   {
			   log.error("Cannot get load test cases", e);
		   }

		   //
		   driver.start();

		   //
		   servletContext.setAttribute("SequenceRegistry", driver);
	   }
   }
   
   
   protected List<Class> getClasses(ServletContext ctx) throws IOException
   {
      TestCaseScanner collector = new TestCaseScanner(loader, ctx);
      collector.collect();
      return collector.classes;
   }

   private static class TestCaseScanner implements URLVisitor, URLFilter
   {

      /** . */
      private List<Class> classes = new ArrayList<Class>();

      /** . */
      private LinkedList<String> packages = new LinkedList<String>();

      /** . */
      private ClassLoader loader;

      /** . */
      private URL classesURL;

      public TestCaseScanner(ClassLoader loader, ServletContext ctx) throws MalformedURLException
      {
         this.loader = loader;

         // Need to use that because tomcat use a jndi based implementation for resources that will not
         // be accepted by the URLNavigator. 2 solutions either rely on JBoss new VFS or implement jndi
         // in URLNavigator
         this.classesURL = new File(ctx.getRealPath("/WEB-INF/classes/")).toURL();
      }

      public void collect() throws IOException
      {
         URLNavigator.visit(classesURL, this, this);
      }

      public void startDir(URL url, String name)
      {
         packages.addLast(name);
      }

      public void endDir(URL url, String name)
      {
         packages.removeLast();
      }

      public void file(URL url, String name)
      {
         StringBuffer tmp = new StringBuffer();
         Iterator<String> i = packages.iterator();

         // Skip "classes"
         i.next();

         // Iterate package names
         while (i.hasNext())
         {
            tmp.append(i.next());
            tmp.append('.');
         }

         // Append class name
         tmp.append(name.substring(0, name.length() - ".class".length()));

         //
         String classname = tmp.toString();

         //
         try
         {
            Class clazz = loader.loadClass(classname);
            classes.add(clazz);
         }
         catch (NoClassDefFoundError e)
         {
            log.error("Was not able to load class " + classname, e);
         }
         catch (ClassNotFoundException e)
         {
            log.error("Was not able to load class " + classname, e);
         }
      }

      public boolean acceptFile(URL url)
      {
         return url.getFile().endsWith("TestCase.class");
      }

      public boolean acceptDir(URL url)
      {
         return true;
      }
   }

   public void attributeRemoved(ServletContextAttributeEvent event)
   {

   }

   public void attributeReplaced(ServletContextAttributeEvent event)
   {
	   
   }

   public void contextInitialized(ServletContextEvent sce)
   {
      loader = Thread.currentThread().getContextClassLoader();
      
      // need to check if the testDriver has been already setup or not.
      // Issues exist with JBoss AS 5 in which the listeners are not started before the attribute is added
      // and thus the attributeAdded method never gets called (like it does for JBoss 4.2.x and Tomcat).
      Object testDriverServer = sce.getServletContext().getAttribute("TestDriverServer");
      if (testDriverSetup == false && testDriverServer != null && testDriverServer instanceof RemoteTestDriverServer )
      {
    	  RemoteTestDriverServer remoteDriver = (RemoteTestDriverServer)testDriverServer;
    	  ServletContext servletContext = sce.getServletContext();
    	  setupRemoteDriver(servletContext, remoteDriver);
      }
   }

   public void contextDestroyed(ServletContextEvent sce)
   {
      sce.getServletContext().removeAttribute("SequenceRegistry");

      //
      if (driver != null)
      {
         driver.stop();
         driver = null;
      }
   }
}
