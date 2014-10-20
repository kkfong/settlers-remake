package jsettlers.common.utils.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

/**
 * This class implements a double linked list of {@link DoubleLinkedListItem}s.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DoubleLinkedList<T extends DoubleLinkedListItem<T>> implements Serializable, Iterable<T> {
	private static final long serialVersionUID = -8229566677756169997L;

	transient T head;
	private transient int size = 0;

	public DoubleLinkedList() {
		initHead();
	}

	@SuppressWarnings("unchecked")
	private void initHead() {
		head = (T) new DoubleLinkedListItem<T>();
		head.next = head;
		head.prev = head;
	}

	public void pushFront(T newItem) {
		newItem.next = head.next;
		newItem.prev = head;
		newItem.next.prev = newItem;
		head.next = newItem;

		size++;
	}

	public void pushEnd(T newItem) {
		newItem.next = head;
		newItem.prev = head.prev;
		newItem.prev.next = newItem;
		head.prev = newItem;

		size++;
	}

	/**
	 * Pops the first element from this list.
	 * <p />
	 * NOTE: NEVER EVER call popFront on an empty queue! There will be no internal checks!
	 * 
	 * @return
	 */
	public T popFront() {
		final T item = head.next;

		item.next.prev = head;
		head.next = item.next;
		item.next = null;
		item.prev = null;

		size--;

		return item;
	}

	public T getFront() {
		return head.next;
	}

	public void remove(DoubleLinkedListItem<T> item) {
		item.prev.next = item.next;
		item.next.prev = item.prev;

		size--;
	}

	public int size() {
		return size;
	}

	public void clear() {
		head.next = head;
		head.prev = head;
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Generates a new array of {@link DoubleLinkedList}s of the given length. The array will be filled with new {@link DoubleLinkedList} objects.
	 * 
	 * @param length
	 *            Length of the resulting array.
	 * @return
	 */
	public static <T extends DoubleLinkedListItem<T>> DoubleLinkedList<T>[] getArray(final int length) {
		@SuppressWarnings("unchecked")
		DoubleLinkedList<T>[] array = new DoubleLinkedList[length];
		for (int i = 0; i < length; i++) {
			array[i] = new DoubleLinkedList<T>();
		}

		return array;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(size);

		T curr = head.next;
		for (int i = 0; i < size; i++) {
			oos.writeObject(curr);
			curr = curr.next;
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		initHead();

		int size = ois.readInt();

		for (int i = 0; i < size; i++) {
			pushEnd((T) ois.readObject());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		@SuppressWarnings("unchecked")
		DoubleLinkedList<T> other = (DoubleLinkedList<T>) obj;
		if (size != other.size)
			return false;

		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head)) {
			return false;
		} else {
			T thisCurr = head.next;
			T otherCurr = other.head.next;
			for (int i = 0; i < size; i++) {
				if (thisCurr == null) {
					if (otherCurr != null)
						return false;
				} else if (!thisCurr.equals(otherCurr))
					return false;

				thisCurr = thisCurr.next;
				otherCurr = otherCurr.next;
			}
		}

		return true;
	}

	@Override
	public Iterator<T> iterator() {
		return new DoubleLinkedListIterator<T>(this);
	}

	/**
	 * Adds all elements of this list to the given {@link DoubleLinkedList}. After this operation this list will not contain any elements.
	 * 
	 * @param newList
	 *            The list to append all the elements of this list.
	 */
	public void mergeInto(DoubleLinkedList<T> newList) {
		newList.head.prev.next = this.head.next;
		this.head.next.prev = newList.head.prev;
		this.head.prev.next = newList.head;
		newList.head.prev = this.head.prev;
		newList.size += size;

		clear();
	}

	// public void simpleSanityCheck() {
	// if (size == 0 && (head.next != head || head.prev != head)) {
	// System.err.println("LIST IS FUCKED UP");
	// }
	// }
}