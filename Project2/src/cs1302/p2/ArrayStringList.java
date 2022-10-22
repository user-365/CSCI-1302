package cs1302.p2;

import cs1302.adt.StringList;

/**
 * ArrayStringList implements a {@code StringList} using an
 * underlying {@code String} array.
 * 
 * The underlying array has at least one extra space at the end as buffer,
 * due to its constructors.
 */
public class ArrayStringList extends BaseStringList {

    private String[] items;

    /**
     * Default constructor. (For invocation by subclass
     * constructors, typically implicit.)
     */
    public ArrayStringList() {
        items = new String[3];
        // init length doesn't matter since array will be replaced
    } // Constructor()

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
        copy(array, 0, new String[count + 3], 0, count);
        // ^we want the array up to the first null element.
        // ^adds one space as buffer
        this.size = count; // update size
    } // Constructor(String[])

    /**
     * Adds a new {@code String} at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * Short-circuits appending OR copies like normal (leaving a space),
     * then inserting provided {@code item} at {@code index}.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean add(int index, String item) {
        try {
            intercept(index, false); // may throw
            intercept(item); // may throw
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
        if (size > 0) {
            if (index == size) { // append
                // copies contents over to bigger array
                String[] bigger = new String[items.length + 3];
                items = copy(items, 0, bigger, 0, size);
                // fall through to `items[index] = item;`
            } else {
                // size cannot be > items.length due to intercept
                // prepend or insert
                // filled or underfilled handled in copySurrounding()
                copySurrounding(index, 0);
                // fall through to `items[index] = item;`
            } // if-else
        } // if
        // size==0 OR fell through
        items[index] = item; // fill in w/ item at index
        size++; // update size
        return true;
    } // add

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
        items = new String[items.length]; // keep previous length
        size = 0; // update size
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
            return items[index] != null ? items[index] : null;
        } catch (RuntimeException re) {
            throw re;
        } // try-catch
    } // get

    /**
     * Removes the item at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * Stashes the to-be-removed item in {@code delenda}.
     * "Popping" here is simply nullifying the last element.
     * Otherwise, copy the array so that the hole left by the removed item
     * is filled in.
     * 
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String remove(int index) {
        try {
            intercept(index, true); // may throw
            String delenda = get(index); // to be returned
            if (index == size - 1) { // "pop": short-circuit to avoid copying
                items[index] = null;
                // fall through to size update
            } else {
                // index < items.length (i.e., "shift" or extract)
                copySurrounding(index, -1);
                // fall through to size update
            } // if-else
            size--; // update size
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
            intercept(start, false); // may throw
            intercept(stop, false); // may throw
            // guarantees stop <= size
            if (start > stop) { // guarantees start < stop
                throw new IndexOutOfBoundsException("Start index of slice " +
                        "cannot be greater than stop index.");
            } else if (stop == start) { // empty ArrayStringList
                return new ArrayStringList();
            } else {
                intercept(start, true); // may throw
                // guarantees start >= 0
                int num = stop - start;
                return new ArrayStringList(
                        copy(items, start, new String[num], 0, num));
                // constructor will add one space as buffer
            } // if-elif-else
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
     * Side effect: will modify {@code dest} array. Even still, don't treat it
     * like {@code void}, since it will NOT modify SIZE of {@code dest} array.
     * i can't get rid of this side effect since, to avoid modifying
     * {@code dest}, a COPY of {@code dest} is required in the first place,
     * which would lead to an infinite recusion.
     * 
     * @param src      the source array
     * @param srcIndx  the source index (inclusive) from which is copied
     * @param dest     the destination array
     * @param destIndx the destination index (inclusive) at which is pasted
     * @param num      number of elements, counting from srcIndx, to copy
     * @return a modified {@code dest} array, with a "subset" of the
     *         {@code src} array copied over it
     */
    private String[] copy(String[] src,
                            int srcIndx,
                            String[] dest,
                            int destIndx,
                            int num) {
        // srcIndx within bounds of src, which can accommodate num
        // destIndx within bounds of dest, which can accommodate num
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
     * Copies elements both below and above point of addition/removal
     * to another array. If adding, pastes the elements, leaving a space
     * for added item. If removing, pastes the elements, filling in
     * the hole left by removed item.
     * 
     * Splits source array at point of addition/removal.
     * Pastes bottom section to the same position in destination array.
     * If adding, shifts top section rightward and pastes,
     * leaving a space for added item.
     * If removing, shifts top section leftward and pastes,
     * filling in the hole left by the removed item.
     * (In both cases, destination array is gapless).
     * 
     * <p>
     * Side effects: {@code items} refers to a new array with item inserted.
     * 
     * <p>
     * Notice: {@code size} NOT updated (handled by {@code add()}
     * and {@code remove()}).
     * 
     * @param index the index at which to insert item
     * @param setting 0 for add, -1 for remove
     */
    private void copySurrounding(int index, int setting) {
        /*------------LEGALITY CHECK------------*/
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
        /*------------INITIALIZE------------*/ 
        // preSize+postSize = total # of elements to be copied
        int preSize = index; // # of elements before the index
        int postSize = size - index + setting;
        // ^"add": # of elements at/after the index before change
        // ^"remove": # of elements after the index before change
        String[] newArray; // will replace current items array
        if (setting == 0 && items.length <= size + 1) {
            // if array imminently filled by adding, enlarge array
            newArray = new String[(int) Math.ceil(items.length * 1.5)];
            // ^ArrayList supposedly does +50%, so i'll do that too
        } else { // "remove" setting OR array NOT imminently filled
            newArray = new String[items.length]; // keep array length
        } // if-else
        /*------------POST-INDEX COPY------------*/
        // post-index element copying
        newArray =
            copy(items, index - setting, newArray, index-~setting, postSize);
        //          src     srcIndx         dest        destIndx        num
        // srcIndx should be add:(index-(0)) or remove:(index-(-1))
        // destIndx should be add:(index+1+(0)) or remove:(index+1+(-1))
        // num should be add:(size-index+(0)) or remove:(size-index+(-1))
        if (index == 0) { // prepended/"shifted"
            items = newArray; // re-refer to newArray
            return; // no need to copy pre-index elements
        } // if
        /*------------PRE-INDEX COPY------------*/
        // pre-index element copying (same for both remove/add)
        newArray = copy(items, 0, newArray, 0, preSize);
        items = newArray; // re-refer to newArray
    } // insert
    
} // ArrayStringList
