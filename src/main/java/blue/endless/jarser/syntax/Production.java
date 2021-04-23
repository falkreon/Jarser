/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.List;

/**
 * A node of the syntax tree built by jarser. A Production is either a Token or a Nonterminal. Only those two subclasses are allowed.
 */
public interface Production {
	public String getName();
	public CharSequence value();
	public int getStartLine();
	public int getStartChar();
	public int getEndLine();
	public int getEndChar();
	
	public List<Production> getChildren();
	public boolean isTerminal();
}
