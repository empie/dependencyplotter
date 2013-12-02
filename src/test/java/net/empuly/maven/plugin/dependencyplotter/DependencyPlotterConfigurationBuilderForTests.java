package net.empuly.maven.plugin.dependencyplotter;

import java.io.File;

public class DependencyPlotterConfigurationBuilderForTests {

	private final boolean ignoreAllButCompileDependenciesDuringAnalyzation = true;
	private final boolean ignoreCompileDependenciesDuringAnalyzation = true;
	private final boolean ignoreTestDependenciesDuringAnalyzation = true;
	private final boolean ignoreRuntimeDependenciesDuringAnalyzation = true;
	private final boolean ignoreProvidedDependenciesDuringAnalyzation = true;
	private final boolean printUsedAndDeclaredDependencies = true;
	private final boolean printUnusedButDeclaredDependencies = true;
	private final boolean printUsedButUndeclaredDependencies = true;
	private final File targetDirectory = new File("target");
	private final String[] listOfDependencyNamesToIncludeInGraph = new String[] {};
	private final String[] dependenciesToIgnoreAsUnusedButDeclared = new String[] {};
	private final String[] dependenciesToIgnoreAsUsedButUndeclared = new String[] {};

	public DependencyPlotterConfigurationBuilderForTests() {

	}

}
