package blue.endless.jarser.syntax;

import java.util.List;

public class ElementProductionRule implements ProductionRule {
	String elementName;
	boolean isLiteral = true;
	
	protected ElementProductionRule(String elementName, boolean literal) {
		this.elementName = elementName;
		this.isLiteral = literal;
	}
	
	@Override
	public String getName() {
		return (isLiteral) ? '"'+elementName+'"' : elementName;
	}

	@Override
	public boolean test(List<Production> productions) {
		if (productions.isEmpty()) return false;
		
		Production firstElement = productions.get(0);
		if (isLiteral) {
			return firstElement.value().equals(elementName);
		} else {
			return firstElement.getName().equals(elementName);
		}
	}

	@Override
	public Nonterminal produce(List<Production> productions, Nonterminal addTo) throws IllegalArgumentException {
		Production captured = productions.remove(0);
		addTo.add(captured);
		return addTo;
	}
	
	public static ElementProductionRule matchProduction(String production) {
		return new ElementProductionRule(production, false);
	}
	
	public static ElementProductionRule matchLiteral(String literal) {
		return new ElementProductionRule(literal, true);
	}
}
