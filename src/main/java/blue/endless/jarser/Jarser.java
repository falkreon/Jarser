/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser;

import java.util.ArrayList;
import java.util.Collections;
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
	protected boolean finished = false;
	
	public Jarser(Syntax syntax) {
		this.syntax = syntax;
		this.lexer = new Lexer(syntax);
	}
	
	public Jarser startMatching(List<Production> productions) {
		subject.clear();
		subject.addAll(productions);
		return this;
	}
	
	/**
	 * Start matching against a String. This will completely assemble the String into terminal Tokens,
	 * and make this Jarser ready to apply production rules.
	 * @param string
	 * @return
	 */
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
	 * Start matching *within* a Token, preserving the line/character position within that Token. This
	 * can be especially useful for waterfalling multiple Jarsers together for better control over
	 * context.
	 * @param token
	 * @return
	 */
	public Jarser startMatching(Token token) {
		subject.clear();
		lexer.startMatching(token);
		Token cur = lexer.nextToken();
		while(cur!=null) {
			subject.add(cur);
			cur = lexer.nextToken();
		}
		return this;
	}
	
	/**
	 * Attempts to apply a production rule to the production stream to continue grouping it into a
	 * syntax tree.
	 * @return true if Jarser is still working, and apply needs to be called again.
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
		
		finished |= !anythingChanged;
		return anythingChanged;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	private static final int ITERATIONS_MAX = 10000;
	/**
	 * Applies all production rules, as many times as necessary (up to the maximum iteration count), and returns the assembled syntax tree.
	 * This method can be safely called multiple times, and the completion status checked with {@link #isFinished()}.
	 * @param maxIterations a cap on how many times production rules can be applied. Setting this to a negative number allows the Jarser to hang forever.
	 * @return an immutable List view of the assembled list of productions. If this method is called again, the view will "read through" to the new AST state.
	 */
	public List<Production> applyAll(int maxIterations) {
		int iterations = 0;
		while(apply() && (maxIterations<0 || iterations<=maxIterations)) { iterations++; }
		return Collections.unmodifiableList(subject);
	}
	
	/**
	 * Applies all production rules, up to ten thousand times if necessary, and returns the assembled syntax tree.
	 * @return
	 */
	public List<Production> applyAll() {
		return applyAll(ITERATIONS_MAX);
	}
}
