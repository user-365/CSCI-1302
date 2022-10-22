package cs1302.p2;

import cs1302.adt.StringList;
import cs1302.adt.Node;

/**
 * LinkedStringList implements a {@code StringList} using an
 * underlying {@code Node}-chain.
 * 
 * <p>
 * Note: some {@code for} loop conditions must take into account that
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
        head = null; // redundant but readable
    } // Constructor

    /**
     * Overloaded constructor. Necessary because {@code head} is private,
     * so there wasn't a way to set {@code head} from {@code public} areas.
     * The only alternative is a {@code head} accessor/get method, which is
     * out of the question.
     * 
     * Can attach extant {@code Node}-chains, and {@code size} updates
     * correspondingly.
     * 
     * <p>
     * <strong>Notice</strong>: {@code Node}s are not guaranteed
     * to be non-{@code null}.
     * This is due to {@code Node}-specific methods like {@code setNext()}.
     * Thankfully, the only {@code null} {@code Node}s restricted to
     * the end of the chain, so they are easy to comb out.
     * 
     * <p>
     * <strong>Notice</strong>: {@code Node}s are not guaranteed to have
     * a non-{@code null} item. This is since it's not known whether
     * {@code Node} constructors have anti-{@code null} safety checks.
     * This again implies that {@code LinkedStringList} may not be gapless,
     * requiring a "comb" function to eliminate gaps.
     * 
     * <p>
     * This constructor is the only potentially unsafe point to introduce
     * {@code null} items ({@code add()} is safe).
     * In an effort to ensure gapless-ness,
     * the access modifier for this constructor is
     * as strict as possible (package-level).
     * 
     * @param head the {@code LinkedStringList}'s head {@code Node}
     */
    LinkedStringList(Node head) {
        Node temp = this.head = head;
        Node before, after;
        this.size = this.head.getItem() != null ? 1 : 0; // head not guaranteed
        // Node isn't Iterable :(
        while (temp.hasNext()) { // while more readable than for-loop
            before = temp;
            temp = temp.getNext(); // shift
            if (temp.getItem() != null) {
                before.setNext(temp);
                this.size++;
            } else if (temp.getItem() == null) {
                after = temp.getNext();
                before.setNext(after); // stitch
            } // if-elif
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
     * TK: must prepend before appending, there is concept of before-ness
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean add(int index, String item) {
        intercept(item); // may throw
        // Must initialize BEFORE intercept(index)
        if (head == null && index == 0) { // initialize
            // size == 0
            head = new Node(item);
            size = 1;
            return true;
        } else try { // unconventional but readable
            // now: check index (only)
            intercept(index, false); // may throw
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
        if (index == 0) { // prepend
            Node newNode = new Node(item, head);
            head = newNode;
            // fall through to size increment
        } else if (index == size) { // has Before; append
            Node before = getAt(index - 1); // at index before
            before.setNext(new Node(item));
            // fall through to size increment
        } else {
            // insert (neither prepend nor append)
            Node before = getAt(index - 1); // at index before
            Node after = before.getNext(); // previously at index
            Node newNode = new Node(item, after); // link newNode to After
            before.setNext(newNode); // link Before to newNode
            // fall through to size increment
        } // if-elif-else
        size++; // update size
        return true;
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
        size = 0; // update size
    } // clear

    /**
     * Accesses the item at the specified index
     * in the {@code LinkedStringList}'s {@code Node}-chain.
     * 
     * Accesses the specified {@code Node},
     * then returns its value (an item).
     * Different from {@code getAt()}: this method gets the {@code String}/item,
     * not the {@code Node}.
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
            Node delenda = getAt(index);
            Node after = delenda.hasNext() ? delenda.getNext() : null;
            // ^link to next or delete
            if (index == 0) { // "shift"
                head = after;
                // fall through to size decrement
            } else {
                Node before = getAt(index - 1);
                before.setNext(after);
                // fall through to size decrement
            } // if-else
            size--; // update size
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
            intercept(start, false); // may throw
            intercept(stop, false); // may throw
            // guarantees stop <= size
            if (start > stop) { // guarantees start < stop
                throw new IndexOutOfBoundsException("Start index of slice " +
                        "cannot be greater than stop index.");
            } else if (stop == start) { // empty LinkedStringList
                return new LinkedStringList();
            } else {
                intercept(start, true); // may throw
                // guarantees start >= 0
                Node temp = getAt(start); // original chain; don't modify!
                Node newTemp = new Node(temp.getItem()); // copy to new's head
                LinkedStringList subset = new LinkedStringList(newTemp);
                // ^overloaded constructor because can't access head (private)
                for (int i = start + 1; i < stop; i++) {
                    // for-loop condition takes into account head Node
                    newTemp.setNext(temp.getNext()); // copy to next Node
                    // temp.getNext() is safe due to intercept(stop-1)
                    temp = temp.getNext();
                    newTemp = newTemp.getNext(); // shift to next Node
                    // newTemp.getNext() also safe; we just set it!
                    subset.size++; // update size
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
     * Necessary because {@code get()} doesn't return a {@code Node}.
     * 
     * @param index the index of the requested {@code Node}, beginning at
     *              {@code head}
     * @return the {@code Node} at the given index
     */
    private Node getAt(int index) {
        try {
            intercept(index, true); // may throw
            // guarantees index >= 0
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