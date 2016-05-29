package com.knapp.codingcontest.cc20160408.data;

import com.knapp.codingcontest.cc20160408.entities.PickOrder;
import com.knapp.codingcontest.cc20160408.entities.PickOrderLine;
import com.knapp.codingcontest.cc20160408.util.Contract;
import com.knapp.codingcontest.cc20160408.util.CsvReader;

import java.util.*;

public class PickOrderCollection {

	private final Map<String, PickOrder> orders = new LinkedHashMap<String, PickOrder>();
	private final List<PickOrderLine> orderLines = new ArrayList<PickOrderLine>();
	private final Map<String, int[]> cacheNeededQuantity = new HashMap<String, int[]>();

	// ----------------------------------------------------------------------------


	/**
	 * PickOrderCollection can only be created via creatFromCsv
	 */
	private PickOrderCollection() {

	}


	/**
	 * load the PickOrders from the csv and create the objects
	 *
	 * @param fullFilename full path of the csv
	 *
	 * @return new PickOrderCollection with all PickOrders and their PickOrderLines from the csv
	 *
	 * @throws Exception
	 */
	public static PickOrderCollection createFromCsv(final String fullFilename)
			throws Exception {

		Contract.requires(!Contract.isNullOrWhiteSpace(fullFilename), "filename mandatory but is null or whitespace only");

		final PickOrderCollection orderCollection = new PickOrderCollection();

		int lineCount = 0;
		for(final PickOrderLine pol : CsvReader.readCsvFile(fullFilename, PickOrderLine.class)) {
			++lineCount;
			final PickOrder order;
			if(orderCollection.orders.containsKey(pol.getOrderId())) {
				order = orderCollection.orders.get(pol.getOrderId());
				if(order == null) {
					throw new IllegalArgumentException("order not found: " + pol.getOrderId());
				}
			} else {
				order = new PickOrder(pol.getOrderId());
				orderCollection.orders.put(order.getOrderId(), order);
			}

			order.add(pol);
			orderCollection.orderLines.add(pol);
		}

		orderCollection.prepareCacheNeededQuantity();

		System.err.println(String.format("+++ loaded: %d orders with %d lines", orderCollection.count(), lineCount));

		return orderCollection;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Sum up the needed quantity for a product and store it
	 * only called initially
	 * <p>
	 * Function used only internally to keep the product/ demand cache up to date
	 */
	private void prepareCacheNeededQuantity() {

		for(final PickOrderLine ol : orderLines) {
			int[] q = cacheNeededQuantity.get(ol.getProductCode());
			if(q == null) {
				q = new int[] {0};
				cacheNeededQuantity.put(ol.getProductCode(), q);
			}
			q[0] += ol.getQuantity();
		}
	}


	/**
	 * The total number of PickOrders in this collection
	 *
	 * @return
	 */
	public int count() {

		return orders.size();
	}


	/**
	 * The total number of lines for all orders
	 *
	 * @return
	 */
	public int lineCount() {

		return orderLines.size();
	}

	// ----------------------------------------------------------------------------


	/**
	 * Retrieve the quantity that is currently needed for the product spcified
	 * for all still open orders
	 *
	 * @param productCode code of the product to get the quantity for
	 *
	 * @return needed pcs, 0 in all other cases (also unkown items)
	 */
	public int getCurrentNeededQuantity(final String productCode) {

		if(cacheNeededQuantity.containsKey(productCode)) {
			return cacheNeededQuantity.get(productCode)[0];
		}
		return 0;
	}


	/**
	 * Get a specific pickorder
	 *
	 * @param orderId id of the pickorder to retrieve
	 *
	 * @return the specified pickorder or null if it could not be found
	 */
	public PickOrder findPickOrder(final String orderId) {

		Contract.requires(orderId == null, "illegal argument: orderId = null");

		return orders.containsKey(orderId) ? orders.get(orderId) : null;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Get all PickOrders
	 */
	public Collection<PickOrder> getPickOrders() {

		return Collections.unmodifiableCollection(orders.values());
	}


	/**
	 * get all the currently pending orderlines
	 *
	 * @return
	 */
	public List<PickOrderLine> getPickOrderLines() {

		return Collections.unmodifiableList(orderLines);
	}


	public void remove(final PickOrder order) {

		Contract.requires(order != null, "order must not be <null>");

		orders.remove(order.getOrderId());

		for(final Iterator<PickOrderLine> it = orderLines.iterator(); it.hasNext(); ) {
			final PickOrderLine ol = it.next();
			if(order.getOrderId().equals(ol.getOrderId())) {
				it.remove();
			}
		}

		decreaseNeededQuantity(order);
	}


	/**
	 * decrease the cached needed quantity for an order
	 * <p>
	 * Function used only internally to keep the product/ demand cache up to date
	 *
	 * @param pickOrder
	 */
	private void decreaseNeededQuantity(final PickOrder pickOrder) {

		for(final PickOrderLine pickOrderLine : pickOrder.getPickOrderLines()) {
			if(cacheNeededQuantity.containsKey(pickOrderLine.getProductCode())) {
				cacheNeededQuantity.get(pickOrderLine.getProductCode())[0] -= pickOrderLine.getQuantity();
			}
		}
	}

	// ----------------------------------------------------------------------------
}
