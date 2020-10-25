package blue.endless.jarser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Production implements List<Token> {
	protected ArrayList<Token> tokens = new ArrayList<>();
	
	public Production() {}

	@Override
	public boolean add(Token e) {
		return tokens.add(e);
	}

	@Override
	public void add(int index, Token element) {
		tokens.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends Token> c) {
		return tokens.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Token> c) {
		return tokens.addAll(index, c);
	}

	@Override
	public void clear() {
		tokens.clear();
	}

	@Override
	public boolean contains(Object o) {
		return tokens.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return tokens.containsAll(c);
	}

	@Override
	public Token get(int index) {
		return tokens.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return tokens.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Token> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<Token> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<Token> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Token remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Token set(int index, Token element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Token> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
