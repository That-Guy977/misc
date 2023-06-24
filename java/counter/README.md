## Java - Counter

This program implements an indexed counter.

### CLI

`java Counter.java <count> [cap]`

#### Input

Input is taken from STDIN.  
Operations:
- `help` - Show help message
- `info` - Show detailed status
- `show` - Enables automatic printing
- `hide` - Disables automatic printing
- `print` - Print state
- `reset` - Reset counter
- `end` - End counter
- `n+[v]` - Add at position `n` value `v` (default `1`)
- `n-[v]` - Remove at position `n` value `v` (default `1`)
- `n=[v]` - Set at position `n` value `v` (default `0`)

### API

- `Counter(int count, int cap?)` - Instantiates counter with provided `count` and `cap` (default `1`)
- `int get(int pos)` - Gets value at position `pos`
- `boolean add(int pos, int val?)` - Adds at position `pos`, value `val` (default `1`)
- `boolean rem(int pos, int val?)` - Removes at position `pos`, value `val` (default `1`)
- `boolean set(int pos, int val?)` - Sets at position `pos`, value `val` (default `0`)
- `String state()` - Gives state representation
- `String info()` - Gives state breakdown
- `void reset()` - Resets counter

### Limitations

- Count: `Integer.MAX_VALUE`
- Cap: `Integer.MAX_VALUE`
