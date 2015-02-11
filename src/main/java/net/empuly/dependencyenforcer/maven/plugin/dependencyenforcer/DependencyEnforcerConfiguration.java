package net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

public class DependencyEnforcerConfiguration {

    private Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUsedButUndeclared;
    private Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUnusedButDeclared;

    public DependencyEnforcerConfiguration(
            String[] dependenciesToIgnoreAsUnusedButDeclared,
            String[] dependenciesToIgnoreAsUsedButUndeclared) {

        readDependenciesToIgnoreAsUnusedButDeclared(dependenciesToIgnoreAsUnusedButDeclared);
        readDependenciesToIgnoreAsUsedButUndeclared(dependenciesToIgnoreAsUsedButUndeclared);

    }

    public Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUnusedButDeclared() {
        return dependenciesToIgnoreAsUnusedButDeclared;
    }

    public Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUsedButUndeclared() {
        return dependenciesToIgnoreAsUsedButUndeclared;
    }

    public boolean hasDependenciesToIgnoreAsUnusedButDeclared() {
        return !dependenciesToIgnoreAsUnusedButDeclared().isEmpty();
    }

    public boolean hasDependenciesToIgnoreAsUsedButUndeclared() {
        return !dependenciesToIgnoreAsUsedButUndeclared().isEmpty();
    }

    private Set<MinimalMavenDependencyDescription> convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(
            String[] dependenciesAsArrayOfStrings) {
        Set<MinimalMavenDependencyDescription> setOfMinimalMavenDependencies = Sets.newHashSet();
        for (int i = 0; i < dependenciesAsArrayOfStrings.length; i++) {
            String dependencyAsString = dependenciesAsArrayOfStrings[i];
            String[] groupAndArtifactId = StringUtils.split(dependencyAsString, ":");
            setOfMinimalMavenDependencies.add(new MinimalMavenDependencyDescription(groupAndArtifactId[0],
                    groupAndArtifactId[1]));
        }
        return setOfMinimalMavenDependencies;
    }

    private void readDependenciesToIgnoreAsUnusedButDeclared(String[] someDependenciesToIgnoreAsUnusedButDeclared) {
        this.dependenciesToIgnoreAsUnusedButDeclared = convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(someDependenciesToIgnoreAsUnusedButDeclared);
    }

    private void readDependenciesToIgnoreAsUsedButUndeclared(String[] someDependenciesToIgnoreAsUsedButUndeclared) {
        this.dependenciesToIgnoreAsUsedButUndeclared = convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(someDependenciesToIgnoreAsUsedButUndeclared);
    }

}
