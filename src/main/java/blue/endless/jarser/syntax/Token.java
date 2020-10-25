package blue.endless.jarser.syntax;

public class Token {
	protected final String name;
	protected final CharSequence value;
	protected final int startLine;
	protected final int startChar;
	protected final int endLine;
	protected final int endChar;
	
	public Token(String name, CharSequence value, int startLine, int startChar, int endLine, int endChar) {
		this.name = name;
		this.value = value;
		this.startLine = startLine;
		this.startChar = startChar;
		this.endLine = endLine;
		this.endChar = endChar;
	}
	
	public String getName() { return name; }
	public CharSequence value() { return value; }
	
	public int getStartLine() { return startLine; }
	public int getStartChar() { return startChar; }
	public int getEndLine()   { return endLine;   }
	public int getEndChar()   { return endChar;   }
	
}
