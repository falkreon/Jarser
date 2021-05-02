/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SyntaxException extends Exception {
	private static final long serialVersionUID = 3138920872324704908L;
	protected Production production;
	
	public SyntaxException(String message) {
		super(message);
	}
	
	public SyntaxException(String message, Production prod) {
		super(message);
		this.production = prod;
	}
	
	@Nullable
	public Production getProduction() {
		return production;
	}
	
	public String getLocationString() {
		if (production==null) return "unknown";
		return "from line "+production.getStartLine()+" char "+production.getStartChar()+" to line "+production.getEndLine()+" char "+production.getEndChar();
	}
}
