package com.knapp.codingcontest.cc20160408.solution;

import com.knapp.codingcontest.cc20160408.Input;
import com.knapp.codingcontest.cc20160408.data.LocationCollection;
import com.knapp.codingcontest.cc20160408.data.PickOrderCollection;
import com.knapp.codingcontest.cc20160408.data.ProductCollection;
import com.knapp.codingcontest.cc20160408.entities.*;
import com.knapp.codingcontest.cc20160408.util.Contract;

import java.util.*;

public class Solution {

	/**
	 * Your name
	 */
	public final String participantName = "Michael Krickl";
	/**
	 * The Id of your institute - please refer to the handout
	 */
	public final String instituteId = "12";
	/**
	 * local reference to the global location collection
	 */
	private final LocationCollection locationCollection;
	/**
	 * local reference to the global product collection
	 */
	private final ProductCollection productCollection;
	/**
	 * local reference to the global collection with unfulfilled pick-orders
	 * <p>
	 * Note: the pickOrderCollection is always up to date when GetNextReplenishmentOrder is called
	 */
	private final PickOrderCollection pickOrderCollection;

	// ----------------------------------------------------------------------------

	public static final boolean DEBUG = false;

	private Map<String, Integer> quantityOnHand;
	private List<PickOrder> pickOrders;

	/**
	 * Create the solution instance74
	 * <p>
	 * Do all your preparations here
	 *
	 * @param input
	 */
	public Solution(final Input input) {

		Contract.requires(input != null, "illegal argument");

		Contract.requires(input.getLocationCollection() != null, "illegal argument");
		Contract.requires(input.getLocationCollection().count() > 0, "illegal argument");

		Contract.requires(input.getProductCollection() != null, "illegal argument");
		Contract.requires(input.getProductCollection().count() > 0, "illegal argument");

		Contract.requires(input.getPickOrderCollection() != null, "illegal argument");
		Contract.requires(input.getPickOrderCollection().count() > 0, "illegal argument");

		Contract.requires(!Contract.isNullOrWhiteSpace(instituteId), "Please set InstituteId in Solution.java");
		Contract.requires(!Contract.isNullOrWhiteSpace(participantName), "Please set ParticipantName in Solution.java");

		//
		locationCollection = input.getLocationCollection();
		productCollection = input.getProductCollection();
		pickOrderCollection = input.getPickOrderCollection();
		// Your code goes here

		// Prepare custom Collections
		quantityOnHand = new HashMap<>();
		for(Product product : productCollection.getProducts()) {
			quantityOnHand.put(product.getCode(), 0);
		}

		pickOrders = new LinkedList<>(pickOrderCollection.getPickOrders());
		for(PickOrder pickOrder : pickOrderCollection.getPickOrders()) {
			pickOrder.calcReplenishmentsNeeded(quantityOnHand);
		}
	}

	// ----------------------------------------------------------------------------


	/**
	 * return the next replen move for the caller to execute
	 *
	 * @return the next replen move for the caller to execute
	 */
	public ReplenishmentOrder getNextReplenishmentOrder() {
		// The caller (KNAPP code) executes the replen and performs the next possible pick
		// If no replenishment order should be executed in this timeframe, return null

		// add your code here to select the next (best) replen move
		// and return it to the caller

		// return your created ReplenishmentOrder, or null if u do not want to do anything in this frame

		Product product = null;
		Location location = null;
		int quantity = 0;

		Collections.sort(pickOrders);
		product = getNextProduct();

		if(DEBUG) {
			System.out.println("Quantity on Hand:");
			for(Map.Entry<String, Integer> entry : quantityOnHand.entrySet()) {
				if(entry.getValue() != 0) {
					System.out.println("Product: " + entry.getKey() + " Quantity: " + entry.getValue());
				}
			}
			System.out.println("Queue:");
			for(int i = 0; i < pickOrders.size() && i < 3; ++i) {
				System.out.println(pickOrders.get(i).toString());
			}
		}

		if(product != null) {

			location = getNextFreeLocation(product);

			quantity = calcQuantity(product);

			// Update Quantity on Hand
			quantityOnHand.replace(product.getCode(), quantityOnHand.get(product.getCode()) + quantity);

			if(DEBUG) {
				System.out.println("Product: " + product);
				System.out.println("Location: " + location);
				System.out.println("Quantity: " + quantity);
			}

			return new ReplenishmentOrder(product, location, quantity);
		}

		return null;
	}


	private Product getNextProduct() {

		for(PickOrder pickOrder : pickOrders) {
			for(PickOrderLine pickOrderLine : pickOrder.getPickOrderLines()) {
				if(pickOrderLine.getQuantity() > quantityOnHand.get(pickOrderLine.getProductCode())) {
					return productCollection.findByCode(pickOrderLine.getProductCode());
				}
			}
		}
		return null;
	}

	// ............................................................................


	private Location getNextFreeLocation(Product product) {

		for(Location location : locationCollection.getLocations()) {
			if(location.getAssignedProduct() == null) {
				return location;
			}
		}

		//throw new Exception("All Locations occupied");
		return null;
	}


	private int calcQuantity(Product product) {

		int productQuantityOnHand = quantityOnHand.get(product.getCode());
		int productQuantityNeeded = pickOrderCollection.getCurrentNeededQuantity(product.getCode()) -
		                            productQuantityOnHand;

		return productQuantityNeeded > product.getMaxLocationQuantity() ?
		       product.getMaxLocationQuantity() :
		       productQuantityNeeded;
	}

	// ----------------------------------------------------------------------------


	/**
	 * This function is called after all picks have been performed by the framework.
	 * <p>
	 * If necessary, you can handle it.
	 * Note: the pickOrderCollection is also updated and always reflects the current state,
	 * which meansm the id's in pickedOrderIds can no longer be found in the pickOrderCollection
	 *
	 * @param pickedOrders read-only collection with the ids of the order that have been picked
	 */
	public void handlePickedOrders(final List<PickOrder> pickedOrders) {

		Contract.requires(pickedOrders != null, "illegal argument");

		// Your code goes here - if needed

		if(DEBUG) {
			new Scanner(System.in).nextLine();
		}

		if(!pickedOrders.isEmpty()) {

			// Get Products from the picked Orders
			Map<String, Integer> removedProducts = new LinkedHashMap<>();
			for(PickOrder pickedOrder : pickedOrders) {
				for(PickOrderLine pickedOrderLines : pickedOrder.getPickOrderLines()) {
					if(!removedProducts.containsKey(pickedOrderLines.getProductCode())) {
						removedProducts.put(pickedOrderLines.getProductCode(), pickedOrderLines.getQuantity());
					} else {
						removedProducts.replace(pickedOrderLines.getProductCode(),
						                        removedProducts.get(pickedOrderLines.getProductCode()) +
						                        pickedOrderLines.getQuantity());
					}
				}
			}

			// Update Quantity on Hand
			for(Map.Entry<String, Integer> entry : removedProducts.entrySet()) {
				if(DEBUG) {
					System.out.println("Remove " + entry.getValue() + " pieces of " + entry.getKey());
				}

				quantityOnHand.replace(entry.getKey(), quantityOnHand.get(entry.getKey()) - entry.getValue());
			}

			// Performance optimization
			if(DEBUG) {
				System.out.println("Remove Pick Orders " + pickedOrders + " from Queue");
			}

			pickOrders.removeAll(pickedOrders);
			for(PickOrder pickOrder : pickOrders) {
				for(String productCode : removedProducts.keySet()) {
					if(pickOrder.containsProduct(productCode)) {
						pickOrder.calcReplenishmentsNeeded(quantityOnHand);
						if(DEBUG) {
							System.out.println("Recalculating: " + pickOrder);
						}
						break;
					}
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
}
