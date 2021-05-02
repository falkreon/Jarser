package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

public class AlternativesProductionRule implements ProductionRule {
	protected String name;
	protected ArrayList<ProductionRule> alternatives = new ArrayList<>();
	
	public AlternativesProductionRule(String name, ProductionRule... alternatives) {
		this.name = name;
		for(ProductionRule alternative : alternatives) this.alternatives.add(alternative);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean test(List<Production> productions) {
		for(ProductionRule productionRule : alternatives) {
			if (productionRule.test(productions)) return true;
		}
		
		return false;
	}
	
	@Override
	public Nonterminal produce(List<Production> productions, Nonterminal addTo) {
		for(ProductionRule productionRule : alternatives) {
			if (productionRule.test(productions)) {
				productionRule.produce(productions, addTo);
				return addTo;
			}
		}
		
		throw new IllegalArgumentException("No matching alternative for '"+name+"'");
	}
	
	public static AlternativesProductionRule of(String name, ProductionRule... alternatives) {
		return new AlternativesProductionRule(name, alternatives);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for(int i=0; i<alternatives.size(); i++) {
			result.append(alternatives.get(i));
			if (i<alternatives.size()-1) {
				result.append('|');
			}
		}
		
		return result.toString();
	}
}
