### Java - Brainfuck interpreter

#### About

[Brainfuck](https://en.wikipedia.org/wiki/Brainfuck) is an esoteric programming language comprised of 8 single-letter commands; this class is an interpreter for Brainfuck that implements all 8 commands.

#### CLI
###### Arguments
- `"<brainfuck>" "[input]"`

###### Input
Input is taken from STDIN (`System.in`) if no input is provided.

#### API
- `exec(String input)` - Takes input from the provided `String`.
- `exec(InputStream inputSource)` - Takes input from the provided `InputStream`.

#### Limitations

- Memory is limited to `Integer.MAX_VALUE` cells as memory is implemented using an `ArrayList<String>`.
- Program length is limited to `Integer.MAX_VALUE` commands as commands are read and stored as a `String`.

#### Sample code

###### Hello World
```brainfuck
++++++++[>+++++++++>++++++++++++>++++++++++++++>++++>+++++++++++<<<<<-]>.>+++++.>----..+++.>.>-.<<.+++.------.<-.>>+.
```

###### Dice Roll Guess game
Minified version of [`bf/dice-roll-guess`](/bf/dice-roll-guess/):
```brainfuck
++++++++[>++++++++>++++++++++++++>++++++++++++>++++++++++++++>++++++++++++++>++++>++++>++++>+++++>++++++>+++++>++++++>+++++>++++>++++++++>+++++++++++++++>++++>++++++++++>+++++++++++++>++++++++++++>++++++++++++++>+++++++++++>++++++++>++++++++++++++>+++++++++++++++>+++++++>++++++>++++++<<<<<<<<<<<<<<<<<<<<<<<<<<<<-]>+++++++>+++++>+++++>+++>+++>>+++>>>+>+++++>++++++>+>>++>+>>++++>>+>++++>+++++++>+++++++>+++++>+>+>+++++++>+++++++[<]>[.>]>+>>++++++++[>++++++>>+>++++++++>++++++++++++++>++++++++++++>++++++++++++++>++++++++++++++>+++++++>++++>>++++++++>+++++++++>+++++++++++++>++++++++++++>>+++++++++++++>++++++++++++++>++++++++++++++>++++++++++++>++++++++++++>++++++++++++++<<<<<<<<<<<<<<<<<<<<<<-]+>++++>>++>+++++++>+++++>+++++>+++>+++>++>>>+++>+>++++++>+++>>+++++++>++>++>+++++>+++>++++[<]<[<]<[<]<<<<[>>>>>[.>]<[<]<<<,>>[>+<-]>[<<<->>+>-]<<<[[-]>->>>.[>]>>[.>]>[.>]<[<]<[<]<[<]<<<]>[>>>.[>]>.[>]>[.>]<[<]<[<]<[<]<<<<->]+<<]
```

#### Notes

Thanks to Anurag for helping with receiving input and Shinra for inspiring me to make this, both on the [Java Community Discord](https://discord.gg/java).
This project is a continuation of [`bf/dice-roll-guess`](/bf/dice-roll-guess/).
