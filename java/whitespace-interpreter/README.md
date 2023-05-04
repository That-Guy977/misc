### Java - Whitespace interpreter - WIP

#### About

[Whitespace](https://en.wikipedia.org/wiki/Whitespace_(programming_language)) is an esoteric programming language with commands comprised purely of whitespace characters; this class is an interpreter for Whitespace that implements all commands.

#### CLI
###### Arguments
- `"<file.ws>" "[input]"`

###### Input
Input is taken from STDIN (`System.in`) if no input is provided.

#### API
- `exec(String input)` - Takes input from the provided `String`.
- `exec(InputStream inputSource)` - Takes input from the provided `InputStream`.

#### Limitations

- Stack Memory is limited to `Integer.MAX_VALUE` entries as the stack is implemented using an `ArrayDeque<Integer>`.
- Heap Memory is limited to `Integer.MAX_VALUE` entries as the heap is implemented using a `HashMap<Integer, Integer>`.
- Call Stack is limited to `Integer.MAX_VALUE` calls as the stack is implemented using an `ArrayDeque<Integer>`.
- Label count is limited to `Integer.MAX_VALUE` labels as the label map is implemented using an `int[]`.
- Program length is limited to `Integer.MAX_VALUE` characters as the program is read and stored as a `String`.

#### Sample code

###### Hello World
See [`ws/java-polyglot`](/ws/java-polyglot/).
