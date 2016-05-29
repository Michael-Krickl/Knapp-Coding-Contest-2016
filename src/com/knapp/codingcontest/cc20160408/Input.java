package com.knapp.codingcontest.cc20160408;

import com.knapp.codingcontest.cc20160408.data.LocationCollection;
import com.knapp.codingcontest.cc20160408.data.PickOrderCollection;
import com.knapp.codingcontest.cc20160408.data.ProductCollection;

import java.io.File;

/**
 * Container class for all input into the solution
 */
public class Input {
	// ----------------------------------------------------------------------------

	private LocationCollection locationCollection;
	private ProductCollection productCollection;
	private PickOrderCollection pickOrderCollection;

	// ----------------------------------------------------------------------------


	/**
	 * Create from outside only via CreateFromCsv
	 */
	protected Input() {

	}


	/**
	 * Load all input data from the csv files and create instance (and composite instances)
	 *
	 * @return a newly created instance of the input
	 *
	 * @throws Exception
	 */
	public static Input createFromCsv()
			throws Exception {

		final Input input = new Input();
		input.locationCollection = LocationCollection.createFromCsv(Settings.dataPath + File.separator + "locations.csv");
		input.productCollection = ProductCollection.createFromCsv(Settings.dataPath + File.separator + "products.csv");
		input.pickOrderCollection = PickOrderCollection.createFromCsv(Settings.dataPath +
		                                                              File.separator +
		                                                              "pickorders.csv");
		return input;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Container for all locations in the warehouse
	 *
	 * @return
	 */
	public LocationCollection getLocationCollection() {

		return locationCollection;
	}


	/**
	 * Container for all products that have to be slotted (assigned to locations)
	 *
	 * @return
	 */
	public ProductCollection getProductCollection() {

		return productCollection;
	}


	/**
	 * Container for all pickOrders that will be picked out of the warehouse
	 * <p>
	 * You do not need to implement picking, this will be done by KNAPP during
	 * evaluation
	 *
	 * @return
	 */
	public PickOrderCollection getPickOrderCollection() {

		return pickOrderCollection;
	}

	// ----------------------------------------------------------------------------
}
