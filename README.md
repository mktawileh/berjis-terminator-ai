# Berjis Terminator AI
**Berjis Terminator AI** is a terminal-based implementation of the traditional Berjis board game. It allows a human player to play against a computer AI opponent in the classic race to bear off all game pieces into the central "kitchen" square.

About
This terminal game was created by [mktawileh](https://github.com/mktawileh) (UI and gameplay) and [Omar-Fostok](https://github.com/Omar-Fostok) (AI algorithm). It renders a text-based version of the Berjis board and pieces in the terminal. The player uses keyboard inputs to control their pieces each turn, while the AI player uses minimax search with alpha-beta pruning to determine optimal moves.

The AI opponent aims to provide a challenging Berjis game experience right in your terminal!

## Usage
- Building
```console
$ ./build.sh
```

- Running
```console
$ ./build.sh run
```

## Gamepaly

* Players take turns throwing 6 dice called "Al Wad'at" and move their pieces accordingly:
  * Score combinations allow entering more pieces or moving existing ones around the 4 loops to the central "kitchen" square.
  * If an opponent's piece lands on the same square as yours, you can knock them out back to the starting point.
  * Special squares called "Al Shareta" protect against knockout.
* The first team to bring all 4 pieces into the central kitchen wins! If one team bears off all pieces before the other places even one piece, they win "Mursh" (a gammon).
