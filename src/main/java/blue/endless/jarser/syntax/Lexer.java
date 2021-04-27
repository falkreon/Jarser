/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.code.regexp.Matcher;

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
	protected Syntax syntax;
	protected CharSequence subject;
	
	public Lexer(Syntax syntax) {
		this.syntax = syntax;
	}
	
	public void startMatching(CharSequence sequence) {
		this.subject = sequence;
		
		for(LexerRule rule : syntax.getLexerRules()) {
			rule.start(sequence);
		}
	}
	
	public void startMatching(Token token) {
		this.subject = token.value();
		this.line = token.getStartLine();
		this.character = token.getStartChar();
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
	
	public Token nextRawToken() {
		if (pointer>=subject.length()) return null;
		
		for(LexerRule rule : syntax.getLexerRules()) {
			Matcher matcher = rule.getMatcher();
			matcher.region(pointer, subject.length());
			boolean found = matcher.lookingAt();
			if (!found) continue;
			
			int end = matcher.end();
			CharSequence result = subject.subSequence(pointer, end);
			int startLine = line;
			int startChar = character;
			advance(end-pointer);
			HashMap<String, String> captures = new HashMap<>();
			for(String s : rule.getPattern().groupNames()) {
				captures.put(s, matcher.group(s));
			}
			//System.out.println("MATCHED");
			return new Token(rule.getName(), result, startLine, startChar, line, character, captures);
		}
		
		//TODO: There is no good way to handle characters that escape every rule. For now, emit a single-character token for the offending character. Maybe later, throw an exception.
		CharSequence result = subject.subSequence(pointer, pointer+1);
		int startLine = line;
		int startChar = character;
		advance();
		return new Token("ERROR", result, startLine, startChar, line, character, new HashMap<>());
	}
	
	public Token nextToken() {
		while(pointer<subject.length()) {
			Token next = nextRawToken();
			if (!syntax.getIgnoredTokens().contains(next.getName())) {
				return next;
			}
		}
		
		return null;
	}
}
