/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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
 
 package org.objectstyle.wolips.target;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.objectstyle.cayenne.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.core.project.ProjectHelper;
/**
 * @author uwe
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TargetBuilderNature implements IProjectNature
{
	public static String ID = "org.objectstyle.wolips.targetbuilder.targetbuildernature";
	public static String NAME = "name";
	public static String OUTPUT = "output";
	public static String SOURCE = "source";
	public static String PROJECTCLASSPATH = "projectclasspath";
	public static String CLASSPATH = "classpath";
	public static String TARGETS = "targets";
	public static String TARGETFILE = "targets.plist";

	private IProject _project;
	private List _buildTargets;

	public TargetBuilderNature()
	{
		super();
		_buildTargets = new ArrayList();
	}

	public IJavaProject javaProject()
	{
		return JavaCore.create(getProject());
	}

	public List targets()
	{
		return _buildTargets;
	}

	public IPath filePath()
	{
		return getProject().getFile(TargetBuilderNature.TARGETFILE).getLocation();
	}

	public void synchronizeWithFile() throws CoreException
	{
		Map targetMap;

		IJavaProject javaProject = javaProject();

		_buildTargets = new ArrayList();
		File targetFile = filePath().toFile();
		try
		{
			targetMap = (Map) PropertyListSerialization.propertyListFromFile(targetFile);
		}
		catch (Exception e)
		{
			System.out.println("Could not find targets.plist file for Project:" + getProject() + "!");
			return;
		}

		if (targetMap == null)
		{
			Status status = new Status(IStatus.ERROR, "org.objectstyle.wolips.target", IStatus.OK,
					"look at system console", null);
			ErrorDialog.openError(null, "Error", "Parse Error while parsing targets.plist", status);
			return;
		}

		List targets = (List) targetMap.get(TargetBuilderNature.TARGETS);
		for (int i = 0; i < targets.size(); i++)
		{
			BuildTarget buildTarget = new BuildTarget((Map) targets.get(i));
			_buildTargets.add(buildTarget);
		}
		synchronizeProjectClassPath();
	}

	public IClasspathEntry[] classPathEntries()
	{
		ArrayList classPathEntries = new ArrayList();

		for (Iterator iter = _buildTargets.iterator(); iter.hasNext();)
		{
			BuildTarget element = (BuildTarget) iter.next();
			IClasspathEntry[] tmp = element.classPathEntries();
			for (int i = 0; i < tmp.length; i++)
			{
				if (!classPathEntries.contains(tmp[i]))
					classPathEntries.add(tmp[i]);
			}
		}

		IClasspathEntry[] result = new IClasspathEntry[classPathEntries.size()];
		for (int i = 0; i < classPathEntries.size(); i++)
			result[i] = (IClasspathEntry) classPathEntries.get(i);

		return result;
	}

	public void synchronizeProjectClassPath()
	{
		try
		{
			javaProject().setRawClasspath(classPathEntries(), null);
		}
		catch (JavaModelException e)
		{
			System.out.println("synchronizeProjectClassPath:" + e);
		}
	}

	public void configure() throws CoreException
	{
		int installPosition;
		
		// Add nature-specific information
		// for the project, such as adding a builder
		// to a project's build spec.*/
		IProject project = getProject();
		IJavaProject javaProject = JavaCore.create(project);
		
		installPosition = ProjectHelper.positionForBuilder(project, "org.eclipse.jdt.core.javabuilder");
		if(installPosition == ProjectHelper.NotFound)
			installPosition = ProjectHelper.positionForBuilder(project, TargetBuilder.ID);
		if(installPosition == ProjectHelper.NotFound)
			installPosition = 0;
			
		ProjectHelper.removeBuilder(project, "org.eclipse.jdt.core.javabuilder");
		ProjectHelper.removeBuilder(project, TargetBuilder.ID);
		synchronizeWithFile();
		
		ProjectHelper.installBuilderAtPosition(project, TargetBuilder.ID, installPosition, null);
	}
		
	public void deconfigure() throws CoreException
	{
		int installPosition;
		
		installPosition = ProjectHelper.positionForBuilder(getProject(), TargetBuilder.ID);
		if(installPosition == ProjectHelper.NotFound)
			return;
		ProjectHelper.removeBuilder(getProject(), TargetBuilder.ID);
		ProjectHelper.installBuilderAtPosition(getProject(), "org.eclipse.jdt.core.javabuilder", installPosition, null);
	}

	public IProject getProject()
	{
		return _project;
	}

	public void setProject(IProject value)
	{
		_project = value;
	}

	public BuildTarget getTarget(String name)
	{
		for (Iterator iter = _buildTargets.iterator(); iter.hasNext();)
		{
			BuildTarget element = (BuildTarget) iter.next();
			if (element.name().equals(name))
				return element;
		}
		return null;
	}

	public static void add(IProject project) throws CoreException
	{
		if (project.hasNature(TargetBuilderNature.ID))
			return;
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = TargetBuilderNature.ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	public static void remove(IProject project) throws CoreException
	{
		if (!project.hasNature(TargetBuilderNature.ID))
			return;
		IProjectDescription description = project.getDescription();
		ArrayList natureList = new ArrayList();
		natureList.addAll(Arrays.asList(description.getNatureIds()));
		for (int i = 0; i < natureList.size(); i++)
			if (natureList.get(i).equals(TargetBuilderNature.ID))
				natureList.remove(i);
		String[] newNatures = new String[natureList.size()];
		for (int i = 0; i < natureList.size(); i++)
			newNatures[i] = (String) natureList.get(i);
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	public static void update(IProject project) throws CoreException
	{
		remove(project);
		add(project);
	}
}
