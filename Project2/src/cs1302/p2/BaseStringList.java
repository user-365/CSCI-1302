package cs1302.p2;

import cs1302.adt.StringList;

/**
 * BaseStringList is the abstract base class for the two different
 * implementations of a {@code BaseStringList}: {@code ArrayStringList}
 * and {@code LinkedStringList}.
 */
public abstract class BaseStringList implements StringList {

    protected int size;
    
    /**
    * Sole constructor. (For invocation by subclass
    * constructors, typically implicit.)
    */
    public BaseStringList() {
        size = 0;
    } // Constructor

    /**
     * Adds new item to end of {@code BaseStringList}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean append(String item) {
        return add(size, item);
    } // append

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
        return add(0, item);
    } // prepend

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
     * Helper method. Throws an exception if "out of bounds".
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
     * is when it's called {@code add()}).
     * 
     * <p>
     * One of two overloaded {@code intercept()}s.
     * 
     * @param index    the index to be checked
     * @param checkEnd whether to check {@code index == size} or not;
     *                 true if want to check end, false otherwise
     * @throws IndexOutOfBoundsException if index is out of bounds
     * @see #intercept(String)
     */
    protected void intercept(int index, boolean checkEnd) {
        // leaves index==size corner case on behalf of add()#append
        // if not appending, throw if (index==size) as well
        if (index < 0 || (checkEnd
                            ? index >= size
                            : index > size)) {
            throw new IndexOutOfBoundsException("Index cannot be out of " +
            "bounds!");
        } // if
    } // ifOut

    /**
     * Helper method.
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