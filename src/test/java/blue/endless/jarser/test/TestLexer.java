/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import blue.endless.jarser.syntax.JarserJarser;
import blue.endless.jarser.syntax.LexerRule;
import blue.endless.jarser.syntax.ProductionRule;
import blue.endless.jarser.syntax.Syntax;
import blue.endless.jarser.syntax.SyntaxException;

public class TestLexer {
	
	@Test
	public void testSingleLine() throws SyntaxException {
		Syntax syntax = JarserJarser.makeSyntax("\"object\" : \"{\" keyValuePair* \"}\";");
		Assert.assertTrue(syntax.getLexerRules().isEmpty());
		Assert.assertEquals(1, syntax.getProductionRules().size());
		//for(ProductionRule rule : syntax.getProductionRules()) {
		//	System.out.println(rule.getName()+" = "+rule);
		//}
	}
	
	@Test
	public void testFormalBNF() throws SyntaxException, IOException {
		String file = Files.lines(new File("doc/jarser.bnf").toPath()).reduce((String a,String b)->a+'\n'+b).get(); //Is this really the easiest way to turn a file into a string?
		System.out.println(file);
		Syntax syntax = JarserJarser.makeSyntax(file);
		System.out.println("Assembled lexer rules:");
		for(LexerRule rule : syntax.getLexerRules()) {
			System.out.println("  "+rule.getName()+" : "+rule);
		}
		
		System.out.println("Assembled production rules:");
		for(ProductionRule rule : syntax.getProductionRules()) {
			System.out.println("  "+rule.getName()+" : "+rule);
		}
	}
}
