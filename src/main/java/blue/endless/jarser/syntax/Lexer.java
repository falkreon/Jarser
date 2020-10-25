package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * This is kind of like Scanner in that it turns a String into tokens. This is unlike Scanner in that:
 * <ul>
 *   <li>The parsing is controlled by regex patterns
 *   <li>The tokens are aware of what line and character within the line that they start and end at
 *   <li>Tokens come with a class so that you can tell what rule produced them, and use that rule in production rules
 * </ul>
 */
public class Lexer {
	protected int pointer = 0;
	protected int line = 0;
	protected int character = 0;
	protected ArrayList<LexerRule> rules = new ArrayList<>();
	protected CharSequence subject;
	
	public Lexer() {
		
	}
	
	public void addRule(String name, String pattern) {
		LexerRule rule = new LexerRule(name, pattern);
		rules.add(rule);
	}
	
	public void addRule(LexerRule rule) {
		rules.add(rule);
	}
	
	public void startMatching(CharSequence sequence) {
		this.subject = sequence;
		
		for(LexerRule rule : rules) {
			rule.start(sequence);
		}
	}
	
	private void advance() {
		if (pointer>=subject.length()) return;
		
		char ch = subject.charAt(pointer);
		if (ch=='\n') {
			line++;
			character = 0;
		}
		pointer++;
	}
	
	private void advance(int numCharacters) {
		for(int i=0; i<numCharacters; i++) advance();
	}
	
	public Token nextToken() {
		if (pointer>=subject.length()) return null;
		
		for(LexerRule rule : rules) {
			Matcher matcher = rule.getMatcher();
			matcher.region(pointer, subject.length());
			boolean found = matcher.lookingAt();
			if (!found) continue;
			
			int end = matcher.end();
			CharSequence result = subject.subSequence(pointer, end);
			int startLine = line;
			int startChar = character;
			advance(end-pointer);
			//System.out.println("MATCHED");
			return new Token(rule.getName(), result, startLine, startChar, line, character);
		}
		
		//TODO: There is no good way to handle characters that escape every rule. For now, emit a single-character token for the offending character.
		CharSequence result = subject.subSequence(pointer, pointer+1);
		int startLine = line;
		int startChar = character;
		advance();
		//System.out.println("FALLBACK");
		return new Token("ERROR", result, startLine, startChar, line, character);
	}
}
