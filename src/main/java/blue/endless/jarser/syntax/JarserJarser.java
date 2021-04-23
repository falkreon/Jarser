package blue.endless.jarser.syntax;

import blue.endless.jarser.Jarser;

/** Class for jarsing jarser BNF and lexer rules */
public class JarserJarser {
	public static Jarser createJarser() {
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
				ElementProductionRule.matchProduction("token"),
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
}
