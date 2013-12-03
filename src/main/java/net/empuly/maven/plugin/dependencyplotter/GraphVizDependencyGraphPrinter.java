package net.empuly.maven.plugin.dependencyplotter;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class GraphVizDependencyGraphPrinter {

	private static String GRAPHVIZ_DOT_EXE_LOCATION = "C:/Program Files (x86)/Graphviz2.34/bin/dot.exe";

	private static String OUTPUT_DIRECTORY = "C:/development/dependencyplotter-maven-plugin/sources/output/";

	public void printGraph(String dotSource, String type) {
		try {
			File tempFileDotSource = File.createTempFile("dependencyplotter", "dotSource");
			File tempFileOutputImage = File.createTempFile("dependencyplotter", "image");
			FileUtils.writeStringToFile(tempFileDotSource, dotSource);

			Runtime runtime = Runtime.getRuntime();
			String outputBestandstype = "-T" + type;
			String[] args = { GRAPHVIZ_DOT_EXE_LOCATION, outputBestandstype, tempFileDotSource.getAbsolutePath(), "-o",
					tempFileOutputImage.getAbsolutePath() };
			Process graphVizProcess = runtime.exec(args);
			graphVizProcess.waitFor();

			FileUtils.copyFile(tempFileOutputImage, new File(OUTPUT_DIRECTORY, "output." + type));
			FileUtils.forceDelete(tempFileOutputImage);
			FileUtils.forceDelete(tempFileDotSource);

		} catch (java.io.IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (java.lang.InterruptedException ie) {
			throw new RuntimeException(ie);
		}

	}

}
