package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jarser.Jarser;

/** Class for jarsing jarser BNF and lexer rules */
public class JarserJarser {
	protected static Jarser createJarser() {
		Lexer lexer = new Lexer();
		
		lexer.addRule("quoted_string", "\"(?:[^\"\\\\]*(?:\\\\.)?)*\"");
		lexer.addRule("equals_operator", "=");
		lexer.addRule("repetition_operator", "[\\*\\?\\+]");
		lexer.addRule("alternatives_operator", "\\|");
		lexer.addRule("token", "[a-zA-Z_]+[a-zA-Z0-9_]*");
		lexer.addRule("parens", "[\\(\\)]");
		lexer.addRule("whitespace", "\\s+");
		lexer.ignoreToken("whitespace");
		
		Jarser jarser = new Jarser(lexer);
		
		ProductionRule repetitionRule = SequenceProductionRule.of("repetition",
				ElementProductionRule.matchProduction("token"),
				ElementProductionRule.matchProduction("repetition_operator")
				);
		jarser.addRule(repetitionRule);
		
		ProductionRule alternativesRule = SequenceProductionRule.of("alternatives",
				ElementProductionRule.matchProduction("token"),
				ElementProductionRule.matchProduction("alternatives_operator"),
				ElementProductionRule.matchProduction("token")
				);
		jarser.addRule(alternativesRule);
		
		//TODO: what the hell
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
		jarser.addRule(groupingRule);
		
		ProductionRule productionProductionRule = SequenceProductionRule.of("rule",
				AlternativesProductionRule.of("nameAlternatives",
						ElementProductionRule.matchProduction("token"),
						ElementProductionRule.matchProduction("quoted_string")
				),
				ElementProductionRule.matchLiteral("="),
				RepetitionProductionRule.with(
						AlternativesProductionRule.of("repetitionAlternatives",
								ElementProductionRule.matchProduction("token"),
								ElementProductionRule.matchProduction("quoted_string"),
								ElementProductionRule.matchProduction("repetition"),
								ElementProductionRule.matchProduction("alternatives"),
								ElementProductionRule.matchProduction("grouping")
								),
						RepetitionProductionRule.RepetitionType.ONE_OR_MORE
					)
				);
		jarser.addRule(productionProductionRule);
		
		return jarser;
	}
	
	public static ProductionRule create(String rule) throws SyntaxException {
		Jarser jarser = createJarser();
		jarser.startMatching(rule);
		List<Production> ast = jarser.applyAll();
		
		if (ast.size()==0) throw new SyntaxException("rule String was empty");
		if (ast.size()>1) throw new SyntaxException("Extra tokens after end", ast.get(1));
		
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
}
