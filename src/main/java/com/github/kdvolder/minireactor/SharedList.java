package com.github.kdvolder.minireactor;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

/**
 * Datastructure for managing a list of stuff. It can be accessed from 
 * multiple threads simultaneously to add/remove elements or iterate
 * all elements.
 */
public class SharedList<T> implements Iterable<T> {

	//This implementation is simple but not very efficient.
	
	//An efficient version might use a custom implementation of LinkedList where
	// the element handles are direct pointers into the list for efficient 
	// removal and iterator.

	private class SimpleHandle implements ElementHandle {
		private T e;

		public SimpleHandle(T e) {
			this.e = e;
		}

		@Override
		public void remove() {
			synchronized (SharedList.this) {
				listCopy = null;
				elements.remove(e);
			}
		}
	}

	/**
	 * Removes corresponding element from the list. If there are 
	 * active iterators, these iterators are not disrupted (no concurrent modification exceptions)
	 * <p>
	 * However, we provide no guarantees whether or not active iterators will or will not see
	 * the removed element. (This depends on the implementation of the SharedList)
	 */
	interface ElementHandle {
		void remove();
	}
	
	private ArrayList<T> elements = new ArrayList<>();
	private ImmutableList<T> listCopy = null;

	public synchronized ElementHandle add(T e) {
		listCopy = null;
		elements.add(e);
		return new SimpleHandle(e);
	}

	@Override
	public synchronized Iterator<T> iterator() {
		//The easiest, if not most efficient way to avoid concurrent mod exceptions is to make a copy 
		// of the list. At least, we make sure to reuse the same copy as long as the list hasn't changed.
		if (listCopy==null) {
			listCopy = ImmutableList.copyOf(elements);
		}
		return ImmutableList.copyOf(elements).iterator();
	}

}
