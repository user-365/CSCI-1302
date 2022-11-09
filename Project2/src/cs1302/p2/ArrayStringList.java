package cs1302.p2;

import cs1302.adt.*;

/**
 * ArrayStringList implements a {@code StringList} using an
 * underlying {@code String} array.
 * 
 * The underlying array has at least one extra space at the end as buffer,
 * due to its constructors.
 *
 * <p>
 * Last substantial revision: 2022-11-08
 *
 * @author user-365
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
     * TK remove...or not?
     * Overloaded constructor. Guaranteed to result in a gapless
     * instance {@code items}. Necessary because {@code items} is private,
     * so there wasn't a way to set {@code items} from {@code public} areas.
     * 
     * @param array the array to initialize with
     */
    private ArrayStringList(String[] array) {
        int count = 0;
        for (String string : array) {
            if (string == null) {
                break; // break on first-encountered null element
            } // if
            count++; // count every non-null element
        } // for
        this.items =
        copy(array, 0, new String[count + 3], 0, count, 1);
        // ^we want the array up to the first null element.
        // ^adds one space as buffer
        this.size = count; // update size
    } // Constructor(String[])

    /**
     * Copy constructor.
     * <p>  
     * Assume {@code StringList} is gapless.
     * @param other an existing string list object that serves as the source
     *              object for the copy
     */
    public ArrayStringList(StringList other) {
        if (other == null) {
            throw new NullPointerException("Copied StringList must not be null");
        } // ifnull
        this.items = new String[other.size() + 3];
        // ^adds three spaces as buffer
        for (int i = 0; i < other.size(); i++) {
            this.items[i] = other.get(i);
        } // for
        this.size = other.size(); // update size
    } // ArrayStringList

    /**
     * Adds a new {@code String} at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * Short-circuits appending OR copies like normal (leaving a space),
     * then inserting provided {@code item} at {@code index}.
     * 
     * <p>
     * TK removed try catch
     * {@inheritDoc}
     */
    @Override
    public boolean add(int index, String item) {
        intercept(index, false); // may throw IOOBE
        intercept(item); // may throw NPE, IAE
        // Q: why do i need the try block?
        // A: TK i don't think i need it
        // Q: why did i extract this vvv from the try ^^^?
        // A: so that it's guaranteed to return something
        if (this.size > 0) {
            if (index == this.size) { // append
                // copies contents over to bigger array
                String[] bigger = new String[this.items.length + 3];
                this.items = copy(this.items, 0, bigger, 0, size, 1);
                // fall through to `items[index] = item;`
            } else { // prepend or insert
                // size cannot be > items.length due to intercept
                // filled or underfilled handled in copySurrounding()
                copySurrounding(index, 0);
                // fall through to `items[index] = item;`
            } // if-else
        } // if
        // size==0 OR fell through
        this.items[index] = item; // fill in w/ item at index
        this.size++; // update size
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
        this.items = new String[this.items.length]; // keep previous length
        this.size = 0; // update size
    } // clear

    /**
     * Accesses the item at the specified index
     * in the {@code ArrayStringList}'s array.
     * 
     * ditto
     * 
     * <p>
     * TK removed try-catch. if bad, put it back!
     * {@inheritDoc}
     */
    @Override
    public String get(int index) {
        intercept(index, true); // may throw
        return this.items[index] != null ? this.items[index] : null;
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
     * TK moved EVERYTHING OUT of try. if bad, put everything back IN
     * {@inheritDoc}
     */
    @Override
    public String remove(int index) {
        intercept(index, true); // may throw
        String delenda = get(index); // to be returned
        if (index == this.size - 1) { // "pop": short-circuit to avoid copying
            this.items[index] = null;
            // fall through to size update
        } else { // index < items.length (i.e., "shift" or extract)
            copySurrounding(index, -1);
            // fall through to size update
        } // if-else
        this.size--; // update size
        return delenda;
    } // remove

    /**
     * Returns a "subset" of the {@code ArrayStringList}
     * between the specified indices.
     * 
     * <p>
     * TK changed first start check to true bc it's default
     * {@inheritDoc}
     */
    @Override
    public StringList slice(int start, int stop) {
        intercept(start, true); // may throw
        // ^guarantees start >= 0
        intercept(stop, false); // may throw
        // ^guarantees stop <= size
        if (stop < start) { // guarantees start <= stop
            throw new IndexOutOfBoundsException("Start index of slice " +
                    "cannot be greater than stop index.");
        } else if (stop == start) { // empty ArrayStringList
            return new ArrayStringList();
        } else { // stop > start
            intercept(start, true); // may throw
            // TK ^why the redundancy?
            int d = stop - start;
            return new ArrayStringList(
                    copy(this.items, start, new String[d], 0, d, 1));
            // constructor will add one space as buffer
        } // if-elif-else
    } // slice

    /**
     * Returns a new {@code FancyStringList} that contains the items from this list
     * between the specified start index (inclusive) and stop index (exclusive) by
     * step.
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
        // argument check
        if (isEmpty()) {
            return null;
        } // if
        intercept(start, true); // may throw
        intercept(stop, false); // may throw
        // ^guarantees stop <= size
        // boundary check
        if (stop < start) { // guarantees stop >= start
            throw new IndexOutOfBoundsException("Start index of slice " +
                    "cannot be greater than stop index.");
        } else if (stop == start) { // empty ArrayStringList
            return new ArrayStringList();
        } else { // stop > start
            // step size check
            if (step < 1) {
                throw new IndexOutOfBoundsException("Step size must be 1 or greater.");
            } else if (step == 1) { // contiguous slice
                return (ArrayStringList) slice(start, stop);
            } else { // normal/non-contiguous slice
                // intercept(start, true); // TK seems redundant
                int d = stop - start;
                return new ArrayStringList(
                        copy(this.items, start, new String[d], 0, d, step));
                // constructor will add one space as buffer
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
        // no size==0 check bc handled in for-loop
        String[] reverse = new String[this.items.length];
        for (int i = 0; i < this.size; i++) {
            reverse[this.size - -~i] = this.items[i];
            // TK check         ^this
        } // for
        return new ArrayStringList(reverse);
    } // reverse

    /**
     * Helper method. Similar to
     * {@link java.lang.System#arraycopy(Object, int, Object, int, int)} for
     * {@code String[]}s.
     * 
     * <p>
     * Differences: Not dependent on side-effects: returns a {@code String[]};
     * has a step size parameter (for
     * {@link ArrayStringList#slice(int, int, int)}).
     * 
     * <p>
     * Side effect: will modify {@code dest} array. Even still, don't treat it
     * like {@code void}, since it will NOT modify SIZE of {@code dest} array.
     * i can't get rid of this side effect since, to avoid modifying
     * {@code dest}, a COPY of {@code dest} is required in the first place,
     * which would lead to an infinite recusion.
     * 
     * <p>
     * {@code step} is included as an argument here because
     * it's easy to implement.
     * 
     * @param src      the source array
     * @param srcIdx  the source index (inclusive) from which is copied
     * @param dest     the destination array
     * @param destIdx the destination index (inclusive) at which is pasted
     * @param d      dber of elements, counting from srcIdx, to copy
     * @param step     step size; default is 1
     * @return a modified {@code dest} array, with a "subset" of the
     *         {@code src} array copied over it
     */
    private String[] copy(String[] src,
                            int srcIdx,
                            String[] dest,
                            int destIdx,
                            int d,
                            int step) {
        // srcIdx within bounds of src, which can accommodate d
        // destIdx within bounds of dest, which can accommodate d
        boolean legal = 0 <= srcIdx && 0 <= destIdx
                        && srcIdx + d <= src.length
                        && destIdx + d <= dest.length;
        try { // this is actually needed
            if (!legal) {
                System.out.println("ArrayStringList.copyOf()");
                throw new IllegalArgumentException("fix ur args lol");
            } // if
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } // try-catch
        // all arguments legal!
        for (int i = 0; i < d; i += step) {
            dest[destIdx + i] = src[srcIdx + i];
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
        try { // actually needed!
            if (!legal) {
                System.out.println("ArrayStringList.copySurrounding()");
                throw new IllegalArgumentException("fix ur args lol");
            } // if
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } // try-catch
        /*------------INITIALIZE------------*/ 
        // preSize+postSize = total # of elements to be copied
        int preSize = index; // # of elements before the index
        int postSize = this.size - index + setting;
        // ^"add": # of elements at/after the index before change
        // ^"remove": # of elements after the index before change
        String[] newArray; // will replace current items array
        if (setting == 0 && this.items.length <= this.size + 1) {
            // if array imminently filled by adding, enlarge array
            newArray = new String[(int) Math.ceil(this.items.length * 1.5)];
            // ^ArrayList supposedly does +50%, so i'll do that too
        } else { // "remove" setting OR array NOT imminently filled
            newArray = new String[this.items.length]; // keep array length
        } // if-else
        /*------------POST-INDEX COPY------------*/
        // post-index element copying
        newArray =
            copy(this.items, index - setting, newArray, index-~setting, postSize, 1);
        //          src     srcIdx         dest        destIdx        d
        // srcIdx should be add:(index-(0)) or remove:(index-(-1))
        // destIdx should be add:(index+1+(0)) or remove:(index+1+(-1))
        // d should be add:(size-index+(0)) or remove:(size-index+(-1))
        if (index == 0) { // prepended/"shifted"
            this.items = newArray; // re-refer to newArray
            return; // no need to copy pre-index elements
        } // if
        /*------------PRE-INDEX COPY------------*/
        // pre-index element copying (same for both remove/add)
        newArray = copy(this.items, 0, newArray, 0, preSize, 1);
        this.items = newArray; // re-refer to newArray
    } // insert
    
} // ArrayStringList
