/* ====================================================================
*
* The ObjectStyle Group Software License, Version 1.0
*
* Copyright (c) 2004 The ObjectStyle Group
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

package org.objectstyle.wolips.workbenchutilities;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.commons.logging.PluginLogger;

/**
 * The main plugin class to be used in the desktop.
 */
public class WorkbenchUtilitiesPlugin extends AbstractUIPlugin {
	private static final String PLUGIN_ID = "org.objectstyle.wolips.workbenchutilities";
	//The shared instance.
	private static WorkbenchUtilitiesPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	private PluginLogger pluginLogger = new PluginLogger(WorkbenchUtilitiesPlugin.PLUGIN_ID, false);
	
	/**
	 * The constructor.
	 */
	public WorkbenchUtilitiesPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle   = ResourceBundle.getBundle("org.objectstyle.wolips.workbenchutilities.WorkbenchutilitiesPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static WorkbenchUtilitiesPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = WorkbenchUtilitiesPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Returns the PluginID.
	 * @return
	 */
	public static String getPluginId() {
		if (plugin != null) {
			return getDefault().getDescriptor().getUniqueIdentifier();
		} else
			return WorkbenchUtilitiesPlugin.PLUGIN_ID;
	}

	/**
	 * Prints a Status.
	 * @param e
	 */
	public static void log(IStatus status) {
		WorkbenchUtilitiesPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Prints a Throwable.
	 * @param e
	 */
	public static void log(Throwable e) {
		WorkbenchUtilitiesPlugin.log(new Status(IStatus.ERROR, WorkbenchUtilitiesPlugin.getPluginId(), IStatus.ERROR, "Internal Error", e)); //$NON-NLS-1$
	}

	/**
	 * Utility method with conventions
	 */
	public final static void errorDialog(
		Shell shell,
		String title,
		String message,
		IStatus s) {
		WorkbenchUtilitiesPlugin.log(s);
		// if the 'message' resource string and the IStatus' message are the same,
		// don't show both in the dialog
		if (s != null && message.equals(s.getMessage())) {
			message = null;
		}
		ErrorDialog.openError(shell, title, message, s);
	}

	/**
	 * Utility method with conventions
	 */
	public final static void errorDialog(
		Shell shell,
		String title,
		String message,
		Throwable t) {
		WorkbenchUtilitiesPlugin.log(t);
		IStatus status;
		if (t instanceof CoreException) {
			status = ((CoreException) t).getStatus();
			// if the 'message' resource string and the IStatus' message are the same,
			// don't show both in the dialog
			if (status != null && message.equals(status.getMessage())) {
				message = null;
			}
		} else {
			status = new Status(IStatus.ERROR, WorkbenchUtilitiesPlugin.getPluginId(), IStatus.ERROR, "Error within Debug UI: ", t); //$NON-NLS-1$	
		}
		ErrorDialog.openError(shell, title, message, status);
	}

	/**
			 * Method projectISReferencedByProject.
			 * @param child
			 * @param mother
			 * @return boolean
			 */
	private static boolean projectISReferencedByProject(
		IProject child,
		IProject mother) {
		IProject[] projects = null;
		try {
			if(!mother.isOpen() || !mother.isAccessible())
				//return maybe;
				return false;
			projects = mother.getReferencedProjects();
		} catch (Exception anException) {
			WorkbenchUtilitiesPlugin.log(anException);
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].equals(child))
				return true;
		}
		return false;
	}

	public final static List findResourcesInProjectByNameAndExtensions(
		IProject project,
		String name,
		String[] extensions,
		boolean includesReferencedProjects) {
		if (includesReferencedProjects) {
			IProject[] projects =
				WorkbenchUtilitiesPlugin.getWorkspace().getRoot().getProjects();
			ArrayList referencedProjects = new ArrayList();
			for (int i = 0; i < projects.length; i++) {
				if (WorkbenchUtilitiesPlugin
					.projectISReferencedByProject(projects[i], project)
					|| WorkbenchUtilitiesPlugin.projectISReferencedByProject(
						project,
						projects[i]))
					referencedProjects.add(projects[i]);
			}
			int numReferencedProjects = referencedProjects.size();
			IProject[] searchScope = new IProject[numReferencedProjects + 1];
			for (int i = 0; i < numReferencedProjects; i++) {
				searchScope[i] = (IProject) referencedProjects.get(i);
			}
			searchScope[numReferencedProjects] = project;
			return WorkbenchUtilitiesPlugin
				.findResourcesInResourcesByNameAndExtensions(
				searchScope,
				name,
				extensions);
		}
		IProject[] searchScope = new IProject[1];
		searchScope[0] = project;
		return WorkbenchUtilitiesPlugin.findResourcesInResourcesByNameAndExtensions(
			searchScope,
			name,
			extensions);
	}

	public final static List findResourcesInResourcesByNameAndExtensions(
		IResource[] resources,
		String name,
		String[] extensions) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < resources.length; i++)
			list.addAll(
					WorkbenchUtilitiesPlugin.findResourcesInResourceByNameAndExtensions(
					resources[i],
					name,
					extensions));
		return list;
	}

	public final static List findResourcesInResourceByNameAndExtensions(
		IResource resource,
		String name,
		String[] extensions) {
		ArrayList list = new ArrayList();
		if ((resource != null)) {
			if ((resource instanceof IContainer)
				|| (resource instanceof IProject)) {
				for (int i = 0; i < extensions.length; i++) {
					IResource foundResource =
						((IContainer) resource).findMember(
							name + "." + extensions[i]);
					if ((foundResource != null))
						list.add(foundResource);
				}
				IResource[] members = WorkbenchUtilitiesPlugin.members(resource);
				WorkbenchUtilitiesPlugin
					.findResourcesInResourceByNameAndExtensionsAndAddToArrayList(
					members,
					name,
					extensions,
					list);
			}
		}
		return list;
	}

	private final static void findResourcesInResourceByNameAndExtensionsAndAddToArrayList(
		IResource[] resources,
		String name,
		String[] extensions,
		ArrayList list) {
		for (int i = 0; i < resources.length; i++) {
			IResource resource = resources[i];
			if ((resource != null)
				&& (resource instanceof IContainer)
				&& (!resource.toString().endsWith(".framework"))
				&& (!resource.toString().endsWith(".woa"))) {
				if ((resource != null)) {
					if ((resource instanceof IContainer)
						|| (resource instanceof IProject)) {
						for (int j = 0; j < extensions.length; j++) {
							IResource foundResource =
								((IContainer) resource).findMember(
									name + "." + extensions[j]);
							if ((foundResource != null))
								list.add(foundResource);
						}
						IResource[] members =
							WorkbenchUtilitiesPlugin.members(resource);
						WorkbenchUtilitiesPlugin
							.findResourcesInResourceByNameAndExtensionsAndAddToArrayList(
							members,
							name,
							extensions,
							list);
					}
				}
			}
		}
	}
	/**
	 * Method findFilesInResourceByName.
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 */
	public final static void findFilesInResourceByName(
		ArrayList anArrayList,
		IResource aResource,
		String aFileName) {
		if ((aResource != null)) {
			if ((aResource instanceof IContainer)
				|| (aResource instanceof IProject)) {
				IResource resource =
					((IContainer) aResource).findMember(aFileName);
				if ((resource != null) && (resource instanceof IFile))
					anArrayList.add(resource);
				IResource[] members = WorkbenchUtilitiesPlugin.members(aResource);
				WorkbenchUtilitiesPlugin.findFilesInResourceByName(
					anArrayList,
					members,
					aFileName);
			}
		}
	}
	/**
	 * Method findFilesInResourceByName.
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 */
	private final static void findFilesInResourceByName(
		ArrayList anArrayList,
		IResource[] aResource,
		String aFileName) {
		for (int i = 0; i < aResource.length; i++) {
			IResource memberResource = aResource[i];
			if ((memberResource != null)
				&& (memberResource instanceof IContainer)
				&& (!memberResource.toString().endsWith(".framework"))
				&& (!memberResource.toString().endsWith(".woa")))
				WorkbenchUtilitiesPlugin.findFilesInResourceByName(
					anArrayList,
					memberResource,
					aFileName);
		}
	}
	/**
	 * Returns the ActiveEditor.
	 * @return IEditorPart
	 */
	public final static IEditorPart getActiveEditor() {
		IWorkbenchPage page = WorkbenchUtilitiesPlugin.getActivePage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}
	/**
	 * Method getEditorInput.
	 * @return IEditorInput
	 */
	public final static IEditorInput getActiveEditorInput() {
		IEditorPart part = WorkbenchUtilitiesPlugin.getActiveEditor();
		if (part != null) {
			return part.getEditorInput();
		}
		return null;
	}
	/**
	 * @return Returns the active page.
	 */
	public final static IWorkbenchPage getActivePage() {
		return WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow().getActivePage();
	}
	/**
	 * @return Returns the active workbench shell.
	 */
	public final static Shell getActiveWorkbenchShell() {
		return WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow().getShell();
	}
	/**
	 * @return Returns the the active workbench window.
	 */
	public final static IWorkbenchWindow getActiveWorkbenchWindow() {
		return WorkbenchUtilitiesPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Method getShell.
	 * @return Shell
	 */
	public final static Shell getShell() {
		if (WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow() != null) {
			return WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow().getShell();
		}
		return null;
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public final static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	/**
	 * Method members.
	 * @param aResource
	 * @return IResource[]
	 */
	private final static IResource[] members(IResource aResource) {
		IResource[] members = null;
		try {
			members = ((IContainer) aResource).members();
		} catch (Exception anException) {
			WorkbenchUtilitiesPlugin.log(anException);
		}
		return members;
	}
	/**
	 * Method open.
	 * @param anArrayList
	 */
	public final static void open(ArrayList anArrayList) {
		for (int i = 0; i < anArrayList.size(); i++) {
			IResource resource = (IResource) anArrayList.get(i);
			if ((resource != null) && (resource.getType() == IResource.FILE))
				WorkbenchUtilitiesPlugin.open((IFile) resource);
		}
	}
	/**
	 * Method open.
	 * @param file The file to open.
	 */
	public final static void open(IFile file) {
		WorkbenchUtilitiesPlugin.open(file, false, null);
	}

	/**
	 * Method open.
	 * @param file The file to open.
	 * @param If forceToOpenIntextEditor is set to true the resource opens in a texteditor.
	 */
	public final static void open(
		IFile file,
		boolean forceToOpenIntextEditor,
		String editor) {
		IWorkbenchWindow workbenchWindow =
			WorkbenchUtilitiesPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					IEditorDescriptor editorDescriptor =
						WorkbenchUtilitiesPlugin
					.getDefault()
					.getWorkbench()
					.getEditorRegistry()
					.getDefaultEditor(
							file.getName());
					if (forceToOpenIntextEditor) {
						workbenchPage.openEditor(new FileEditorInput(file), editor);
						WorkbenchUtilitiesPlugin
							.getDefault()
							.getWorkbench()
							.getEditorRegistry()
							.setDefaultEditor(
							file.getName(),
							editorDescriptor.getId());
					} else
						workbenchPage.openEditor(new FileEditorInput(file), editorDescriptor.getId());
				} catch (Exception anException) {
					WorkbenchUtilitiesPlugin.log(anException);
				}
			}
		}
	}
	/**
	 * Method handleException.
	 * 
	 * @param shell
	 * @param target
	 * @param message
	 */
	public static void handleException(Shell shell, Throwable target,
			String message) {
		WorkbenchUtilitiesPlugin.getDefault().getPluginLogger().debug(target);
		String title = "Error";
		if (message == null) {
			message = target.getMessage();
		}
		if (target instanceof CoreException) {
			IStatus status = ((CoreException) target).getStatus();
			ErrorDialog.openError(shell, title, message, status);
			//WOLipsLog.log(status);
		} else {
			MessageDialog.openError(shell, title, target.getMessage());
			//WOLipsLog.log(target);
		}
		WorkbenchUtilitiesPlugin.getDefault().getPluginLogger().log(message, target);
	}
	
	/**
	 * @return Returns the pluginLogger.
	 */
	public PluginLogger getPluginLogger() {
		return pluginLogger;
	}
}
