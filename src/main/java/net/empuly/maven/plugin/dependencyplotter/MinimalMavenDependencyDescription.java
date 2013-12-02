package net.empuly.maven.plugin.dependencyplotter;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MinimalMavenDependencyDescription {

	private final String groupId;
	private final String artifactId;

	public MinimalMavenDependencyDescription(String groupId, String artifactId) {
		checkArgument(StringUtils.isNotBlank(groupId));
		checkArgument(StringUtils.isNotBlank(artifactId));
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	@Override
	public final boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public final int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
