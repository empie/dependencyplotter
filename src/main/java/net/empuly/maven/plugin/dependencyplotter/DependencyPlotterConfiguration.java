package net.empuly.maven.plugin.dependencyplotter;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

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
	private Set<MinimalMavenDependencyDescription> listOfDependencyNamesToExcludeInGraph;
	private final File plotOutputDirectory;
	private Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUsedButUndeclared;
	private Set<MinimalMavenDependencyDescription> dependenciesToIgnoreAsUnusedButDeclared;
	private boolean plotGraph;

	public DependencyPlotterConfiguration(
			boolean ignoreAllButCompileDependenciesDuringAnalyzation,
			boolean ignoreCompileDependenciesDuringAnalyzation,
			boolean ignoreTestDependenciesDuringAnalyzation,
			boolean ignoreRuntimeDependenciesDuringAnalyzation,
			boolean ignoreProvidedDependenciesDuringAnalyzation,
			boolean plotGraph,
			boolean printUsedAndDeclaredDependencies,
			boolean printUnusedButDeclaredDependencies,
			boolean printUsedButUndeclaredDependencies,
			File plotOutputDirectory,
			String[] listOfDependencyNamesToIncludeInGraph,
			String[] listOfDependencyNamesToExcludeInGraph,
			String[] dependenciesToIgnoreAsUnusedButDeclared,
			String[] dependenciesToIgnoreAsUsedButUndeclared) {
		this.ignoreAllButCompileDependenciesDuringAnalyzation = ignoreAllButCompileDependenciesDuringAnalyzation;
		this.ignoreCompileDependenciesDuringAnalyzation = ignoreCompileDependenciesDuringAnalyzation;
		this.ignoreTestDependenciesDuringAnalyzation = ignoreTestDependenciesDuringAnalyzation;
		this.ignoreRuntimeDependenciesDuringAnalyzation = ignoreRuntimeDependenciesDuringAnalyzation;
		this.ignoreProvidedDependenciesDuringAnalyzation = ignoreProvidedDependenciesDuringAnalyzation;
		this.plotGraph = plotGraph;
		this.printUsedAndDeclaredDependencies = printUsedAndDeclaredDependencies;
		this.printUnusedButDeclaredDependencies = printUnusedButDeclaredDependencies;
		this.printUsedButUndeclaredDependencies = printUsedButUndeclaredDependencies;
		this.plotOutputDirectory = plotOutputDirectory;

		readListOfDependencyNamesToIncludeInGraph(listOfDependencyNamesToIncludeInGraph);
		readListOfDependencyNamesToExcludeInGraph(listOfDependencyNamesToExcludeInGraph);
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

	public boolean hasDependencyNamesToIncludeInGraph() {
		return !listOfDependencyNamesToIncludeInGraph().isEmpty();
	}
	
	public boolean hasDependencyNamesToExcludeInGraph() {
		return !listOfDependencyNamesToExcludeInGraph().isEmpty();
	}
	
	public boolean plotGraph() {
		return plotGraph;
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
	
	public Set<MinimalMavenDependencyDescription> listOfDependencyNamesToExcludeInGraph() {
		return listOfDependencyNamesToExcludeInGraph;
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

	public File plotOutputDirectory() {
		return plotOutputDirectory;
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

	private void readListOfDependencyNamesToIncludeInGraph(String[] listOfDependencyNamesToIncludeInGraph) {
		this.listOfDependencyNamesToIncludeInGraph = convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(listOfDependencyNamesToIncludeInGraph);
	}
	
	private void readListOfDependencyNamesToExcludeInGraph(String[] listOfDependencyNamesToExcludeInGraph) {
		this.listOfDependencyNamesToExcludeInGraph = convertArrayOfStringDependenciesToSetOfMinimalMavenDependencies(listOfDependencyNamesToExcludeInGraph);
	}

}
