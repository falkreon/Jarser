package blue.endless.jarser.json;

import blue.endless.jarser.syntax.ProductionRule;
import blue.endless.jarser.syntax.Syntax;

public class JsonSyntax extends Syntax {
	public JsonSyntax() {
		ProductionRule object = new ProductionRule();
		// '{'
		// KEY_VALUE_PAIR*
		// '}'
		
		ProductionRule keyValuePair = new ProductionRule();
		// STRING | UNQUOTED_STRING
		// ':'
		// ELEMENT
		
		ProductionRule unquotedString = new ProductionRule();
		// [a..zA..Z0..9_]+
		
		ProductionRule quotedString = new ProductionRule();
		// '"' * '"'
		
		ProductionRule element = new ProductionRule();
		
	}
}
