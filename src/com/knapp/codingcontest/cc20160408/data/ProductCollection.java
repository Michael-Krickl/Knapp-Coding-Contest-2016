package com.knapp.codingcontest.cc20160408.data;

import com.knapp.codingcontest.cc20160408.entities.Product;
import com.knapp.codingcontest.cc20160408.util.Contract;
import com.knapp.codingcontest.cc20160408.util.CsvReader;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductCollection {

	private final Map<String, Product> products = new LinkedHashMap<String, Product>();

	// ----------------------------------------------------------------------------


	/**
	 * A product collection can only be created using CreateFromCsv
	 */
	private ProductCollection() {

	}


	/**
	 * Create a ProductColleciton and load products from given csv
	 *
	 * @param fullFilename full path of the csv
	 *
	 * @return newly created instance
	 *
	 * @throws Exception
	 */
	public static ProductCollection createFromCsv(final String fullFilename)
			throws Exception {

		Contract.requires(!Contract.isNullOrWhiteSpace(fullFilename), "fullFilename mandatory but is null or whitespace");

		final ProductCollection productCollection = new ProductCollection();

		for(final Product product : CsvReader.readCsvFile(fullFilename, Product.class)) {
			productCollection.add(product);
		}
		System.err.println(String.format("+++ loaded: %d products", productCollection.count()));

		return productCollection;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Add a product to the collection
	 *
	 * @param product the product to add
	 */
	private void add(final Product product) {

		Contract.requires(product != null, "product mandatory but is null");

		products.put(product.getCode(), product);
	}


	/**
	 * Get the total number of products currently in this collection
	 *
	 * @return
	 */
	public int count() {

		return products.size();
	}


	/**
	 * Get the product with the given code
	 *
	 * @param productCode code of the product to return
	 *
	 * @return product with the given code if it was found in the collection; null otherwise
	 */
	public Product findByCode(final String productCode) {

		Contract.requires(!Contract.isNullOrWhiteSpace(productCode), "productCode mandatory but is null or whitespace");

		if(products.containsKey(productCode)) {
			return products.get(productCode);
		}

		return null;
	}

	// ----------------------------------------------------------------------------


	/**
	 * Get all products in the collection
	 */
	public Collection<Product> getProducts() {

		return Collections.unmodifiableCollection(products.values());
	}
}
