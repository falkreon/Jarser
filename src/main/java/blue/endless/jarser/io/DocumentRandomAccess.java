/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.io;

public class DocumentRandomAccess {
	protected final RawDocument document;
	protected int pointer = 0;
	
	public DocumentRandomAccess(RawDocument document) {
		this.document = document;
	}
	
	public void advance() { pointer++; }
	public char read() {
		if (pointer>=document.data.length) {
			return (char) -1;
		} else {
			char result = document.data[pointer];
			pointer++;
			return result;
		}
	}
	
	public char peek() {
		return peek(1);
	}
	
	public char peekBack() {
		return peek(-1);
	}
	
	public char peek(int distance) {
		int peekPtr = pointer+distance;
		if (peekPtr>=document.data.length || peekPtr<0) return (char) -1;
		return document.data[peekPtr];
	}
	
	public int position() { return pointer; }
	
	public void seek(int pointer) { this.pointer = pointer; }
	
	public DocumentSlice slice(int rawPosition, int length) { return document.slice(rawPosition, length); }
	
	
	public DocumentSlice sliceFrom(int rawPosition) {
		int len = pointer - rawPosition;
		if (len<0) len = 0;
		
		return new DocumentSlice(document, rawPosition, len);
	}
}
