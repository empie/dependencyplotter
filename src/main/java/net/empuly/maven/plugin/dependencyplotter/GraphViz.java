package net.empuly.maven.plugin.dependencyplotter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

public class GraphViz {

	private static String TEMP_DIR = "c:/temp"; // Windows

	private static String GRAPHVIZ_DOT_EXE_LOCATION = "C:/Program Files (x86)/Graphviz2.34/bin/dot.exe"; // Windows

	private static String OUTPUT_DIRECTORY = "C:/development/dependencyplotter-maven-plugin/sources/output/";

	/**
	 * The source of the graph written in dot language.
	 */
	private StringBuilder graph = new StringBuilder();

	/**
	 * Constructor: creates a new GraphViz object that will contain a graph.
	 */
	public GraphViz() {
	}

	/**
	 * Adds a string to the graph's source (without newline).
	 */
	public void add(String line) {
		graph.append(line);
	}

	/**
	 * Adds a newline to the graph's source.
	 */
	public void addln() {
		graph.append('\n');
	}

	/**
	 * Adds a string to the graph's source (with newline).
	 */
	public void addln(String line) {
		graph.append(line + "\n");
	}

	/**
	 * Returns a string that is used to end a graph.
	 * 
	 * @return A string to close a graph.
	 */
	public String end_graph() {
		return "}";
	}

	/**
	 * Returns the graph's source description in dot language.
	 * 
	 * @return Source of the graph in dot language.
	 */
	public String getDotSource() {
		return graph.toString();
	}

	/**
	 * Returns the graph as an image in binary format.
	 * 
	 * @param dotSource
	 *            Source of the graph to be drawn.
	 * @param type
	 *            Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
	 * @return A byte array containing the image of the graph.
	 */
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
			ioe.printStackTrace();
		} catch (java.lang.InterruptedException ie) {
			ie.printStackTrace();
		}

	}

	/**
	 * Read a DOT graph from a text file.
	 * 
	 * @param input
	 *            Input text file containing the DOT graph source.
	 */
	public void readSource(String input) {
		StringBuilder sb = new StringBuilder();

		try {
			FileInputStream fis = new FileInputStream(input);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			dis.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		graph = sb;
	}

	/**
	 * Returns a string that is used to start a graph.
	 * 
	 * @return A string to open a graph.
	 */
	public String start_graph() {
		return "digraph G {";
	}

	/**
	 * Writes the graph's image in a file.
	 * 
	 * @param img
	 *            A byte array containing the image of the graph.
	 * @param to
	 *            A File object to where we want to write.
	 * @return Success: 1, Failure: -1
	 */
	public void writeGraphToFile(byte[] img, File to) {
		try {
			FileUtils.writeByteArrayToFile(to, img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * It will call the external dot program, and return the image in binary format.
	 * 
	 * @param dot
	 *            Source of the graph (in dot language).
	 * @param type
	 *            Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
	 * @return The image of the graph in .gif format.
	 */
	private byte[] get_img_stream(File dot, String type) {
		File img;
		byte[] img_stream = null;

		try {
			img = File.createTempFile("graph_", "." + type, new File(GraphViz.TEMP_DIR));
			Runtime rt = Runtime.getRuntime();

			// patch by Mike Chenault
			String[] args = { GRAPHVIZ_DOT_EXE_LOCATION, "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath() };
			Process p = rt.exec(args);

			p.waitFor();

			FileInputStream in = new FileInputStream(img.getAbsolutePath());
			img_stream = new byte[in.available()];
			in.read(img_stream);
			// Close it if we need to
			if (in != null) {
				in.close();
			}

			if (img.delete() == false) {
				System.err.println("Warning: " + img.getAbsolutePath() + " could not be deleted!");
			}
		} catch (java.io.IOException ioe) {
			System.err.println("Error:    in I/O processing of tempfile in dir " + GraphViz.TEMP_DIR + "\n");
			System.err.println("       or in calling external command");
			ioe.printStackTrace();
		} catch (java.lang.InterruptedException ie) {
			System.err.println("Error: the execution of the external program was interrupted");
			ie.printStackTrace();
		}

		return img_stream;
	}

	/**
	 * Writes the source of the graph in a file, and returns the written file as a File object.
	 * 
	 * @param str
	 *            Source of the graph (in dot language).
	 * @return The file (as a File object) that contains the source of the graph.
	 */
	private File writeDotSourceToFile(String str) throws java.io.IOException {
		File temp;
		try {
			temp = File.createTempFile("graph_", ".dot.tmp", new File(GraphViz.TEMP_DIR));
			FileWriter fout = new FileWriter(temp);
			fout.write(str);
			fout.close();
		} catch (Exception e) {
			System.err.println("Error: I/O error while writing the dot source to temp file!");
			return null;
		}
		return temp;
	}

} // end of class GraphViz

