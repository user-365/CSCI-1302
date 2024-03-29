package cs1001.game;

import java.lang.ArrayIndexOutOfBoundsException;
import java.util.function.IntConsumer;
import java.util.Arrays;
import cs1302.gameutil.GamePhase;
import cs1302.gameutil.Token;
import cs1302.gameutil.TokenGrid;

/**
 * {@code ConnectFour} represents a two-player connection game involving a
 * two-dimensional grid of
 * {@linkplain cs1001.gameutil.Token tokens}. When a {@code ConnectFour} game
 * object is
 * constructed, several instance variables representing the game's state are
 * initialized and
 * subsequently accessible, either directly or indirectly, via "getter" methods.
 * Over time, the
 * values assigned to these instance variables should change so that they always
 * reflect the
 * latest information about the state of the game. Most of these changes are
 * described in the
 * project's <a href=
 * "https://github.com/cs1001uga/cs1001-c4-alpha#functional-requirements">
 * functional requirements</a>.
 *
 * <p>
 * Last substantial revision: 2022-09-26
 *
 * @author user-365
 */
public class ConnectFour {

    // ----------------------------------------------------------------------------------------------
    // INSTANCE VARIABLES: You should NOT modify the instance variable declarations
    // below.
    // You should also NOT add any additional instance variables. Static variables
    // should
    // also NOT be added.
    // ----------------------------------------------------------------------------------------------

    private int rows; // number of grid rows
    private int cols; // number of grid columns
    private Token[][] grid; // 2D array of tokens in the grid
    private Token[] player; // 1D array of player tokens (length 2)
    private int numDropped; // number of tokens dropped so far
    private int lastDropRow; // row index of the most recent drop
    private int lastDropCol; // column index of the most recent drop
    private GamePhase phase; // current game phase

    // ----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------------------------------

    /**
     * Constructs a {@link cs1001.game.ConnectFour} game with a grid that has
     * {@code rows}-many
     * rows and {@code cols}-many columns. All of the game's instance variables are
     * expected to
     * be initialized by this constructor as described in the project's
     * <a href=
     * "https://github.com/cs1001uga/cs1001-c4-alpha#functional-requirements">functional
     * requirements</a>.
     *
     * @param rows the number of grid rows
     * @param cols the number of grid columns
     * @throws IllegalArgumentException if the value supplied for {@code rows} or
     *                                  {@code cols} is
     *                                  not supported. The following values are
     *                                  supported: {@code 6 <= rows <= 9} and
     *                                  {@code 7 <= cols <= 9}.
     */
    public ConnectFour(int rows, int cols) {
        if (6 > rows || rows > 9
                || 7 > cols || cols > 9) { // De Morgan's Laws
            throw new IllegalArgumentException(
                    "Unsupported Values: please make sure that 6 ≤ rows ≤ 9 and 7 ≤ cols ≤ 9.");
        } // if
        this.rows = rows;
        this.cols = cols;
        this.grid = new Token[rows][cols];
        this.player = new Token[2];
        this.numDropped = 0;
        this.lastDropRow = -1;
        this.lastDropCol = -1;
        this.phase = GamePhase.NEW;
    } // ConnectFour

    // ----------------------------------------------------------------------------------------------
    // INSTANCE METHODS
    // ----------------------------------------------------------------------------------------------

    /**
     * Return the number of rows in the game's grid.
     *
     * @return the number of rows
     */
    public int getRows() {
        return this.rows;
    } // getRows

    /**
     * Return the number of columns in the game's grid.
     *
     * @return the number of columns
     */
    public int getCols() {
        return this.cols;
    } // getCols

    /**
     * Return whether {@code row} and {@code col} specify a location inside this
     * game's grid.
     *
     * @param row the position's row index
     * @param col the positions's column index
     * @return {@code true} if {@code row} and {@code col} specify a location inside
     *         this game's grid and {@code false} otherwise
     */
    public boolean isInBounds(int row, int col) {
        return 0 <= row && row < this.rows && 0 <= col && col < this.cols;
    } // isInBounds

    /**
     * Return the grid {@linkplain cs1001.gameutil.Token token} located at the
     * specified position
     * or {@code null} if no token has been dropped into that position.
     *
     * @param row the token's row index
     * @param col the token's column index
     * @return the grid token located in row {@code row} and column {@code col}, if
     *         it exists;
     *         otherwise, the value {@code null}
     * @throws IndexOutOfBoundsException if {@code row} and {@code col} specify a
     *                                   position that is
     *                                   not inside this game's grid.
     */
    public Token getTokenAt(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IndexOutOfBoundsException(
                    "Out of bounds: Please make sure that 1 ≤ row ≤ %d and 1 ≤ col ≤ %d."
                            .formatted(rows, cols)); // screw you, you gaslighting, outdated compiler!
        } // if
        return this.grid[row][col]; // != null ? this.grid[row][col] : null;???
    } // getTokenAt

    /**
     * Set the first player token and second player token to {@code token0} and
     * {@code token1},
     * respectively. If the current game phase is
     * {@link cs1001.gameutil.GamePhase#NEW}, then
     * this method changes the game phase to
     * {@link cs1001.gameutil.GamePhase#READY}, but only
     * if no exceptions are thrown.
     * .
     * 
     * @param token0 token for first player
     * @param token1 token for second player
     * @throws NullPointerException     if {@code token0} or {@code token1} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code token0 == token1}.
     * @throws IllegalStateException    if {@link #getPhase getPhase()} returns
     *                                  {@link cs1001.gameutil.GamePhase#PLAYABLE}
     *                                  or {@link cs1001.gameutil.GamePhase#OVER}.
     */
    public void setPlayerTokens(Token token0, Token token1) {
        if (token0 == null
                || token1 == null) {
            throw new NullPointerException(
                    "Null argument(s): Neither argument can be null.");
        } // if
        if (isPlayed()) {
            throw new IllegalStateException(
                    "Wrong phase: Tokens can only be assigned upon creation of a new game.");
        } // if
        if (token0 == token1) {
            throw new IllegalArgumentException(
                    "Bad arguments: Both players can't be assigned the same token.");
        } // if
        this.player[0] = token0;
        this.player[1] = token1;
        this.phase = GamePhase.READY;
    } // setPlayerTokens

    /**
     * Return a player's token.
     *
     * @param player the player ({@code 0} for first player and {@code 1} for second
     *               player)
     * @return the token for the specified player
     * @throws IllegalArgumentException if {@code player} is neither {@code 0} nor
     *                                  {@code 1}
     * @throws IllegalStateException    if {@link #getPhase getPhase()} returns
     *                                  {@link cs1001.gameutil.GamePhase#NEW}.
     */
    public Token getPlayerToken(int player) {
        if (player != 0 && player != 1) {
            throw new IllegalArgumentException(
                    "Bad Argument: Argument must either be 0 or 1.");
        } // if
        if (this.phase == GamePhase.NEW) {
            throw new IllegalStateException(
                    "Wrong phase: You can only get tokens after they have been assigned.");
        } // if
        return this.player[player];
    } // getPlayerToken

    /**
     * Return the number of tokens that have been dropped into this game's grid so
     * far.
     *
     * @return the number of dropped tokens
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *                               {@link cs1001.gameutil.GamePhase#NEW} or
     *                               {@link cs1001.gameutil.GamePhase#READY}.
     */
    public int getNumDropped() {
        if (!isPlayed()) {
            throw new IllegalStateException(
                    "Wrong phase: The game just started; no tokens are on the board yet.");
        } // if
        return this.numDropped;
    } // getNumDropped

    /**
     * Return the row index of the last (i.e., the most recent) token dropped into
     * this
     * game's grid.
     *
     * @return the row index of the last drop
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *                               {@link cs1001.gameutil.GamePhase#NEW} or
     *                               {@link cs1001.gameutil.GamePhase#READY}.
     */
    public int getLastDropRow() {
        if (!isPlayed()) {
            throw new IllegalStateException(
                    "Wrong phase: The game just started; no tokens are on the board yet.");
        } // if
        return this.lastDropRow;
    } // getLastDropRow

    /**
     * Return the col index of the last (i.e., the most recent) token dropped into
     * this
     * game's grid.
     *
     * @return the column index of the last drop
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *                               {@link cs1001.gameutil.GamePhase#NEW} or
     *                               {@link cs1001.gameutil.GamePhase#READY}.
     */
    public int getLastDropCol() {
        if (!isPlayed()) {
            throw new IllegalStateException(
                    "Wrong phase: The game just started; no tokens are on the board yet.");
        }
        return this.lastDropCol;
    } // getLastDropCol

    /**
     * Return the current game phase.
     *
     * @return current game phase
     */
    public GamePhase getPhase() {
        return this.phase;
    } // getPhase

    /**
     * Drop a player's token into a specific column in the grid. This method should
     * not enforce turn
     * order -- that is the players' responsibility should they desire an polite and
     * honest game.
     *
     * @param player the player ({@code 0} for first player and {@code 1} for second
     *               player)
     * @param insCol the grid column where the token is dropped/inserted
     * @throws IndexOutOfBoundsException if {@code col} is not a valid column index
     * @throws IllegalArgumentException  if {@code player} is neither {@code 0} nor
     *                                   {@code 1}
     * @throws IllegalStateException     if {@link #getPhase getPhase()} does not
     *                                   return
     *                                   {@link cs1001.gameutil.GamePhase#READY} or
     *                                   {@link cs1001.gameutil.GamePhase#PLAYABLE}
     * @throws IllegalStateException     if the specified column in the grid is full
     */
    public void dropToken(int player, int insCol) {
        if (!isInBounds(0, insCol)) { // check out-of-bounds
            throw new IndexOutOfBoundsException(
                    "Out of bounds: Please enter a valid column index.");
        } // if
        this.getPlayerToken(player); // check player = 0 or 1, intentionally unassigned
        if (this.phase == GamePhase.OVER) { // check phase (^NEW checked w/ getPlayerToken)
            throw new IllegalStateException(
                    "Wrong phase: Game isn't ready or isn't being played.");
        } // if
        this.phase = GamePhase.PLAYABLE; // start the game!
        int maxRow = this.rows - 1; // renaming vars
        // shortcut: if same column as last token...
        if (insCol == this.lastDropCol) {
            if (this.lastDropRow <= 0) { // check full column
                throw new IllegalStateException(
                        "Illegal Argument: Sorry, column full!");
            } // if
            grid[this.lastDropRow - 1][insCol] = this.getPlayerToken(player); // go up one row
        } // if
          // estimate where last token is, then find next empty (null) cell
        int startRow = maxRow - (int) Math.round(numDropped / this.cols); // estim. avg unfilled row

        H<IntConsumer> h = new H<>(); // int -> void
        h.f = j -> {
            for (int i = startRow; 0 <= i && i <= maxRow; i += j) {
                if (isInBounds(i, insCol) && grid[i][insCol] == null) {
                    grid[i][insCol] = this.getPlayerToken(player);
                    this.lastDropCol = insCol;
                    this.lastDropRow = i;
                    break; // leave asap
                } // if
            } // for
        }; // h.f
        if (grid[startRow][insCol] == null) { // if null (empty)...
            h.f.accept(1); // then go down
        } else { // if filled...
            h.f.accept(-1); // go up
        } // if-else
        this.numDropped++;
        if (numDropped > 3 // short-circuit if less than 4 tokens on the grid
                && (isLastDropConnectFour()
                        || numDropped >= this.rows * this.cols)) {
            this.phase = GamePhase.OVER;
        } // if
    } // dropToken

    /**
     * Return {@code true} if the last token dropped via {@link #dropToken} created
     * a
     * <em>connect four</em>. A <em>connect four</em> is a sequence of four equal
     * tokens (i.e., they
     * have the same color) -- this sequence can occur horizontally, vertically, or
     * diagonally.
     *
     * <p>
     * Checks for a <em>connect four</em> on all vertical, horizontal, and diagonal
     * directions. Via short-circuiting, it will only check in the directions where
     * there can actually be a four-in-a-row, as opposed to running into the edge of
     * the grid.
     *
     * <p>
     * <strong>NOTE:</strong> Called after each* call to {@link #dropToken}.
     *
     * @return {@code true} if the last token dropped created a <em>connect
     *         four</em>, else
     *         {@code false}
     */
    public boolean isLastDropConnectFour() {
        // deprecated: made 4 proximity-to-bounds booleans, which doesn't apply
        // when checking entire rows. also, previous method DID NOT CHECK FOR
        // EVERY POSSIBLE CONNECT FOUR AT THAT POINT, only connect-fours with an
        // endpoint at lastDrop location. *facepalm*

        /**
         * There IS a built-in IntBinaryOperator but no IntTernaryOperator.
         * So i made this one. Accepts ("computes") three integers, and outputs
         * another. Specifically, in the {@code H.delta} implementation,
         * the three arguments are the y/x direction to angle the frame, a for-loop
         * index variable, and the coefficient to check the cells some distance
         * away from the base cell.
         *
         * @functionalinterface yeah this is a functional interface
         */
        @FunctionalInterface
        interface IntTernaryOperator {
            int compute(int xOrY, int i, int coef);
        } // IntTernaryOperator

        /**
         * Again, there IS a built-in IntPredicate but no IntBiPredicate.
         * So i made this one. Accepts ("tests") two integers, and outputs
         * a boolean. Specifically, in the {@code H.coordsToIsMatch} implementation,
         * the two arguments are the y and the x directions, respectively.
         *
         * @functionalinterface yeah this is a functional interface
         */
        @FunctionalInterface
        interface IntBiPredicate {
            boolean testCoords(int y, int x);
        } // IntBiPredicate

        H<IntBiPredicate> coordsToIsMatch = new H<>();
        H<IntTernaryOperator> delta = new H<>();
        // "delta" means "change in (a variable)." Here, delta changes the row/col index
        delta.f = (xOrY, i, coef) -> Math.abs(xOrY) * ((xOrY > 0 ? i : -i) + (coef * xOrY));
        // param-arg name redundancy justified because it's a single-purpose function
        // (xOrY)>0?i:-i handles checking Northward/negative direction (i.e., -i)
        // ^ for x, i will never be turned negative (see ternary)
        // e.g., if Math.abs(y)=0, checks only horizontally
        int col = this.lastDropCol, row = this.lastDropRow;
        coordsToIsMatch.f = (y, x) -> { // (y, x): compass directions as ordered pairs
            // y and x are used for moving down a file (vert., hori., diag., anti-diag.)
            // 2D arrays go down a row first, so x and y are switched.
            // i.e., y = vert/row, x = hor/col
            boolean isMatch = false; // default: "no connect-fours"
            int maxI = 1; // default: tetromino-frame shifts (1-(-3)=)4 times
            for (int i = -3, adjustment; i < maxI; i++) {
                // avoiding ArrayIndexOutOfBounds for negative indices
                // i==-3&& to short-circuit (bc happens only initially)
                // since check only initially, use row instead of row+delta
                if (i == -3 && ((adjustment = row - 3) < 0
                        || (adjustment = col - 3) < 0)) {
                    // abs coef to ensure horiz & vert independent of each other
                    // (e.g. if y = 0, i doesn't change in this if block)
                    i += Math.max(Math.abs(y) * -adjustment,
                            Math.abs(x) * -adjustment); // adjustment<0 so we negate it
                } // if
                  // below 2 ifs ADJUST indices one iteration AHEAD
                  // avoid AIOOBE for big positive indices
                if ((adjustment = row + delta.f.compute(y, i, 4)) >= maxI) {
                    maxI -= Math.abs(y) * (adjustment - maxI + 3);
                } // if
                if ((adjustment = col + delta.f.compute(x, i, 4)) >= maxI) {
                    maxI -= Math.abs(x) * (adjustment - maxI + 3);
                } // if
                  // serendipitously, the above also implicitly adjusts for diagonals too,
                  // since diagonals are slope=±1, i & maxI are the same for both row and col.
                  // uses Math.max to get the needed adjustment for BOTH y and x.
                try { // i give up
                    isMatch |= equal(grid[row + delta.f.compute(y, i, 0)][col + delta.f.compute(x, i, 0)], // lastDrop
                            grid[row + delta.f.compute(y, i, 3)][col + delta.f.compute(x, i, 3)], // 3 away
                            grid[row + delta.f.compute(y, i, 1)][col + delta.f.compute(x, i, 1)], // 1 away
                            grid[row + delta.f.compute(y, i, 2)][col + delta.f.compute(x, i, 2)]);// 2 away
                    // the order is this way to short-circuit
                } catch (ArrayIndexOutOfBoundsException aioobe) {
                    ; // to escape detection by checkstyle
                }
                if (isMatch) { // don't run the entire loop if we don't have to!
                    return true;
                } // if
            } // for
            return isMatch;
        }; // coordsToIsMatch.f
           // Directions in terms of (y, x): towards West: (0,1), South: (1,0), NW: (1,1),
           // SW: (-1,1)
           // aw i just realized (y, x) still doesn't match cartesian (W should be
           // negative)
           // short-circuiting :)
        return coordsToIsMatch.f.testCoords(0, 1) // check ALONG row
                || coordsToIsMatch.f.testCoords(1, 0) // check ALONG col
                || coordsToIsMatch.f.testCoords(1, 1) // check diag (SE)
                || coordsToIsMatch.f.testCoords(-1, 1);// check anti-diag (NE)
        // deprecated: replaced by the for loop, used to be a ||, && tree,
        // which used to be an ugly array w/ a frankenstein for-loop
    } // isLastDropConnectFour

    // ----------------------------------------------------------------------------------------------
    // ADDITIONAL METHODS: If you create any additional methods, then they should be
    // placed in the
    // space provided below.
    // ----------------------------------------------------------------------------------------------

    /**
     * H, short for "Helper", is a single-purpose Class to house a method {@code f}
     * which
     * will save me a few lines of code, wherever it is used. Did this as proof of
     * concept.
     * Justification: i need specific helper methods to condense functionality in
     * bigger
     * methods. Defining those helper methods locally increases readability, but
     * Java doesn't
     * allow me to define a method within a method. So i did a parameterized
     * functional interface.
     *
     * @param <I> - Functional Interface Type
     */
    class H<I> {
        I f;
    } // H

    /**
     * PLAYABLE or OVER: Returns a boolean based on the current phase of the game.
     *
     * @return {@code true} if {@link #getPhase getPhase()} returns
     *         {@link cs1001.gameutil.GamePhase#PLAYABLE} or
     *         {@link cs1001.gameutil.GamePhase#OVER} and {@code false} otherwise
     */
    boolean isPlayed() {
        return getPhase() == GamePhase.PLAYABLE || getPhase() == GamePhase.OVER;
    } // isPlayed

    /**
     * Token Equality: Checks whether all the elements of an array of {@code Token}
     * enums are
     * equal, through recursion.
     * Compares the first {@code Token} with the second, then second with
     * the third, and so on.
     * Inspired by <a href="https://stackoverflow.com/a/8198279">this answer
     * on Stack Exchange</a>.
     * 
     * @param z a varargs of type {@code Token}
     * @return {@code true} if they are ALL equal, {@code false} otherwise
     */
    static boolean equal(Token... z) {
        return z[0] == z[1]
                && (z.length < 3 ? true : equal(Arrays.copyOfRange(z, 1, z.length)));
    } // q

    // ----------------------------------------------------------------------------------------------
    // DO NOT MODIFY THE METHODS BELOW!
    // ----------------------------------------------------------------------------------------------

    /**
     * <strong>DO NOT MODIFY:</strong>
     * Print the game grid to standard output. This method assumes that the
     * constructor
     * is implemented correctly.
     *
     * <p>
     * <strong>NOTE:</strong> This method should not be modified!
     */
    public void printGrid() {
        TokenGrid.println(this.grid);
    } // printGrid

    /**
     * <strong>DO NOT MODIFY:</strong>
     * Construct a {@link cs1001.game.ConnectFour} game object from the description
     * provided in the
     * the specified file. This method assumes the following about the contents of
     * the file:
     *
     * 1) the first two entries in the file denote the {@code rows} and {@code cols}
     * that should be
     * passed into the {@link cs1001.game.ConnectFour} constructor;
     * 2) the next two entries denote the token names for the first and second
     * player;
     * 3) if present, the next {@code (rows * cols + 2)}-many entries specify the
     * contents of the
     * grid and the location of the last drop -- of these, the first
     * {@code (rows * cols)}-many
     * entries denote the grid and the last two entries denote the row and column
     * index of the
     * last drop.
     *
     * The descriptions are assumed to be always be valid. If the game is won, then
     * it must due to
     * the last drop.
     *
     * <p>
     * Here is an example of what the contents of a valid file might look like for a
     * game with a
     * 6-by-7 grid:
     *
     * <pre>
     * 6 7 RED BLUE
     * 3 3 3 3 3 3 3
     * 3 3 3 3 3 3 3
     * 3 3 0 3 3 3 3
     * 3 3 0 3 3 3 3
     * 1 3 0 3 3 3 3
     * 0 1 0 1 1 3 3
     * 2 2
     * </pre>
     *
     * <p>
     * <strong>NOTE:</strong> This method should not be modified!
     *
     * @param filename path to a file describing a game
     * @return game object constructed from the file
     * @throws java.io.FileNotFoundException if the specified file cannot be found
     */
    public static ConnectFour fromFile(String filename) throws java.io.FileNotFoundException {
        java.io.File file = new java.io.File(filename);
        java.util.Scanner fileScanner = new java.util.Scanner(file);
        // first two entries in file specify the grid size
        int rows = fileScanner.nextInt();
        int cols = fileScanner.nextInt();
        // next two entries are the player's token names
        Token token0 = Token.valueOf(fileScanner.next());
        Token token1 = Token.valueOf(fileScanner.next());
        // construct the game object and set the player tokens
        ConnectFour game = new ConnectFour(rows, cols);
        game.setPlayerTokens(token0, token1);
        if (fileScanner.hasNext()) {
            // next (rows * cols)-many entries denote the grid
            for (int row = rows - 1; row >= 0; row--) {
                for (int col = 0; col < cols; col++) {
                    int player = fileScanner.nextInt();
                    if (player != 3) {
                        game.dropToken(player, col);
                    } // if
                } // for
            } // for
              // last two entries denote the position of the latest drop
            game.lastDropRow = fileScanner.nextInt();
            game.lastDropCol = fileScanner.nextInt();
            // trigger phase change if game is won or full
            game.isLastDropConnectFour();
        } // if
        return game;
    } // fromFile

} // ConnectFour