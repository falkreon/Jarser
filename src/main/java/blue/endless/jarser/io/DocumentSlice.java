/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.io;

public class DocumentSlice implements CharSequence {
	
	protected final RawDocument document;
	protected final int start;
	protected final int len;
	
	public DocumentSlice(RawDocument document, int start, int len) {
		this.document = document;
		this.start = start;
		this.len = len;
	}
	
	@Override
	public char charAt(int index) {
		return document.data[start+index];
	}

	@Override
	public int length() {
		return len;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (start==end) return "";
		
		//TODO: Range check to verify that the result falls within the bounds of this slice
		int sublen = end - start;
		return new DocumentSlice(document, start+this.start, sublen);
	}
	
	public DocumentSlice slice(int start, int len) {
		return new DocumentSlice(document, start+this.start, len);
	}

}
