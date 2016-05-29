package com.knapp.codingcontest.cc20160408.data;

import com.knapp.codingcontest.cc20160408.entities.Location;
import com.knapp.codingcontest.cc20160408.util.Contract;
import com.knapp.codingcontest.cc20160408.util.CsvReader;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocationCollection {

	private final Map<String, Location> locations = new LinkedHashMap<String, Location>();

	// ----------------------------------------------------------------------------


	private LocationCollection() {

	}

	// ----------------------------------------------------------------------------


	/**
	 * Create a ProductColleciton and load products from given csv
	 *
	 * @param fullFilename
	 *
	 * @return newly created instance
	 *
	 * @throws Exception
	 */
	public static LocationCollection createFromCsv(final String fullFilename)
			throws Exception {

		Contract.requires(!Contract.isNullOrWhiteSpace(fullFilename), "fullFilename mandatory but is null or whitespace");

		final LocationCollection locationCollection = new LocationCollection();

		for(final Location location : CsvReader.readCsvFile(fullFilename, Location.class)) {
			locationCollection.add(location);
		}
		System.err.println(String.format("+++ loaded: %d products", locationCollection.count()));

		return locationCollection;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Add a location to the collection
	 *
	 * @param location
	 */
	private void add(final Location location) {

		locations.put(location.getCode(), location);
	}


	/**
	 * Get the number of locations currently available
	 *
	 * @return
	 */
	public final int count() {

		return locations.size();
	}

	// ----------------------------------------------------------------------------


	/**
	 * Get the location with the given code
	 *
	 * @param code code of the location to return
	 *
	 * @return location with the given code, null otherwise
	 */
	public Location findByCode(final String code) {

		Contract.requires(code != null, "code must not be <null>");

		if(locations.containsKey(code)) {
			return locations.get(code);
		}

		return null;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Get an iterator for all locations
	 */
	public Collection<Location> getLocations() {

		return Collections.unmodifiableCollection(locations.values());
	}

	// ----------------------------------------------------------------------------
}
