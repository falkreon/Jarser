package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

public class SequenceProductionRule implements ProductionRule {
	protected String name;
	protected ArrayList<ProductionRule> elements = new ArrayList<>();
	
	protected SequenceProductionRule(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean test(List<Production> productions) {
		ArrayList<Production> workingSet = new ArrayList<>(productions);
		Nonterminal trash = new Nonterminal("sequence");
		for(ProductionRule element : elements) {
			if (element.test(workingSet)) {
				element.produce(workingSet, trash); //Throw away the production, but consume productions up to where the next element should start
			} else {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Nonterminal produce(List<Production> productions, Nonterminal addTo) throws IllegalArgumentException {
		for(ProductionRule element : elements) {
			element.produce(productions, addTo);
		}
		
		return addTo;
	}
	
	public static SequenceProductionRule of(String name, ProductionRule... elements) {
		SequenceProductionRule rule = new SequenceProductionRule(name);
		for(ProductionRule element : elements ) rule.elements.add(element);
		return rule;
	}
	
	@Override
	public String toString() {
		if (elements.isEmpty()) return "()";
		StringBuilder result = new StringBuilder();
		for(int i=0; i<elements.size(); i++) {
			ProductionRule element = elements.get(i);
			if (element==null) {
				result.append("NULL");
			} else {
				result.append(element.toString());
			}
			if (i<elements.size()-1) result.append(" ");
		}
		return result.toString();
	}
}
