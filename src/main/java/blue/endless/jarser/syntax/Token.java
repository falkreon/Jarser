package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

public class Token implements Production {
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
	
	//implements Production {
	
		@Override
		public String getName() { return name; }
		@Override
		public CharSequence value() { return value; }
		@Override
		public int getStartLine() { return startLine; }
		@Override
		public int getStartChar() { return startChar; }
		@Override
		public int getEndLine()   { return endLine;   }
		@Override
		public int getEndChar()   { return endChar;   }
	
		@Override
		public List<Production> getChildren() {
			return new ArrayList<>();
		}
	
		@Override
		public boolean isTerminal() {
			return true;
		}
		
	//}
}
