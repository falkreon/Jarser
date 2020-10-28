package blue.endless.jarser.syntax;

import java.util.List;

/**
 * A node of the syntax tree built by jarser.
 */
public interface Production {
	public String getName();
	public CharSequence value();
	public int getStartLine();
	public int getStartChar();
	public int getEndLine();
	public int getEndChar();
	
	public List<Production> getChildren();
	public boolean isTerminal();
}
