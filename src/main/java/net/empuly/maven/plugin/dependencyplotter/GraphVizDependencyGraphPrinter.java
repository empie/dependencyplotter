package net.empuly.maven.plugin.dependencyplotter;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;

public class GraphVizDependencyGraphPrinter {

	private static final String TYPE = "gif";

	private static String GRAPHVIZ_DOT_EXE_LOCATION = "C:/Program Files (x86)/Graphviz2.34/bin/dot.exe";

	private static String OUTPUT_DIRECTORY = "C:/PrivateWS/jozef/output";

	private DependencyPlotterConfiguration dependencyPlotterConfiguration;

	private MavenProject mavenProjectToAnalyze;

	public GraphVizDependencyGraphPrinter(
			DependencyPlotterConfiguration dependencyPlotterConfiguration, MavenProject mavenProjectToAnalyze) {
				this.dependencyPlotterConfiguration = dependencyPlotterConfiguration;
				this.mavenProjectToAnalyze = mavenProjectToAnalyze;
	}

	public void printGraph(String dotSource) {
		try {
			File tempFileDotSource = File.createTempFile("dependencyplotter", "dotSource");
			File tempFileOutputImage = File.createTempFile("dependencyplotter", "image");
			FileUtils.writeStringToFile(tempFileDotSource, dotSource);

			Runtime runtime = Runtime.getRuntime();
			String outputBestandstype = "-T" + TYPE;
			String[] args = { GRAPHVIZ_DOT_EXE_LOCATION, outputBestandstype, tempFileDotSource.getAbsolutePath(), "-o",
					tempFileOutputImage.getAbsolutePath() };
			Process graphVizProcess = runtime.exec(args);
			graphVizProcess.waitFor();

			FileUtils.copyFile(tempFileOutputImage, new File(dependencyPlotterConfiguration.plotOutputDirectory(), outputFileName()+"." + TYPE));
			FileUtils.forceDelete(tempFileOutputImage);
			FileUtils.forceDelete(tempFileDotSource);

		} catch (java.io.IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (java.lang.InterruptedException ie) {
			throw new RuntimeException(ie);
		}

	}

	private String outputFileName() {
		return mavenProjectToAnalyze.getGroupId()+"_"+mavenProjectToAnalyze.getArtifactId();
	}

}
