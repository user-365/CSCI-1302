package cs1302.p2;

import cs1302.adt.StringList;
import cs1302.adt.Node;

/**
 * LinkedStringList implements a {@code StringList} using an
 * underlying {@code Node}-chain.
 * 
 * Note: {@code for} loop conditions must take into account that
 * the head {@code Node}s are already attached,
 * meaning the loop should be executed ONE LESS time than expected.
 */
public class LinkedStringList extends BaseStringList {

    private Node head;

    /**
     * Default constructor. (For invocation by subclass
     * constructors, typically implicit.)
     */
    public LinkedStringList() {

    } // Constructor

    /**
     * Overloaded constructor. Necessary because {@code head} is private,
     * so there wasn't a way to set {@code head} from {@code public} areas.
     * 
     * Can attach extant {@code Node}-chain, and {@code size} updates
     * correspondingly.
     * 
     * @param head the {@code LinkedStringList}'s head {@code Node}
     */
    public LinkedStringList(Node head) {
        Node temp = this.head = head;
        this.size = 1; // head guaranteed to have item due to Node constructor
        // Node isn't Iterable :(
        while (temp.hasNext()) { // while more readable than for-loop
            temp = temp.getNext(); // doesn't modify since it's COPYING to temp
            this.size++;
        } // while
    } // Constructor(head)

    /**
     * Adds a new {@code Node} at the specified index
     * in the {@code LinkedStringList}'s {@code Node}-chain.
     * 
     * Re-links to and from a new {@code Node} (using a temp {@code Node}
     * called {@code newNode}), integrating it into the instance's
     * {@code Node} chain at the specified index.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean add(int index, String item) {
        // Must initialize BEFORE intercept()
        if (index == 0 && head == null) { // initialize
            head = new Node(item);
            size = 1;
            return true;
        } else try { // unconventional but readable
            intercept(index, item); // may throw
            if (index == size) { // append
                Node before = getAt(index - 1); // at index before
                before.setNext(new Node(item));
                // fall through to size increment
            } else if (index == 0) { // prepend
                Node newNode = new Node(item, head);
                head = newNode;
                // fall through to size increment
            } else {
                // insert (neither prepend nor append)
                Node before = getAt(index - 1); // at index before
                Node after = getAt(index); // previously at index
                Node newNode = new Node(item, after); // link newNode to After
                before.setNext(newNode); // link Before to newNode
                // fall through to size increment
            } // if-elif-else
            size++;
            return true;
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // add

    /**
     * Empties the {@code LinkedStringList}'s {@code Node}-chain.
     * 
     * Unlinks the {@code head} from the rest of the chain.
     * Sets size to 0.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
    } // clear

    /**
     * Accesses the item at the specified index
     * in the {@code LinkedStringList}'s {@code Node}-chain.
     * 
     * Accesses the specified {@code Node},
     * then returns its value (an item).
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String get(int index) {
        try {
            intercept(index, true); // may throw
            return getAt(index).getItem();
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // get

    /**
     * Removes the item at the specified index
     * in the {@code LinkedStringList}'s {@code Node}-chain.
     * 
     * Unlinks preceding {@code Node},
     * re-linking it to the succeeding {@code Node}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String remove(int index) {
        try {
            intercept(index, true); // may throw
            Node before = getAt(index - 1);
            Node delenda = getAt(index);
            if (delenda.hasNext()) { // does it have "next"?
                before.setNext(delenda.getNext()); // link Before to After
                // fall through to size decrement
            } else {
                // if last in chain
                before.setNext(null);
                // fall through to size decrement
            } // if-else
            size--;
            return delenda.getItem();
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // remove

    /**
     * Returns a "subset" of the {@code LinkedStringList}
     * between the specified indices. Returns same type as caller.
     * 
     * Copies chain during operation since this method
     * shouldn't modify original chain.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public StringList slice(int start, int stop) {
        try {
            intercept(start, true); // may throw
            // guarantees start > 0
            intercept(stop - 1, true); // may throw
            // guarantees stop <= size
            if (start > stop) { // guarantees start < stop
                throw new IndexOutOfBoundsException("Start index of slice " +
                        "cannot be greater than stop index.");
            } else if (stop == start) { // empty LinkedStringList
                return new LinkedStringList();
            } else {
                Node temp = getAt(start); // original chain; don't modify!
                Node newTemp = new Node(temp.getItem()); // copy to new's head
                LinkedStringList subset = new LinkedStringList(newTemp);
                // ^overloaded constructor because can't access head (private)
                for (int i = start + 1; i < stop; i++) {
                    // for-loop condition takes into account head Node
                    newTemp.setNext(temp.getNext()); // copy to next Node
                    // temp.getNext() is safe due to intercept(stop-1)
                    newTemp = newTemp.getNext(); // move to next Node
                    // newTemp.getNext() also safe; we just set it!
                    subset.size++;
                } // for
                return subset;
            } // if-elif-else      
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // slice

    /**
     * Helper method. Gets the {@code Node} at a given index,
     * relative to instance's {@code head}.
     * 
     * @param index the index of the requested {@code Node}, beginning at
     *              {@code head}
     * @return the {@code Node} at the given index
     */
    private Node getAt(int index) {
        try {
            intercept(index, true); // may throw
            // guarantees index < size, thus substitutes hasNext()
            Node temp = head;
            for (int i = 1; i <= index; i++) {
                // for-loop condition takes into account head Node
                // but this is cancelled out bc we actually WANT @ end index
                temp = temp.getNext();
            } // for
            return temp;
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // getAt

} // LinkedStringList
