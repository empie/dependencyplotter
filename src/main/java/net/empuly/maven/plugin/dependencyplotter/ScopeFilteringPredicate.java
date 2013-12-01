package net.empuly.maven.plugin.dependencyplotter;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class ScopeFilteringPredicate implements Predicate<Artifact> {

	private final String scopeToFilter;

	public ScopeFilteringPredicate(String scopeToFilter) {
		Preconditions.checkArgument(StringUtils.isNotBlank(scopeToFilter));
		this.scopeToFilter = scopeToFilter;
	}

	public boolean apply(Artifact artifact) {
		Preconditions.checkNotNull(artifact);
		return scopeToFilter.equals(artifact.getScope());
	}

}
