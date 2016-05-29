package com.knapp.codingcontest.cc20160408.util;

import com.knapp.codingcontest.cc20160408.Program;
import com.knapp.codingcontest.cc20160408.Settings;
import com.knapp.codingcontest.cc20160408.data.LocationCollection;
import com.knapp.codingcontest.cc20160408.data.PickOrderCollection;
import com.knapp.codingcontest.cc20160408.data.ProductCollection;
import com.knapp.codingcontest.cc20160408.entities.*;
import com.knapp.codingcontest.cc20160408.solution.Solution;

import java.util.*;

/**
 * Changes in this class will only impact your local code,
 * the evaluation code on the server (determining your rating) will not be affetcted.
 * <p>
 * So, if you are going to make any changes here - make sure the algorithms aren't changed
 * (e.g: order of order- and location-evaluation in tryPickOrders())
 */
public class Scheduler {
	// ----------------------------------------------------------------------------

	private final ProductCollection productCollection;
	private final PickOrderCollection pickOrderCollection;
	private final LocationCollection locationCollection;

	// ----------------------------------------------------------------------------


	/**
	 * Construct a framework for the simulation of the day
	 *
	 * @param productCollection   products for the day
	 * @param pickOrderCollection orders for the day
	 * @param locationCollection  zone with all locations
	 * @param resultFilename      name of the file for the result
	 *
	 * @throws Exception
	 */
	public Scheduler(final ProductCollection productCollection,
	                 final PickOrderCollection pickOrderCollection,
	                 final LocationCollection locationCollection)
			throws Exception {

		Contract.requires(productCollection != null, "illegal argument");
		Contract.requires(pickOrderCollection != null, "illegal argument");
		Contract.requires(locationCollection != null, "illegal argument");

		this.productCollection = productCollection;
		this.pickOrderCollection = pickOrderCollection;
		this.locationCollection = locationCollection;
	}


	/**
	 * Main simulation loop
	 * <p>
	 * It performs the following actions in this sequence:
	 * (1) call GetNextReplenOrder to get the replen move from your solution
	 * (2) executes the replen order and adds the stock to the locations
	 * (3) picks all orders that have sufficient stock on the locations in
	 * in the sequence as the orders are listed in the pickOrderCollection
	 * (4) calls HandlePick in your solution with the ids of all orders that have been picked
	 *
	 * @param solution
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public List<Map.Entry<Integer, ReplenishmentOrder>> run(final Solution solution)
			throws Exception {

		Contract.requires(null != solution, "solution required but is null");

		System.err.println(String.format("### Starting work..."));
		final List<Map.Entry<Integer, ReplenishmentOrder>> replenOrders =
				new ArrayList<Map.Entry<Integer, ReplenishmentOrder>>();

		int currentTick = 0;
		while((pickOrderCollection.count() > 0) && (currentTick < Settings.MAX_ALLOWED_CYCLES)) {
			final ReplenishmentOrder replenOrder;
			try {
				replenOrder = solution.getNextReplenishmentOrder();
			} catch(final Exception e) {
				Program.showException(e, "USER-Code");
				throw e;
			}

			try {
				if(replenOrder != null) {
					replenOrders.add(new AbstractMap.SimpleEntry<Integer, ReplenishmentOrder>(Integer.valueOf(currentTick),
					                                                                          replenOrder));
					if(!refillLocations(replenOrder, currentTick)) {
						System.err.println("### Ending because of error during replenishment.");
						break;
					}
				}

				final List<PickOrder> pickedOrders = tryPickOrders(currentTick);
				solution.handlePickedOrders(Collections.unmodifiableList(pickedOrders));

				if(pickOrderCollection.lineCount() == 0) {
					System.err.println("### Congratulations, all pick orders done!");
					break;
				}
			} catch(final Exception e) {
				System.err.println(String.format("!!! Exception in GetNextReplenOrder @ %d", currentTick));
				System.err.println(String.format("   %s", e.getClass().getName()));
				System.err.println(String.format("   %s", e.getMessage()));
				e.printStackTrace(System.err);
				throw e;
			}
			currentTick++;
		}

		System.err.println(String.format("### Ending work @ %d", currentTick));
		if(pickOrderCollection.count() > 0) {
			System.err.println(String.format("### Remaining work: %d orders with %d lines",
			                                 pickOrderCollection.count(),
			                                 pickOrderCollection.lineCount()));
		}
		return replenOrders;
	}


	/**
	 * Execute a replenishment order
	 * <p>
	 * add the quantities within the replenishment order to the locations specified
	 *
	 * @param replenishmentOrder the order to execute
	 * @param currentTick        current cycle number
	 *
	 * @return
	 */
	private boolean refillLocations(final ReplenishmentOrder replenishmentOrder, final int currentTick) {

		final Location target = locationCollection.findByCode(replenishmentOrder.getReplenishedLocation());
		if(target == null) {
			System.err.println(String.format("ERROR @ %d: target location '%s' not found for order %s: ",
			                                 currentTick,
			                                 replenishmentOrder.getReplenishedLocation(),
			                                 replenishmentOrder.getOrderId()));
			return false;
		}

		final Product product = productCollection.findByCode(replenishmentOrder.getReplenishedProductCode());

		if(null == product) {
			throw new IllegalArgumentException("REFILL: Trying to assign an unknown product: " +
			                                   replenishmentOrder.getReplenishedProductCode());
		}

		if((target.getAssignedProduct() != null) &&
		   !target.getAssignedProduct().getCode().equals(product.getCode()) &&
		   (target.getQuantityOnHand() > 0)) {
			throw new IllegalStateException("REFILL: Already a different product at the location: " +
			                                target.getAssignedProduct().getCode());
		}

		if((target.getQuantityOnHand() + replenishmentOrder.getReplenishedQuantity()) > product.getMaxLocationQuantity()) {
			throw new IllegalArgumentException("REFILL: Exceeded maximum location amount");
		}

		target.setAssignedProduct(product);
		target.setQuantityOnHand(target.getQuantityOnHand() + replenishmentOrder.getReplenishedQuantity());

		return true;
	}


	/**
	 * Pick all orders the have sufficient stock on locations
	 * <p>
	 * The orders are searched in the sequence in which they are listed in the pickOrderCollection
	 *
	 * @param currentTick current cycle number
	 *
	 * @return orders that have been picked
	 */
	private List<PickOrder> tryPickOrders(final int currentTick) {
		// prepare a list of products with stock on hand and remember the locations
		final Map<String, ProductLocations> fourWall = getProductsLocations();

		final List<PickOrder> pickedOrders = new ArrayList<PickOrder>();
		for(final PickOrder order : pickOrderCollection.getPickOrders()) {
			boolean isPickable = true;

			for(final PickOrderLine line : order.getPickOrderLines()) {
				if(!fourWall.containsKey(line.getProductCode())) {
					isPickable = false;
					break;
				}
				final long onHand = sumQuantityOnHand(fourWall.get(line.getProductCode()));

				if(onHand < line.getQuantity()) {
					isPickable = false;
					break;
				}
			}

			if(isPickable) {
				for(final PickOrderLine line : order.getPickOrderLines()) {
					int neededQuantity = line.getQuantity();
					for(final Location location : fourWall.get(line.getProductCode()).locations) {
						if(location.getQuantityOnHand() >= neededQuantity) {
							//(remaining) line can be picked from one location
							location.setQuantityOnHand(location.getQuantityOnHand() - neededQuantity);
							if(location.getQuantityOnHand() == 0) {
								location.setAssignedProduct(null);
							}
							break;
						} else {
							//pick all items from location
							final int q = location.getQuantityOnHand();
							location.setQuantityOnHand(0);
							location.setAssignedProduct(null);
							neededQuantity -= q;
						}
					}
				}
				pickedOrders.add(order);
				pickOrderCollection.remove(order);

				if(pickedOrders.size() >= Settings.PICKS_PER_CYCLE) {
					break;
				}
			}
		}

		System.err.println(String.format("[Cyle %d] Picked %d orders, orders left: %d",
		                                 currentTick,
		                                 pickedOrders.size(),
		                                 pickOrderCollection.count()));
		return pickedOrders;
	}


	private Map<String, ProductLocations> getProductsLocations() {

		final Map<String, List<Location>> productsLocations_ = new HashMap<String, List<Location>>();
		for(final Location l : locationCollection.getLocations()) {
			if(l.getQuantityOnHand() > 0) {
				List<Location> pl = productsLocations_.get(l.getAssignedProduct().getCode());
				if(pl == null) {
					pl = new ArrayList<Location>();
					productsLocations_.put(l.getAssignedProduct().getCode(), pl);
				}
				pl.add(l);
			}
		}

		final Map<String, ProductLocations> productsLocations = new HashMap<String, ProductLocations>();
		for(final Map.Entry<String, List<Location>> e : productsLocations_.entrySet()) {
			final ProductLocations pl = new ProductLocations(e.getValue());
			productsLocations.put(e.getKey(), pl);
		}
		return productsLocations;
	}


	private long sumQuantityOnHand(final ProductLocations productLocations) {

		long sum = 0;
		for(final Location l : productLocations.locations) {
			sum += l.getQuantityOnHand();
		}
		return sum;
	}

	// ===========================================================================
	// ===========================================================================

	private static class ProductLocations {

		private final List<Location> locations = new ArrayList<Location>();


		private ProductLocations(final List<Location> locations) {

			this.locations.addAll(locations);
			Collections.sort(this.locations, new Comparator<Location>() {
				public int compare(final Location l1, final Location l2) {

					final int cmp = l1.getQuantityOnHand() - l2.getQuantityOnHand();
					if(cmp != 0) {
						return cmp;
					}
					return l1.getCode().compareTo(l2.getCode());
				}
			});
		}
	}
}
