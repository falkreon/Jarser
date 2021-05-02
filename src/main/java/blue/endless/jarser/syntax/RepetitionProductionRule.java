/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

public class RepetitionProductionRule implements ProductionRule {
	protected String name;
	protected RepetitionType type = RepetitionType.ONE_OR_MORE;
	protected int exactCount = 1;
	protected ProductionRule delegate;
	
	protected RepetitionProductionRule(ProductionRule rule, RepetitionType type) {
		this.name = rule.getName();
		this.delegate = rule;
		this.type = type;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean test(List<Production> productions) {
		if (type==RepetitionType.ZERO_OR_MORE) return true; //We will always match at least zero.
		
		if (type==RepetitionType.ONE_OR_MORE) {
			//Match at least one
			return delegate.test(productions);
		}
		
		if (type==RepetitionType.EXACT_COUNT || type==RepetitionType.ZERO_OR_ONE) {
			//Take a copy and consume until we know we're good
			ArrayList<Production> workingSet = new ArrayList<>(productions);
			Nonterminal trash = new Nonterminal("repetition");
			for(int i=0; i<exactCount-1; i++) {
				if (delegate.test(workingSet)) {
					delegate.produce(workingSet, trash); //Throw away the production, we just care that it produces
				} else {
					return false;
				}
			}
			
			//No need to consume the last repetition
			return delegate.test(workingSet);
		}
		
		return false;
	}

	@Override
	public Nonterminal produce(List<Production> productions, Nonterminal addTo) throws IllegalArgumentException {
		if (productions.isEmpty()) return addTo;
		
		boolean continueProducing = true;
		int productionCount = 0;
		while (continueProducing) {
			continueProducing = delegate.test(productions);
			if (continueProducing) {
				delegate.produce(productions, addTo);
				productionCount++;
			}
			
			if ((type==RepetitionType.EXACT_COUNT||type==RepetitionType.ZERO_OR_ONE) && productionCount>=exactCount) return addTo;
		}
		
		//Check possibilities that result in inconsistent list states.
		if (type==RepetitionType.ONE_OR_MORE && productionCount<1) throw new IllegalArgumentException();
		if (type==RepetitionType.EXACT_COUNT && productionCount<exactCount) throw new IllegalArgumentException();
		
		return addTo;
	}
	
	public static RepetitionProductionRule with(ProductionRule rule, RepetitionType type) {
		return new RepetitionProductionRule(rule, type);
	}
	
	public static RepetitionProductionRule withCount(ProductionRule rule, int count) {
		RepetitionProductionRule result = new RepetitionProductionRule(rule, RepetitionType.EXACT_COUNT);
		result.exactCount = count;
		return result;
	}
	
	public static enum RepetitionType {
		ONE_OR_MORE,
		ZERO_OR_MORE,
		ZERO_OR_ONE,
		EXACT_COUNT;
	}
	
	@Override
	public String toString() {
		String result = delegate.toString();
		//System.out.println("DELEGATE IS "+delegate);
		
		switch(type) {
		case ONE_OR_MORE:
			return result+"+";
		case ZERO_OR_MORE:
			return result+"*";
		case ZERO_OR_ONE:
			return result+"?";
		case EXACT_COUNT:
			return result+"{"+exactCount+"}";
		}
		
		return result;
	}
}
