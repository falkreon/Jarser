/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.test;

import java.util.List;

import org.junit.Test;

import blue.endless.jarser.Jarser;
import blue.endless.jarser.syntax.JarserJarser;
import blue.endless.jarser.syntax.Lexer;
import blue.endless.jarser.syntax.Production;
import blue.endless.jarser.syntax.ProductionRule;
import blue.endless.jarser.syntax.SyntaxException;
import blue.endless.jarser.syntax.Token;

public class TestLexer {
	@Test
	public void testFoo() {
		Lexer lexer = new Lexer();
		
		//Various structures
		lexer.addRule("brace_start", "\\{");
		lexer.addRule("brace_end", "\\}");
		lexer.addRule("paren_start", "\\(");
		lexer.addRule("paren_end", "\\)");
		lexer.addRule("bracket_start", "\\[");
		lexer.addRule("bracket_end", "\\]");
		
		lexer.addRule("number", "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
		lexer.addRule("token", "[a-zA-Z]+[a-zA-Z0-9_]*");
		
		lexer.addRule("operator", ">=|<=|!=|==|:=|\\+=|-=|\\*=|::|&&|:|\\.|<|>|=|&|\\||%|!|\\+|-|\\*|\\/|\\^");
		
		lexer.addRule("whitespace", "\\s+");
		
		String subject = "foo<=bar";
		
		lexer.startMatching(subject);
		
		Token token = lexer.nextToken();
		while(token!=null) {
			//System.out.println("Token: '"+token.value()+"' named: '"+token.getName()+"'");
			token = lexer.nextToken();
		}
	}
	
	@Test
	public void testJNF() throws SyntaxException {
		ProductionRule rule = JarserJarser.create("\"object\" = \"{\" keyValuePair* \"}\"");
		System.out.println(rule.getName()+" = "+rule);
		
		
		
		//Jarser jarser = JarserJarser.createJarser();
		//jarser.startMatching("object = \"{\" keyValuePair* \"}\"");
		//List<Production> ast = jarser.applyAll();
		
		//System.out.println(ast);
		/*
		Lexer lexer = new Lexer();
		
		
		lexer.addRule("quoted_string", "\"(?:[^\"\\\\]|\\\\.)*\"");
		lexer.addRule("operator", ">=|<=|!=|==|:=|\\+=|-=|\\*=|::|&&|:|\\.|<|>|=|&|\\||%|!|\\+|-|\\*|\\/|\\^");
		lexer.addRule("token", "[a-zA-Z_]+[a-zA-Z0-9_]*");
		lexer.addRule("whitespace", "\\s+");
		lexer.ignoreToken("whitespace");
		
		String subject = "object = \"{\" keyValuePair* \"}\"";
		
		lexer.startMatching(subject);
		
		Token token = lexer.nextToken();
		while(token!=null) {
			//if(!token.getName().equals("whitespace")) { //ignore whitespace
				System.out.println("Token: '"+token.value()+"' named: '"+token.getName()+"'");
			//}
			token = lexer.nextToken();
		}*/
	}
}
