/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ArrayListree<E> implements Listree<E> {
	protected ArrayListree<E> parent = null;
	protected E value;
	protected ArrayList<ArrayListree<E>> children = new ArrayList<>();
	
	public ArrayListree() {}
	
	public ArrayListree(E value) {
		this.value = value;
	}
	
	@Override
	public List<Listree<E>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public @Nullable E getValue() {
		return value;
	}

	@Override
	public void add(@NonNull E e) {
		ArrayListree<E> child = new ArrayListree<>();
		child.parent = this;
		child.value = e;
		children.add(child);
	}

	@Override
	public @Nullable E remove(E e) {
		Iterator<ArrayListree<E>> iterator = children.iterator();
		while (iterator.hasNext()) {
			E val = iterator.next().getValue();
			if (val!=null && val.equals(e)) {
				iterator.remove();
				return val;
			}
		}
		
		return null;
	}

	@Override
	public boolean isRoot() {
		return (parent==null);
	}

	@Override
	public @Nullable Listree<E> getParent() {
		return parent;
	}

}
