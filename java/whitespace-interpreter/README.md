## Java - Whitespace Interpreter

This class implements a complete [Whitespace](/ws/) <sub><sup>[🌐](https://en.wikipedia.org/wiki/Whitespace_(programming_language) 'Wikipedia') [🍋](https://esolangs.org/wiki/Whitespace 'Esolang')</sup></sub> interpreter.

### CLI

`java Whitespace.java "<file.ws>" "[input]"`

### Input

Input is taken from STDIN if no input is provided.

### API

- `Whitespace(String)` - Parses Whitespace code.
- `exec(String)` - Executes with the given `String` input.
- `exec(InputStream)` - Executes with the given `InputStream` input.

### Limitations

- Integer values: `-2147483647`-`2147483647`
- Stack memory: `Integer.MAX_VALUE` entries
- Heap memory: `Integer.MAX_VALUE` entries
- Call stack: `Integer.MAX_VALUE` entries
- Label count: `Integer.MAX_VALUE` labels
- Program length: `Integer.MAX_VALUE` commands
- Source code length: `Integer.MAX_VALUE` characters

### Sample code

#### Hello World
[`ws/java-polyglot`](/ws/java-polyglot/)
