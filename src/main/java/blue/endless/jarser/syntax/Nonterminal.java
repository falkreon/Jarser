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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nonterminal implements Production {
	private static final Logger log = LoggerFactory.getLogger(Nonterminal.class);
	protected final String name;
	protected String source = "";
	protected ArrayList<Production> children = new ArrayList<>();
	protected int startLine = 0;
	protected int startChar = 0;
	protected int endLine = 0;
	protected int endChar = 0;
	
	public Nonterminal(String name) {
		this.name = name;
	}
	
	/**
	 * As per {@link java.util.Collection#add}
	 */
	public boolean add(Production p) {
		if (children.isEmpty()) {
			this.startLine = p.getStartLine();
			this.startChar = p.getStartChar();
			this.endLine = p.getEndLine();
			this.endChar = p.getEndChar();
			this.source = p.getSource();
		} else {
			include(p.getStartLine(), p.getStartChar());
			include(p.getEndLine(), p.getEndChar());
			if (p.getSource()!=source) { //(sic). We really do want to pass the same source file *object* around, not just the same characters. equals() can be a lot slower, too, for long strings with matching sizes.
				log.warn("Nonterminal inherits different source String objects from its children. File traces from these symbols may be inconsistent!");
			}
		}
		return children.add(p);
	}
	
	/**
	 * As per {@link java.util.Collection#remove}
	 */
	public boolean remove(Production p) {
		boolean result = children.remove(p);
		if (result) {
			if (children.isEmpty()) {
				//Just zero it out
				startLine = 0;
				startChar = 0;
				endLine = 0;
				endChar = 0;
			} else {
				//Recalculate the whole thing from our remaining children
				for(int i=0; i<children.size(); i++) {
					Production q = children.get(i);
					if (i==0) {
						this.startLine = q.getStartLine();
						this.startChar = q.getStartChar();
						this.endLine = q.getEndLine();
						this.endChar = q.getEndChar();
					} else {
						include(q.getStartLine(), q.getStartChar());
						include(q.getEndLine(), q.getEndChar());
					}
				}
			}
		}
		
		return result;
	}
	
	protected void include(int line, int character) {
		if (line<startLine) { //Earlier line than our start
			startLine = line;
			startChar = character;
		} else if (line==startLine && character<startChar) { //Earlier character on the same line as our start
			startChar = character;
		} else if (line==endLine && character>endChar) { //Later character on the same line as our end
			endChar = character;
		} else if (line>endLine) { //Later line than our end
			endLine = line;
			endChar = character;
		} else { //Inside our existing range
			//do nothing
		}
	}
	
	//implements Production {
	
		@Override
		public String getName() {
			return name;
		}
	
		@Override
		public String getValue() {
			return getRawValue();
		}
		
		@Override
		public String getRawValue() {
			//TODO: Probably more efficient to build substring from the source text using startChar/startLine/endChar/endLine
			
			StringBuilder result = new StringBuilder();
			for(Production p : children) {
				result.append(p.getValue());
			}
			return result.toString();
		}
		
		@Override
		public String getSource() {
			return this.source;
		}
	
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
			return Collections.unmodifiableList(children);
		}
	
		@Override
		public boolean isTerminal() {
			return false;
		}
	
	//}
	
	@Override
	public String toString() {
		return getValue();
		/*
		StringBuilder result = new StringBuilder();
		for(int i=0; i<children.size(); i++) {
			Production p = children.get(i);
			result.append(p.value());
			if (i<children.size()-1) result.append(' ');
		}
		
		return result.toString();*/
	}
}
