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

- Stack Memory is limited to `Integer.MAX_VALUE` as the stack is implemented using a `Stack`.
- Heap Memory is limited to `Integer.MAX_VALUE` entries as the heap is implemented using a `HashMap<Integer>`.
- Call Stack is limited to `Integer.MAX_VALUE` calls as the stack is implemented using a `Stack`.
- Program length is limited to `Integer.MAX_VALUE` commands as commands are read and stored as a `String`.

#### Sample code

<!-- add -->
