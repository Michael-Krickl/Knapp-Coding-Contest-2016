package com.knapp.codingcontest.cc20160408.util;

public final class Strings {

	private Strings() {

	}

	// ----------------------------------------------------------------------------


	public static boolean isNullOrWhiteSpace(final String string) {

		if(string == null) {
			return true;
		}
		return string.matches("^[ \t]*$");
	}

	// ----------------------------------------------------------------------------
}
