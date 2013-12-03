package net.empuly.maven.plugin.dependencyplotter;

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;

public class DependencyPlotter {

	private final ProjectDependencyAnalyzer projectDependencyAnalyzer;
	private final Log logger;
	private final DependencyPlotterConfiguration dependencyPlotterConfiguration;

	public DependencyPlotter(ProjectDependencyAnalyzer projectDependencyAnalyzer, Log logger,
			DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		this.projectDependencyAnalyzer = projectDependencyAnalyzer;
		this.logger = logger;
		this.dependencyPlotterConfiguration = dependencyPlotterConfiguration;
	}

	public boolean plotDependencies(MavenProject mavenProjectToAnalyze) throws MojoExecutionException {
		DependencyPlotterProjectDependencyAnalysis analysis = analyzeMavenProject(mavenProjectToAnalyze);

		analysis.filterDependenciesBasedOnConfiguration(dependencyPlotterConfiguration);

		Set<Artifact> usedAndDeclaredDependencies = analysis.getUsedAndDeclaredArtifacts();
		Set<Artifact> usedButUndeclaredDependencies = analysis.getUsedButUndeclaredArtifacts();
		Set<Artifact> unusedButDeclaredDependencies = analysis.getUnusedButDeclaredArtifacts();

		GraphVizDependencyGraphBuilder graphBuilder = new GraphVizDependencyGraphBuilder(mavenProjectToAnalyze);

		for (Artifact artifact : usedAndDeclaredDependencies) {
			graphBuilder.addUsedAndDeclaredDependency(artifact);
		}

		for (Artifact artifact : usedButUndeclaredDependencies) {
			graphBuilder.addUsedButUndeclaredDependency(artifact);
		}

		for (Artifact artifact : unusedButDeclaredDependencies) {
			graphBuilder.addUnusedButDeclaredDependency(artifact);
		}

		String dotSource = graphBuilder.getDotSource();
		System.out.println(dotSource);

		String type = "gif";

		GraphVizDependencyGraphPrinter graphPrinter = new GraphVizDependencyGraphPrinter();
		graphPrinter.printGraph(dotSource, type);

		return !usedButUndeclaredDependencies.isEmpty() || !unusedButDeclaredDependencies.isEmpty();
	}

	private DependencyPlotterProjectDependencyAnalysis analyzeMavenProject(MavenProject mavenProjectToAnalyze)
			throws MojoExecutionException {
		try {
			ProjectDependencyAnalysis analysis = projectDependencyAnalyzer.analyze(mavenProjectToAnalyze);
			return new DependencyPlotterProjectDependencyAnalysis(analysis, logger);

		} catch (ProjectDependencyAnalyzerException exception) {
			throw new MojoExecutionException("Cannot analyze dependencies", exception);
		}
	}

}
