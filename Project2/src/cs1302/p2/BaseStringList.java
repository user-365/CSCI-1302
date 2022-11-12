package cs1302.p2;

import cs1302.adt.*;

/**
 * BaseStringList is the abstract base class for the two different
 * implementations of a {@code BaseStringList}: {@code ArrayStringList}
 * and {@code LinkedStringList}.
 *
 * <p>
 * Last substantial revision: 2022-11-11 19:20
 *
 * @author user-365
 */
public abstract class BaseStringList implements FancyStringList {

    protected int size;
    
    /**
    * Default constructor. (For invocation by subclass
    * constructors, typically implicit.)
    */
    public BaseStringList() {
        size = 0;
    } // Constructor (default)

    /**
     * Inserts items into this {@code StringList}
     * at the specified index position.
     * 
     * @param index index at which the specified items are to be inserted
     * @param items {@code StringList} of items to be inserted
     * @return true if this list changed as a result of the call,
     *         false otherwise
     * @throws NullPointerException if items is null
     *                              {@inheritDoc}
     */
    @Override
    public boolean add(int index, StringList items) {
        if (items == null) {
            throw new NullPointerException("Added StringList cannot be null!");
        } // ifnull
        intercept(index, false); // throws IOOBE
        if (items == this) { // yes self-reference          
            // pencil-and-paper algorithm: setup, before, middle, after
            // setup
            final int len = this.size;
            final int init = index; // initial index

            // before (a). get(i=0++), put at index=index+0++
            // before (b). get(i=1++), put at index=index+1++
            // ...
            // before (z). get(i=(init)-1++), put at index=index+index(init)-1++
            int i = 0;
            for (; i < init; i++) {
                add(i + init, get(i)); // concrete-subclass method
            } // for
            // i == init due to last i++

            int loc = i;
            // middle (a). if index(set) == 2*(init)...
            if (i == init) {
                // ...then add (init) to loc
                loc += init;
            } // if
            // middle (b). assert loc = (set)
            assert loc == i + init;

            // after (a). get(loc=set+0 +=2), put at index=(set)++
            // after (b). get(loc=set+2 +=2), put at index=(set)++
            // ...
            for (; i < len + init && loc < size; i++, loc += 2) {
                // loc>=i always
                add(i + init, get(loc)); // concrete-subclass method
            } // for
        } else { // no self-reference
            for (int i = 0; i < items.size(); i++) {
                add(i + index, items.get(i)); // concrete-subclass method
            } // for
        } // if-else
        return !items.isEmpty();
    } // add(int,StringList)
    
    /**
     * Adds new item to end of {@code BaseStringList}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean append(String item) {
        return add(size, item); // concrete-subclass method
    } // append

    /**
     * Appends items to this {@code BaseStringList} (i.e., it inserts the items
     * starting at
     * index {@code size}).
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean append(StringList items) {
        return add(size, items); // throws NPE, IOOBE
    } // append(StringList)

    /**
     * Returns true if start >= 0 and there exists an item at or after the start
     * index that equals the target string.
     * 
     * @param start  the index from which to start the search
     * @param target the item to search for
     * @return true if there exists an item at or after start such that
     *         item.equals(target), or false if there is no such occurrence
     *         {@inheritDoc}
     */
    @Override
    public boolean contains(int start, String target) {
        if (start >= 0) {
            for (int i = start; i < size; i++) {
                // ^out of bounds taken care of by condition
                if (get(i).equals(target)) {
                    return true;
                } // if
            } // for
        } // if
        return false;
    } // contains

    /**
     * Returns the index of the first item at or after the start index or 0
     * (whichever is larger) within this {@code BaseStringList} that equals
     * the target string.
     * 
     * @param start  the index from which to start the search
     * @param target the item to search for
     * @return the index of the first item at or after start that equals target,
     *         starting at start, or -1 if there is no such occurrence
     *         {@inheritDoc}
     */
    @Override
    public int indexOf(int start, String target) {
        for (int k = 0; k < size; k++) {
            if (k >= start && start >= 0 && get(k).equals(target)) {
                return Math.max(k, 0);
            } // if
        } // for
        return -1;
    } // indexOf

    /**
     * Checks whether {@code BaseStringList} is empty.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    } // isEmpty

    /**
     * Encodes the {@code BaseStringList}'s items
     * into a human-readable {@code String}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String makeString(String start, String sep, String end) {
        StringBuilder sb = new StringBuilder(start == null ? "null" : start);
        // ^i have a feeling the null-check is implicit
        for (int i = 0; i < size; i++) { // note: size, not array length
            if (i == size - 1) { // last element doesn't need a separator after
                sb.append(get(i));
                break;
            } // if
            sb.append(get(i)).append(sep);
        } // for
        return sb.append(end).toString();
    } // makeString

    /**
     * Adds new item to beginning of {@code BaseStringList}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean prepend(String item) {
        return add(0, item); // concrete-subclass method
    } // prepend

    /**
     * Prepends items to this {@code BaseStringList} (i.e., it inserts the items
     * starting at
     * index 0).
     * 
     * @param items string list of items to be inserted
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if items is null
     *                              {@inheritDoc}
     */
    @Override
    public boolean prepend(StringList items) {
        return add(0, items); // throws NPE, IOOBE
    } // prepend(StringList)

    /**
     * Returns the size of the {@code BaseStringList}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return this.size;
    } // size

    /**
     * Imitates {@link java.util.Arrays#toString(boolean[]) Arrays.toString()}
     * for {@code BaseStringList}s.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return makeString("[", ", ", "]");
    } // toString

    /**
     * Helper method.
     * Throws an exception if "out of bounds".
     * Here, "out of bounds," in terms of an array index,
     * means {@code (index < 0 || index >= size)},
     * or {@code (index < 0 || index > size)}.
     * Assuming all {@code BaseStringList}s are gapless, also guarantees
     * that all preceding items exist.
     * 
     * The effect is that {@code index} is sandwiched
     * between {@code 0} and {@code size}.
     * 
     * <p>
     * Prefer: set {@code checkEnd} to {@code true}.
     * The <em>only</em> case where {@code false} is passed in
     * is when it's called in {@code add()} or for `stop` argument of {@code slice()}).
     * 
     * <p>
     * One of two overloaded {@code intercept()}s.
     * 
     * @param index    the index to be checked
     * @param checkEnd whether to check {@code index == size} or not;
     *                 true if want to check end (default), false otherwise
     * @throws IndexOutOfBoundsException if index is out of bounds
     * @see #intercept(String)
     */
    protected void intercept(int index, boolean checkEnd) {
        // leaves index==size corner case on behalf of add()#append
        // if not appending, throw if (index==size) as well
        if (index < 0 || (checkEnd
                            ? index >= size
                            : index > size)) {
            throw new IndexOutOfBoundsException("Index %1$d must be in [0, %2$d"
            .formatted(index, size) + (checkEnd ? ")." : "]."));
        } // if
    } // ifOut

    /**
     * Helper method. Not called/used in {@code BaseStringList}.
     * Check if arguments can be expected by other methods (i.e., do not
     * produce an exception when passed to the various other instance methods).
     * Throws exceptions if arguments are exceptional.
     * "Exceptional", in this case, means one of two things:
     * <ul>
     * <li>item is {@code null}, or</li>
     * <li>item (as a {@code String}) is empty.</li>
     * </ul>
     * 
     * <p>
     * One of two overloaded {@code intercept()}s.
     * 
     * @param item   the item to be checked
     * @throws NullPointerException     If item is {@code null}
     * @throws IllegalArgumentException If item (as a {@code String}) is
     *                                  empty
     * @see #intercept(int, boolean)
     */
    protected void intercept(String item) {
        // ^assumes ONLY add() calls intercept(int, String)
        if (item == null) { // null
            throw new NullPointerException("Item cannot be null!");
        } // if
        if (item.length() == 0) { // empty
            throw new IllegalArgumentException(
                "Item cannot be an empty string!");
        } // if
    } // intercept

} // BaseStringList
