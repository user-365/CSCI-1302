package cs1302.game;

import java.util.*; // List, Arrays, function.IntConsumer
import cs1302.gameutil.GamePhase;
import cs1302.gameutil.Token;
import cs1302.gameutil.TokenGrid;

/**
 * {@code ConnectFour} represents a two-player connection game involving a two-dimensional grid of
 * {@linkplain cs1302.gameutil.Token tokens}. When a {@code ConnectFour} game object is
 * constructed, several instance variables representing the game's state are initialized and
 * subsequently accessible, either directly or indirectly, via "getter" methods. Over time, the
 * values assigned to these instance variables should change so that they always reflect the
 * latest information about the state of the game. Most of these changes are described in the
 * project's <a href="https://github.com/cs1302uga/cs1302-c4-alpha#functional-requirements">
 * functional requirements</a>.
 */
public class ConnectFour {

    //----------------------------------------------------------------------------------------------
    // INSTANCE VARIABLES: You should NOT modify the instance variable declarations below.
    // You should also NOT add any additional instance variables. Static variables should
    // also NOT be added.
    //----------------------------------------------------------------------------------------------

    private int rows;        // number of grid rows
    private int cols;        // number of grid columns
    private Token[][] grid;  // 2D array of tokens in the grid
    private Token[] player;  // 1D array of player tokens (length 2)
    private int numDropped;  // number of tokens dropped so far
    private int lastDropRow; // row index of the most recent drop
    private int lastDropCol; // column index of the most recent drop
    private GamePhase phase; // current game phase

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Constructs a {@link cs1302.game.ConnectFour} game with a grid that has {@code rows}-many
     * rows and {@code cols}-many columns. All of the game's instance variables are expected to
     * be initialized by this constructor as described in the project's
     * <a href="https://github.com/cs1302uga/cs1302-c4-alpha#functional-requirements">functional
     * requirements</a>.
     *
     * @param rows the number of grid rows
     * @param cols the number of grid columns
     * @throws IllegalArgumentException if the value supplied for {@code rows} or {@code cols} is
     *     not supported. The following values are supported: {@code 6 <= rows <= 9} and
     *     {@code 7 <= cols <= 9}.
     */
    public ConnectFour(int rows, int cols)  {
        if (6 > rows || rows > 9
            || 7 > cols || cols > 9) {  // De Morgan's Laws
            throw new IllegalArgumentException(
                "Unsupported Values: please make sure that 6 ≤ rows ≤ 9 and 7 ≤ cols ≤ 9."
            );
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new Token[rows][cols];
        this.player = new Token[2];
        this.numDropped = 0;
        this.lastDropRow = -1;
        this.lastDropCol = -1;
        this.phase = GamePhase.NEW;
        //throw new UnsupportedOperationException("constructor: not yet implemented.");
    } // ConnectFour

    //----------------------------------------------------------------------------------------------
    // INSTANCE METHODS
    //----------------------------------------------------------------------------------------------
    
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
     * Return the grid {@linkplain cs1302.gameutil.Token token} located at the
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
                .formatted(rows, cols));
        } 
        return this.grid[row][col] != null ? this.grid[row][col] : null;
    } // getTokenAt

    /**
     * Set the first player token and second player token to {@code token0} and {@code token1},
     * respectively. If the current game phase is {@link cs1302.gameutil.GamePhase#NEW}, then
     * this method changes the game phase to {@link cs1302.gameutil.GamePhase#READY}, but only
     * if no exceptions are thrown.
     *.
     * @param token0 token for first player
     * @param token1 token for second player
     * @throws NullPointerException if {@code token0} or {@code token1} is {@code null}.
     * @throws IllegalArgumentException if {@code token0 == token1}.
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#PLAYABLE} or {@link cs1302.gameutil.GamePhase#OVER}.
     */
    public void setPlayerTokens(Token token0, Token token1) {
        if (token0 == null
            || token1 == null) {
            throw new NullPointerException(
                "Null argument(s): Neither argument can be null.");
        }
        if (isPlayed(this)) {
            throw new IllegalStateException(
                "Wrong phase: Tokens can only be assigned upon creation of a new game.");
        }
        if (token0 == token1) {
            throw new IllegalArgumentException(
                "Bad arguments: Both players can't be assigned the same token.");
        }
        this.player[0] = token0;
        this.player[1] = token1;
        this.phase = GamePhase.READY;
    } // setPlayerTokens

    /**
     * Return a player's token.
     *
     * @param player the player ({@code 0} for first player and {@code 1} for second player)
     * @return the token for the specified player
     * @throws IllegalArgumentException if {@code player} is neither {@code 0} nor {@code 1}
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW}.
     */
    public Token getPlayerToken(int player) {
        if (player != 0 && player != 1) {
            throw new IllegalArgumentException(
                "Bad Argument: Argument must either be 0 or 1.");
        }
        if (this.phase == GamePhase.NEW) {
            throw new IllegalStateException(
                "Wrong phase: You can only get tokens after they have been assigned.");
        }
        return this.player[player];
    } // getPlayerToken

    /**
     * Return the number of tokens that have been dropped into this game's grid so far.
     *
     * @return the number of dropped tokens
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW} or {@link cs1302.gameutil.GamePhase#READY}.
     */
    public int getNumDropped() {
        if (!isPlayed(this)) {
            throw new IllegalStateException(
                "Wrong phase: The game just started; no tokens are on the board yet.");
        }
        return this.numDropped;
    } // getNumDropped

    /**
     * Return the row index of the last (i.e., the most recent) token dropped into this
     * game's grid.
     *
     * @return the row index of the last drop
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW} or {@link cs1302.gameutil.GamePhase#READY}.
     */
    public int getLastDropRow() {
        if (!isPlayed(this)) {
            throw new IllegalStateException(
                "Wrong phase: The game just started; no tokens are on the board yet.");
        }
        return this.lastDropRow;
    } // getLastDropRow

    /**
     * Return the col index of the last (i.e., the most recent) token dropped into this
     * game's grid.
     *
     * @return the column index of the last drop
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW} or {@link cs1302.gameutil.GamePhase#READY}.
     */
    public int getLastDropCol() {
        if (!isPlayed(this)) {
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
     * Drop a player's token into a specific column in the grid. This method should not enforce turn
     * order -- that is the players' responsibility should they desire an polite and honest game.
     *
     * @param player the player ({@code 0} for first player and {@code 1} for second player)
     * @param col the grid column where the token will be dropped
     * @throws IndexOutOfBoundsException if {@code col} is not a valid column index
     * @throws IllegalArgumentException if {@code player} is neither {@code 0} nor {@code 1}
     * @throws IllegalStateException if {@link #getPhase getPhase()} does not return
     *    {@link cs1302.gameutil.GamePhase#READY} or {@link cs1302.gameutil.GamePhase#PLAYABLE}
     * @throws IllegalStateException if the specified column in the grid is full
     */
    public void dropToken(int player, int col) {
        if (!isInBounds(0, col)) { // check out-of-bounds
            throw new IndexOutOfBoundsException(
                "Out of bounds: Please enter a valid column index.");
        }
        this.getPlayerToken(player); // check player = 0 or 1
        if (this.phase == GamePhase.OVER) { // check phase (^NEW checked w/ getPlayerToken)
            throw new IllegalStateException(
                "Wrong phase: Game isn't ready or isn't being played.");
        }
        this.phase = GamePhase.PLAYABLE; // start the game!
        int c = col, r = this.rows - 1; // 0-indexing
        // shortcut: if same column as last token
        if (c == this.lastDropCol) {
            // check full column
            if (this.lastDropRow <= 0) {
                throw new IllegalStateException(
                    "Illegal Argument: Sorry, column full!");
            }
            grid[this.lastDropRow - 1][c] = this.getPlayerToken(player);
        } 
        // estimate where last token is, then find next empty (null) cell
        int startRow = r - (int) Math.round(numDropped / this.cols); // estim. avg unfilled row
        /**
         * H is short for "Helper". Single-purpose class to house a method which
         * will save a few lines of code. Did this as proof of concept.
         * 
         * @param <I> - Functional Interface Type
         */
        class H <I> {
            I f;
        } // H
        
        H<IntConsumer> h = new H<>();
        h.f = j -> {
            for (int i = startRow; 0 <= i && i <= r; i += j) {
                if (isInBounds(i,c) && grid[i][c] == null) {
                    grid[i][c] = ConnectFour.this.getPlayerToken(player);
                    ConnectFour.this.lastDropCol = c;
                    ConnectFour.this.lastDropRow = i;
                    break;
                } // if
            } // for
        }; // a.f
        if (grid[startRow][c] == null) { // if null, then go down
            h.f.accept(1);
        } else { // if filled, go up
            h.f.accept(-1);
        } // if-else
        this.numDropped++; // one more token dropped!
        if (numDropped > 3 // check win (after fourth move)
            && isLastDropConnectFour()
            || numDropped > this.rows * this.cols) {
            this.phase = GamePhase.OVER;
        }
    } // dropToken

    /**
     * Return {@code true} if the last token dropped via {@link #dropToken} created a
     * <em>connect four</em>. A <em>connect four</em> is a sequence of four equal tokens (i.e., they
     * have the same color) -- this sequence can occur horizontally, vertically, or diagonally.
     * If the grid is full or the last drop created a <em>connect four</em>, then this method
     * changes the game's phase to {@link cs1302.gameutil.GamePhase#OVER}.
     *
     * <p>
     * <strong>NOTE:</strong> The only instance variable that this method might change, if
     * applicable, is ``phase``.
     *
     * <p>
     * <strong>NOTE:</strong> If you want to use this method to determine a winner, then you must
     * call it after each call to {@link #dropToken}.
     *
     * @return {@code true} if the last token dropped created a <em>connect four</em>, else
     *     {@code false}
     */
    public boolean isLastDropConnectFour() {
        int c = this.lastDropCol, r = this.lastDropRow;
        Token lastToken = getTokenAt(r, c); // last token played
        boolean e, w, s, n;

        // Proximity to bounds
        // East
        e = this.cols - c > 3;
        // West
        w = c >= 3;
        // South
        s = this.rows - r > 3;
        // North
        n = r >= 3;
        
        // used to be a nested-if tree,
        // pardon the heavy-duty short-circuiting.
        return check(lastToken, {r, c}, w, n, e, s);
            
        /* deprecated; replaced by check()
                false // default: not a connect-four
                // -east
                || e && (q(lastToken, grid[r][c + 3], grid[r][c + 1], grid[r][c + 2])
                    // southeast
                    || s && q(lastToken, grid[r + 3][c + 3], grid[r + 1][c + 1], grid[r + 2][c + 2])
                    // northeast
                    || n && q(lastToken, grid[r - 3][c + 3], grid[r - 1][c + 1], grid[r - 2][c + 2])
                        )
                // -west
                || w && (q(lastToken, grid[r][c - 3], grid[r][c - 1], grid[r][c - 2])
                    // southwest
                    || s && q(lastToken, grid[r + 3][c - 3], grid[r + 1][c - 1], grid[r + 2][c - 2])
                    // northwest
                    || n && q(lastToken, grid[r - 3][c - 3], grid[r - 1][c - 1], grid[r - 2][c - 2])
                        )
                // south only
                || s && q(lastToken, grid[r + 3][c], grid[r + 1][c], grid[r + 2][c]);
        */
    } // isLastDropConnectFour

    //----------------------------------------------------------------------------------------------
    // ADDITIONAL METHODS: If you create any additional methods, then they should be placed in the
    // space provided below.
    //----------------------------------------------------------------------------------------------

    /**
     * PLAYABLE or OVER: Returns a boolean based on the current phase of the game.
     * 
     * @param c an object of type {@code ConnectFour} representing a game.
     * @return {@code true} if {@link #getPhase getPhase()} returns
     *         {@link cs1302.gameutil.GamePhase#PLAYABLE} or
     *         {@link cs1302.gameutil.GamePhase#OVER} and {@code false} otherwise
     */
    static boolean isPlayed(ConnectFour c) {
        return Arrays.asList( GamePhase.PLAYABLE, GamePhase.OVER ).contains( c.getPhase() );
    } // isPlayed

    /**
     * Token Equality: Checks whether all the elements of an array of {@code Token} enums are
     * equal, recursively.
     * Inspired by <a href="https://stackoverflow.com/a/8198279">this answer
     * on Stack Exchange</a>.
     * 
     * @param z   a varargs of type {@code Token}
     * @return {@code true} if they are ALL equal, {@code false} otherwise
     */
    static boolean q (Token...z) {
        return z[0] == z[1]
                && (z.length < 3 ? true : q(Arrays.copyOfRange(z, 1, z.length)));
    }

    /**
     * Checks for a <em>connect four</em> in the calling {@ConnectFour} game 
     * on all vertical, horizontal, and diagonal directions. It will only check, by way
     * of {@code &&} short-circuiting, in the directions based on the settings (given in
     * its third parameter {@code b}), which contain four {@code boolean} values stating
     * whether the coordinated point is far enough from the bounds of the game board, so
     * that there's a <em>possibility</em> of there being a <em>connect four</em> in
     * said direction at the point (indexed in its second parameter {@code n}).
     *
     * <p>
     * It is a helper method which will ONLY be called in the
     * {@link cs1302.game.ConnectFour.isLastDropConnectFour} method, which explains the
     * specificity of the parameters.
     * 
     * @param t a {@code Token} object, representing the last-played token
     * @param n an {@code int} array, representing a duple of a row and a column index
     * @param b a {@boolean} array of four values (representing the four cardinal directions)
     * in a certain order (W->N->E->S), each representing whether it is possible to have a
     * <em>connect four</em> in that direction from the point determined by {@code n}
     * @return {@code true} if there is at least one <em>connect four</em>, and
     *         {@code false} otherwise
     * @throws java.lang.IllegalArgumentException if the argument is not in [0,7]
     */
    static boolean check (Token t, int[] n, boolean...b) {
        if (b.length != 4 || n.length != 2) {
            throw new IllegalArgumentException(
                "Illegal Argument: Please check array arguments, then try again."
            );
        }
        List<int>a = Arrays.asList(b[0], b[1], b[2], true, true, b[3]);  
        boolean o = false;
        for (double i = 1; i > -2; i -= .5) {
            k = (int) Math.ceil(i);
            for (int j = 1; j > -3; j -= 2) {
                o |= a.get(j + 1)
                     // East or West proximity check
                     && a.get(i + 4)
                     // North or South proximity check
                     && q(lastToken,
                          grid[n[0] + k * 3][n[1] + j * 3],
                          grid[n[0] + k][n[1] + j],
                          grid[n[0] + k * 2][n[1] + j * 2]);
                if (o) {
                    return o;
                }
            } // for
        } // for
        // need modulo 4?
        o |= b[3] && q(lastToken,
                        grid[n[0] + 3][n[1]],
                        grid[n[0] + 1][n[1]],
                        grid[n[0] + 2][n[1]]);
        return o;
        /*
        Check in this order:
        SE: (1,1)
        SW: (1,-1)
        E: (0,1)
        W: (0,-1)
        NE: (-1,1)
        NW: (-1,-1)
        
        Arraylist:
        [W,N,E,true,true,S]
        
        Lastly, check:
        S: (1,0)
        
        Format:
        o |= b[] && q(lastToken,
                    grid[n[0] + k * 3][n[1] + l * 3],
                    grid[n[0] + k][n[1] + l],
                    grid[n[0] + k * 2][n[1] + l * 2])
        */
    } // check

    //----------------------------------------------------------------------------------------------
    // DO NOT MODIFY THE METHODS BELOW!
    //----------------------------------------------------------------------------------------------

    /**
     * <strong>DO NOT MODIFY:</strong>
     * Print the game grid to standard output. This method assumes that the constructor
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
     * Construct a {@link cs1302.game.ConnectFour} game object from the description provided in the
     * the specified file. This method assumes the following about the contents of the file:
     *
     * 1) the first two entries in the file denote the {@code rows} and {@code cols} that should be
     *    passed into the {@link cs1302.game.ConnectFour} constructor;
     * 2) the next two entries denote the token names for the first and second player;
     * 3) if present, the next {@code (rows * cols + 2)}-many entries specify the contents of the
     *    grid and the location of the last drop -- of these, the first {@code (rows * cols)}-many
     *    entries denote the grid and the last two entries denote the row and column index of the
     *    last drop.
     *
     * The descriptions are assumed to be always be valid. If the game is won, then it must due to
     * the last drop.
     *
     * <p> Here is an example of what the contents of a valid file might look like for a game with a
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
