/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.portlet;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Determine version information from the artifact metadata. When the version cannot be determined, the
 * version <code>0.0.0-GA</code> is used.</p>
 *
 *
 * <p>This class only uses classes provided by the Java Platform in order to be used without any further dependency.
 * Information is logged through {@link java.util.logging.Logger} on purpose instead of any other logger.</p>
 *
 * <p>This class provides a main class that can be executed to check the version manually (hence the reason
 * it does not depend on any other class than the Java Platform):</p>
 *
 * <code>java -cp target/pc-portlet-2.4.1.CR03-SNAPSHOT.jar org.gatein.pc.portlet.Version</code>
 *
 *  @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class Version
{

   /** Major and minor extracted. */
   private static final Pattern MAJOR_MINOR_PATTERN = Pattern.compile("^(\\d)+\\.(\\d)+");

   /** . */
   public static final String VALUE;

   /** . */
   public static final int MAJOR;

   /** . */
   public static final int MINOR;

   static
   {
      String value = "0.0.0-GA";
      int major = 0;
      int minor = 0;
      try
      {
         Properties props = new Properties();
         InputStream in = Version.class.getResourceAsStream("/META-INF/maven/org.gatein.pc/pc-portlet/pom.properties");
         if (in != null)
         {
            props.load(in);
            String version = props.getProperty("version");
            if (version != null)
            {
               version = version.trim();
               if (version.length() > 0)
               {
                  value = version.trim();
                  Matcher matcher = MAJOR_MINOR_PATTERN.matcher(value);
                  if (matcher.find())
                  {
                     major = Integer.parseInt(matcher.group(1));
                     minor = Integer.parseInt(matcher.group(2));
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         Logger.getLogger(Version.class.getName()).log(Level.WARNING, "Could not load version from maven", e);
      }
      VALUE = value;
      MAJOR = major;
      MINOR = minor;
   }

   public static void main(String[] args)
   {
      System.out.println("version=" + VALUE);
      System.out.println("major=" + MAJOR);
      System.out.println("minor=" + MINOR);
   }
}
