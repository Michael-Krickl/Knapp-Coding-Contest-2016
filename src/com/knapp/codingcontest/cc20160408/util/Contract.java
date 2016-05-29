package com.knapp.codingcontest.cc20160408.util;

public class Contract {

	public static final boolean enabled = false;

	// ----------------------------------------------------------------------------


	public static void requires(final boolean condition, final String message) {

		if(Contract.enabled) {
			if(!condition) {
				throw new IllegalArgumentException(message);
			}
		}
	}

	// ----------------------------------------------------------------------------


	public static boolean isNullOrWhiteSpace(final String string) {

		if(Contract.enabled) {
			return Strings.isNullOrWhiteSpace(string);
		} else {
			if(string == null) {
				return true;
			}
			return "".equals(string);
		}
	}

	// ----------------------------------------------------------------------------
}
