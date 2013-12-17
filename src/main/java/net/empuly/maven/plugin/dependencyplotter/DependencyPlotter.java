package net.empuly.maven.plugin.dependencyplotter;

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

	public DependencyPlotter(
			ProjectDependencyAnalyzer projectDependencyAnalyzer, Log logger,
			DependencyPlotterConfiguration dependencyPlotterConfiguration) {
		this.projectDependencyAnalyzer = projectDependencyAnalyzer;
		this.logger = logger;
		this.dependencyPlotterConfiguration = dependencyPlotterConfiguration;
	}

	public DependencyPlotterProjectDependencyAnalysis analyzeAndPlotDependencies(
			MavenProject mavenProjectToAnalyze) throws MojoExecutionException {
		DependencyPlotterProjectDependencyAnalysis analysis = analyzeMavenProject(mavenProjectToAnalyze);

		analysis.filterDependenciesBasedOnConfiguration(dependencyPlotterConfiguration);

		plotGraph(mavenProjectToAnalyze, analysis);

		return analysis;
	}

	private void plotGraph(MavenProject mavenProjectToAnalyze,
			DependencyPlotterProjectDependencyAnalysis analysis) {
		if (dependencyPlotterConfiguration.plotGraph()) {

			GraphVizDependencyGraphBuilder graphBuilder = new GraphVizDependencyGraphBuilder(
					mavenProjectToAnalyze, dependencyPlotterConfiguration);

			for (Artifact artifact : analysis
					.getUsedAndDeclaredArtifacts()) {
				graphBuilder.addUsedAndDeclaredDependency(artifact);
			}

			for (Artifact artifact : analysis
					.getUsedButUndeclaredArtifacts()) {
				graphBuilder.addUsedButUndeclaredDependency(artifact);
			}

			for (Artifact artifact : analysis
					.getUnusedButDeclaredArtifacts()) {
				graphBuilder.addUnusedButDeclaredDependency(artifact);
			}

			String dotSource = graphBuilder.getDotSource();
			logger.info(dotSource);

			GraphVizDependencyGraphPrinter graphPrinter = new GraphVizDependencyGraphPrinter(dependencyPlotterConfiguration, mavenProjectToAnalyze);
			graphPrinter.printGraph(dotSource);
		}
	}

	private DependencyPlotterProjectDependencyAnalysis analyzeMavenProject(
			MavenProject mavenProjectToAnalyze) throws MojoExecutionException {
		try {
			ProjectDependencyAnalysis analysis = projectDependencyAnalyzer
					.analyze(mavenProjectToAnalyze);
			return new DependencyPlotterProjectDependencyAnalysis(analysis,
					logger);

		} catch (ProjectDependencyAnalyzerException exception) {
			throw new MojoExecutionException("Cannot analyze dependencies",
					exception);
		}
	}

}
