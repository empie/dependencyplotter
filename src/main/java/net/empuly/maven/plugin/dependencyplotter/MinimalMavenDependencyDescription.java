package net.empuly.maven.plugin.dependencyplotter;

public class MinimalMavenDependencyDescription {

	private final String groupId;
	private final String artifactId;

	public MinimalMavenDependencyDescription(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}
}
