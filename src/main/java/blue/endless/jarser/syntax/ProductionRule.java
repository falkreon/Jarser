package blue.endless.jarser.syntax;

import java.util.ArrayList;

public class ProductionRule {
	protected ArrayList<ProductionRule> alternatives = new ArrayList<>();
	
	protected String pattern;
	protected boolean isLiteral = false;
}
