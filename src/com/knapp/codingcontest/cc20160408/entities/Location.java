package com.knapp.codingcontest.cc20160408.entities;

import com.knapp.codingcontest.cc20160408.util.Contract;

/**
 * represents a location within a shelf that can hold one single product
 */
public class Location {

	private final String code;

	/**
	 * The currently assigned product
	 */
	private Product assignedProduct = null;

	/**
	 * The current quantity that is physically available at this location
	 */
	private int quantityOnHand = 0;

	// ----------------------------------------------------------------------------


	/**
	 * Construct the zone from the given data
	 *
	 * @param dataAsArray data to construct this instance from
	 */
	public Location(final String[] dataAsArray) {

		Contract.requires(dataAsArray != null, "dataAsArray mandatory but is null");
		Contract.requires(dataAsArray.length == 3, "location record must have 3 fields");
		Contract.requires(!Contract.isNullOrWhiteSpace(dataAsArray[0]), "zone name must be set");
		Contract.requires(!Contract.isNullOrWhiteSpace(dataAsArray[2]), "location code must be set");

		code = dataAsArray[2];
	}

	// ----------------------------------------------------------------------------


	public Product getAssignedProduct() {

		return assignedProduct;
	}


	public void setAssignedProduct(Product assignedProduct) {

		this.assignedProduct = assignedProduct;
	}


	public int getQuantityOnHand() {

		return quantityOnHand;
	}


	public void setQuantityOnHand(int quantityOnHand) {

		this.quantityOnHand = quantityOnHand;
	}


	public String getCode() {

		return code;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Get a stringified representation of the object
	 */
	@Override
	public String toString() {

		if(assignedProduct != null) {
			return String.format("[%s] holds '%s', pcs: %d", code, assignedProduct.getCode(), quantityOnHand);
		} else {
			return String.format("[%s] is unassigned", code);
		}
	}

	// ----------------------------------------------------------------------------
}
