package com.knapp.codingcontest.cc20160408.entities;

import com.knapp.codingcontest.cc20160408.util.Contract;

public class ReplenishmentOrder {

	/**
	 * internal field to create a 'unique' order id
	 */
	private static int nextOrderId = 1;

	// ----------------------------------------------------------------------------

	/**
	 * Id of this order
	 */
	private final String orderId;

	/**
	 * the code of the product that should be replenished
	 */
	private final String replenishedProductCode;

	/**
	 * the location to which the replenishment should go
	 */
	private final String replenishedLocation;

	/**
	 * the number of pieces to replenis
	 */
	private final int replenishedQuantity;

	// ----------------------------------------------------------------------------


	/**
	 * Create a new replenishment order and assign a generated id
	 */
	public ReplenishmentOrder(final Product product, final Location location, final int quantity) {

		Contract.requires(product != null, "product mandatory but is missing");
		Contract.requires(location != null, "location mandatory but is missing");

		orderId = String.format("ReplenOrder_%d", ReplenishmentOrder.nextOrderId++);
		replenishedProductCode = product.getCode();
		replenishedLocation = location.getCode();
		replenishedQuantity = quantity;
	}

	// ----------------------------------------------------------------------------


	public String getOrderId() {

		return orderId;
	}


	public String getReplenishedProductCode() {

		return replenishedProductCode;
	}


	public String getReplenishedLocation() {

		return replenishedLocation;
	}


	public int getReplenishedQuantity() {

		return replenishedQuantity;
	}

	// ----------------------------------------------------------------------------
}
