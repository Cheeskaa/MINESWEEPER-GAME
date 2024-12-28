Tile Class is an abstract class.

MineTile, TreasureTile, and EmptyTile inherit from Tile class.

Clickable is an Interface that is used for common behaviors.

Polymorphism is used in the Minesweeper class to call the reveal method on Tile references, which executes the appropriate method based on the actual object type (MineTile, TreasureTile, or EmptyTile).


