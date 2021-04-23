/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.collections;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents an ordered tree with infinite cardinality, i.e. an ordered list of symbols, some of which may themselves
 * be composed of ordered lists of symbols. Structures like these are used frequently for textual information,
 * because text is an ordered list of symbols which are frequently organized into syntactic hierarchies.
 * 
 * <p>Unless otherwise specified, this data structure does not accept nulls.
 * 
 * @param <E> The type of element stored at each node of the tree.
 */
public interface Listree<E> {
	/**
	 * Gets a List view of the top-level children of this node
	 * @return an unmodifiable List of Listrees representing the children of this node.
	 */
	public List<Listree<E>> getChildren();
	
	/**
	 * Gets the value associated with this node.
	 * @return The value associated with this node. This may be null if this Listree accepts nulls, or if this
	 * is the root node which has no values.
	 */
	public @Nullable E getValue();
	
	/**
	 * Adds an element to the end of this node's list of children/
	 * @param e the element to add.
	 */
	public void add(E e);
	
	/**
	 * Removes the first occurrence of the specified element from this node's list of immediate children.
	 * @param e the value to remove
	 * @return the object which was removed, or null if no object was removed.
	 */
	public @Nullable E remove(E e);
	
	/**
	 * Determines whether this is the root node or a subtree. Implementations MAY return false for all nodes if their
	 * parentage is unknown.
	 * @return true if this node is the top-level root node, whose value must be null. False if this is a subtree node.
	 */
	public boolean isRoot();
	
	/**
	 * Optional operation. Gets this node's immediate parent.
	 * @return The immediate parent of this node, or null if this node is the root node.
	 */
	public @Nullable Listree<E> getParent();
}
