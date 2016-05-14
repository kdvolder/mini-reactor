package com.github.kdvolder.minireactor.tests;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import com.google.common.io.CharStreams;

/**
 * Meant to be used by tests that are expected to produce 
 * the same deterministic, printed output on every test run.
 * <p>
 * It provides the means to record and save the output to
 * a file, as well as compare recorded output to a previously
 * recorded output.
 * 
 * @author Kris De Volder
 */
public class OutputVerifier {
	
	private StringBuilder out = new StringBuilder();
	
	private String testKey;
	
	public OutputVerifier(String testKey) {
		this.testKey = testKey;
	}
	
	/**
	 * When this is set to true, then a 'verify_output' which fails to find the
	 * the expected output resource to compare with will save the current output
	 * (so it can be compared with on the next test-run).
	 * <p>
	 * Use with care! This meant to help create 'expected_output' files easily.
	 * But you do not want to leave it on all the time and risk to have tests
	 * accidentally passing when they are missing the expected output file.
	 */
	public boolean RECORD_OUTPUT = false;
	
	/**
	 * When set to true the output sent via 'println' is also shown on System.out.
	 * If set to false it is only recorded silently for comparison to the expected
	 * output.
	 */
	public boolean SHOW_OUTPUT = true;

	/**
	 * Print some recorded output. 
	 */
	public void println(Object msg) {
		if (SHOW_OUTPUT) {
			System.out.println("%out: "+msg);
		}
		out.append(msg+"\n");
	}
	
	public void verify() throws Exception {
		String actualOutput = out.toString();
		String testFileName = testKey+".out.txt";
		InputStream is = this.getClass().getResourceAsStream(testFileName);
		if (is!=null) {
			String expectedOutput = CharStreams.toString(new InputStreamReader(is, "utf8"));
			assertEquals(expectedOutput, actualOutput);
		} else {
			assertTrue(RECORD_OUTPUT, "Expected test output has not been defined '"+testFileName+"'");
			URL myLocation = this.getClass().getResource(this.getClass().getSimpleName()+".class");
			File myFile = new File(myLocation.toURI());
			assertTrue(myFile.toString().contains("/target/test-classes/"));
			File resourcesDir = new File(myFile.getParentFile().toString().replace("/target/test-classes/", "/src/test/resources/"));
			resourcesDir.mkdirs();
			File dataFile = new File(resourcesDir, testFileName);
			try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dataFile), "utf8")) {
				out.write(actualOutput);
			}
		}
	}
}
