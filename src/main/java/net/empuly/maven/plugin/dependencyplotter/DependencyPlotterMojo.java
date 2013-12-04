package net.empuly.maven.plugin.dependencyplotter;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

@Mojo(name = "analyzeAndPlot", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class DependencyPlotterMojo extends AbstractMojo implements Contextualizable {

	private static final String ROLE_HINT = "default";

	private Context plexusContext;

	@Component
	private MavenProject mavenProjectToAnalyze;

	@Parameter(property = "ignoreAllButCompileDependenciesDuringAnalyzation", defaultValue = "false")
	private boolean ignoreAllButCompileDependenciesDuringAnalyzation;
	@Parameter(property = "ignoreCompileDependenciesDuringAnalyzation", defaultValue = "false")
	private boolean ignoreCompileDependenciesDuringAnalyzation;
	@Parameter(property = "ignoreTestDependenciesDuringAnalyzation", defaultValue = "false")
	private boolean ignoreTestDependenciesDuringAnalyzation;
	@Parameter(property = "ignoreRuntimeDependenciesDuringAnalyzation", defaultValue = "false")
	private boolean ignoreRuntimeDependenciesDuringAnalyzation;
	@Parameter(property = "ignoreProvidedDependenciesDuringAnalyzation", defaultValue = "false")
	private boolean ignoreProvidedDependenciesDuringAnalyzation;

	@Parameter(property = "mavenBuildMustFailOnDependencyAnalyzationWarnings", defaultValue = "false")
	private boolean mavenBuildMustFailOnDependencyAnalyzationWarnings;
	@Parameter(property = "printUsedButUndeclaredDependencies", defaultValue = "true")
	private boolean printUsedButUndeclaredDependencies;
	@Parameter(property = "printUnusedButDeclaredDependencies", defaultValue = "true")
	private boolean printUnusedButDeclaredDependencies;
	@Parameter(property = "printUsedAndDeclaredDependencies", defaultValue = "true")
	private boolean printUsedAndDeclaredDependencies;

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File targetDirectory;

	@Parameter(property = "listOfDependencyNamesToIncludeInGraph")
	private String[] listOfDependencyNamesToIncludeInGraph;
	@Parameter(property = "dependenciesToIgnoreAsUnusedButDeclared")
	private String[] dependenciesToIgnoreAsUnusedButDeclared;
	@Parameter(property = "dependenciesToIgnoreAsUsedButUndeclared")
	private String[] dependenciesToIgnoreAsUsedButUndeclared;

	@Parameter(property = "mdep.analyze.skip", defaultValue = "false")
	private boolean skip;

	public void contextualize(Context context) throws ContextException {
		plexusContext = context;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipDependencyPlotterExecution()) {
			getLog().info("Skipping plugin execution");
			return;
		}

		if (isPomProject()) {
			getLog().info("Skipping pom project");
			return;
		}

		if (noValidTargetDirectory()) {
			getLog().info("Skipping project with no build directory. First execute mvn clean install");
			return;
		}

		boolean dependencyAnalyzationResultedInWarnings = analyzeDependenciesAndPlotThem();

		if (dependencyAnalyzationResultedInWarnings && mavenBuildMustFailOnDependencyAnalyzationWarnings) {
			throw new MojoExecutionException("Dependency analyzation resulted in warnings");
		}
	}

	private boolean analyzeDependenciesAndPlotThem() throws MojoExecutionException {

		DependencyPlotterConfiguration dependencyPlotterConfiguration = new DependencyPlotterConfiguration(
				ignoreAllButCompileDependenciesDuringAnalyzation,
				ignoreCompileDependenciesDuringAnalyzation,
				ignoreTestDependenciesDuringAnalyzation,
				ignoreRuntimeDependenciesDuringAnalyzation,
				ignoreProvidedDependenciesDuringAnalyzation,
				printUsedAndDeclaredDependencies,
				printUnusedButDeclaredDependencies,
				printUsedButUndeclaredDependencies,
				targetDirectory,
				listOfDependencyNamesToIncludeInGraph,
				dependenciesToIgnoreAsUnusedButDeclared,
				dependenciesToIgnoreAsUsedButUndeclared);

		ProjectDependencyAnalyzer projectDependencyAnalyzer = lookupProjectDependencyAnalyzerInPlexusContext();
		DependencyPlotter dependencyPlotter = new DependencyPlotter(projectDependencyAnalyzer,
				getLog(), dependencyPlotterConfiguration);
		boolean analyzeResultedInWarnings = dependencyPlotter.plotDependencies(mavenProjectToAnalyze);
		return analyzeResultedInWarnings;
	}

	private boolean isPomProject() {
		return "pom".equals(mavenProjectToAnalyze.getPackaging());
	}

	private ProjectDependencyAnalyzer lookupProjectDependencyAnalyzerInPlexusContext() throws MojoExecutionException {
		try {
			final PlexusContainer container = (PlexusContainer) plexusContext.get(PlexusConstants.PLEXUS_KEY);
			return (ProjectDependencyAnalyzer) container.lookup(ProjectDependencyAnalyzer.ROLE, ROLE_HINT);
		} catch (Exception exception) {
			throw new MojoExecutionException("Failed to instantiate ProjectDependencyAnalyser with role " + ProjectDependencyAnalyzer.ROLE
					+ " / role-hint " + ROLE_HINT, exception);
		}
	}

	private boolean noValidTargetDirectory() {
		return targetDirectory == null || !targetDirectory.exists();
	}

	private boolean skipDependencyPlotterExecution() {
		return skip;
	}
}
