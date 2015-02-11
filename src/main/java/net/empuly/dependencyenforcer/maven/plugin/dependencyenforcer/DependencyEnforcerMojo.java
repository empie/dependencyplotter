package net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
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

@Mojo(name = "analyzeAndEnforce", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class DependencyEnforcerMojo extends AbstractMojo implements Contextualizable {
    
    private static final String ROLE_HINT = "default";
    
    private Context plexusContext;
    
    @Component
    private MavenProject mavenProjectToAnalyze;

    @Parameter(property = "mavenBuildMustFailOnDependencyAnalyzationWarnings", defaultValue = "false")
    private boolean mavenBuildMustFailOnDependencyAnalyzationWarnings;
    
    @Parameter(property = "dependenciesToIgnoreAsUnusedButDeclared")
    private String[] dependenciesToIgnoreAsUnusedButDeclared;
    @Parameter(property = "dependenciesToIgnoreAsUsedButUndeclared")
    private String[] dependenciesToIgnoreAsUsedButUndeclared;
    
    @Parameter(property = "skipPlugin", defaultValue = "false")
    private boolean skipPlugin;
    
    @Override
    public void contextualize(Context context) throws ContextException {
        plexusContext = context;
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipDependencyEnforcerExecution()) {
            getLog().info("Skipping plugin execution");
            return;
        }
        
        if (isPomProject()) {
            getLog().info("Skipping pom project");
            return;
        }
        
        DependencyEnforcerProjectDependencyAnalysis analysis = analyzeDependenciesAndPlotThem();
        
        if (analysis.hasWarnings() && mavenBuildMustFailOnDependencyAnalyzationWarnings) {
            throw new MojoExecutionException("Dependency analyzation resulted in warnings.\n"
                    + analysis.printWarnings());
        }
    }
    
    private DependencyEnforcerProjectDependencyAnalysis analyzeDependenciesAndPlotThem() throws MojoExecutionException {
        
        DependencyEnforcerConfiguration dependencyEnforcerConfiguration = new DependencyEnforcerConfiguration(
                dependenciesToIgnoreAsUnusedButDeclared,
                dependenciesToIgnoreAsUsedButUndeclared);
        
        ProjectDependencyAnalyzer projectDependencyAnalyzer = lookupProjectDependencyAnalyzerInPlexusContext();
        DependencyEnforcer dependencyEnforcer = new DependencyEnforcer(projectDependencyAnalyzer,
                dependencyEnforcerConfiguration);
        DependencyEnforcerProjectDependencyAnalysis dependencyAnalysis = dependencyEnforcer
                .analyzeAndPlotDependencies(mavenProjectToAnalyze);
        return dependencyAnalysis;
    }
    
    private boolean isPomProject() {
        return "pom".equals(mavenProjectToAnalyze.getPackaging());
    }
    
    private ProjectDependencyAnalyzer lookupProjectDependencyAnalyzerInPlexusContext() throws MojoExecutionException {
        try {
            final PlexusContainer container = (PlexusContainer) plexusContext.get(PlexusConstants.PLEXUS_KEY);
            return (ProjectDependencyAnalyzer) container.lookup(ProjectDependencyAnalyzer.ROLE, ROLE_HINT);
        } catch (Exception exception) {
            throw new MojoExecutionException("Failed to instantiate ProjectDependencyAnalyser with role "
                    + ProjectDependencyAnalyzer.ROLE
                    + " / role-hint " + ROLE_HINT, exception);
        }
    }
    
    private boolean skipDependencyEnforcerExecution() {
        return skipPlugin;
    }
}
