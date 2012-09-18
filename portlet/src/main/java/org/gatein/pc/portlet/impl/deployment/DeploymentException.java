package org.gatein.pc.portlet.impl.deployment;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class DeploymentException extends Exception
{

   public DeploymentException()
   {
   }

   public DeploymentException(String message)
   {
      super(message);
   }

   public DeploymentException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DeploymentException(Throwable cause)
   {
      super(cause);
   }
}
