package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jarser.Jarser;

/** Class for jarsing jarser BNF and lexer rules */
public class JarserJarser {
	public static final Syntax JARSER_SYNTAX = createSyntax();
	
	protected static Syntax createSyntax() {
		Syntax.Builder builder = Syntax.builder();
		//Lexer lexer = new Lexer();
		
		builder.addLexerRule("line_end_comment", "\\/\\/[^\n]*\n");
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
				ElementProductionRule.matchProduction("token"),
				ElementProductionRule.matchProduction("repetition_operator")
				);
		builder.add(repetitionRule);
		
		ProductionRule alternativesRule = SequenceProductionRule.of("alternatives",
				ElementProductionRule.matchProduction("token"),
				ElementProductionRule.matchProduction("alternatives_operator"),
				ElementProductionRule.matchProduction("token")
				);
		builder.add(alternativesRule);
		
		ProductionRule groupingRule = SequenceProductionRule.of("grouping",
				ElementProductionRule.matchLiteral("("),
				RepetitionProductionRule.with(
						AlternativesProductionRule.of("repetitionAlternatives",
								ElementProductionRule.matchProduction("token"),
								ElementProductionRule.matchProduction("quoted_string"),
								ElementProductionRule.matchProduction("repetition"),
								ElementProductionRule.matchProduction("alternatives")
								),
						RepetitionProductionRule.RepetitionType.ONE_OR_MORE
						)
				);
		builder.add(groupingRule);
		
		/*
		ProductionRule tokenProductionRule = SequenceProductionRule.of("tokenRule",
				AlternativesProductionRule.of("nameAlternatives",
						ElementProductionRule.matchProduction("token"),
						ElementProductionRule.matchProduction("quoted_string")
				),
				ElementProductionRule.matchLiteral(":"),
				ElementProductionRule.matchProduction("token_keyword"),
				ElementProductionRule.matchProduction("quoted_string")
				);*/
		
		ProductionRule productionProductionRule = SequenceProductionRule.of("rule",
				AlternativesProductionRule.of("nameAlternatives",
						ElementProductionRule.matchProduction("token"),
						ElementProductionRule.matchProduction("quoted_string")
				),
				ElementProductionRule.matchLiteral(":"),
				AlternativesProductionRule.of("tokenOrProduction",
					//Production
					RepetitionProductionRule.with(
							AlternativesProductionRule.of("repetitionAlternatives",
									ElementProductionRule.matchProduction("token"),
									ElementProductionRule.matchProduction("quoted_string"),
									ElementProductionRule.matchProduction("repetition"),
									ElementProductionRule.matchProduction("alternatives"),
									ElementProductionRule.matchProduction("grouping")
									),
							RepetitionProductionRule.RepetitionType.ONE_OR_MORE
					),
					//Token
					ElementProductionRule.matchProduction("regexp")
				),
				ElementProductionRule.matchLiteral(";")
			);
		builder.add(productionProductionRule);
		
		return builder.build();
	}
	
	public static ProductionRule parseRule(String rule) throws SyntaxException {
		Jarser jarser = new Jarser(JARSER_SYNTAX);
		jarser.startMatching(rule);
		List<Production> ast = jarser.applyAll();
		
		if (ast.size()==0) throw new SyntaxException("Rule String was empty");
		if (ast.size()>1) throw new SyntaxException("Malformed rule", ast.get(1));
		
		Production astRoot = ast.get(0);
		if (astRoot.getName().equals("rule")) {
			ArrayList<Production> rulePieces = new ArrayList<>(astRoot.getChildren()); //Defensive copy so we can mutate it
			if (rulePieces.size()<3) {
				throw new SyntaxException("Rule ends prematurely.", rulePieces.get(rulePieces.size()-1));
			}
			
			Production ruleNameToken = rulePieces.remove(0);
			String ruleName;
			if (ruleNameToken.getName().equals("quoted_string")) {
				ruleName = ruleNameToken.value().toString().substring(1, ruleNameToken.value().length()-1);
			} else if (ruleNameToken.getName().equals("token")) {
				ruleName = ruleNameToken.value().toString();
			} else {
				throw new SyntaxException("Expected rule name, got "+ruleNameToken.getName());
			}
			
			//System.out.println("RuleName: "+ruleName);
			
			Production equalsSign = rulePieces.remove(0);
			if (!equalsSign.getName().equals("equals_operator")) {
				throw new SyntaxException("Expected '=', found '"+equalsSign.value()+"'", equalsSign);
			}
			
			//The rest of the rule MUST be ProductionRule nodes, which can be grouped up in a SequenceProductionRule.
			ProductionRule[] subRules = new ProductionRule[rulePieces.size()];
			for(int i=0; i<subRules.length; i++) {
				subRules[i] = makeRule(rulePieces.get(i));
			}
			return SequenceProductionRule.of(ruleName, subRules);
			
		} else {
			throw new SyntaxException("Expected 'rule', found '"+astRoot.getName()+"'", astRoot);
		}
	}
	
	private static ProductionRule makeRule(Production prod) throws SyntaxException {
		String ruleClass = prod.getName();
		List<Production> children = prod.getChildren();
		switch(ruleClass) {
		case "repetition":
			//None of these three should ever be possible given our format rules
			if (children.isEmpty()) throw new SyntaxException("SEVERE: Empty repetition group!");
			if (children.size()<2) throw new SyntaxException("SEVERE: Can't find a repetition qualifier in a repetition!", children.get(0));
			if (children.size()>2) throw new SyntaxException("SEVERE: Too many things in a repetition!", children.get(children.size()-1));
			
			Production toRepeat = children.get(0);
			String repeatQualifier = children.get(1).value().toString();
			RepetitionProductionRule.RepetitionType repetitionType = RepetitionProductionRule.RepetitionType.ONE_OR_MORE;
			if (repeatQualifier.equals("+")) {
				repetitionType = RepetitionProductionRule.RepetitionType.ONE_OR_MORE;
			} else if (repeatQualifier.equals("*")) {
				repetitionType = RepetitionProductionRule.RepetitionType.ZERO_OR_MORE;
			} else if (repeatQualifier.equals("?")) {
				repetitionType = RepetitionProductionRule.RepetitionType.ZERO_OR_ONE;
			}
			ProductionRule repeatedRule = makeRule(toRepeat);
			return RepetitionProductionRule.with(repeatedRule, repetitionType);
			
		case "alternatives":
			//TODO: A|B|C, needs tweaks to production rules above first
			if (children.size()<3) throw new SyntaxException("SEVERE: Not enough tokens to describe alternatives!", children.get(children.size()-1));
			if (children.size()>3) throw new SyntaxException("SEVERE: Too many parts to an alternative set!", children.get(children.size()-1));
			if (!children.get(1).value().equals("|")) throw new SyntaxException("SEVERE: expected '|' between alternatives, found '"+children.get(1).value()+"'", children.get(1));
			
			ProductionRule a = makeRule(children.get(0));
			ProductionRule b = makeRule(children.get(2));
			return AlternativesProductionRule.of("alternatives", a, b);
			
		case "grouping":
			ProductionRule[] rules = new ProductionRule[children.size()];
			for(int i=0; i<rules.length; i++) {
				rules[i] = makeRule(children.get(i));
			}
			return SequenceProductionRule.of("grouping", rules);
			
		case "token":
			return ElementProductionRule.matchProduction(prod.value().toString());
			
		case "quoted_string":
			return ElementProductionRule.matchLiteral(prod.value().toString().substring(1, prod.value().length()-1));
			
		default:
			throw new SyntaxException("Expected part of a production rule, found '"+prod.value()+"'", prod);
		}
	}
	
	public static Syntax makeSyntax(String s) throws SyntaxException {
		Jarser jarser = new Jarser(JARSER_SYNTAX);
		jarser.startMatching(s);
		List<Production> ast = jarser.applyAll();
		
		Syntax.Builder builder = Syntax.builder();
		for(Production p : ast) {
			if (p.getName().equals("rule")) {
				/* 
				 * There should be 5 kinds of rulePieces: name, colon_operator, [stuff]+, semicolon.
				 * The first element will be name. Second is colon. Last is semicolon. Everything else is the meat, the Stuff inside the rule.
				 */
				List<Production> rulePieces = p.getChildren();
				//String ruleName = rulePieces.get(0).value().toString();
				Production ruleNameToken  = rulePieces.get(0);
				String ruleName;
				if (ruleNameToken.getName().equals("quoted_string")) {
					ruleName = ruleNameToken.value().toString().substring(1, ruleNameToken.value().length()-1); //TODO: use value named-capture group
				} else if (ruleNameToken.getName().equals("token")) {
					ruleName = ruleNameToken.value().toString();
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
					ProductionRule[] subRules = new ProductionRule[rulePieces.size()];
					for(int i=0; i<subRules.length; i++) {
						subRules[i] = makeRule(rulePieces.get(i));
					}
					SequenceProductionRule productionRule = SequenceProductionRule.of(ruleName, subRules);
					builder.add(productionRule);
				}
			}
		}
		
		return builder.build();
	}
}
