package cs1302.p2;

import cs1302.adt.StringList;

/**
 * ArrayStringList implements a {@code StringList} using an
 * underlying {@code String} array.
 */
public class ArrayStringList extends BaseStringList {

    private String[] items;
    private final int INITIAL_BUFFER = 2;
    // TK:need items.length?

    /**
     * Default constructor. (For invocation by subclass
     * constructors, typically implicit.)
     */
    public ArrayStringList() {
        items = new String[INITIAL_BUFFER];
        // doesn't matter since array will be replaced
    } // Constructor

    /**
     * Overloaded constructor. Guaranteed to result in a gapless
     * instance {@code items}. Necessary because {@code items} is private,
     * so there wasn't a way to set {@code items} from {@code public} areas.
     * 
     * @param array the array to initialize with
     */
    public ArrayStringList(String[] array) {
        int count = 0;
        for (String string : array) {
            if (string == null) {
                break; // break on first-encountered null element
            } // if
            count++; // count every non-null element
        } // for
        this.items =
        copyOf(array, 0, new String[-~count], 0, -~count);
        // ^we want the array up to and including the first null element
        this.size = count;
    } // Constructor(items)

    /**
     * Adds a new {@code String} at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * github: TK
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean add(int index, String item) {
        try {
            intercept(index, item); // may throw
            if (index == size) { // append
                items[index] = item;
                // fall through to return statement
            } else {
                // size cannot be > items.length due to intercept
                // prepend or insert
                // filled or underfilled handled in copySurrounding()
                copySurrounding(index, 0);
                items[index] = item; // fill in w/ item at index TK
                // fall through to return statement
            } // if-else
            size++;
            return true;
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // add

    // TK
    public void printArray(String label, String[] array) {
        System.out.println("vvv"+"-".repeat(80));
        System.out.print(label+": [");
        for (String string : array) {
            System.out.print(string + ", ");
        }
        System.out.println("]");
        System.out.println("^^^"+"-".repeat(80));
    }

    /**
     * Empties the {@code ArrayStringList}'s array.
     * 
     * Reassigns {@code items} to a new, empty array.
     * Sets size to 0.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        items = new String[size]; // TK
        size = 0;
    } // clear

    /**
     * Accesses the item at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * ditto
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String get(int index) {
        try {
            intercept(index, true); // may throw
            return items[index];
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // get

    /**
     * Removes the item at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * github: TK
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String remove(int index) {
        // TK: similar to insert().
        try {
            intercept(index, true); // may throw
            String delenda = get(index);
            if (index == size - 1) { // "pop": short-circuit to avoid copying
                items[index] = null;
                // fall through to return statement
            } else {
                // index < items.length (i.e., "shift" or extract)
                copySurrounding(index, -1);
                // fall through to return statement
            } // if-else
            size--;
            return delenda;
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // remove

    /**
     * Returns a "subset" of the {@code ArrayStringList}
     * between the specified indices.
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
                "cannot be greater than stop index!");
            } else if (stop == start) { // empty ArrayStringList
                return new ArrayStringList();
            } // if-elif
            int num = stop - start;
            return new ArrayStringList(
                copyOf(items, start, new String[num], 0, num));
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // slice

    /**
     * Helper method. Similar to
     * {@link java.lang.System#arraycopy(Object, int, Object, int, int)} for
     * {@code String[]}s.
     * Difference: Not dependent on side-effects: returns a {@code String[]}.
     * 
     * <p>
     * Will not modify {@code dest} array,
     * due to the fact that TK
     * Thus, do not treat as if void.
     * 
     * @param src      the source array
     * @param srcIndx  the source index (inclusive) from which is copied
     * @param dest     the destination array
     * @param destIndx the destination index (inclusive) at which is pasted
     * @param num      number of elements, counting from srcIndx, to copy
     * @return a modified {@code dest} array, with a "subset" of the
     *         {@code src} array copied over it
     */
    private String[] copyOf(String[] src,
                            int srcIndx,
                            String[] dest,
                            int destIndx,
                            int num) {
        // TK: check argument legality
        // srcIndx within bounds of src, and can accommodate num
        // destIndx within bounds of dest, and can accommodate num
        boolean legal = 0 <= srcIndx && 0 <= destIndx
                        && srcIndx + num <= src.length
                        && destIndx + num <= dest.length;
        try {
            if (!legal) {
                System.out.println("ArrayStringList.copyOf()");
                throw new IllegalArgumentException("fix ur args lol");
            } // if
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } // try-catch
        // all arguments legal!
        for (int i = 0; i < num; i++) {
            dest[destIndx + i] = src[srcIndx + i];
        } // for
        return dest;
    } // copy

    /**
     * Helper method.
     * TK
     * 
     * <p>
     * Side effects: {@code items} refers to a new array with item inserted;
     * instance {@code size} updated.
     * 
     * @param index the index at which to insert item
     * @param setting 0 for add, -1 for remove
     */
    private void copySurrounding(int index, int setting) {
        // TK: check remove setting
        boolean legal = setting == 0 || setting == -1;
        try {
            if (!legal) {
                System.out.println("ArrayStringList.copySurrounding()");
                throw new IllegalArgumentException("fix ur args lol");
            } // if
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } // try-catch
        int preSize = index; // number of elements before the index
        int postSize = size - index + setting;
        // number of elements at/after the index before change
        String[] newArray; // will replace current items array
        if (setting == 0 && items.length <= size + 1) {
            // if array will be filled by adding, enlarge array
            newArray = new String[(int) Math.ceil(items.length * 1.5)];
            // ^ArrayList supposedly does +50%, so i'll do that too
        } else {
            newArray = new String[size]; // won't be filled: no change in length
        } // if-else
        // post-index element copying
        newArray =
        copyOf(items, index - setting, newArray, index + 1 + setting, postSize);
        // srcIndx should be index or index +1
        // num should be (size-index) or (size-index)-1
        if (index == 0) { // prepended/"shifted"
            size = postSize; // reset and update size
            items = newArray; // re-refer to newArray
            return; // no need to copy pre-index elements
        } // if
        // pre-index element copying (same for both remove/add)
        newArray = copyOf(items, 0, newArray, 0, preSize);
        size = postSize + preSize; // reset and update size
        items = newArray; // re-refer to newArray
    } // insert
    
} // ArrayStringList
