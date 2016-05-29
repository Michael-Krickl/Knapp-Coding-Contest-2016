package com.knapp.codingcontest.cc20160408;

import com.knapp.codingcontest.cc20160408.entities.ReplenishmentOrder;
import com.knapp.codingcontest.cc20160408.solution.Solution;
import com.knapp.codingcontest.cc20160408.util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public final class Program {
	// ----------------------------------------------------------------------------


	public static void main(final String... args) {

		if(!Contract.enabled) {
			System.err.println("### Contract.enabled=false");
		}
		Input input = null;
		try {
			input = Input.createFromCsv();
		} catch(final Exception e) {
			Program.showException(e, "Exception in startup code");
			System.exit(-1);
		}

		try {
			System.err.println("### Your output starts here");

			final Solution solution = new Solution(input);

			Program.writeProperties(solution, Settings.outputPath + File.separator + Settings.outPropertyFilename);

			final Scheduler scheduler = new Scheduler(input.getProductCollection(),
			                                          input.getPickOrderCollection(),
			                                          input.getLocationCollection());
			final List<Map.Entry<Integer, ReplenishmentOrder>> result = scheduler.run(solution);
			ResultWriter writer = null;
			try {
				writer = new ResultWriter(Settings.outputPath + File.separator + Settings.outReplenFilename);
				writer.write(result);
			} finally {
				if(writer != null) {
					writer.close();
				}
			}
			System.err.println("### Your output stops here");

			PrepareUpload.CreateZipFile();
			System.err.println(">>> Created " + Settings.outZipFilename);
		} catch(final Exception e) {
			Program.showException(e, "Exception in application code");
		}
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------


	/**
	 * Write exception to System.err (stderr)
	 *
	 * @param exception   exception that should be shown
	 * @param codeSegment segment where the exception was caught
	 */
	public static void showException(final Exception exception, final String codeSegment) {

		Contract.requires(exception != null, "exception is mandatory but is null");
		Contract.requires(!Contract.isNullOrWhiteSpace(codeSegment), "codeSegment is mandatory but is null or whitespace");

		System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.err.println(codeSegment);
		System.err.println(String.format("[%s]: %s", exception.getClass().getName(), exception.getMessage()));
		exception.printStackTrace(System.err);
		System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------


	/**
	 * Helper function to write the properties to the file
	 *
	 * @param solution
	 * @param outFilename
	 *
	 * @throws Exception when either solution.InstituteId or solution.ParticipantName is not valid
	 */
	private static void writeProperties(final Solution solution, final String outFilename)
			throws Exception {

		if(Strings.isNullOrWhiteSpace(solution.participantName)) {
			throw new IllegalArgumentException("solution.ParticipantName must not be empty - please set to correct value");
		}
		if(Strings.isNullOrWhiteSpace(solution.instituteId)) {
			throw new IllegalArgumentException("solution.InstituteId must not be empty - please set to correct value");
		}

		final File file = new File(outFilename);
		if(file.exists()) {
			file.delete();
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

			writer.write("# -*- conf-javaprop -*-");
			writer.newLine();
			writer.write(String.format("participant = %s %s", solution.instituteId.trim(), solution.participantName.trim()));
			writer.newLine();
			writer.write("technology = java");
			writer.newLine();
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
	}
}
