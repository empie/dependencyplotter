package net.empuly.maven.plugin.dependencyplotter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import edu.emory.mathcs.backport.java.util.Arrays;

public class DependencyPlotterConfiguration {

	private final boolean ignoreAllButCompileDependenciesDuringAnalyzation;
	private final boolean ignoreCompileDependenciesDuringAnalyzation;
	private final boolean ignoreTestDependenciesDuringAnalyzation;
	private final boolean ignoreRuntimeDependenciesDuringAnalyzation;
	private final boolean ignoreProvidedDependenciesDuringAnalyzation;
	private final boolean printUsedAndDeclaredDependencies;
	private final boolean printUnusedButDeclaredDependencies;
	private final boolean printUsedButUndeclaredDependencies;
	private Set<MinimalMavenDependencyDescription> listOfDependencyNamesToIncludeInGraph;
	private final File targetDirectory;
	private Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUsedButUndeclared;
	private Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUnusedButDeclared;

	public DependencyPlotterConfiguration(
			boolean ignoreAllButCompileDependenciesDuringAnalyzation,
			boolean ignoreCompileDependenciesDuringAnalyzation,
			boolean ignoreTestDependenciesDuringAnalyzation,
			boolean ignoreRuntimeDependenciesDuringAnalyzation,
			boolean ignoreProvidedDependenciesDuringAnalyzation,
			boolean printUsedAndDeclaredDependencies,
			boolean printUnusedButDeclaredDependencies,
			boolean printUsedButUndeclaredDependencies,
			File targetDirectory,
			String[] listOfDependencyNamesToIncludeInGraph,
			String[] dependenciesToIgnoreAsUnusedButDeclared,
			String[] dependenciesToIgnoreAsUsedButUndeclared) {
		this.ignoreAllButCompileDependenciesDuringAnalyzation = ignoreAllButCompileDependenciesDuringAnalyzation;
		this.ignoreCompileDependenciesDuringAnalyzation = ignoreCompileDependenciesDuringAnalyzation;
		this.ignoreTestDependenciesDuringAnalyzation = ignoreTestDependenciesDuringAnalyzation;
		this.ignoreRuntimeDependenciesDuringAnalyzation = ignoreRuntimeDependenciesDuringAnalyzation;
		this.ignoreProvidedDependenciesDuringAnalyzation = ignoreProvidedDependenciesDuringAnalyzation;
		this.printUsedAndDeclaredDependencies = printUsedAndDeclaredDependencies;
		this.printUnusedButDeclaredDependencies = printUnusedButDeclaredDependencies;
		this.printUsedButUndeclaredDependencies = printUsedButUndeclaredDependencies;
		this.targetDirectory = targetDirectory;

		readListOfDependencyNamesToIncludeInGraph(listOfDependencyNamesToIncludeInGraph);
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
		return dependenciesToIgnoreAsUnusedButDeclared().isEmpty();
	}

	public boolean hasDependenciesToIgnoreAsUsedButUndeclared() {
		return dependenciesToIgnoreAsUsedButUndeclared().isEmpty();
	}

	public boolean hasDependencyNamesToIncludeInGraph() {
		return listOfDependencyNamesToIncludeInGraph().isEmpty();
	}

	public boolean ignoreAllButCompileDependenciesDuringAnalyzation() {
		return ignoreAllButCompileDependenciesDuringAnalyzation;
	}

	public boolean ignoreCompileDependenciesDuringAnalyzation() {
		return ignoreCompileDependenciesDuringAnalyzation;
	}

	public boolean ignoreProvidedDependenciesDuringAnalyzation() {
		return ignoreProvidedDependenciesDuringAnalyzation;
	}

	public boolean ignoreRuntimeDependenciesDuringAnalyzation() {
		return ignoreRuntimeDependenciesDuringAnalyzation;
	}

	public boolean ignoreTestDependenciesDuringAnalyzation() {
		return ignoreTestDependenciesDuringAnalyzation;
	}

	public Set<MinimalMavenDependencyDescription> listOfDependencyNamesToIncludeInGraph() {
		return listOfDependencyNamesToIncludeInGraph;
	}

	public boolean printUnusedButDeclaredDependencies() {
		return printUnusedButDeclaredDependencies;
	}

	public boolean printUsedAndDeclaredDependencies() {
		return printUsedAndDeclaredDependencies;
	}

	public boolean printUsedButUndeclaredDependencies() {
		return printUsedButUndeclaredDependencies;
	}

	public File targetDirectory() {
		return targetDirectory;
	}

	private Set<MinimalMavenDependencyDescription> convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(
			String[] dependenciesAsArrayOfStrings) {
		Set<MinimalMavenDependencyDescription> setOfMinimalMavenDependencies = Sets.newHashSet();
		for (int i = 0; i < dependenciesAsArrayOfStrings.length; i++) {
			String dependencyAsString = dependenciesAsArrayOfStrings[i];
			String[] groupAndArtifactId = StringUtils.split(dependencyAsString, ":");
			setOfMinimalMavenDependencies.add(new MinimalMavenDependencyDescription(groupAndArtifactId[0], groupAndArtifactId[1]));
		}
		return setOfMinimalMavenDependencies;
	}

	private void readDependenciesToIgnoreAsUnusedButDeclared(String[] dependenciesToIgnoreAsUnusedButDeclared) {
		this.dependenciesToIgnoreAsUnusedButDeclared = convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(dependenciesToIgnoreAsUnusedButDeclared);
	}

	private void readDependenciesToIgnoreAsUsedButUndeclared(String[] dependenciesToIgnoreAsUsedButUndeclared) {
		this.dependenciesToIgnoreAsUsedButUndeclared = convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(dependenciesToIgnoreAsUsedButUndeclared);
	}

	@SuppressWarnings("unchecked")
	private void readListOfDependencyNamesToIncludeInGraph(String[] listOfDependencyNamesToIncludeInGraph) {
		this.listOfDependencyNamesToIncludeInGraph = new HashSet<MinimalMavenDependencyDescription>(
				Arrays.asList(listOfDependencyNamesToIncludeInGraph));
	}

}
