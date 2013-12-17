package net.empuly.maven.plugin.dependencyplotter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class DependencyPlotterProjectDependencyAnalysis {

	private ProjectDependencyAnalysis projectDependencyAnalysis;
	private final Log logger;

	public DependencyPlotterProjectDependencyAnalysis(ProjectDependencyAnalysis projectDependencyAnalysis, Log logger) {
		this.projectDependencyAnalysis = projectDependencyAnalysis;
		this.logger = logger;
	}

	public Set<Artifact> getUnusedButDeclaredArtifacts() {
		return projectDependencyAnalysis.getUnusedDeclaredArtifacts();
	}

	public Set<Artifact> getUsedAndDeclaredArtifacts() {
		return projectDependencyAnalysis.getUsedDeclaredArtifacts();
	}

	public Set<Artifact> getUsedButUndeclaredArtifacts() {
		return projectDependencyAnalysis.getUsedUndeclaredArtifacts();
	}

	public void filterDependenciesBasedOnConfiguration(DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		filterOnScopes(dependencyPlotterConfiguration);
		filterOnExclusions(dependencyPlotterConfiguration);
	}

	private void filterOnScopes(DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		if (dependencyPlotterConfiguration.ignoreAllButCompileDependenciesDuringAnalyzation()) {
			filterAllButCompileScope();
		} else {
			filterOnIndividualScopes(dependencyPlotterConfiguration);
		}
	}

	private void filterAllButCompileScope() {
		filterAllDependenciesInScope(Artifact.SCOPE_PROVIDED);
		filterAllDependenciesInScope(Artifact.SCOPE_RUNTIME);
		filterAllDependenciesInScope(Artifact.SCOPE_TEST);
	}

	private void filterOnIndividualScopes(DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		if (dependencyPlotterConfiguration.ignoreCompileDependenciesDuringAnalyzation()) {
			filterAllDependenciesInScope(Artifact.SCOPE_COMPILE);
		}

		if (dependencyPlotterConfiguration.ignoreProvidedDependenciesDuringAnalyzation()) {
			filterAllDependenciesInScope(Artifact.SCOPE_PROVIDED);
		}

		if (dependencyPlotterConfiguration.ignoreRuntimeDependenciesDuringAnalyzation()) {
			filterAllDependenciesInScope(Artifact.SCOPE_RUNTIME);
		}

		if (dependencyPlotterConfiguration.ignoreTestDependenciesDuringAnalyzation()) {
			filterAllDependenciesInScope(Artifact.SCOPE_TEST);
		}
	}

	private void filterAllDependenciesInScope(String scopeToFilter) {
		Set<Artifact> unusedButDeclaredArtifacts = filterAllDependenciesInScope(projectDependencyAnalysis.getUnusedDeclaredArtifacts(),
				scopeToFilter);
		Set<Artifact> usedButUndeclaredArtifacts = filterAllDependenciesInScope(projectDependencyAnalysis.getUsedUndeclaredArtifacts(),
				scopeToFilter);
		Set<Artifact> usedAndDeclaredArtifacts = filterAllDependenciesInScope(projectDependencyAnalysis.getUsedDeclaredArtifacts(),
				scopeToFilter);

		updateProjectDependencyAnalysis(usedAndDeclaredArtifacts, usedButUndeclaredArtifacts, unusedButDeclaredArtifacts);
	}

	private Set<Artifact> filterAllDependenciesInScope(Set<Artifact> artifactSetToFilter, String scopeToFilter) {
		return new HashSet<Artifact>(Collections2.filter(artifactSetToFilter, Predicates.not(new ArtifactIsScopePredicate(scopeToFilter))));
	}

	private void filterOnExclusions(DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		if (dependencyPlotterConfiguration.hasDependenciesToIgnoreAsUnusedButDeclared()) {
			ignoreDependenciesAsUnusedButDeclared(dependencyPlotterConfiguration);
		}
		if (dependencyPlotterConfiguration.hasDependenciesToIgnoreAsUsedButUndeclared()) {
			ignoreDependenciesAsUsedButUndeclared(dependencyPlotterConfiguration);
		}
	}

	private void ignoreDependenciesAsUnusedButDeclared(DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUnusedButDeclared = dependencyPlotterConfiguration
				.dependenciesToIgnoreAsUnusedButDeclared();
		for (MinimalMavenDependencyDescription dependencyToIgnoreAsUnusedButDeclared : dependenciesToIgnoreAsUnusedButDeclared) {
			ignoreDependencyAsUnusedButDeclared(dependencyToIgnoreAsUnusedButDeclared);
		}
	}

	private void ignoreDependenciesAsUsedButUndeclared(DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUsedButUndeclared = dependencyPlotterConfiguration
				.dependenciesToIgnoreAsUsedButUndeclared();
		for (MinimalMavenDependencyDescription dependencyToIgnoreAsUsedButUndeclared : dependenciesToIgnoreAsUsedButUndeclared) {
			ignoreDependencyAsUsedButUndeclared(dependencyToIgnoreAsUsedButUndeclared);
		}
	}

	
	private void ignoreDependencyAsUsedButUndeclared(MinimalMavenDependencyDescription dependencyToIgnoreAsUsedButUndeclared) {
		Set<Artifact> usedUndeclaredArtifacts = projectDependencyAnalysis.getUsedUndeclaredArtifacts();
		Set<Artifact> usedDeclaredArtifacts = projectDependencyAnalysis.getUsedDeclaredArtifacts();
		Set<Artifact> artifactsToIgnore = Sets.newHashSet(Collections2.filter(usedUndeclaredArtifacts, new ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate(dependencyToIgnoreAsUsedButUndeclared)));
		Iterable<Artifact> filter = Iterables.filter(usedUndeclaredArtifacts, Predicates.not(Predicates.in(artifactsToIgnore)));
		Set<Artifact> filteredUsedUndeclaredArtifacts = Sets.newHashSet(filter);
		Set<Artifact> newUsedDeclaredArtifacts = Sets.newHashSet(usedDeclaredArtifacts);
		newUsedDeclaredArtifacts.addAll(artifactsToIgnore);
		updateProjectDependencyAnalysis(newUsedDeclaredArtifacts, filteredUsedUndeclaredArtifacts, projectDependencyAnalysis.getUnusedDeclaredArtifacts());
		
	}

	private void ignoreDependencyAsUnusedButDeclared(MinimalMavenDependencyDescription dependencyToIgnoreAsUnusedButDeclared) {
		Set<Artifact> unusedDeclaredArtifacts = projectDependencyAnalysis.getUnusedDeclaredArtifacts();
		Set<Artifact> usedDeclaredArtifacts = projectDependencyAnalysis.getUsedDeclaredArtifacts();
		Set<Artifact> artifactsToIgnore = Sets.newHashSet(Collections2.filter(unusedDeclaredArtifacts, new ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate(dependencyToIgnoreAsUnusedButDeclared)));
		Iterable<Artifact> filter = Iterables.filter(unusedDeclaredArtifacts, Predicates.not(Predicates.in(artifactsToIgnore)));
		Set<Artifact> filteredUnusedDeclaredArtifacts = Sets.newHashSet(filter);
		Set<Artifact> newUsedDeclaredArtifacts = Sets.newHashSet(usedDeclaredArtifacts);
		newUsedDeclaredArtifacts.addAll(artifactsToIgnore);
		updateProjectDependencyAnalysis(newUsedDeclaredArtifacts, projectDependencyAnalysis.getUsedUndeclaredArtifacts(), filteredUnusedDeclaredArtifacts);
	}


	// Update methods

	private void updateProjectDependencyAnalysis(Set<Artifact> usedAndDeclaredArtifacts, Set<Artifact> usedButUndeclaredArtifacts,
			Set<Artifact> unusedButDeclaredArtifacts) {
		ProjectDependencyAnalysis filteredProjectDependencyAnalysis = new ProjectDependencyAnalysis(usedAndDeclaredArtifacts,
				usedButUndeclaredArtifacts, unusedButDeclaredArtifacts);
		updateProjectDependencyAnalysis(filteredProjectDependencyAnalysis);
	}

	private void updateProjectDependencyAnalysis(ProjectDependencyAnalysis projectDependencyAnalysis) {
		this.projectDependencyAnalysis = projectDependencyAnalysis;
	}

	public boolean hasWarnings() {
		return !getUnusedButDeclaredArtifacts().isEmpty() || !getUsedButUndeclaredArtifacts().isEmpty();
	}

	public String printWarnings() {
		StringBuilder stringBuilder = new StringBuilder();
		Set<Artifact> usedButUndeclaredArtifacts = getUsedButUndeclaredArtifacts();
		if (usedButUndeclaredArtifacts.isEmpty()) {
			stringBuilder.append("No dependencies used but undeclared");
			stringBuilder.append("\n");
			stringBuilder.append("\n");
		} else {
			stringBuilder.append("Dependencies used but undeclared:\n");
			for (Artifact artifact : usedButUndeclaredArtifacts) {
				stringBuilder.append(artifact.getGroupId() + ":" + artifact.getArtifactId());
				stringBuilder.append("\n");
			}
		}
		Set<Artifact> unusedButDeclaredArtifacts = getUnusedButDeclaredArtifacts();
		if (unusedButDeclaredArtifacts.isEmpty()) {
			stringBuilder.append("No dependencies unused but declared");
			stringBuilder.append("\n");
			stringBuilder.append("\n");
		} else {
			stringBuilder.append("Dependencies unused but declared:\n");
			for (Artifact artifact : unusedButDeclaredArtifacts) {
				stringBuilder.append(artifact.getGroupId() + ":" + artifact.getArtifactId());
				stringBuilder.append("\n");
			}
		}
		return stringBuilder.toString();
	}

}
