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
import blue.endless.jarser.syntax.Token;

public class Jarser {
	protected Lexer lexer;
	protected ArrayList<Production> subject = new ArrayList<>();
	protected ArrayList<ProductionRule> rules = new ArrayList<>();
	
	public Jarser() {
		this.lexer = new Lexer();
	}
	
	public Jarser(Lexer lexer) {
		this.lexer = lexer;
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
			System.out.println("Matching "+token.value()+":"+token.getName());
			subject.add(token);
			token = lexer.nextToken();
		}
		return this;
	}
	
	public Jarser addRule(ProductionRule rule) {
		this.rules.add(rule);
		return this;
	}
	
	/**
	 * 
	 * @return true if Jarser is still working
	 */
	public boolean apply() {
		ArrayList<Production> nextRound = new ArrayList<>();
		boolean anythingChanged = false;
		
		for(ProductionRule rule : rules) {
			System.out.println("Considering rule: "+rule.getName());
			
			while(!subject.isEmpty()) {
				if (rule.test(subject)) {
					System.out.println("Matched rule '"+rule.getName()+"'");
					Nonterminal ruleResult = new Nonterminal(rule.getName());
					rule.produce(subject, ruleResult);
					nextRound.add(ruleResult);
					anythingChanged = true;
				} else {
					Production p = subject.remove(0);
					System.out.println("Rejecting "+p.getName());
					nextRound.add(p);
				}
			}
			
			//Swap lists
			ArrayList<Production> nextNextRound = subject;
			subject = nextRound;
			nextRound = nextNextRound;
			
			System.out.println(subject.toString()); //TODO: This is debug
			
			if (anythingChanged) break;
		}
		
		return anythingChanged;
	}
	
	public List<Production> applyAll() {
		while(apply()) {}
		return new ArrayList<Production>(subject);
	}
}
