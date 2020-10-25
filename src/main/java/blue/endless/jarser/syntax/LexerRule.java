package blue.endless.jarser.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class LexerRule {
	protected final String name;
	protected final Pattern pattern;
	protected Matcher matcher;
	
	public LexerRule(String name, String pattern) throws PatternSyntaxException {
		this.name = name;
		this.pattern = Pattern.compile(pattern);
	}
	
	public String getName() { return name; }
	public Pattern getPattern() { return pattern; }
	public void start(CharSequence seq) {
		matcher = pattern.matcher(seq);
	}
	public Matcher getMatcher() { return matcher; }
	
}
