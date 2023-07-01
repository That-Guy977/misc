## JavaScript - Directory Explore

This script provides utility for deeply traversing directories to provide statistics on file types.

### CLI

`node dir-explore.js [... dir]`

If no directories are provided, the `CWD` is explored.

### API

`explore(filepath = "/", log = false)` - Explores the given directory.

### Notes

Using `sudo` will allow exploring protected directories.
