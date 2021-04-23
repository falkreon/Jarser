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

public class Nonterminal implements Production {
	protected final String name;
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
		} else {
			include(p.getStartLine(), p.getStartChar());
			include(p.getEndLine(), p.getEndChar());
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
		public CharSequence value() {
			StringBuilder result = new StringBuilder();
			for(Production p : children) {
				result.append(p.value());
			}
			return result.toString();
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
		return value().toString();
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
