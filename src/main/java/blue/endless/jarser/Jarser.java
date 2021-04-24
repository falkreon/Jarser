/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jarser.syntax.Lexer;
import blue.endless.jarser.syntax.Nonterminal;
import blue.endless.jarser.syntax.Production;
import blue.endless.jarser.syntax.ProductionRule;
import blue.endless.jarser.syntax.Syntax;
import blue.endless.jarser.syntax.Token;

public class Jarser {
	protected Syntax syntax;
	protected Lexer lexer;
	protected ArrayList<Production> subject = new ArrayList<>();
	
	public Jarser(Syntax syntax) {
		this.syntax = syntax;
		this.lexer = new Lexer(syntax);
	}
	
	public Jarser startMatching(List<Production> productions) {
		subject.clear();
		subject.addAll(productions);
		return this;
	}
	
	public Jarser startMatching(String string) {
		subject.clear();
		lexer.startMatching(string);
		Token token = lexer.nextToken();
		while(token!=null) {
			subject.add(token);
			token = lexer.nextToken();
		}
		return this;
	}
	
	/**
	 * 
	 * @return true if Jarser is still working
	 */
	public boolean apply() {
		ArrayList<Production> nextRound = new ArrayList<>();
		boolean anythingChanged = false;
		
		for(ProductionRule rule : syntax.getProductionRules()) {
			
			while(!subject.isEmpty()) {
				if (rule.test(subject)) {
					Nonterminal ruleResult = new Nonterminal(rule.getName());
					rule.produce(subject, ruleResult);
					nextRound.add(ruleResult);
					anythingChanged = true;
				} else {
					Production p = subject.remove(0);
					nextRound.add(p);
				}
			}
			
			//Swap lists
			ArrayList<Production> nextNextRound = subject;
			subject = nextRound;
			nextRound = nextNextRound;
			
			if (anythingChanged) break;
		}
		
		return anythingChanged;
	}
	
	private static final int ITERATIONS_MAX = 10000;
	public List<Production> applyAll() {
		int iterations = 0;
		while(apply() && iterations<=ITERATIONS_MAX) { iterations++; }
		return new ArrayList<Production>(subject);
	}
}
