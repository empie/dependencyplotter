package net.empuly.maven.plugin.dependencyplotter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

public class GraphVizDependencyGraphBuilder {

	private static final String EDGE = " -> ";
	private static final String NEW_LINE = "&#92;n";
	private final MavenProject mavenProjectToAnalyze;
	private final StringBuilder stringBuilder;

	public GraphVizDependencyGraphBuilder(MavenProject mavenProjectToAnalyze) {
		this.mavenProjectToAnalyze = mavenProjectToAnalyze;
		stringBuilder = new StringBuilder();
		addStartOfGraph();
	}

	public void addUsedAndDeclaredDependency(Artifact artifact) {
		legLinkVanProjectArtifactNaarDependency(artifact);
		voegUsedAndDeclaredStyleToe();
		voegEindeLijnEnNewLineToe();
	}

	public void addUsedButUndeclaredDependency(Artifact artifact) {
		legLinkVanProjectArtifactNaarDependency(artifact);
		voegUsedButUndeclaredStyleToe();
		voegEindeLijnEnNewLineToe();
	}

	public void addUnusedButDeclaredDependency(Artifact artifact) {
		legLinkVanProjectArtifactNaarDependency(artifact);
		voegUnusedButDeclaredStyleToe();
		voegEindeLijnEnNewLineToe();
	}

	public String getDotSource() {
		addEndOfGraph();
		return stringBuilder.toString();
	}

	private void addStartOfGraph() {
		stringBuilder.append("digraph G {");
	}

	private void addEndOfGraph() {
		stringBuilder.append("}");
	}

	private String currentMavenArtifactAsString() {
		return mavenArtifactAsString(mavenProjectToAnalyze.getArtifact());
	}

	private String mavenArtifactAsString(Artifact artifact) {
		return "\"" + artifact.getGroupId() + ":" + NEW_LINE + artifact.getArtifactId() + "\"";
	}

	private void voegEindeLijnEnNewLineToe() {
		stringBuilder.append(";\n");
	}

	private void voegUsedAndDeclaredStyleToe() {
		stringBuilder.append(" [color=\"green\"]");
	}

	private void voegUnusedButDeclaredStyleToe() {
		stringBuilder.append(" [color=\"red\"]");
	}

	private void voegUsedButUndeclaredStyleToe() {
		stringBuilder.append(" [style=\"dashed\"]");
	}

	private void legLinkVanProjectArtifactNaarDependency(Artifact artifact) {
		stringBuilder.append(currentMavenArtifactAsString());
		stringBuilder.append(EDGE);
		stringBuilder.append(mavenArtifactAsString(artifact));
	}

}