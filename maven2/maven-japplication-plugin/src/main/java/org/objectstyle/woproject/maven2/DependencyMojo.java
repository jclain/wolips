/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group,
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
package org.objectstyle.woproject.maven2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * @author andrus
 */
// andrus, 9/28/2006: This is a clone of
// org.codehaus.mojo.dependency.AbstractFromDependenciesMojo
// from "maven-dependency-plugin". Inheriting from that class didn't
// work, so I had to copy the class contents
public abstract class DependencyMojo extends AbstractMojo {

	/**
	 * Contains the full list of projects in the reactor.
	 * 
	 * @parameter expression="${reactorProjects}"
	 * @required
	 * @readonly
	 */
	private List reactorProjects;

	/**
	 * Creates a Map of artifacts within the reactor using the
	 * groupId:artifactId:classifer:version as key
	 * 
	 * @return A HashMap of all artifacts available in the reactor
	 */
	protected Map getMappedReactorArtifacts() {
		Map mappedReactorArtifacts = new HashMap();

		for (Iterator i = reactorProjects.iterator(); i.hasNext();) {
			MavenProject reactorProject = (MavenProject) i.next();

			String key = reactorProject.getGroupId() + ":" + reactorProject.getArtifactId() + ":" + reactorProject.getVersion();

			mappedReactorArtifacts.put(key, reactorProject.getArtifact());
		}

		return mappedReactorArtifacts;
	}

	/**
	 * Retrieves all artifact dependencies within the reactor.
	 * 
	 * @return A set of artifacts that are the dependencies of a given root
	 *         artifact.
	 */
	protected Set getDependencies() {

		Map reactorArtifacts = getMappedReactorArtifacts();
		Map dependencies = new HashMap();

		for (Iterator i = reactorProjects.iterator(); i.hasNext();) {
			MavenProject reactorProject = (MavenProject) i.next();

			for (Iterator j = reactorProject.getArtifacts().iterator(); j.hasNext();) {
				Artifact artifact = (Artifact) j.next();

				String key = artifact.getId();

				if (!reactorArtifacts.containsKey(key) && !dependencies.containsKey(key)) {
					dependencies.put(key, artifact);
				}
			}
		}

		return new HashSet(dependencies.values());
	}
}