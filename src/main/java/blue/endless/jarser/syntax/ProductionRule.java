/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.List;
import java.util.function.Predicate;

public interface ProductionRule extends Predicate<List<Production>> {
	/*
	protected String name;
	
	protected ArrayList<String> elements = new ArrayList<>();
	protected RepetitionType repetiton = RepetitionType.ONE;
	protected boolean isLiteral = false;*/
	/*
	public static enum RepetitionType {
		ONE,
		ONE_OR_MORE,
		ZERO_OR_MORE;
	}*/
	
	public String getName();
	
	@Override
	public boolean test(List<Production> productions);
	
	/**
	 * Consumes elements at the front of the productions List to create the Production indicated by this rule
	 * @param productions the list of Productions so far
	 * @return a Production representing the matched prefix of the list which was consumed.
	 * @throws IllegalArgumentException if test(productions) would have returned false. This method will
	 *         consume elements as it matches them, but if it cannot "correctly" match the production rule,
	 *         the list will end up in an inconsistent, partial-match state, and an error will be thrown.
	 * @return addTo for further composition
	 */
	public Nonterminal produce(List<Production> productions, Nonterminal addTo) throws IllegalArgumentException;
}

/*

Organizational levels and where they sit in the object hierarchy:

Alternatives
	foo | bar

Sequence Group
	(foo bar) baz

Atom
* Repetition rules
* Optional?
* Terminals / Patterns




*/