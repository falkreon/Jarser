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
import blue.endless.jarser.syntax.Lexer;
import blue.endless.jarser.syntax.ProductionRule;
import blue.endless.jarser.syntax.SyntaxException;
import blue.endless.jarser.syntax.Token;

public class TestLexer {
	
	@Test
	public void testJNF() throws SyntaxException {
		ProductionRule rule = JarserJarser.create("\"object\" = \"{\" keyValuePair* \"}\"");
		System.out.println(rule.getName()+" = "+rule);
		
	}
}
