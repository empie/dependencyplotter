package net.empuly.maven.plugin.dependencyplotter;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class ArtifactIsScopePredicate implements Predicate<Artifact> {

	private static Set<String> MAVEN_SCOPES = Sets.newHashSet(Artifact.SCOPE_COMPILE, Artifact.SCOPE_PROVIDED, Artifact.SCOPE_RUNTIME,
			Artifact.SCOPE_TEST);

	private final String scopeToFilter;

	public ArtifactIsScopePredicate(String scopeToFilter) {
		Preconditions.checkArgument(StringUtils.isNotBlank(scopeToFilter));
		Preconditions.checkArgument(MAVEN_SCOPES.contains(scopeToFilter));
		this.scopeToFilter = scopeToFilter;
	}

	public boolean apply(Artifact artifact) {
		Preconditions.checkNotNull(artifact);
		return scopeToFilter.equals(artifact.getScope());
	}

}
