package com.knapp.codingcontest.cc20160408.entities;

import com.knapp.codingcontest.cc20160408.util.Contract;

/**
 * A product that is handled in a warehouse
 */
public class Product {

	/**
	 * Unique code of this product
	 */
	private final String code;

	/**
	 * The maximum number of items that can be stored in a pick location
	 */
	private final int maxLocationQuantity;

	/**
	 * Flag wether this item is a fast mover
	 */
	private final boolean fastMover;

	// ----------------------------------------------------------------------------


	/**
	 * Create a product from the given data
	 *
	 * @param dataAsArray
	 */
	public Product(final String[] dataAsArray) {

		Contract.requires(dataAsArray != null, "dataAsArray mandatory but is null");
		Contract.requires(dataAsArray.length == 3, "dataAsArray must contain 3 elements");
		Contract.requires(!Contract.isNullOrWhiteSpace(dataAsArray[0]), "Code must not be null @ offset 0");

		code = dataAsArray[0].trim();
		maxLocationQuantity = Integer.parseInt(dataAsArray[1]);
		fastMover = Boolean.parseBoolean(dataAsArray[2]);
	}

	// ----------------------------------------------------------------------------


	public boolean isFastMover() {

		return fastMover;
	}


	public String getCode() {

		return code;
	}


	public int getMaxLocationQuantity() {

		return maxLocationQuantity;
	}

	// ----------------------------------------------------------------------------


	@Override
	public int hashCode() {

		return code.hashCode();
	}

	// ----------------------------------------------------------------------------


	/**
	 * true when obj is a Product and has the same code, false in any other case
	 */
	@Override
	public boolean equals(final Object obj) {

		final Product other = (Product) obj;
		return (other != null) && (code == other.code);
	}


	/**
	 * Get stringified representation of this instance
	 */
	@Override
	public String toString() {

		return "Item " + code;
	}

	// ----------------------------------------------------------------------------
}
