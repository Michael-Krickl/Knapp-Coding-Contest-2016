package com.knapp.codingcontest.cc20160408.util;

import com.knapp.codingcontest.cc20160408.entities.ReplenishmentOrder;

import java.io.*;
import java.util.List;
import java.util.Map.Entry;

/**
 * Write for result file
 * <p>
 * Should not be modified
 */
public class ResultWriter {

	private BufferedWriter resultFileWriter;


	/**
	 * Create a result writer that will write to the given file
	 * An existing file is deleted
	 *
	 * @param fileName
	 */
	public ResultWriter(final String fileName)
			throws FileNotFoundException {

		Contract.requires(!Strings.isNullOrWhiteSpace(fileName), "filename required but is null or whitespace");

		final File file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}

		resultFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
	}


	/**
	 * Write the results within the collection into the file
	 *
	 * @param result the list with results as Tuples (tick, replenOrder)
	 *
	 * @throws IOException
	 */
	public void write(final List<Entry<Integer, ReplenishmentOrder>> result)
			throws IOException {

		Contract.requires(result != null, "result mandatory but is required");

		for(final Entry<Integer, ReplenishmentOrder> cro : result) {
			Contract.requires(null != cro.getKey(), "illegal argument");
			Contract.requires(null != cro.getValue(), "illegal argument");

			final String resultLine = String.format("%d;%s;%s;%s;%d;",
			                                        cro.getKey(),
			                                        cro.getValue().getOrderId(),
			                                        cro.getValue().getReplenishedProductCode(),
			                                        cro.getValue().getReplenishedLocation(),
			                                        cro.getValue().getReplenishedQuantity());

			resultFileWriter.write(resultLine);
			resultFileWriter.newLine();
		}
		resultFileWriter.flush();
	}


	public void close()
			throws IOException {

		if(resultFileWriter != null) {
			resultFileWriter.flush();
			resultFileWriter.close();
			resultFileWriter = null;
		}
	}
}
