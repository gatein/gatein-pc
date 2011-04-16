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

package org.gatein.pc.test.unit.reports;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

import java.util.Collection;
import java.util.Set;
import java.util.Collections;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class TestCaseAPF implements AnnotationProcessorFactory
{
   public Collection<String> supportedOptions()
   {
      return Collections.emptyList();
   }

   public Collection<String> supportedAnnotationTypes()
   {
      return Collections.singletonList("org.jboss.portal.unit.annotations.TestCase");
   }

   public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> annotationTypeDeclarations, AnnotationProcessorEnvironment annotationProcessorEnvironment)
   {
      AnnotationProcessor result;
      if(annotationTypeDeclarations.isEmpty()) {
         result = AnnotationProcessors.NO_OP;
      }
      else {
         // Next Step - implement this class:
         result = new TestCaseAnnotationProcessor(annotationProcessorEnvironment);
         annotationProcessorEnvironment.addListener(new ResultProducingListener());
      }
      return result;

   }


}
