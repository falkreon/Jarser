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

import blue.endless.jarser.Jarser;

/** Class for jarsing jarser BNF and lexer rules */
public class JarserJarser {
	public static final Grammar JARSER_SYNTAX = createSyntax();
	
	protected static Grammar createSyntax() {
		Grammar.Builder builder = Grammar.builder();
		//Lexer lexer = new Lexer();
		
		builder.addLexerRule("line_end_comment", "\\/\\/[^$\\n]*[\\n$]");
		builder.addLexerRule("quoted_string", "\"(?:[^\"\\\\]*(?:\\\\.)?)*\"");
		builder.addLexerRule("regexp", "\\/(?<value>(?:[^\\/\\\\]*(?:\\\\.)?)+)\\/"); // \/(?<value>(?:[^\/\\]*(?:\\.)?)+)\/
		//builder.addLexerRule("equals_operator", "=");
		builder.addLexerRule("colon_operator", ":");
		builder.addLexerRule("repetition_operator", "[\\*\\?\\+]");
		builder.addLexerRule("alternatives_operator", "\\|");
		builder.addLexerRule("rule_end_operator", ";");
		builder.addLexerRule("token", "[a-zA-Z_]+[a-zA-Z0-9_]*");
		builder.addLexerRule("parens", "[\\(\\)]");
		builder.addLexerRule("whitespace", "\\s+");
		builder.ignoreToken("whitespace");
		
		
		
		ProductionRule repetitionRule = SequenceProductionRule.of("repetition",
				ElementProductionRule.matchProduction("subrule"),
				ElementProductionRule.matchProduction("repetition_operator")
				);
		builder.add(repetitionRule);
		
		ProductionRule alternativesRule = SequenceProductionRule.of("alternatives",
					ElementProductionRule.matchProduction("subrule"),
					ElementProductionRule.matchProduction("alternatives_operator"),
					ElementProductionRule.matchProduction("subrule")
				);
		builder.add(alternativesRule);
		
		ProductionRule groupingRule = SequenceProductionRule.of("grouping",
				ElementProductionRule.matchLiteral("("),
				RepetitionProductionRule.with(
						ElementProductionRule.matchProduction("subrule"),
						RepetitionProductionRule.RepetitionType.ONE_OR_MORE
						),
				ElementProductionRule.matchLiteral(")")
				);
		builder.add(groupingRule);
		
		ProductionRule lexerProductionRule = SequenceProductionRule.of("lexerRule",
				ElementProductionRule.matchProduction("rule_name"),
				ElementProductionRule.matchProduction("regexp"),
				ElementProductionRule.matchLiteral(";")
				);
		builder.add(lexerProductionRule);
		
		//TODO: Keys in this rule may get eaten by the subrule rule!
		ProductionRule productionProductionRule = SequenceProductionRule.of("rule",
				ElementProductionRule.matchProduction("rule_name"),
				RepetitionProductionRule.with(
						ElementProductionRule.matchProduction("subrule"),
						RepetitionProductionRule.RepetitionType.ONE_OR_MORE
				),
				ElementProductionRule.matchLiteral(";")
				);
		builder.add(productionProductionRule);
		
		ProductionRule ignoreRule = SequenceProductionRule.of("ignore_rule",
				ElementProductionRule.matchLiteral("ignore"),
				ElementProductionRule.matchProduction("token"),
				ElementProductionRule.matchLiteral(";")
				);
		builder.add(ignoreRule);
		
		ProductionRule ruleName = SequenceProductionRule.of("rule_name",
				AlternativesProductionRule.of("nameAlternatives",
						ElementProductionRule.matchProduction("token"),
						ElementProductionRule.matchProduction("quoted_string")
				),
				ElementProductionRule.matchLiteral(":")
				);
		builder.add(ruleName);
		
		ProductionRule subruleRule = AlternativesProductionRule.of("subrule",
				ElementProductionRule.matchProduction("token"),
				ElementProductionRule.matchProduction("quoted_string"),
				ElementProductionRule.matchProduction("repetition"),
				ElementProductionRule.matchProduction("alternatives"),
				ElementProductionRule.matchProduction("grouping")
				);
		builder.add(subruleRule);
		
		return builder.build();
	}
	
	public static ProductionRule parseRule(String rule) throws SyntaxException {
		Jarser jarser = new Jarser(JARSER_SYNTAX);
		jarser.startMatching(rule);
		List<Production> ast = jarser.applyAll();
		
		if (ast.size()==0) throw new SyntaxException("Rule String was empty");
		if (ast.size()>1) throw new SyntaxException("Malformed rule: "+ast.get(0).getValue(), ast.get(1));
		
		Production astRoot = ast.get(0);
		if (astRoot.getName().equals("rule")) {
			ArrayList<Production> rulePieces = new ArrayList<>(astRoot.getChildren()); //Defensive copy so we can mutate it
			if (rulePieces.size()<3) {
				throw new SyntaxException("Rule ends prematurely.", rulePieces.get(rulePieces.size()-1));
			}
			
			Production ruleNameToken = rulePieces.remove(0);
			String ruleName;
			if (ruleNameToken.getName().equals("quoted_string")) {
				ruleName = ruleNameToken.getValue().toString().substring(1, ruleNameToken.getValue().length()-1);
			} else if (ruleNameToken.getName().equals("token")) {
				ruleName = ruleNameToken.getValue().toString();
			} else {
				throw new SyntaxException("Expected rule name, got "+ruleNameToken.getName());
			}
			
			//System.out.println("RuleName: "+ruleName);
			
			Production equalsSign = rulePieces.remove(0);
			if (!equalsSign.getName().equals("colon_operator")) {
				throw new SyntaxException("Expected '=', found '"+equalsSign.getValue()+"'", equalsSign);
			}
			
			//The rest of the rule MUST be ProductionRule nodes, which can be grouped up in a SequenceProductionRule.
			ProductionRule[] subRules = new ProductionRule[rulePieces.size()];
			for(int i=0; i<subRules.length; i++) {
				subRules[i] = makeSubrule(rulePieces.get(i));
			}
			return SequenceProductionRule.of(ruleName, subRules);
			
		} else {
			throw new SyntaxException("Expected 'rule', found '"+astRoot.getName()+"'", astRoot);
		}
	}
	
	private static ProductionRule makeSubrule(Production prod) throws SyntaxException {
		if (!prod.getName().equals("subrule")) throw new IllegalArgumentException("Can only unpack subrules. got "+prod.getName()+": '"+prod.getValue()+"'");
		if (prod.getChildren().size()!=1) throw new SyntaxException("Expected subrule node to have one child, found "+prod.getChildren().size());
		
		prod = prod.getChildren().get(0); //subrule parent has been validated, now work with the subrule child
		
		String ruleClass = prod.getName();
		List<Production> children = prod.getChildren();
		switch(ruleClass) {
		case "repetition":
			//None of these three should ever be possible given our format rules
			if (children.isEmpty()) throw new SyntaxException("SEVERE: Empty repetition group!");
			if (children.size()<2) throw new SyntaxException("SEVERE: Can't find a repetition qualifier in a repetition!", children.get(0));
			if (children.size()>2) throw new SyntaxException("SEVERE: Too many things in a repetition!", children.get(children.size()-1));
			
			Production toRepeat = children.get(0);
			String repeatQualifier = children.get(1).getValue().toString();
			RepetitionProductionRule.RepetitionType repetitionType = RepetitionProductionRule.RepetitionType.ONE_OR_MORE;
			if (repeatQualifier.equals("+")) {
				repetitionType = RepetitionProductionRule.RepetitionType.ONE_OR_MORE;
			} else if (repeatQualifier.equals("*")) {
				repetitionType = RepetitionProductionRule.RepetitionType.ZERO_OR_MORE;
			} else if (repeatQualifier.equals("?")) {
				repetitionType = RepetitionProductionRule.RepetitionType.ZERO_OR_ONE;
			}
			
			ProductionRule repeatedRule = makeSubrule(toRepeat);
			return RepetitionProductionRule.with(repeatedRule, repetitionType);
			
		case "alternatives":
			//TODO: A|B|C, needs tweaks to production rules above first
			if (children.size()<3) throw new SyntaxException("SEVERE: Not enough tokens to describe alternatives!", children.get(children.size()-1));
			if (children.size()>3) throw new SyntaxException("SEVERE: Too many parts to an alternative set!", children.get(children.size()-1));
			if (!children.get(1).getValue().equals("|")) throw new SyntaxException("SEVERE: expected '|' between alternatives, found '"+children.get(1).getValue()+"'", children.get(1));
			
			ProductionRule a = makeSubrule(children.get(0));
			ProductionRule b = makeSubrule(children.get(2));
			return AlternativesProductionRule.of("alternatives", a, b);
			
		case "grouping":
			List<Production> groupingChildren = new ArrayList<>(children);
			Production openParen = groupingChildren.remove(0);
			Production closeParen = groupingChildren.remove(groupingChildren.size()-1);
			
			ProductionRule[] rules = new ProductionRule[groupingChildren.size()];
			for(int i=0; i<rules.length; i++) {
				rules[i] = makeSubrule(groupingChildren.get(i));
				//System.out.println("Assembled "+rules[i]);
			}
			return SequenceProductionRule.of("grouping", rules);
			
		case "token":
			return ElementProductionRule.matchProduction(prod.getValue().toString());
			
		case "quoted_string":
			return ElementProductionRule.matchLiteral(prod.getValue().toString().substring(1, prod.getValue().length()-1));
		default:
			throw new SyntaxException("Expected part of a production rule, found '"+prod.getName()+"("+prod.getValue()+")'", prod);
		}
	}
	
	public static ProductionRule parseSubrule(Production p) {
		if (!p.getName().equals("subrule")) throw new IllegalArgumentException("parseSubrule can only process 'subrule' nodes");
		List<Production> p_ch = p.getChildren();
		if (p_ch.size()>1) throw new IllegalArgumentException("SEVERE: subrule has too many child nodes!");
		
		Production subruleChild = p_ch.get(0);
		switch(subruleChild.getName()) {
		
		
		
		default:
			throw new IllegalArgumentException("Unknown subrule type: '"+subruleChild.getName()+"'");
		}
	}
	
	public static Grammar makeSyntax(String s) throws SyntaxException {
		Jarser jarser = new Jarser(JARSER_SYNTAX);
		jarser.startMatching(s);
		List<Production> ast = jarser.applyAll();
		
		Grammar.Builder builder = Grammar.builder();
		for(Production p : ast) {
			if (p.getName().equals("rule")) {
				//TODO: Fix for new ruleName and subrule production nodes
				
				//Copy into a mutable list
				ArrayList<Production> rulePieces = new ArrayList<>(p.getChildren());
				Production nameProduction = rulePieces.remove(0);
				Production tail = rulePieces.remove(rulePieces.size()-1);
				if (!tail.getValue().equals(";")) throw new SyntaxException("SEVERE: Rule does not end with semicolon");
				String ruleName = nameProduction.getChildren().get(0).getValue();
				//System.out.println("RULE> "+ruleName+" : "+rulePieces);
				
				
				ProductionRule[] subrules = new ProductionRule[rulePieces.size()];
				for(int i=0; i<subrules.length; i++) {
					Production child = rulePieces.get(i);
					if (child.getName().equals("subrule")) {
						subrules[i] = makeSubrule(child);
					} else {
						throw new SyntaxException("Expected subrule, got '"+child.getName()+"'.", child);
					}
				}
				SequenceProductionRule rule = SequenceProductionRule.of(ruleName, subrules);
				builder.add(rule);
				//System.out.println(p.recursiveExplain());
				/* 
				 * There should be 5 kinds of rulePieces: name, colon_operator, [stuff]+, semicolon.
				 * The first element will be name. Second is colon. Last is semicolon. Everything else is the meat, the Stuff inside the rule.
				 */
				/*
				List<Production> rulePieces = p.getChildren();
				System.out.println(rulePieces);
				//String ruleName = rulePieces.get(0).value().toString();
				Production ruleNameToken  = rulePieces.get(0);
				String ruleName;
				if (ruleNameToken.getName().equals("quoted_string")) {
					ruleName = ruleNameToken.getValue().toString().substring(1, ruleNameToken.getValue().length()-1); //TODO: use value named-capture group
				} else if (ruleNameToken.getName().equals("token")) {
					ruleName = ruleNameToken.getValue().toString();
				} else {
					throw new SyntaxException("Expected rule name, got "+ruleNameToken.getName());
				}
				
				ArrayList<Production> stuff = new ArrayList<>();
				for(int i=2; i<rulePieces.size()-1; i++) {
					stuff.add(rulePieces.get(i));
				}
				
				if (stuff.size()==1 && stuff.get(0).getName().equals("regexp")) {
					//Token / Terminal / Lexer rule
					if (stuff.get(0) instanceof Token) {
						String regexValue = ((Token)stuff.get(0)).getNamedCapture("value");
						builder.addLexerRule(ruleName, regexValue);
					} else {
						throw new IllegalStateException("SEVERE: We should not be able to produce a nonterminal 'regexp' Production!");
					}
				} else {
					//The rest of the rule MUST be ProductionRule nodes, which can be grouped up in a SequenceProductionRule.
					ProductionRule[] subRules = new ProductionRule[stuff.size()];
					for(int i=0; i<stuff.size(); i++) {
						//System.out.println(stuff.get(i));
						subRules[i] = makeRule(stuff.get(i));
					}
					SequenceProductionRule productionRule = SequenceProductionRule.of(ruleName, subRules);
					builder.add(productionRule);
				}*/
			} else if (p.getName().equals("lexerRule")) {
				//System.out.println("Lexer Rule: "+ p.getChildren().get(1).getValue());
				builder.addLexerRule(p.getChildren().get(0).getChildren().get(0).getValue(), p.getChildren().get(1).getValue());
			} else if (p.getName().equals("line_end_comment")) {
				//do nothing
			} else if (p.getName().equals("ignore_rule")) {
				builder.ignoreToken(p.getChildren().get(1).getValue());
			} else {
				System.out.println("Bad token: "+p.getName()+"("+p.getValue()+")");
			}
		}
		
		return builder.build();
	}
}
