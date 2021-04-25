/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Syntax {
	protected List<LexerRule> lexerRules;
	protected Set<String> ignoreTokens;
	protected List<ProductionRule> productionRules;
	
	/**
	 * Gets a read-only view of the lexer rules of this Syntax. Do not modify
	 * these rules!
	 */
	public List<LexerRule> getLexerRules() {
		return lexerRules;
	}
	
	/**
	 * Gets a read-only view of the production rules of this Syntax. Do not
	 * modify these rules!
	 */
	public List<ProductionRule> getProductionRules() {
		return productionRules;
	}
	
	/**
	 * Gets a read-only set of tokens that should be omitted from the token stream for ast production (usually just "whitespace")
	 */
	public Set<String> getIgnoredTokens() {
		return ignoreTokens;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		protected ArrayList<LexerRule> lexerRules = new ArrayList<>();
		protected HashSet<String> ignoreTokens = new HashSet<>();
		protected ArrayList<ProductionRule> productionRules = new ArrayList<>();
		private boolean frozen = false;
		
		protected Builder() {}
		
		public Syntax build() {
			frozen = true;
			Syntax syntax = new Syntax();
			syntax.lexerRules = Collections.unmodifiableList(lexerRules);
			syntax.ignoreTokens = Collections.unmodifiableSet(ignoreTokens);
			syntax.productionRules = Collections.unmodifiableList(productionRules);
			return syntax;
		}
		
		public Builder addLexerRule(String name, String pattern) {
			LexerRule cur = new LexerRule(name, pattern);
			lexerRules.add(cur);
			return this;
		}
		
		public Builder add(LexerRule rule) {
			if (frozen) throw new IllegalStateException();
			lexerRules.add(rule);
			return this;
		}
		
		public Builder addProductionRule(String rule) throws SyntaxException {
			productionRules.add(JarserJarser.parseRule(rule));
			return this;
		}
		
		public Builder add(ProductionRule rule) {
			if (frozen) throw new IllegalStateException();
			productionRules.add(rule);
			return this;
		}
		
		public void ignoreToken(String tokenName) {
			if (frozen) throw new IllegalStateException();
			ignoreTokens.add(tokenName);
		}
	}
}
