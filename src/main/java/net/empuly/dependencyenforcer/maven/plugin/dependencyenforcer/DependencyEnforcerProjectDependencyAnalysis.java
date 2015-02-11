package net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class DependencyEnforcerProjectDependencyAnalysis {

    private ProjectDependencyAnalysis projectDependencyAnalysis;

    public DependencyEnforcerProjectDependencyAnalysis(ProjectDependencyAnalysis projectDependencyAnalysis) {
        this.projectDependencyAnalysis = projectDependencyAnalysis;
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

    public void filterDependenciesBasedOnConfiguration(DependencyEnforcerConfiguration dependencyEnforcerConfiguration) {
        filterAllButCompileScope();
        filterOnExclusions(dependencyEnforcerConfiguration);
    }

    private void filterAllButCompileScope() {
        filterAllDependenciesInScope(Artifact.SCOPE_PROVIDED);
        filterAllDependenciesInScope(Artifact.SCOPE_RUNTIME);
        filterAllDependenciesInScope(Artifact.SCOPE_TEST);
    }

    private void filterAllDependenciesInScope(String scopeToFilter) {
        Set<Artifact> unusedButDeclaredArtifacts = filterAllDependenciesInScope(
                projectDependencyAnalysis.getUnusedDeclaredArtifacts(),
                scopeToFilter);
        Set<Artifact> usedButUndeclaredArtifacts = filterAllDependenciesInScope(
                projectDependencyAnalysis.getUsedUndeclaredArtifacts(),
                scopeToFilter);
        Set<Artifact> usedAndDeclaredArtifacts = filterAllDependenciesInScope(
                projectDependencyAnalysis.getUsedDeclaredArtifacts(),
                scopeToFilter);

        updateProjectDependencyAnalysis(usedAndDeclaredArtifacts, usedButUndeclaredArtifacts,
                unusedButDeclaredArtifacts);
    }

    private Set<Artifact> filterAllDependenciesInScope(Set<Artifact> artifactSetToFilter, String scopeToFilter) {
        return new HashSet<>(Collections2.filter(artifactSetToFilter,
                Predicates.not(new ArtifactIsScopePredicate(scopeToFilter))));
    }

    private void filterOnExclusions(DependencyEnforcerConfiguration dependencyEnforcerConfiguration) {
        if (dependencyEnforcerConfiguration.hasDependenciesToIgnoreAsUnusedButDeclared()) {
            ignoreDependenciesAsUnusedButDeclared(dependencyEnforcerConfiguration);
        }
        if (dependencyEnforcerConfiguration.hasDependenciesToIgnoreAsUsedButUndeclared()) {
            ignoreDependenciesAsUsedButUndeclared(dependencyEnforcerConfiguration);
        }
    }

    private void ignoreDependenciesAsUnusedButDeclared(DependencyEnforcerConfiguration dependencyEnforcerConfiguration) {
        Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUnusedButDeclared = dependencyEnforcerConfiguration
                .dependenciesToIgnoreAsUnusedButDeclared();
        for (MinimalMavenDependencyDescription dependencyToIgnoreAsUnusedButDeclared : dependenciesToIgnoreAsUnusedButDeclared) {
            ignoreDependencyAsUnusedButDeclared(dependencyToIgnoreAsUnusedButDeclared);
        }
    }

    private void ignoreDependenciesAsUsedButUndeclared(DependencyEnforcerConfiguration dependencyEnforcerConfiguration) {
        Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUsedButUndeclared = dependencyEnforcerConfiguration
                .dependenciesToIgnoreAsUsedButUndeclared();
        for (MinimalMavenDependencyDescription dependencyToIgnoreAsUsedButUndeclared : dependenciesToIgnoreAsUsedButUndeclared) {
            ignoreDependencyAsUsedButUndeclared(dependencyToIgnoreAsUsedButUndeclared);
        }
    }

    private void ignoreDependencyAsUsedButUndeclared(
            MinimalMavenDependencyDescription dependencyToIgnoreAsUsedButUndeclared) {
        Set<Artifact> usedUndeclaredArtifacts = projectDependencyAnalysis.getUsedUndeclaredArtifacts();
        Set<Artifact> usedDeclaredArtifacts = projectDependencyAnalysis.getUsedDeclaredArtifacts();
        Set<Artifact> artifactsToIgnore = Sets.newHashSet(Collections2
                .filter(usedUndeclaredArtifacts, new ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate(
                        dependencyToIgnoreAsUsedButUndeclared)));
        Iterable<Artifact> filter = Iterables.filter(usedUndeclaredArtifacts,
                Predicates.not(Predicates.in(artifactsToIgnore)));
        Set<Artifact> filteredUsedUndeclaredArtifacts = Sets.newHashSet(filter);
        Set<Artifact> newUsedDeclaredArtifacts = Sets.newHashSet(usedDeclaredArtifacts);
        newUsedDeclaredArtifacts.addAll(artifactsToIgnore);
        updateProjectDependencyAnalysis(newUsedDeclaredArtifacts, filteredUsedUndeclaredArtifacts,
                projectDependencyAnalysis.getUnusedDeclaredArtifacts());

    }

    private void ignoreDependencyAsUnusedButDeclared(
            MinimalMavenDependencyDescription dependencyToIgnoreAsUnusedButDeclared) {
        Set<Artifact> unusedDeclaredArtifacts = projectDependencyAnalysis.getUnusedDeclaredArtifacts();
        Set<Artifact> usedDeclaredArtifacts = projectDependencyAnalysis.getUsedDeclaredArtifacts();
        Set<Artifact> artifactsToIgnore = Sets.newHashSet(Collections2
                .filter(unusedDeclaredArtifacts, new ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate(
                        dependencyToIgnoreAsUnusedButDeclared)));
        Iterable<Artifact> filter = Iterables.filter(unusedDeclaredArtifacts,
                Predicates.not(Predicates.in(artifactsToIgnore)));
        Set<Artifact> filteredUnusedDeclaredArtifacts = Sets.newHashSet(filter);
        Set<Artifact> newUsedDeclaredArtifacts = Sets.newHashSet(usedDeclaredArtifacts);
        newUsedDeclaredArtifacts.addAll(artifactsToIgnore);
        updateProjectDependencyAnalysis(newUsedDeclaredArtifacts,
                projectDependencyAnalysis.getUsedUndeclaredArtifacts(), filteredUnusedDeclaredArtifacts);
    }

    // Update methods

    private void updateProjectDependencyAnalysis(Set<Artifact> usedAndDeclaredArtifacts,
            Set<Artifact> usedButUndeclaredArtifacts,
            Set<Artifact> unusedButDeclaredArtifacts) {
        ProjectDependencyAnalysis filteredProjectDependencyAnalysis = new ProjectDependencyAnalysis(
                usedAndDeclaredArtifacts,
                usedButUndeclaredArtifacts, unusedButDeclaredArtifacts);
        updateProjectDependencyAnalysis(filteredProjectDependencyAnalysis);
    }

    private void updateProjectDependencyAnalysis(ProjectDependencyAnalysis theProjectDependencyAnalysis) {
        this.projectDependencyAnalysis = theProjectDependencyAnalysis;
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
