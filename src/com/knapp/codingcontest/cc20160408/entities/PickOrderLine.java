package com.knapp.codingcontest.cc20160408.entities;

import com.knapp.codingcontest.cc20160408.util.Contract;

/**
 * Represents a product that must be picked for a PickOrder
 */
public class PickOrderLine {

	/**
	 * The id of the order this line belongs to
	 */
	private final String orderId;

	/**
	 * The code of the product to pack
	 */
	private final String productCode;

	/**
	 * The number of items
	 */
	private final int quantity;

	// ----------------------------------------------------------------------------


	/**
	 * Create a PickorderLine for the given product
	 *
	 * @param dataAsArray string array containing the data from the csv file
	 */
	public PickOrderLine(final String[] dataAsArray) {

		Contract.requires(dataAsArray.length == 3, "data array must contain three elemetns for PickOrderLine");
		Contract.requires(!Contract.isNullOrWhiteSpace(dataAsArray[0]), "orderId required but is null or whitespace only");
		Contract.requires(!Contract.isNullOrWhiteSpace(dataAsArray[1]),
		                  "productCode required but is null or whitespace only");
		Contract.requires(!Contract.isNullOrWhiteSpace(dataAsArray[2]),
		                  "quantity required but is null or whitespace " + "only");

		orderId = dataAsArray[0].trim();
		productCode = dataAsArray[1].trim();
		quantity = Integer.parseInt(dataAsArray[2]);
	}

	// ----------------------------------------------------------------------------


	public String getOrderId() {

		return orderId;
	}


	public String getProductCode() {

		return productCode;
	}


	public int getQuantity() {

		return quantity;
	}

	// ----------------------------------------------------------------------------


	/**
	 * stringified partial representation of this instance
	 */
	@Override
	public String toString() {

		return productCode + ": " + quantity + "pcs";
	}
}
