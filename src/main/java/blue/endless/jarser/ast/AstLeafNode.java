/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.ast;

public class AstLeafNode {
	protected String value;
	
	public String getValue() {
		return value;
	}
	
	public int asInteger() throws NumberFormatException {
		return Integer.parseInt(value);
	}
	
	public double asDouble() throws NumberFormatException {
		return Double.parseDouble(value);
	}
	
	public long asLong() throws NumberFormatException {
		return Long.parseLong(value);
	}
}
