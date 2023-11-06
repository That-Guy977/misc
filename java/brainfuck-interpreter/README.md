## Java - Brainfuck Interpreter

This class implements a complete [Brainfuck](/bf/) <sub><sup>[🌐](https://en.wikipedia.org/wiki/Brainfuck 'Wikipedia') [🍋](https://esolangs.org/wiki/Brainfuck 'Esolang')</sup></sub> interpreter.

### CLI

`java Brainfuck.java "<brainfuck>" "[input]"`

#### Input

Input is taken from STDIN if no input is provided.

### API

- `Brainfuck(String)` - Parses Brainfuck code.
- `exec(String)` - Executes with the given `String` input.
- `exec(InputStream)` - Executes with the given `InputStream` input.

### Limitations

- Memory values: `0`-`255`
- Memory tape: `Integer.MAX_VALUE` cells
- Program length: `Integer.MAX_VALUE` commands
- Source code: `Integer.MAX_VALUE` characters

### Sample code

#### Hello World
```brainfuck
++++++++[>+++++++++>++++++++++++>++++++++++++++>++++>+++++++++++<<<<<-]>.>+++++.>----..+++.>.>-.<<.+++.------.<-.>>+.
```

#### Dice Roll Guess game
[`bf/dice-roll-guess`](/bf/dice-roll-guess/) (Minified)
```brainfuck
++++++++[>++++++++>++++++++++++++>++++++++++++>++++++++++++++>++++++++++++++>++++>++++>++++>+++++>++++++>+++++>++++++>+++++>++++>++++++++>+++++++++++++++>++++>++++++++++>+++++++++++++>++++++++++++>++++++++++++++>+++++++++++>++++++++>++++++++++++++>+++++++++++++++>+++++++>++++++>++++++<<<<<<<<<<<<<<<<<<<<<<<<<<<<-]>+++++++>+++++>+++++>+++>+++>>+++>>>+>+++++>++++++>+>>++>+>>++++>>+>++++>+++++++>+++++++>+++++>+>+>+++++++>+++++++[<]>[.>]>+>>++++++++[>++++++>>+>++++++++>++++++++++++++>++++++++++++>++++++++++++++>++++++++++++++>+++++++>++++>>++++++++>+++++++++>+++++++++++++>++++++++++++>>+++++++++++++>++++++++++++++>++++++++++++++>++++++++++++>++++++++++++>++++++++++++++<<<<<<<<<<<<<<<<<<<<<<-]+>++++>>++>+++++++>+++++>+++++>+++>+++>++>>>+++>+>++++++>+++>>+++++++>++>++>+++++>+++>++++[<]<[<]<[<]<<<<[>>>>>[.>]<[<]<<<,>>[>+<-]>[<<<->>+>-]<<<[[-]>->>>.[>]>>[.>]>[.>]<[<]<[<]<[<]<<<]>[>>>.[>]>.[>]>[.>]<[<]<[<]<[<]<<<<->]+<<]
```
