/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.regex.PatternSyntaxException;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

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
	
	@Override
	public String toString() {
		return "/"+pattern.standardPattern()+"/";
	}
}
