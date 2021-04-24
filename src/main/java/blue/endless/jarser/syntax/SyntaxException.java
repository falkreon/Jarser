package blue.endless.jarser.syntax;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SyntaxException extends Exception {
	private static final long serialVersionUID = 3138920872324704908L;
	protected Production production;
	
	public SyntaxException(String message) {
		super(message);
	}
	
	public SyntaxException(String message, Production prod) {
		super(message);
		this.production = prod;
	}
	
	@Nullable
	public Production getProduction() {
		return production;
	}
	
	public String getLocationString() {
		if (production==null) return "unknown";
		return "from line "+production.getStartLine()+" char "+production.getStartChar()+" to line "+production.getEndLine()+" char "+production.getEndChar();
	}
}
