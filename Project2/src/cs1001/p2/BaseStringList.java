package cs1302.p2;

import cs1302.adt.StringList;

/**
 * BaseStringList is the abstract base class for the two different
 * implementations of a {@code StringList}: {@code ArrayStringList}
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
     * Add new item to end of {@code StringList}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean append(String item) {
        return add(size, item);
    } // append

    /**
     * Checks if {@code StringList} is empty.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    } // isEmpty

    /**
     * Formats a {@code String} with the {@code StringList}'s items.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String makeString(String start, String sep, String end) {
        StringBuilder sb = new StringBuilder(start);
        for (int i = 0; i < size; i++) { // note: size, not array length
            if (i == size - 1) { // last element doesn't need a separator after
                sb.append(get(i));
                break;
            } // if
            sb.append(get(i)).append(sep); // TK
        } // for
        return sb.append(end).toString();
    } // makeString

    /**
     * Add new item to beginning of {@code StringList}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean prepend(String item) {
        return add(0, item);
    } // prepend

    /**
     * Returns the size of the {@code StringList}
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
     * Helper method. Throws an exception if out of bounds.
     * Since a {@code StringList} is gapless, also guarantees that all preceding
     * items exist.
     * 
     * Prefer: set {@code checkEnd} to {@code true}.
     * Only case set to {@code false} is in {@code intercept(int, String)}
     * (i.e., only {@code false} when called in {@code add()})
     * ((i.e., only {@code false} inside {@code BaseStringList})).
     * 
     * @param index    the index to be checked
     * @param checkEnd whether to check {@code index == size} or not;
     *                 true if want to check end, false otherwise
     * @throws IndexOutOfBoundsException if index is out of bounds
     * @see intercept(int index, String item) intercept(int, String)
     */
    protected void intercept(int index, boolean checkEnd) {
        // leaves index==size corner case on behalf of add()#append
        // if not appending, throw if (index==size) as well
        if (index < 0
            || checkEnd
                ? index >= size
                : index > size) { // TK
            throw new IndexOutOfBoundsException("Index cannot be out of " +
            "bounds!");
        } // if
    } // ifOut

    /**
     * Helper method.
     * Check if arguments can be expected by other methods (i.e., do not
     * produce an exception when passed to the various other instance methods).
     * Throws exceptions if arguments are exceptional.
     * "Exceptional", in this case, means one of three things:
     * <ul>
     * <li>item is {@code null},</li>
     * <li>item (as a {@code String}) is empty, or</li>
     * <li>index is out of bounds</li>
     * </ul>
     * 
     * @param index the index to be checked
     * @param item  the item to be checked
     * @throws NullPointerException     If item is {@code null}
     * @throws IllegalArgumentException If item (as a {@code String}) is
     *                                  empty
     * @see (int index, boolean checkEnd) intercept(int, boolean)
     */
    protected void intercept(int index, String item) {
        if (item == null) { // null
            throw new NullPointerException("Item cannot be null!");
        } // if
        if (item.length() == 0) { // empty
            throw new IllegalArgumentException(
                "Item cannot be an empty string!");
        } // if
        intercept(index, false);
        // ^assumes ONLY add() calls intercept(int, String)
    } // intercept
    
} // BaseStringList