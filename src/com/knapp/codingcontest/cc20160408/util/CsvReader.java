package com.knapp.codingcontest.cc20160408.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class CsvReader {
	// ----------------------------------------------------------------------------


	private CsvReader() {

	}

	// ----------------------------------------------------------------------------


	/**
	 * read lines from a CSV file, create instances of type Target by calling a string[] constructor
	 *
	 * @param fullFileName Full path to the csv file
	 * @param type         type to create
	 *
	 * @return a list of instances or a empty list
	 *
	 * @throws Exception
	 */
	public static <T extends Object> List<T> readCsvFile(final String fullFileName, final Class<T> type)
			throws Exception {

		Contract.requires(!Contract.isNullOrWhiteSpace(fullFileName), "fullFileName mandatory but is null");

		final File file = new File(fullFileName);
		if(!file.exists()) {
			throw new IllegalArgumentException(String.format("CSV-Input file does not exist: '%s'", fullFileName));
		}

		final Constructor<T> ctor = type.getConstructor(new Class[] {String[].class});

		if(null == ctor) {
			throw new IllegalArgumentException(String.format(
					"Target '%s' can not be constructed because it does not contain a ctor with string [] as argument",
					type.getName()));
		}

		final List<T> objects = new ArrayList<T>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line = reader.readLine()) != null) {
				final String[] fields = line.split(";", 0);

				final T t = ctor.newInstance(new Object[] {fields});

				objects.add(t);
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
		return objects;
	}

	// ----------------------------------------------------------------------------
}
