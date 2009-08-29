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
package javax.portlet;

/**
 * The portlet should throw the <CODE>UnavailableException</CODE> when the portlet is either temporarily or permanently
 * unavailable to handle requests.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public class UnavailableException extends PortletException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -7461927853991648173L;

   private boolean permanent;
   private int unavailableSeconds;

   /**
    * Constructs a new exception with a descriptive message indicating that the portlet is permanently unavailable.
    *
    * @param text a <code>String</code> specifying the descriptive message
    */
   public UnavailableException(String text)
   {
      super(text);
      this.unavailableSeconds = 0;
      this.permanent = true;
   }

   /**
    * Constructs a new exception with a descriptive message indicating that the portlet is temporarily unavailable and
    * giving an estimate of how long it will be unavailable.
    * <p/>
    * <p>In some cases, the portlet cannot make an estimate. For example, the portlet might know that a server it needs
    * is not running, but it might not be able to report how long it will take to be restored to functionality. This can
    * be indicated with a negative or zero value for the <code>seconds</code> argument.
    *
    * @param text               a <code>String</code> specifying the descriptive message. This message can be written to
    *                           a log file or displayed for the user.
    * @param unavailableSeconds an integer specifying the number of seconds for which the portlet expects to be
    *                           unavailable; if this is zero or negative, it indicates that the portlet cannot make an
    *                           estimate.
    */
   public UnavailableException(String text, int unavailableSeconds)
   {
      super(text);
      this.unavailableSeconds = unavailableSeconds;
      this.permanent = false;
   }

   /**
    * Returns the time in seconds for which the portlet can be expected to be unavailable.
    * <p/>
    * If the portlet is called again while it is still unavailable, it indicates the same time estimate. No effort is
    * made to correct for the time elapsed since the exception was first reported.
    * <p/>
    * If this method returns zero or a negative number, the portlet is permanently unavailable or cannot provide an
    * estimate of how long it will be unavailable.
    *
    * @return an integer specifying the number of seconds the portlet will be temporarily unavailable, or zero or a
    * negative number if the portlet is permanently unavailable or cannot make an estimate.
    */
   public int getUnavailableSeconds()
   {
      return unavailableSeconds;
   }

   /**
    * Returns a <code>boolean</code> indicating whether the portlet is permanently unavailable. If so, something is
    * wrong with the portlet, and the system administrator must take some corrective action.
    *
    * @return      <code>true</code> if the portlet is permanently unavailable; <code>false</code> if the portlet is
    * temporarily unavailable.
    */
   public boolean isPermanent()
   {
      return permanent;
   }
}
