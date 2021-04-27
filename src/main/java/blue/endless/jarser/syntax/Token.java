/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token implements Production {
	protected final String name;
	protected final CharSequence value;
	protected final int startLine;
	protected final int startChar;
	protected final int endLine;
	protected final int endChar;
	protected final Map<String, String> namedCaptures;
	
	public Token(String name, CharSequence value, int startLine, int startChar, int endLine, int endChar, HashMap<String, String> namedCaptures) {
		this.name = name;
		this.value = value;
		this.startLine = startLine;
		this.startChar = startChar;
		this.endLine = endLine;
		this.endChar = endChar;
		this.namedCaptures = Collections.unmodifiableMap(namedCaptures);
	}
	
	public String getNamedCapture(String name) {
		return namedCaptures.getOrDefault(name, "");
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
	
	@Override
	public String toString() {
		return value.toString();
	}
}