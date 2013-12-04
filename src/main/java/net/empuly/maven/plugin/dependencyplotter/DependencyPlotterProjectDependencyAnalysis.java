package net.empuly.maven.plugin.dependencyplotter;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

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
		moveDependencyFromFirstToSecondSetIfFound(dependencyToIgnoreAsUsedButUndeclared, usedUndeclaredArtifacts, usedDeclaredArtifacts);
	}

	private void ignoreDependencyAsUnusedButDeclared(MinimalMavenDependencyDescription dependencyToIgnoreAsUnusedButDeclared) {
		Set<Artifact> unusedDeclaredArtifacts = projectDependencyAnalysis.getUnusedDeclaredArtifacts();
		Set<Artifact> usedDeclaredArtifacts = projectDependencyAnalysis.getUsedDeclaredArtifacts();
		moveDependencyFromFirstToSecondSetIfFound(dependencyToIgnoreAsUnusedButDeclared, unusedDeclaredArtifacts, usedDeclaredArtifacts);
	}

	private void moveDependencyFromFirstToSecondSetIfFound(MinimalMavenDependencyDescription dependencyToMove,
			Set<Artifact> setToFilterFrom, Set<Artifact> setToMoveDependencyTo) {
		Artifact foundDependencyToIgnore = Iterables.find(setToFilterFrom, new IsDependencyPredicate(dependencyToMove));
		if (foundDependencyToIgnore != null) {
			boolean removeSuccess = setToFilterFrom.remove(foundDependencyToIgnore);
			Preconditions.checkArgument(removeSuccess);
			setToMoveDependencyTo.add(foundDependencyToIgnore);
		}
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

}
