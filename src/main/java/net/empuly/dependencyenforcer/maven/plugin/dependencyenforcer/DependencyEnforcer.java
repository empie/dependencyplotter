package net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;

public class DependencyEnforcer {
    
    private final ProjectDependencyAnalyzer projectDependencyAnalyzer;
    private final DependencyEnforcerConfiguration dependencyEnforcerConfiguration;
    
    public DependencyEnforcer(
            ProjectDependencyAnalyzer projectDependencyAnalyzer,
            DependencyEnforcerConfiguration dependencyEnforcerConfiguration) {
        this.projectDependencyAnalyzer = projectDependencyAnalyzer;
        this.dependencyEnforcerConfiguration = dependencyEnforcerConfiguration;
    }
    
    public DependencyEnforcerProjectDependencyAnalysis analyzeAndPlotDependencies(
            MavenProject mavenProjectToAnalyze) throws MojoExecutionException {
        DependencyEnforcerProjectDependencyAnalysis analysis = analyzeMavenProject(mavenProjectToAnalyze);
        
        analysis.filterDependenciesBasedOnConfiguration(dependencyEnforcerConfiguration);
        
        return analysis;
    }
    
    private DependencyEnforcerProjectDependencyAnalysis analyzeMavenProject(
            MavenProject mavenProjectToAnalyze) throws MojoExecutionException {
        try {
            ProjectDependencyAnalysis analysis = projectDependencyAnalyzer
                    .analyze(mavenProjectToAnalyze);
            return new DependencyEnforcerProjectDependencyAnalysis(analysis);
            
        } catch (ProjectDependencyAnalyzerException exception) {
            throw new MojoExecutionException("Cannot analyze dependencies",
                    exception);
        }
    }
    
}
