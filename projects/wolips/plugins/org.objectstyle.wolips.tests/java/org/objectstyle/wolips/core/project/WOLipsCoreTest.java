/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.core.project;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author uli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class WOLipsCoreTest extends TestCase {

	private IWorkspace workspace = null;
	/**
	 * Constructor for CheckWorkspaceTest.
	 * @param arg0
	 */
	public WOLipsCoreTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		workspace = ResourcesPlugin.getWorkspace();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		workspace = null;
	}

	public void testCreateProjectNull() {
		CoreException coreException = null;
		try {
			IWOLipsProject project = WOLipsCore.createProject(null);
		} catch (CoreException e) {
			coreException = e;
		}
		assertNotNull(
			"WOLipsCore.createProject(null) should throw an exception",
			coreException);
	}

	public void testCreateJavaProjectNull() {
		CoreException coreException = null;
		try {
			IWOLipsJavaProject project = WOLipsCore.createJavaProject(null);
		} catch (CoreException e) {
			coreException = e;
		}
		assertNotNull(
			"WOLipsCore.createJavaProject(null) should throw an exception",
			coreException);
	}

	public void testCreateProject() {
		CoreException coreException = null;
		String projectName = "Foo";
		IProject project =
			ResourcesPlugin.getWorkspace().getRoot().getProject(
		projectName);
		assertNotNull("project handle should not be null", project);
		try {
			IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription("Foo");
			assertNotNull("projectDescription should not be null", projectDescription);
		projectDescription.setLocation(project.getLocation());
			//org.objectstyle.wolips.antapplicationnature
			projectDescription.setNatureIds(new String[0]);
			String[] natures = projectDescription.getNatureIds();
			assertNotNull("natures should not be null", natures);
			assertEquals("expect one nature", natures.length, 1);
			if (!project.exists()) {
				// set description only in this way
				// to ensure project location is set
				project.create(
				projectDescription,
					null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}
			IWOLipsProject wolipsProject = WOLipsCore.createProject(project);
			assertNotNull("wolipsProject should not be null", wolipsProject);
			IWOLipsProjectTest.PROJECT = project;
		} catch (CoreException e) {
			coreException = e;
		}
		assertNull(
			"project creation should not throw an exception",
			coreException);

	}
	
	public void testCreateJavaProject() {
		CoreException coreException = null;
		String projectName = "FooJava";
		IProject project =
			ResourcesPlugin.getWorkspace().getRoot().getProject(
		projectName);
		assertNotNull("project handle should not be null", project);
		try {
			IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription("Foo");
			assertNotNull("projectDescription should not be null", projectDescription);
		projectDescription.setLocation(project.getLocation());
			//org.objectstyle.wolips.antapplicationnature
			projectDescription.setNatureIds(new String[] {"org.eclipse.jdt.core.javanature"});
			String[] natures = projectDescription.getNatureIds();
			assertNotNull("natures should not be null", natures);
			assertEquals("expect one nature", natures.length, 1);
			if (!project.exists()) {
				// set description only in this way
				// to ensure project location is set
				project.create(
				projectDescription,
					null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}
			IJavaProject javaProject = JavaCore.create(project);
			assertNotNull("javaProject should not be null", javaProject);
			IWOLipsJavaProject wolipsJavaProject = WOLipsCore.createJavaProject(javaProject);
			assertNotNull("wolipsJavaProject should not be null", wolipsJavaProject);
			IWOLipsJavaProjectTest.PROJECT = javaProject;
		} catch (CoreException e) {
			coreException = e;
		}
		assertNull(
			"project creation should not throw an exception",
			coreException);

	}
	
}
