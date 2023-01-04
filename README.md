# CSCI-1302
Course Name: Software Development

## Project 1
Largest personal contribution located in ./src/cs1001/game/<code>ConnectFour</code>.java. When initially cloned, said file consisted of skeleton code with methods waiting to be implemented. The specific implementations were guided by the pre-written javadocs for each method.
The `ConnectFour` class represents a game of [Connect Four](https://en.wikipedia.org/w/index.php?title=Connect_Four&oldid=1106025914), excluding the user interaction (inputting moves) and graphical interface (displaying the game board). Handles the logic of game state and win-checking, and implements outward-facing methods for the other classes in the package to connect to.

### Constructor (line 38), Getter/Setters (line 75)
Pretty simple. Notable functionality includes checking conditions to throw exceptions.

### `dropToken()` (line 238)
Akin to dropping a token into a slot in real-life Connect Four. The main task of this method is to simulate the "gravity" on the token, and update the token's resting coordinates.

First (line 251), it checks for some gamestate conditions, throwing exceptions as necessary. Then (line 263), there's a shortcut if the current token is dropped into the **same column** as the last, setting its location relative to the last. Lastly (line 271), the method implements the gravity for a dropped token in general.

The underlying idea is to first **estimate** the row (where the token will end up), then find the **first empty cell** from the "bottom" of the array. If, at the estimated location, the cell is blank, it means the empty space could be below, so a `for` loop is used to traverse "down" the array. On the other hand, if the cell is occupied by a token, it means the empty space we're looking for is above this location, and likewise the `for` loop traverses "up" the array.

Since traversing up and down an array (with a `for` loop) is only a matter of changing the increment on the index to **either 1 or -1**, the loop is encapsulated into a lambda function (line 274) which is assigned to an "abstract" method `f`.

At the bottom (line 290), there is an `if` to check for a game-over, short-circuited[^shortCircuiting] by the number of tokens dropped, since it is **impossible** to have a four-in-a-row (much less having a full board) with **less than four** tokens on the board.

### `isLastDropConnectFour()` (line 308)
Checks for a four-in-a-row for (almost) every token dropped. Checks in a 8x8 (or less) cell region of the array around the last token dropped along each direction (horizontal, vertical, diagonals). For a given row (or column, etc.) in said region, a `for` loop (in `coordsToIsMatch.f`, line 343) checks a single **4-length "frame"** (through `equal()`), **sliding** this frame from edge to edge by a cell each time.

Since the `for` loop is supposed to check in every direction (where traversing the 2D array `grid` is split into two **independent directions**/dimensions), the index does not apply equally to each coordinate (e.g. the loop should only change the second coordinate for going horizontally, and only the first for vertically).

Thus, to **encode direction** and to maintain coordinate-independence, an **ordered pair** is used, as in (row, col). A -1 in the "row"/vertical direction represents "going up/north" on the array ("row" = +1 means "going down/south").[^negative] Particularly, a diagonal direction (e.g. NW to SE) can be combined into one ordered pair (1, 1), and a purely horizontal/vertical direction like going south has the other coordinate as 0--"going south" is (1, 0). Using the multiplicative property of 0, the change along that direction is nullified, while the traversal of the _other_ direction is unaffected.[^side-benefit] This idea is captured in the `delta.f` method from line 337.[^ternary][^cellOrder]

Since the drop locations can exist anywhere on the board, there sometimes isn't a complete 8x8 region around the drop location one can check, specifically at the **array edges**. In the `if` statements (line 353) at the top of the loop, the parameters (index and maximum index) of the `for` loop are **adjusted**, in case of this possibility. However, at the time of turn-in for the Project, the latter two `if`s (line 362) apparently did not function as intended, so their functionality is replaced by the `try-catch` block below (line 371).

The `try-catch` wasn't my idea, but i do know its logic. Once the array access in the `equal()` on line 372 oversteps the max index, the `ArrayIndex...` exception is thrown. Since, simultaneously, it's checking what is basically only 3 elements (which **cannot** be a four-in-a-row), we _allow_ the exception to be thrown, and the loop continues to the next iteration.

Lastly, this method frequently takes advantage of Java's **OR**'s (`||` or `|=`) short-circuiting nature (e.g., line 393). This is because we only care about **any** veritable four-in-a-row, and there is _no need to check for more beyond that_.

#### Additional Methods (line 321)
Here, i personally wrote a couple of custom methods to encapsulate functionality for the previous methods.

### `H` Class (line 407)
An inner class valued for its unimplemented/"abstract" parameterized (`I`) function `f`. In specific implementations, `f` is parameterized with **functional interface** return types (namely, `IntBinaryOperator`, `IntConsumer`, and a custom `IntTernaryOperator`).

### `isPlayed()` (line 427)
Used to golf condition-checking. May or may not make sense on a high level (considering the game phases `PLAYABLE` and `OVER` as one super-phase).

### `equal()` (line 442)
Static helper method to check equality of **any number** of `Token` objects, through recursion). Based on someone's C++ [implementation](https://stackoverflow.com/a/8198279).

### `printGrid()` (line 451)
i didn't write this (pre-written by the professors).

[^shortCircuiting]: Of course, given a standard, casual Connect Four game, the execution time this short-circuiting saves is negligent. The same goes for all other short-circuiting heuristics in this Project.
[^negative]: "Up" is -1 because lower index values are at the top of the array.
[^side-benefit]: Another benefit is that i only need one indexing variable as opposed to two and/or a nested `for`.
[^ternary]: Those who noticed the ternary in `delta.f`'s lambda: its "else" case handles negative coordinates, namely (-1, 1), or "going northeast".
[^cellOrder]: In the specific `equal()` method for checking each 4-frame, one can see that it checks in a short-circuiting manner (see `equal()` for specifics) the current location first, then 3 cells away, then 1 cell away, then 2 cells away. The logic behind it supposes that it is rarely the case that there will be a four-in-a-row on the board, after any one move. Since all four-in-a-rows have a token and the same token 3 spaces away, this also rarely happens, and by eliminating this possibility, the `equal()` reveals the "false alarm" of a potential four-in-a-row, and quickly ends the loop. If it instead checked the cells in order, the result is that "false alarms" like two-in-a-rows and three-in-a-rows are always checked, to no avail.
