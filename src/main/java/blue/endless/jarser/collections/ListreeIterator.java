/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.collections;

import java.util.Iterator;

/**
 * Iterator for a guided depth-first traversal of a Listree. A full depth-first traversal can be achieved with:
 * 
 * <pre><code>
 * while(iter.canStepInto()) {
 *   E cur = iter.stepInto();
 *   (...)
 * }
 * </code></pre>
 */
public interface ListreeIterator<E> extends Iterator<E> {
	public boolean canStepInto();
	public boolean canStepOver();
	
	/**
	 * Gets the next element in the depth-first traversal of the Listree subject
	 * @return The next element of the Listree
	 */
	public E stepInto();
	
	/**
	 * Skips any children of the current element, and resumes traversal of the tree as if we were returning "upwards" from this element's children.
	 * @return the next element, ignoring this element's children if any exist.
	 */
	public E stepOver();
	
	/**
	 * Returns the node which the current element was found at.
	 * @return the node the current element was found at.
	 */
	public Listree<E> getNode();
	
	
	//Fully implements Iterator<E> based on ListreeIterator<E> methods for enhanced-for tree traversal:
	
	@Override
	public default boolean hasNext() {
		return canStepInto();
	}
	
	@Override
	default E next() {
		return stepInto();
	}
}
