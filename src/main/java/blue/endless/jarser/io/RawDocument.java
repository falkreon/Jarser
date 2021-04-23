/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.io;

public class RawDocument implements CharSequence {
	protected char[] data;
	
	public String getAsString(int start, int len) {
		return new String(data, start, len);
	}

	@Override
	public char charAt(int index) {
		return data[index];
	}

	@Override
	public int length() {
		return data.length;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new DocumentSlice(this, start, end-start);
	}
	
	public DocumentSlice slice(int start, int length) {
		return new DocumentSlice(this, start, length);
	}
}
