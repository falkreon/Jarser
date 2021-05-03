/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.json;

import blue.endless.jarser.syntax.ElementProductionRule;
import blue.endless.jarser.syntax.RepetitionProductionRule;
import blue.endless.jarser.syntax.SequenceProductionRule;
import blue.endless.jarser.syntax.Grammar;

public class JsonSyntax extends Grammar {
	public JsonSyntax() {
		SequenceProductionRule object = SequenceProductionRule.of(
				"object",
				ElementProductionRule.matchLiteral("{"),
				RepetitionProductionRule.with(
						ElementProductionRule.matchProduction("keyValuePair"),
						RepetitionProductionRule.RepetitionType.ZERO_OR_MORE
						),
				ElementProductionRule.matchLiteral("}")
				);
		
		// '{'
		// KEY_VALUE_PAIR*
		// '}'
		
		//ProductionRule keyValuePair = new ProductionRule();
		// STRING | UNQUOTED_STRING
		// ':'
		// ELEMENT
		
		//ProductionRule unquotedString = new ProductionRule();
		// [a..zA..Z0..9_]+
		
		//ProductionRule quotedString = new ProductionRule();
		// '"' * '"'
		
		//ProductionRule element = new ProductionRule();
		
	}
}
