package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class DefineResourcesMojo extends WOMojo {

	public DefineResourcesMojo() {
		super();
	}

	private void addResources(Resource[] resources) {
		for (int i = 0; i < resources.length; i++) {
			Resource resource = resources[i];
			this.getProject().addResource(resource);
		}
	}

	private Resource[] createResources(String directory, String targetPath) {
		String fullTargetPath = this.getFullTargetPath(targetPath);
		ArrayList resources = new ArrayList();
		Resource resource = new Resource();
		resource.setDirectory(this.getProjectFolder() + directory);
		resource.addExclude("*.lproj/**");
		resource.setTargetPath(fullTargetPath);
		resources.add(resource);
		File file = new File(this.getProjectFolder() + directory);
		String[] files = file.list();
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			if (fileName != null && fileName.endsWith(".lproj")) {
				resource = new Resource();
				resource.setDirectory(this.getProjectFolder() + directory + File.separator + fileName);
				if (fileName.equalsIgnoreCase("Nonlocalized.lproj")) {
					resource.setTargetPath(fullTargetPath);
				} else {
					resource.setTargetPath(fullTargetPath + File.separator + fileName);
				}
				resources.add(resource);
			}
		}
		return (Resource[]) resources.toArray(new Resource[resources.size()]);
	}

	private Resource createResources(String[] resourcesInclude, String[] resourcesExclude, String targetPath) {
		Resource resource = new Resource();
		resource.setDirectory(this.getProjectFolder());
		if (resourcesInclude != null) {
			for (int i = 0; i < resourcesInclude.length; i++) {
				String string = resourcesInclude[i];
				resource.addInclude(string);
			}
		}
		if (resourcesExclude != null) {
			for (int i = 0; i < resourcesExclude.length; i++) {
				String string = resourcesExclude[i];
				resource.addExclude(string);
			}
			resource.addExclude("build/**");
			resource.addExclude("dist/**");
			resource.addExclude("target/**");
		}
		String fullTargetPath = this.getFullTargetPath(targetPath);
		resource.setTargetPath(fullTargetPath);
		return resource;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Creating folder");
		this.executeCreateFolders();
		getLog().info("Defining wo resources");
		this.executeExistingComponents();
		this.executeExistingResources();
		this.executeExistingWebServerResources();
		Boolean readPatternsets = this.readPatternsets();
		if (readPatternsets != null && readPatternsets.booleanValue()) {
			this.executeResourcesPatternsetFiles();
			this.executeWebServerResourcesPatternsetFiles();
		}
		this.executeDefineProjectWonderStyleFolders();
		this.executeDefineWOStyleFolders();
	}

	private void executeCreateFolders() {
		File target = new File(getProjectFolder() + File.separator + "target" + File.separator + "classes");
		if (!target.exists()) {
			target.mkdirs();
		}
	}

	private void executeDefineProjectWonderStyleFolders() {
		getLog().debug("Defining wo resources: defining default folder WOnder style");
		String componentsPath = getProjectFolder() + "Components";
		File componentsFile = new File(componentsPath);
		if (componentsFile.exists()) {
			getLog().debug("Defining wo resources: \"Components\" folder found within project. Adding include...");
			Resource[] resourcesFromComponentsFolder = this.createResources("Components", "Resources");
			this.addResources(resourcesFromComponentsFolder);
		} else {
			getLog().debug("Defining wo resources: No \"Components\" folder found within project. Skipping include...");
		}
		String resourcesPath = getProjectFolder() + "Resources";
		File resourcesFile = new File(resourcesPath);
		if (resourcesFile.exists()) {
			getLog().debug("Defining wo resources: \"Resources\" folder found within project. Adding include...");
			Resource[] resourcesFromResourcesFolder = this.createResources("Resources", "Resources");
			this.addResources(resourcesFromResourcesFolder);
		} else {
			getLog().debug("Defining wo resources: No \"Resources\" folder found within project. Skipping include...");
		}
		String webServerResourcesPath = getProjectFolder() + "WebServerResources";
		File webServerResourcesFile = new File(webServerResourcesPath);
		if (webServerResourcesFile.exists()) {
			getLog().debug("Defining wo webserverresources: \"WebServerResources\" folder found within project. Adding include...");
			Resource[] webServerResourcesFromWebServerResourcesFolder = this.createResources("WebServerResources", "WebServerResources");
			this.addResources(webServerResourcesFromWebServerResourcesFolder);
		} else {
			getLog().debug("Defining wo webserverresources: No \"WebServerResources\" folder found within project. Skipping include...");
		}
	}

	private void executeDefineWOStyleFolders() {
		getLog().debug("Defining wo resources: defining default folder WO style");
		String resourcesPath = getProjectFolder() + "wo-resources" + File.separator + "Resources";
		File resourcesFile = new File(resourcesPath);
		if (resourcesFile.exists()) {
			getLog().debug("Defining wo resources: " + "wo-resources" + File.separator + "\"Resources\" folder found within project. Adding include...");
			Resource[] resourcesFromResourcesFolder = this.createResources("Resources", "Resources");
			this.addResources(resourcesFromResourcesFolder);
		} else {
			getLog().debug("Defining wo resources: No " + "wo-resources" + File.separator + "\"Resources\" folder found within project. Skipping include...");
		}
		String webServerResourcesPath = getProjectFolder() + "wo-resources" + File.separator + "WebServerResources";
		File webServerResourcesFile = new File(webServerResourcesPath);
		if (webServerResourcesFile.exists()) {
			getLog().debug("Defining wo webserverresources: " + "wo-resources" + File.separator + "\"WebServerResources\" folder found within project. Adding include...");
			Resource[] webServerResourcesFromWebServerResourcesFolder = this.createResources("WebServerResources", "WebServerResources");
			this.addResources(webServerResourcesFromWebServerResourcesFolder);
		} else {
			getLog().debug("Defining wo webserverresources: No " + "wo-resources" + File.separator + "\"WebServerResources\" folder found within project. Skipping include...");
		}
	}

	private void executeExistingComponents() {
		this.executePatchResources("Components", this.getFullTargetPath("Resources"));
	}

	private void executeExistingResources() {
		this.executePatchResources("WebServerResources", this.getFullTargetPath("WebServerResources"));
	}

	private void executeExistingWebServerResources() {
		this.executePatchResources("Resources", this.getFullTargetPath("Resources"));
	}

	private void executePatchResources(String existingTargetPath, String newTargetPath) {
		List list = this.getProject().getResources();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Resource resource = (Resource) iterator.next();
			if (resource.getTargetPath() != null && resource.getTargetPath().equals(existingTargetPath)) {
				getLog().info("Defining wo resources:  Patching target path of resource: " + resource);
				resource.setTargetPath(newTargetPath);
			}
		}

	}

	private void executeResourcesPatternsetFiles() {
		getLog().debug("Defining wo resources: loading patternsets");
		String woProjectFolder = getWOProjectFolder();
		if (woProjectFolder == null) {
			getLog().debug("Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		File woProjectFile = new File(woProjectFolder);
		if (woProjectFile.exists()) {
			getLog().debug("Defining wo resources: \"woproject\" folder found within project. Reading patternsets...");
		} else {
			getLog().debug("Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		String[] resourcesIncludeFromAntPatternsetFiles = this.getResourcesInclude();
		String[] resourcesExcludeFromAntPatternsetFiles = this.getResourcesExclude();
		if (resourcesIncludeFromAntPatternsetFiles != null && resourcesExcludeFromAntPatternsetFiles != null && (resourcesIncludeFromAntPatternsetFiles.length > 0 || resourcesExcludeFromAntPatternsetFiles.length > 0)) {
			Resource resourcesFromAntPatternsetFiles = this.createResources(resourcesIncludeFromAntPatternsetFiles, resourcesExcludeFromAntPatternsetFiles, "Resources");
			this.getProject().addResource(resourcesFromAntPatternsetFiles);
		}
	}

	private void executeWebServerResourcesPatternsetFiles() {
		getLog().debug("Defining wo webserverresources: loading patternsets");
		String woProjectFolder = getWOProjectFolder();
		if (woProjectFolder == null) {
			getLog().debug("Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		File woProjectFile = new File(woProjectFolder);
		if (woProjectFile.exists()) {
			getLog().debug("Defining wo webserverresources: \"woproject\" folder found within project. Reading patternsets...");
		} else {
			getLog().debug("Defining wo webserverresources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		String[] webserverResourcesIncludeFromAntPatternsetFiles = this.getWebserverResourcesInclude();
		String[] webserverResourcesExcludeFromAntPatternsetFiles = this.getWebserverResourcesExclude();
		if (webserverResourcesIncludeFromAntPatternsetFiles != null && webserverResourcesExcludeFromAntPatternsetFiles != null && (webserverResourcesIncludeFromAntPatternsetFiles.length > 0 || webserverResourcesExcludeFromAntPatternsetFiles.length > 0)) {
			Resource webserverResourcesFromAntPatternsetFiles = this.createResources(webserverResourcesIncludeFromAntPatternsetFiles, webserverResourcesExcludeFromAntPatternsetFiles, "WebServerResources");
			this.getProject().addResource(webserverResourcesFromAntPatternsetFiles);
		}
	}

	private String getFullTargetPath(String targetPath) {
		String fullTargetPath = "../" + this.getProject().getArtifactId();
		if (this.includesVersionInArtifactName()) {
			fullTargetPath = fullTargetPath + "-" + this.getProject().getVersion();
		}
		fullTargetPath = fullTargetPath + "." + getProductExtension();
		if (this.hasContentsFolder()) {
			fullTargetPath = fullTargetPath + File.separator + "Contents";
		}
		fullTargetPath = fullTargetPath + File.separator + targetPath;
		return fullTargetPath;
	}

	private String[] getResourcesExclude() {
		String patternsetFileName = "resources.exclude.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String[] getResourcesInclude() {
		String patternsetFileName = "resources.include.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String[] getWebserverResourcesExclude() {
		String patternsetFileName = "wsresources.exclude.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String[] getWebserverResourcesInclude() {
		String patternsetFileName = "wsresources.include.patternset";
		return this.readPatternset(patternsetFileName);
	}

	public abstract boolean hasContentsFolder();

	public abstract boolean includesVersionInArtifactName();

	private String[] readPatternset(String patternsetFileName) {
		getLog().info("Defining wo resources: loading \"" + patternsetFileName + "\"");

		String woProjectFolder = this.getWOProjectFolder();
		File file = new File(woProjectFolder + File.separator + patternsetFileName);
		if (!file.exists()) {
			return null;
		}
		PatternsetReader patternsetReader;
		String[] pattern = null;
		try {
			patternsetReader = new PatternsetReader(file);
			pattern = patternsetReader.getPattern();
		} catch (IOException e) {
			getLog().info("Defining wo resources: exception while loading \"" + patternsetFileName + "\"", e);
		}
		return pattern;
	}

	protected abstract Boolean readPatternsets();
}