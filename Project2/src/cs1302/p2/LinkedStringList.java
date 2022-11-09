package cs1302.p2;

import cs1302.adt.*;

/**
 * LinkedStringList implements a {@code StringList} using an
 * underlying {@code Node}-chain.
 * 
 * <p>
 * Note: some {@code for} loop conditions must take into account that
 * the head {@code Node}s are already attached,
 * meaning the loop should be executed ONE LESS time than expected.
 *
 * <p>
 * Last substantial revision: 2022-11-08
 *
 * @author user-365
 */
public class LinkedStringList extends BaseStringList {

    private Node head;

    /**
     * Default constructor. (For invocation by subclass
     * constructors, typically implicit.)
     */
    public LinkedStringList() {
        this.head = null; // redundant but readable
    } // Constructor

    /**
     * TK remove...or not?
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
     * {@code null} items ({@code add()} is safe), but i think
     * the private access modifier takes care of that.
     * 
     * @param head the {@code LinkedStringList}'s head {@code Node}
     */
    private LinkedStringList(Node head) {
        Node temp = this.head = head;
        Node before, after;
        this.size = this.head.getItem() != null ? 1 : 0; // head not guaranteed
        // Node isn't Iterable :(
        while (temp.hasNext()) { // while more readable than for-loop
            before = temp;
            temp = temp.getNext(); // shift
            if (temp.getItem() != null) { // no-skip
                before.setNext(new Node(temp.getItem()));
                this.size++;
            } else if (temp.getItem() == null) { // skip
                after = temp.getNext();
                before.setNext(new Node(after.getItem())); // stitch
            } // if-elif
        } // while
    } // Constructor(Node)

    /**
     * Copy constructor.
     * <p>
     * Assume {@code StringList}s are gapless.
     * 
     * @param other an existing string list object that serves as the source
     *              object for the copy
     */
    public LinkedStringList(StringList other) {
        if (other == null) {
            throw new NullPointerException("Copied StringList must not be null");
        } // ifnull
        Node temp = this.head = new Node(other.get(0)); // set head
        this.size = 1; // guaranteed..TK
        for (int i = 1; i < other.size(); i++) {
            temp.setNext(new Node(other.get(i)));
            this.size++;
            temp = temp.getNext();
        } // for
    } // Constructor (copy)

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
        intercept(item); // may throw NPE, IAE
        // Must initialize BEFORE intercept(index)
        if (this.head == null && index == 0) { // initialize
            // size == 0
            this.head = new Node(item);
            this.size = 1;
            return true;
        } else { // ya inicializó
            // now: check index (only)
            intercept(index, false); // may throw
            if (index == 0) { // prepend
                Node newNode = new Node(item, this.head);
                this.head = newNode;
                // fall through to size increment
            } else { // has Before
                Node before = getNodeAt(index - 1); // at index before
                if (index == this.size) { // append
                    before.setNext(new Node(item));
                    // fall through to size increment
                } else { // medial insert (neither prepend nor append)
                    Node after = before.getNext(); // previously at index
                    Node newNode = new Node(item, after); // link newNode to After
                    before.setNext(newNode); // link Before to newNode
                    // fall through to size increment
                } // if-else
            } // if-elif-else
            this.size++; // update size
            return true;
        } // if-else
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
        this.head = null;
        this.size = 0; // update size
    } // clear

    /**
     * Accesses the item at the specified index
     * in the {@code LinkedStringList}'s {@code Node}-chain.
     * 
     * Accesses the specified {@code Node},
     * then returns its value (an item).
     * Different from {@code getNodeAt()}: this method gets the {@code String}/item,
     * not the {@code Node}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String get(int index) {
        intercept(index, true); // may throw
        return getNodeAt(index).getItem();
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
        intercept(index, true); // may throw
        Node delenda = getNodeAt(index);
        Node after = delenda.hasNext() ? delenda.getNext() : null;
        // ^link to next or delete
        if (index == 0) { // "shift"
            this.head = after;
            // fall through to size decrement
        } else { // medial extract
            Node before = getNodeAt(index - 1);
            before.setNext(after); // stitch
            // fall through to size decrement
        } // if-else
        this.size--; // update size
        return delenda.getItem();
    } // remove

    /**
     * Returns a "subset" of the {@code LinkedStringList}
     * between the specified indices. Returns same type as caller.
     * <em>Not</em> a special case of TK methodname`slice(int, int, int)`
     * since they have different return values.
     * 
     * <p>
     * Copies chain during operation since this method
     * shouldn't modify original chain.
     * 
     * <p>
     * TK changed start intercept to true
     * TK removed second start intercept due to redundancy
     * {@inheritDoc}
     */
    @Override
    public StringList slice(int start, int stop) {
        intercept(start, true); // may throw
        // guarantees start >= 0
        intercept(stop, false); // may throw
        // guarantees stop <= size
        if (stop < start) { // guarantees start <= stop
            throw new IndexOutOfBoundsException("Start index of slice " +
                    "cannot be greater than stop index.");
        } else if (stop == start) { // empty LinkedStringList
            return new LinkedStringList();
        } else { // stop > start
            Node temp = getNodeAt(start); // original chain; don't modify!
            Node newTemp = new Node(temp.getItem()); // copy to new's head
            LinkedStringList subset = new LinkedStringList(newTemp);
            // ^overloaded constructor because can't access head (private)
            for (int i = start + 1; i < stop; i++) {
                // for-loop condition takes into account head Node
                newTemp.setNext(new Node(temp.getNext().getItem())); // COPY to next Node
                // temp.getNext() is safe due to intercept(stop-1)
                temp = temp.getNext(); // shift to next Node
                newTemp = newTemp.getNext(); // shift to next Node
                // newTemp.getNext() also safe; we just set it!
                subset.size++; // update size
            } // for
            return subset;
        } // if-elif-else
    } // slice

    /**
     * Returns a new {@code FancyStringList} that contains the items from this list
     * between the specified start index (inclusive) and stop index (exclusive) by
     * step.
     * 
     * Literally the same as TK methodname
     * 
     * @param start left endpoint (inclusive) of the slice
     * @param stop  right endpoint (exclusive) of the slice
     * @param step  step amount
     * @return a new {@code FancyStringList} with the items from this list from
     *         start
     *         (inclusive) to stop (exclusive) by step
     * @throws IndexOutOfBoundsException - for an illegal endpoint index or step
     *                                   value (start < 0 || stop > size() || start
     *                                   > stop || step < 1)
     *                                   {@inheritDoc}
     */
    @Override
    public FancyStringList slice(int start, int stop, int step) {
        // isEmpty check
        if (isEmpty()) {
            return null;
        } // if
        // boundary check
        intercept(start, true); // may throw
        // guarantees start >= 0
        intercept(stop, false); // may throw
        // guarantees stop <= size
        if (start > stop) { // guarantees start < stop
            throw new IndexOutOfBoundsException("Start index of slice " +
                    "cannot be greater than stop index.");
        } else if (stop == start) { // empty LinkedStringList
            return new LinkedStringList();
        } else { // stop > start
            // step check
            if (step < 1) {
                throw new IndexOutOfBoundsException("Step size must be 1 or greater.");
            } else if (step == 1) { // contiguous slice
                return (LinkedStringList) slice(start, stop);
            } else { // step > 1
                Node temp = getNodeAt(start); // original chain; don't modify!
                Node newTemp = new Node(temp.getItem()); // copy to new's head
                LinkedStringList subset = new LinkedStringList(newTemp);
                // ^overloaded constructor because can't access head (private)
                for (int i = start + step; i < stop; i += step) {
                    // for-loop condition takes into account head Node
                    newTemp.setNext(new Node(this.get(i))); // COPY to next Node
                    newTemp = newTemp.getNext(); // shift to next Node
                    // newTemp.getNext() safe; we just set it!
                    subset.size++; // update size
                } // for
                return subset;
            } // if-elif-else
        } // if-elif-else
    } // slice(int,int,int)

    /**
     * Returns a new {@code FancyStringList} that contains the items from this list
     * in reverse order.
     * 
     * @return a new {@code FancyStringList} with items from this list in reverse
     *         order
     *         {@inheritDoc}
     */
    @Override
    public FancyStringList reverse() {
        if (isEmpty()) {
            return null;
        } else { // non-empty
            // using an array bc i think it'll be faster.
            // it's not the fastest but i can't avoid modifying w/ other algos.
            // singly linked lists only go forward not backward
            String[] blueprint = new String[this.size];
            // copy over
            Node temp = this.head;
            blueprint[0] = temp.getItem(); // first item
            for (int i = 1; i < this.size; i++) {
                blueprint[i] = (temp = temp.getNext()).getItem();
                // getting item only so Node-chain not modified
                // TK the parenthesized assignment MUST come first
            } // for
            // copy to new in reverse
            LinkedStringList reverse = new LinkedStringList();
            reverse.head.setItem(blueprint[blueprint.length - 1]); // first item
            temp = reverse.head; // TK make sure no modify this/self
            for (int i = blueprint.length - 2; i >= 0; i--) {
                (temp = temp.getNext()).setItem(blueprint[i]);
                // TK the parenthesized assignment MUST come first
            } // for
            return reverse;
        } // if-else
    } // reverse

    /**
     * Helper method. Gets the {@code Node} at a given index,
     * relative to instance's {@code head}.
     * Necessary because {@code get()} doesn't return a {@code Node}.
     * 
     * @param index the index of the requested {@code Node}, beginning at
     *              {@code head}
     * @return the {@code Node} at the given index
     */
    private Node getNodeAt(int index) {
        intercept(index, true); // may throw
        // guarantees index >= 0
        // guarantees index < size, thus substitutes hasNext()
        Node temp = this.head;
        for (int i = 1; i <= index; i++) {
            // for-loop condition takes into account head Node
            // but this is cancelled out bc we actually WANT @ end index
            temp = temp.getNext();
        } // for
        return temp;
    } // getNodeAt

} // LinkedStringList
