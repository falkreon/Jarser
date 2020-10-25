package blue.endless.jarser.test;

import org.junit.Test;

import blue.endless.jarser.syntax.Lexer;
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
			System.out.println("Token: '"+token.value()+"' named: '"+token.getName()+"'");
			token = lexer.nextToken();
		}
	}
}
