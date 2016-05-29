package com.knapp.codingcontest.cc20160408.entities;

import com.knapp.codingcontest.cc20160408.util.Contract;

import java.util.*;

/**
 * A PickOrder (Kommissionierauftrag) which should be packed in the warehouse with the current assignment of products
 */
public class PickOrder
		implements Comparable<PickOrder> {

	/**
	 * the unique Id for this order
	 */
	private final String orderId;
	private final List<PickOrderLine> orderLines = new ArrayList<PickOrderLine>();
	private int replenishmentsNeeded;


	/**
	 * Create a PickOrder instance with the given id
	 *
	 * @param orderId OrderId to use
	 */
	public PickOrder(final String orderId) {

		Contract.requires(!Contract.isNullOrWhiteSpace(orderId), "orderId mandatory but is null");

		this.orderId = orderId;
	}

	// ----------------------------------------------------------------------------


	public void calcReplenishmentsNeeded(Map<String, Integer> quantityOnHand) {

		replenishmentsNeeded = 0;

		for(PickOrderLine pickOrderLine : getPickOrderLines()) {
			if(quantityOnHand.get(pickOrderLine.getProductCode()) < pickOrderLine.getQuantity()) {
				++replenishmentsNeeded;
			}
		}
	}

	// ----------------------------------------------------------------------------


	/**
	 * Get iterator for the PickOrderLines in this order
	 *
	 * @return
	 */
	public Collection<PickOrderLine> getPickOrderLines() {

		return Collections.unmodifiableCollection(orderLines);
	}


	public String getOrderId() {

		return orderId;
	}


	public int getLineCount() {

		return orderLines.size();
	}


	/**
	 * Add a product to this order
	 * <p>
	 * Adds a new pickorderline for this product
	 *
	 * @param pol the pickOrderLine to add
	 */
	public void add(final PickOrderLine pol) {

		Contract.requires(pol != null, "pickOrderLine mandatory but is null");
		orderLines.add(pol);
	}


	/**
	 * Whether a certain product is already in the order
	 *
	 * @param productCode code of the product to check
	 *
	 * @return true when product is found in this order, false in all other cases
	 */
	public boolean containsProduct(final String productCode) {

		Contract.requires(!Contract.isNullOrWhiteSpace(productCode), "productCode mandatory but is null or whitespace");

		for(final PickOrderLine ol : orderLines) {
			if(productCode.equals(ol.getProductCode())) {
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {

		return getPickOrderLines().toString();
	}


	@Override
	public int compareTo(PickOrder pickOrder) {

		return this.replenishmentsNeeded - pickOrder.replenishmentsNeeded;
	}

	// ----------------------------------------------------------------------------
}
