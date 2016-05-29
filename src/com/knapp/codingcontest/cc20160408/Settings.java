package com.knapp.codingcontest.cc20160408;

import java.io.File;
import java.io.IOException;

/**
 * class containing settings for the program
 */
public final class Settings {
	// ----------------------------------------------------------------------------
	//
	//   MODIFY THESE 2 SETTINGS ONLY IF REALLY NEEDED
	//

	/**
	 * Directory for the input files
	 */
	public static final String dataPath;

	/**
	 * Directory where the output will be written
	 */
	public static final String outputPath;
	/**
	 * Name of the results file
	 */
	public static final String outReplenFilename = "replenishmentOrders.csv";

	// ============================================================================
	//
	//   !!! DO NOT MODIFY THE SETTINGS BELOW !!!
	//
	// ============================================================================
	/**
	 * Name of the properties file
	 */
	public static final String outPropertyFilename = "KCC2016.properties";
	/**
	 * Name of the zip-file that is generated
	 */
	public static final String outZipFilename = "upload2016.zip";
	/**
	 * The max. number of pick orders that will be picked during one cycle
	 */
	public static final int PICKS_PER_CYCLE = 1;
	public static final int MAX_ALLOWED_CYCLES = 20000;

	static {
		try {
			dataPath = new File("./input").getCanonicalPath();
			outputPath = new File("./").getCanonicalPath();
		} catch(final IOException e) {
			throw new RuntimeException(e);
		}
	}

	// ----------------------------------------------------------------------------


	private Settings() {

	}
}
