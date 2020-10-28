package blue.endless.jarser.syntax;

import java.util.ArrayList;

public class ProductionRule {
	protected ArrayList<ProductionRule> alternatives = new ArrayList<>();
	
	protected RepetitionType repetiton = RepetitionType.ONE;
	protected String pattern;
	protected boolean isLiteral = false;
	
	public static enum RepetitionType {
		ONE,
		ONE_OR_MORE,
		ZERO_OR_MORE;
	}
}

/*

Organizational levels and where they sit in the object hierarchy:

Alternatives
	foo | bar

Sequence Group
	(foo bar) baz

Atom
* Repetition rules
* Optional?
* Terminals / Patterns




*/