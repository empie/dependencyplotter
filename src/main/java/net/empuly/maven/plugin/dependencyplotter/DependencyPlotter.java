package net.empuly.maven.plugin.dependencyplotter;

import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

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

		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());

		gv.addln("A -> B;");
		gv.addln("A -> C;");

		for (Artifact artifact : usedAndDeclaredDependencies) {
			gv.addln("\"" + mavenProjectToAnalyze.getGroupId() + ":" + mavenProjectToAnalyze.getArtifactId() + "\" -> \""
					+ artifact.getGroupId() + ":" + artifact.getArtifactId() + "\";");
		}

		for (Artifact artifact : usedButUndeclaredDependencies) {
			gv.addln("\"" + mavenProjectToAnalyze.getGroupId() + ":" + mavenProjectToAnalyze.getArtifactId() + "\" -> \""
					+ artifact.getGroupId() + ":" + artifact.getArtifactId() + "\";");
		}

		for (Artifact artifact : unusedButDeclaredDependencies) {
			gv.addln("\"" + mavenProjectToAnalyze.getGroupId() + ":" + mavenProjectToAnalyze.getArtifactId() + "\" -> \""
					+ artifact.getGroupId() + ":" + artifact.getArtifactId() + "\";");
		}

		gv.addln(gv.end_graph());
		String dotSource = gv.getDotSource();
		System.out.println(dotSource);

		String type = "gif";

		gv.printGraph(dotSource, type);

		if ((usedAndDeclaredDependencies.isEmpty()) && usedButUndeclaredDependencies.isEmpty() && unusedButDeclaredDependencies.isEmpty()) {
			return false;
		}

		if (!usedAndDeclaredDependencies.isEmpty()) {
			logger.info("Used declared dependencies found:");

			logArtifacts(analysis.getUsedAndDeclaredArtifacts(), false);
		}

		if (!usedButUndeclaredDependencies.isEmpty()) {
			logger.warn("Used undeclared dependencies found:");

			logArtifacts(usedButUndeclaredDependencies, true);
		}

		if (!unusedButDeclaredDependencies.isEmpty()) {
			logger.warn("Unused declared dependencies found:");

			logArtifacts(unusedButDeclaredDependencies, true);
		}

		writeDependencyXML(usedButUndeclaredDependencies);

		return !usedButUndeclaredDependencies.isEmpty() || !unusedButDeclaredDependencies.isEmpty();
	}

	private DependencyPlotterProjectDependencyAnalysis analyzeMavenProject(MavenProject mavenProjectToAnalyze)
			throws MojoExecutionException {
		try {
			ProjectDependencyAnalysis analysis = projectDependencyAnalyzer.analyze(mavenProjectToAnalyze);
			return new DependencyPlotterProjectDependencyAnalysis(analysis);

		} catch (ProjectDependencyAnalyzerException exception) {
			throw new MojoExecutionException("Cannot analyze dependencies", exception);
		}
	}

	private void logArtifacts(Set<Artifact> artifacts, boolean warn) {
		if (artifacts.isEmpty()) {
			logger.info("   None");
		} else {
			for (Artifact artifact : artifacts) {
				// called because artifact will set the version to -SNAPSHOT only if I do this. MNG-2961
				artifact.isSnapshot();

				if (warn) {
					logger.warn("   " + artifact);
				} else {
					logger.info("   " + artifact);
				}

			}
		}
	}

	private void writeDependencyXML(Set<Artifact> artifacts) {
		if (!artifacts.isEmpty()) {
			logger.info("Add the following to your pom to correct the missing dependencies: ");

			StringWriter out = new StringWriter();
			PrettyPrintXMLWriter writer = new PrettyPrintXMLWriter(out);

			for (Artifact artifact : artifacts) {
				// called because artifact will set the version to -SNAPSHOT only if I do this. MNG-2961
				artifact.isSnapshot();

				writer.startElement("dependency");
				writer.startElement("groupId");
				writer.writeText(artifact.getGroupId());
				writer.endElement();
				writer.startElement("artifactId");
				writer.writeText(artifact.getArtifactId());
				writer.endElement();
				writer.startElement("version");
				writer.writeText(artifact.getBaseVersion());
				if (!StringUtils.isBlank(artifact.getClassifier())) {
					writer.startElement("classifier");
					writer.writeText(artifact.getClassifier());
					writer.endElement();
				}
				writer.endElement();

				if (!Artifact.SCOPE_COMPILE.equals(artifact.getScope())) {
					writer.startElement("scope");
					writer.writeText(artifact.getScope());
					writer.endElement();
				}
				writer.endElement();
			}

			logger.info("\n" + out.getBuffer());
		}
	}

}
