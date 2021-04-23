/*
 * Copyright (c) 2020 Isaac Ellingson (Falkreon) and contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package blue.endless.jarser.collections;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * This will function as a generalized iterator as long as the listree implementations supply isRoot and getParent
 */
public class ListreeIteratorImpl<E> implements ListreeIterator<E> {
	private Listree<E> localRoot;
	private Listree<E> pointer;
	
	public ListreeIteratorImpl(Listree<E> root) {
		localRoot = root;
		pointer = root;
	}
	
	@Override
	public boolean canStepInto() {
		//If we can step down, do
		if (!pointer.getChildren().isEmpty()) return true;
		
		//Otherwise step over
		return canStepOver();
	}

	@Override
	public boolean canStepOver() {
		//Bail if this is root or localRoot
		if (pointer.getParent()==null || pointer==localRoot) return false;
		
		//Continue the walk
		int index = pointer.getParent().getChildren().indexOf(pointer);
		if (index==-1) return false;
		return (pointer.getParent().getChildren().size() > index+1); //TODO: This needs to recurse all the way up.
	}

	@Override
	public E stepInto() {
		if (!pointer.getChildren().isEmpty()) {
			pointer = pointer.getChildren().get(0);
			return pointer.getValue();
		} else {
			return stepOver();
		}
	}

	@Override
	public E stepOver() {
		//Bail if this is root or localRoot
		if (pointer.getParent()==null || pointer==localRoot) throw new NoSuchElementException(); //We really do mean identity-equals here
		
		//Continue the walk
		int index = pointer.getParent().getChildren().indexOf(pointer);
		if (index==-1) throw new IllegalStateException("Inconsistent tree state -  a parent does not contain its child.");
		List<Listree<E>> peers = pointer.getParent().getChildren();
		if (peers.size() <= index+1) throw new NoSuchElementException(); //TODO: This needs to recurse all the way up, getting next peers of increasingly distant parents
		pointer = peers.get(index+1);
		return pointer.getValue();
	}

	@Override
	public Listree<E> getNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
