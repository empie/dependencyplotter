package net.empuly.maven.plugin.dependencyplotter;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;

import com.google.common.base.Predicate;

public class ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate
		implements Predicate<Artifact> {

	private final MinimalMavenDependencyDescription minimalMavenDependencyDescription;

	public ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate(
			MinimalMavenDependencyDescription minimalMavenDependencyDescription) {
		checkNotNull(minimalMavenDependencyDescription);
		this.minimalMavenDependencyDescription = minimalMavenDependencyDescription;
	}

	public boolean apply(Artifact artifact) {
		checkNotNull(artifact);
		String artifactId = artifact.getArtifactId();
		String groupId = artifact.getGroupId();

		boolean matchOpGroupId;
		if ("*".equals(minimalMavenDependencyDescription.getGroupId())) {
			matchOpGroupId = true;
		} else {
			matchOpGroupId = StringUtils.contains(groupId,
					minimalMavenDependencyDescription.getGroupId());
		}

		boolean matchOpArtifactId;
		if ("*".equals(minimalMavenDependencyDescription.getArtifactId())) {
			matchOpArtifactId = true;
		} else {
			matchOpArtifactId = StringUtils.contains(artifactId,
					minimalMavenDependencyDescription.getArtifactId());
		}
		return matchOpGroupId && matchOpArtifactId;
	}

}
