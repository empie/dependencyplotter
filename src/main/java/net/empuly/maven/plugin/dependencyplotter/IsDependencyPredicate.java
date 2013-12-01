package net.empuly.maven.plugin.dependencyplotter;

import org.apache.maven.artifact.Artifact;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class IsDependencyPredicate implements Predicate<Artifact> {

	private final MinimalMavenDependencyDescription dependencyToFind;

	public IsDependencyPredicate(MinimalMavenDependencyDescription dependencyToFind) {
		Preconditions.checkNotNull(dependencyToFind);
		this.dependencyToFind = dependencyToFind;
	}

	public boolean apply(Artifact input) {
		return input.getGroupId().equals(dependencyToFind.getGroupId()) && input.getArtifactId().equals(dependencyToFind.getGroupId());
	}

}
