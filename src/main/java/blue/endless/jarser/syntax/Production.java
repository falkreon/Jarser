/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * A node of the syntax tree built by jarser. A Production is either a Token or a Nonterminal. Only those two subclasses are allowed.
 */
public interface Production {
	public String getName();
	public String getValue();
	public String getRawValue();
	public String getSource();
	public int getStartLine();
	public int getStartChar();
	public int getEndLine();
	public int getEndChar();
	
	public List<Production> getChildren();
	public boolean isTerminal();
	
	public default String explain() {
		List<Production> children = getChildren();
		if (children.size()==0) return "["+this.getName()+":\""+this.getRawValue()+"\"]";
		
		StringBuilder childResultBuilder = new StringBuilder();
		for(Production p : children) {
			childResultBuilder.append(p.explain());
		}
		String childResult = childResultBuilder.toString();
		//int total = childResult.length();
		String selfResult = this.getName()+":\""+this.getRawValue()+"\"";
		//TODO: Move this child-node-expansion work to recursiveExplain somehow
		//if (selfResult.length()+1>childResult.length()) {
			//strip the far-right brace and pad the last child
			//childResult = childResult.substring(0, childResult.length()-1);
			//int toAdd = selfResult.length()+2-childResult.length()-1;
			//for(int i=0; i<toAdd; i++) childResult += ' ';
			//childResult += ']';
		//} else
		if (childResult.length()>selfResult.length()+2) {
			int toAdd = childResult.length() - (selfResult.length()+2);
			int addLeft = toAdd/2;
			for(int i=0; i<addLeft; i++) selfResult = ' '+selfResult;
			int addRight = toAdd - addLeft;
			for(int i=0; i<addRight; i++) selfResult = selfResult+' ';
		}
		
		return "["+selfResult+"]";
	}
	
	public default String recursiveExplain() {
		ArrayList<Production> nextLineQueue = new ArrayList<>();
		nextLineQueue.add(this);
		StringBuilder result = new StringBuilder();
		while(!nextLineQueue.isEmpty()) {
			ArrayList<Production> currentLineQueue = new ArrayList<>(nextLineQueue);
			nextLineQueue.clear();
			for(Production p : currentLineQueue) {
				result.append(p.explain());
				nextLineQueue.addAll(p.getChildren());
			}
			result.append("\n");
		}
		return result.toString();
	}
}
