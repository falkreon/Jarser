/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.test;

import org.junit.Test;

import blue.endless.jarser.syntax.JarserJarser;
import blue.endless.jarser.syntax.ProductionRule;
import blue.endless.jarser.syntax.Syntax;
import blue.endless.jarser.syntax.SyntaxException;

public class TestLexer {
	
	@Test
	public void testJNF() throws SyntaxException {
		Syntax syntax = JarserJarser.makeSyntax("\"object\" : \"{\" keyValuePair* \"}\";");
		//ProductionRule rule = JarserJarser.parseRule("\"object\" : \"{\" keyValuePair* \"}\"");
		for(ProductionRule rule : syntax.getProductionRules()) {
			System.out.println(rule.getName()+" = "+rule);
		}
	}
}
